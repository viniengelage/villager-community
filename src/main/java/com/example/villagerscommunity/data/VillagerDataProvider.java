package com.example.villagerscommunity.data;

import com.example.villagerscommunity.VillagersCommunity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Provider para o data attachment do villager.
 */
public class VillagerDataProvider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VillagersCommunity.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VillagerData>> VILLAGER_DATA = ATTACHMENT_TYPES.register(
        "villager_data",
        () -> AttachmentType.builder(VillagerData::new).serialize(VillagerData.CODEC).build()
    );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    public static VillagerData get(net.minecraft.world.entity.npc.Villager villager) {
        return villager.getData(VILLAGER_DATA.get());
    }

    public static void set(net.minecraft.world.entity.npc.Villager villager, VillagerData data) {
        villager.setData(VILLAGER_DATA.get(), data);
    }

    public static void generateAndAssignData(net.minecraft.world.entity.npc.Villager villager) {
        String[] nameAndGender = VillagerNameGenerator.generateNameAndGender();
        VillagerData data = new VillagerData(nameAndGender[0], nameAndGender[1], false);
        set(villager, data);
    }
}