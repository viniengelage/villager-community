package com.example.villagerscommunity;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import com.example.villagerscommunity.data.VillagerDataProvider;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(VillagersCommunity.MODID)
public class VillagersCommunity {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "villagerscommunity";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public VillagersCommunity(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register attachment types
        VillagerDataProvider.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Villagers Community mod loaded successfully!");
    }

    // Event to assign names to villagers when they join the level
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.npc.Villager villager && event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // Check if villager already has data
            if (VillagerDataProvider.get(villager) == null) {
                // Generate and assign new data
                VillagerDataProvider.generateAndAssignData(villager);
            }
        }
    }
}
