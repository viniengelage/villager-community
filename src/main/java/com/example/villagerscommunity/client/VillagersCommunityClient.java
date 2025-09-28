package com.example.villagerscommunity.client;

import com.example.villagerscommunity.data.VillagerData;
import com.example.villagerscommunity.data.VillagerDataProvider;
import com.example.villagerscommunity.data.VillagerNameGenerator;
import com.example.villagerscommunity.VillagersCommunity;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

// Esta classe não será carregada em servidores dedicados. Acessar código do lado cliente daqui é seguro.
@Mod(value = VillagersCommunity.MODID, dist = Dist.CLIENT)
// Você pode usar EventBusSubscriber para registrar automaticamente todos os métodos estáticos na classe anotados com @SubscribeEvent
@EventBusSubscriber(modid = VillagersCommunity.MODID, value = Dist.CLIENT)
public class VillagersCommunityClient {
    // Último villager que o jogador olhou (para mostrar nome temporário)
    private static net.minecraft.world.entity.npc.Villager lastLookedVillager = null;

    public VillagersCommunityClient(ModContainer container) {
        // Permite que o NeoForge crie uma tela de configuração para as configs deste mod.
        // A tela de configuração é acessada indo para a tela Mods > clicando no seu mod > clicando em config.
        // Não esqueça de adicionar traduções para suas opções de config no arquivo en_us.json.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Algum código de configuração do cliente
        VillagersCommunity.LOGGER.info("HELLO FROM CLIENT SETUP");
        VillagersCommunity.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Mostra o nome do villager temporariamente quando o jogador olha para ele
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.hitResult != null) {
                if (mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
                    net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) mc.hitResult;
                    if (entityHit != null && entityHit.getEntity() instanceof net.minecraft.world.entity.npc.Villager villager) {
                        if (villager != lastLookedVillager) {
                            // Remove nome do villager anterior
                            if (lastLookedVillager != null) {
                                lastLookedVillager.setCustomName(null);
                                lastLookedVillager.setCustomNameVisible(false);
                            }
                            VillagerData data = VillagerDataProvider.get(villager);
                            String name = data.getName();
                            if (name.isEmpty()) {
                                // Gera nome se não tiver
                                String[] temp = VillagerNameGenerator.generateNameAndGender();
                                name = temp[0];
                                VillagerData newData = new VillagerData(name, temp[1], data.isHasAssignedBed());
                                VillagerDataProvider.set(villager, newData);
                            }
                            // Define nome customizado
                            villager.setCustomName(net.minecraft.network.chat.Component.literal(name));
                            villager.setCustomNameVisible(true);
                            lastLookedVillager = villager;
                        }
                    } else {
                        // Remove nome se não estiver olhando para villager
                        if (lastLookedVillager != null) {
                            lastLookedVillager.setCustomName(null);
                            lastLookedVillager.setCustomNameVisible(false);
                            lastLookedVillager = null;
                        }
                    }
                } else {
                    // Remove nome se não estiver olhando para entidade
                    if (lastLookedVillager != null) {
                        lastLookedVillager.setCustomName(null);
                        lastLookedVillager.setCustomNameVisible(false);
                        lastLookedVillager = null;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // Apenas no modo desenvolvimento/debug - verifica se shift está pressionado
        if (event.getEntity() instanceof net.minecraft.client.player.LocalPlayer player &&
            event.getTarget() instanceof net.minecraft.world.entity.npc.Villager villager &&
            player.isShiftKeyDown()) {

            // Obtém dados do villager
            VillagerData data = VillagerDataProvider.get(villager);
            if (data != null) {
                // Mostra informações do villager no chat
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6=== Informações do Villager ==="));
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eNome: §f" + data.getName()));
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eGênero: §f" + data.getGender()));

                // Mostra informações da profissão
                var profession = villager.getVillagerData().getProfession();
                String professionName = profession.name();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eProfissão: §f" + professionName));

                var level = villager.getVillagerData().getLevel();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eNível: §f" + level));

                // Mostra informações da cama
                if (data.isHasAssignedBed()) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eCama: §f(" + data.getBedX() + ", " + data.getBedY() + ", " + data.getBedZ() + ")"));
                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eCama: §fNenhuma atribuída"));
                }

                // Mostra posição
                var pos = villager.blockPosition();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§ePosição: §f(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));

                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6============================"));

                // Cancela a interação para prevenir outras ações
                event.setCanceled(true);
            }
        }
    }
}
