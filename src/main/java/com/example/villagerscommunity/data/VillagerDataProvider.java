package com.example.villagerscommunity.data;

import com.example.villagerscommunity.VillagersCommunity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provider para o data attachment do villager.
 * Gerencia o armazenamento e recuperação de dados customizados dos villagers.
 */
public class VillagerDataProvider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VillagersCommunity.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VillagerData>> VILLAGER_DATA = ATTACHMENT_TYPES.register(
        "villager_data",
        () -> AttachmentType.builder(VillagerData::new).serialize(VillagerData.CODEC).build()
    );

    // Cache de villagers monitorados para performance (usa WeakHashMap para evitar vazamentos de memória)
    private static final Map<net.minecraft.world.entity.npc.Villager, Boolean> MONITORED_VILLAGERS = new WeakHashMap<>();

    /**
     * Registra os tipos de attachment no mod event bus
     */
    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    /**
     * Recupera os dados customizados de um villager
     */
    public static VillagerData get(net.minecraft.world.entity.npc.Villager villager) {
        return villager.getData(VILLAGER_DATA.get());
    }

    public static void set(net.minecraft.world.entity.npc.Villager villager, VillagerData data) {
        villager.setData(VILLAGER_DATA.get(), data);
        // Adicionar ao cache de monitoramento quando dados são atribuídos
        MONITORED_VILLAGERS.put(villager, true);
    }

    /**
     * Retorna uma coleção de villagers que estão sendo monitorados
     */
    public static java.util.Set<net.minecraft.world.entity.npc.Villager> getMonitoredVillagers() {
        // Limpar entradas inválidas (WeakHashMap faz isso automaticamente, mas garantimos)
        MONITORED_VILLAGERS.entrySet().removeIf(entry -> entry.getKey().isRemoved());
        return MONITORED_VILLAGERS.keySet();
    }

    /**
     * Remove um villager do monitoramento (útil quando o villager morre ou é removido)
     */
    public static void stopMonitoring(net.minecraft.world.entity.npc.Villager villager) {
        MONITORED_VILLAGERS.remove(villager);
    }

    public static void generateAndAssignData(net.minecraft.world.entity.npc.Villager villager) {
        String[] nameAndGender = VillagerNameGenerator.generateNameAndGender();
        VillagerData data = new VillagerData(nameAndGender[0], nameAndGender[1], false);
        set(villager, data);
    }

    /**
     * Verifica se uma cama está ocupada verificando a propriedade BedBlock.OCCUPIED
     * @param level o nível onde a cama está localizada
     * @param bedPos posição da cama (pode ser HEAD ou FOOT)
     * @return true se a cama estiver ocupada
     */
    public static boolean isBedOccupied(Level level, BlockPos bedPos) {
        BlockState state = level.getBlockState(bedPos);
        if (!(state.getBlock() instanceof BedBlock)) {
            return false;
        }

        // Verificar se é a parte HEAD da cama (só ela tem a propriedade OCCUPIED)
        BedPart part = state.getValue(BedBlock.PART);
        if (part == BedPart.HEAD) {
            return state.getValue(BedBlock.OCCUPIED);
        } else {
            // Se é a parte FOOT, verificar a parte HEAD
            BlockPos headPos = bedPos.relative(state.getValue(BedBlock.FACING).getOpposite());
            BlockState headState = level.getBlockState(headPos);
            if (headState.getBlock() instanceof BedBlock) {
                return headState.getValue(BedBlock.OCCUPIED);
            }
        }

        return false;
    }

    /**
     * Busca a cama mais próxima do villager dentro de um raio de 32 blocos
     * @param villager o villager que está procurando cama
     * @param level o nível onde procurar
     * @return a posição da cama mais próxima, ou null se não encontrar
     */
    public static BlockPos findNearestBed(net.minecraft.world.entity.npc.Villager villager, Level level) {
        return findNearestBed(villager, level, false);
    }

    /**
     * Busca a cama mais próxima do villager dentro de um raio de 32 blocos
     * @param skipOccupiedBeds se true, pula camas que estão ocupadas
     */
    public static BlockPos findNearestBed(net.minecraft.world.entity.npc.Villager villager, Level level, boolean skipOccupiedBeds) {
        BlockPos villagerPos = villager.blockPosition();
        int searchRadius = 32;

        // Cria uma bounding box ao redor do villager
        AABB searchArea = new AABB(
            villagerPos.getX() - searchRadius, villagerPos.getY() - searchRadius, villagerPos.getZ() - searchRadius,
            villagerPos.getX() + searchRadius, villagerPos.getY() + searchRadius, villagerPos.getZ() + searchRadius
        );

        BlockPos nearestBed = null;
        double nearestDistance = Double.MAX_VALUE;

        // Itera por todos os blocos na área de busca
        for (BlockPos pos : BlockPos.betweenClosed(
                (int) searchArea.minX, (int) searchArea.minY, (int) searchArea.minZ,
                (int) searchArea.maxX, (int) searchArea.maxY, (int) searchArea.maxZ)) {

            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof BedBlock) {
                // Pular camas ocupadas se solicitado
                if (skipOccupiedBeds && isBedOccupied(level, pos)) {
                    continue;
                }

                double distance = villagerPos.distSqr(pos);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestBed = pos;
                }
            }
        }

        return nearestBed;
    }

    /**
     * Gera dados e atribui uma cama ao villager
     */
    public static void generateAndAssignDataWithBed(net.minecraft.world.entity.npc.Villager villager, Level level) {
        String[] nameAndGender = VillagerNameGenerator.generateNameAndGender();
        BlockPos nearestBed = findNearestBed(villager, level, true); // skip occupied beds

        VillagerData data;
        if (nearestBed != null) {
            data = new VillagerData(nameAndGender[0], nameAndGender[1], true,
                                   nearestBed.getX(), nearestBed.getY(), nearestBed.getZ());
            VillagersCommunity.LOGGER.info("Villager {} spawned with bed at {}", nameAndGender[0], nearestBed);
        } else {
            data = new VillagerData(nameAndGender[0], nameAndGender[1], false);
            VillagersCommunity.LOGGER.info("Villager {} spawned without bed (none available)", nameAndGender[0]);
        }

        // Set initial profession
        VillagerProfession currentProfession = getProfession(villager);
        data.setProfession(currentProfession);

        set(villager, data);
    }

    /**
     * Verifica se a profissão do villager mudou e atualiza os dados
     * @param villager o villager a ser verificado
     * @return true se a profissão mudou, false caso contrário
     */
    public static boolean updateProfessionIfChanged(net.minecraft.world.entity.npc.Villager villager) {
        VillagerData data = get(villager);
        if (data == null) return false;

        VillagerProfession currentProfession = getProfession(villager);
        VillagerProfession storedProfession = data.getProfession();

        if (!currentProfession.equals(storedProfession)) {
            data.setProfession(currentProfession);

            // Se ganhou uma profissão (não é NONE), buscar e atribuir uma cama disponível
            if (!currentProfession.equals(VillagerProfession.NONE) && !data.isHasAssignedBed()) {
                BlockPos availableBed = findNearestBed(villager, (Level) villager.level(), true); // skip occupied beds
                if (availableBed != null) {
                    data.setHasAssignedBed(true);
                    data.setBedX(availableBed.getX());
                    data.setBedY(availableBed.getY());
                    data.setBedZ(availableBed.getZ());
                    VillagersCommunity.LOGGER.info("Villager {} assigned bed at {}", data.getName(), availableBed);
                }
            }

            set(villager, data);
            return true;
        }

        return false;
    }

    /**
     * Obtém a profissão de um villager e retorna como enum VillagerProfession
     * @param villager o villager cuja profissão será obtida
     * @return a profissão como enum, ou NONE se não conseguir determinar
     */
    public static VillagerProfession getProfession(net.minecraft.world.entity.npc.Villager villager) {
        var profession = villager.getVillagerData().getProfession();
        if (profession == null) return VillagerProfession.NONE;
        return VillagerProfession.fromKey(profession.name());
    }
}