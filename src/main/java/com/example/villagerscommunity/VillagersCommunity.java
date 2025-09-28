package com.example.villagerscommunity;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import com.example.villagerscommunity.data.VillagerDataProvider;
import com.example.villagerscommunity.data.VillagerData;
import com.example.villagerscommunity.data.VillagerProfession;

// O valor aqui deve corresponder a uma entrada no arquivo META-INF/neoforge.mods.toml
@Mod(VillagersCommunity.MODID)
public class VillagersCommunity {
    // Define o ID do mod em um local comum para tudo referenciar
    public static final String MODID = "villagerscommunity";
    // Referência direta a um logger slf4j
    public static final Logger LOGGER = LogUtils.getLogger();

    // Contador para verificação de profissões (a cada 20 ticks = 1 segundo)
    private static int tickCounter = 0;
        // Counter for bed assignment checking (every 20 seconds = 400 ticks during night)
    private static int bedCheckCounter = 0;
    // Counter for baby villager sleep prevention (every 20 ticks = 1 second)
    private static int babySleepPreventionCounter = 0;

    // O construtor da classe mod é o primeiro código executado quando o mod é carregado.
    // FML reconhecerá alguns tipos de parâmetro como IEventBus ou ModContainer e os passará automaticamente.
    public VillagersCommunity(IEventBus modEventBus) {
        // Registra o método commonSetup para carregamento do mod
        modEventBus.addListener(this::commonSetup);

        // Registra os tipos de attachment
        VillagerDataProvider.register(modEventBus);

        // Registra-nos para eventos do servidor e outros eventos do jogo que estamos interessados.
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Algum código de configuração comum
        LOGGER.info("Mod Villagers Community carregado com sucesso!");
    }

    // Evento para verificar mudanças de profissão a cada segundo e atribuição de camas a cada 5 minutos
    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        bedCheckCounter++;
        babySleepPreventionCounter++;

        // Verificação de bebês dormindo a cada 20 ticks (1 segundo) - otimizado para performance
        if (babySleepPreventionCounter >= 20) {
            babySleepPreventionCounter = 0;
            preventBabyVillagersFromSleeping(event.getServer());
        }

        // Verifica mudanças de profissão a cada segundo
        if (tickCounter >= 20) { // A cada 20 ticks (1 segundo)
            tickCounter = 0;

            // Verifica apenas villagers monitorados para mudanças de profissão (muito mais eficiente!)
            for (var villager : VillagerDataProvider.getMonitoredVillagers()) {
                if (villager.isAlive() && !villager.isRemoved()) {
                    if (VillagerDataProvider.updateProfessionIfChanged(villager)) {
                        // Profissão mudou, mostrar mensagem
                        VillagerData data = VillagerDataProvider.get(villager);
                        if (data != null && !data.getProfession().equals(VillagerProfession.NONE)) {
                            String professionDisplay = getProfessionDisplayName(data.getProfession());
                            Component message = Component.literal("§6[Villager] §f" + data.getName() + " agora tem a profissão: §e" + professionDisplay);

                            // Envia para todos os jogadores no nível do villager
                            var level = villager.level();
                            for (var player : level.players()) {
                                if (player instanceof ServerPlayer serverPlayer) {
                                    serverPlayer.sendSystemMessage(message);
                                }
                            }

                            LOGGER.info("Villager {} ganhou profissão: {}", data.getName(), data.getProfession());
                        }
                    }
                } else {
                    // Villager está morto ou removido, parar de monitorar
                    VillagerDataProvider.stopMonitoring(villager);
                }
            }
        }

                // Check for bed assignment every 20 seconds (400 ticks) when it's nighttime
        if (bedCheckCounter >= 400) { // A cada 400 ticks (20 segundos)
            bedCheckCounter = 0;

            var server = event.getServer();
            for (var level : server.getAllLevels()) {
                // Only assign beds at night
                if (level.isNight()) {
                    assignVillagersToDesignatedBeds(level);
                }
            }
        }
    }

        // Evento para atribuir nomes a villagers quando eles entram no nível
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.npc.Villager villager && event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // Villagers bebês não são monitorados (não dormem)
            if (villager.isBaby()) {
                return;
            }

            // Verifica se villager já tem dados
            if (VillagerDataProvider.get(villager) == null) {
                // Gera e atribui novos dados com busca de cama
                VillagerDataProvider.generateAndAssignDataWithBed(villager, serverLevel);

                // Log do spawn (sem mensagem no chat)
                VillagerData data = VillagerDataProvider.get(villager);
                if (data != null) {
                    LOGGER.info("Villager {} ({}) spawnou em {} com profissão: {}",
                               data.getName(), data.getGender(),
                               villager.blockPosition(), data.getProfession());
                }
            }
        }
    }

    private static String getProfessionDisplayName(VillagerProfession profession) {
        return profession.getDisplayName();
    }

    /**
     * Atribui villagers às suas camas designadas à noite
     */
    private void assignVillagersToDesignatedBeds(net.minecraft.server.level.ServerLevel level) {
        for (var villager : VillagerDataProvider.getMonitoredVillagers()) {
            // Só processa villagers adultos (bebês não dormem)
            if (villager.isAlive() && !villager.isRemoved() && villager.level() == level && !villager.isBaby()) {
                // Verificação extra: se um bebê conseguiu passar, para ele imediatamente
                if (villager.isBaby()) {
                    if (villager.isSleeping()) {
                        villager.stopSleeping();
                        LOGGER.debug("Villager bebê parado de dormir");
                    }
                    continue;
                }

                VillagerData data = VillagerDataProvider.get(villager);
                if (data != null && data.isHasAssignedBed()) {
                    BlockPos bedPos = new BlockPos(data.getBedX(), data.getBedY(), data.getBedZ());

                    // Verifica se a cama ainda existe e é válida
                    if (!isBedValid(level, bedPos)) {
                        // Cama não existe mais, tenta encontrar uma nova
                        BlockPos newBed = VillagerDataProvider.findNearestBed(villager, level, true);
                        if (newBed != null) {
                            bedPos = newBed;
                            // Atualiza os dados do villager com a nova cama
                            data.setBedX(newBed.getX());
                            data.setBedY(newBed.getY());
                            data.setBedZ(newBed.getZ());
                            VillagerDataProvider.set(villager, data);
                            LOGGER.info("Villager {} teve cama destruída, reatribuído para nova cama em {}", data.getName(), newBed);
                        } else {
                            // Nenhuma cama disponível, pula este villager
                            continue;
                        }
                    }

                    // Verifica se a cama está ocupada por outro villager
                    if (VillagerDataProvider.isBedOccupied(level, bedPos)) {
                        // Cama está ocupada, tenta encontrar outra cama disponível
                        BlockPos newBed = VillagerDataProvider.findNearestBed(villager, level, true);
                        if (newBed != null && !newBed.equals(bedPos)) { // Garante que não é a mesma cama
                            bedPos = newBed;
                            // Atualiza os dados do villager com a nova cama
                            data.setBedX(newBed.getX());
                            data.setBedY(newBed.getY());
                            data.setBedZ(newBed.getZ());
                            VillagerDataProvider.set(villager, data);
                            LOGGER.info("Villager {} cama ocupada, reatribuído para nova cama em {}", data.getName(), newBed);
                        } else {
                            // Nenhuma cama disponível, pula este villager
                            continue;
                        }
                    }

                    // Faz o villager ir dormir na cama designada
                    if (!villager.isSleeping()) {
                        // Verifica se o villager já está indo dormir em alguma cama
                        var sleepingPos = villager.getSleepingPos();
                        if (sleepingPos.isPresent()) {
                            // Villager já está indo dormir, verifica se é na cama certa
                            if (!sleepingPos.get().equals(bedPos)) {
                                // Está indo para cama errada, força ir para a certa
                                villager.stopSleeping();
                                villager.getNavigation().moveTo(bedPos.getX() + 0.5, bedPos.getY(), bedPos.getZ() + 0.5, 1.0);
                                LOGGER.debug("Villager {} redirecionado da cama errada {} para cama designada {}", data.getName(), sleepingPos.get(), bedPos);
                            }
                            // Se já está indo para a cama certa, deixa quieto
                        } else {
                            // Não está indo dormir, verifica se está perto o suficiente da cama
                            if (villager.blockPosition().distSqr(bedPos) <= 4.0) {
                                // Perto da cama, tenta dormir
                                if (level.getBlockState(bedPos).getBlock() instanceof net.minecraft.world.level.block.BedBlock) {
                                    villager.startSleeping(bedPos);
                                    LOGGER.debug("Villager {} indo dormir na cama designada {}", data.getName(), bedPos);
                                }
                            } else {
                                // Longe da cama, faz caminhar até lá
                                villager.getNavigation().moveTo(bedPos.getX() + 0.5, bedPos.getY(), bedPos.getZ() + 0.5, 1.0);
                                LOGGER.debug("Villager {} caminhando para cama designada {}", data.getName(), bedPos);
                            }
                        }
                    }
                }
            } else if (villager.isBaby() && villager.isAlive() && !villager.isRemoved() && villager.level() == level) {
                // Verificação extra para bebês que possam ter passado: garante que não durmam
                if (villager.isSleeping()) {
                    villager.stopSleeping();
                    LOGGER.debug("Villager bebê parado de dormir (segurança extra)");
                }
            }
        }
    }

    /**
     * Verifica se uma cama ainda existe e é válida no mundo
     */
    private boolean isBedValid(net.minecraft.server.level.ServerLevel level, BlockPos bedPos) {
        var blockState = level.getBlockState(bedPos);
        return blockState.getBlock() instanceof net.minecraft.world.level.block.BedBlock;
    }

    /**
     * Método otimizado: impede que villagers bebês durmam, sobrescrevendo qualquer lógica do Minecraft
     * Executado a cada segundo (20 ticks) para balancear segurança e performance
     */
    private void preventBabyVillagersFromSleeping(net.minecraft.server.MinecraftServer server) {
        for (var level : server.getAllLevels()) {
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                // Otimizado: obtém apenas villagers em vez de todas as entidades usando AABB infinito
                AABB worldBounds = new AABB(
                    Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                );
                var villagers = serverLevel.getEntitiesOfClass(net.minecraft.world.entity.npc.Villager.class, worldBounds);

                for (var villager : villagers) {
                    // Se é um bebê E está dormindo, acorda imediatamente
                    if (villager.isBaby() && villager.isSleeping()) {
                        villager.stopSleeping();
                        LOGGER.debug("Villager bebê impedido de dormir - lógica sobrescrita");
                    }
                }
            }
        }
    }
}
