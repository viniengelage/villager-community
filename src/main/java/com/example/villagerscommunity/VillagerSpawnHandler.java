package com.example.villagerscommunity;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;

/**
 * Clean skeleton handler for villager spawn processing.
 *
 * Use `VillagerSpawnHandler.register(modBus)` from your mod constructor when
 * you are ready to attach listeners. This avoids accidental registration of
 * incorrectly-typed @SubscribeEvent methods which cause runtime crashes.
 */
public class VillagerSpawnHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Register listeners to the provided event bus. Replace the stubbed
     * registration below with a concrete event type when known.
     */
    public static void register(IEventBus bus) {
        LOGGER.info("VillagerSpawnHandler.register() called — listeners attached.");
    }

    // Example handler to adapt later. Keep unannotated until you replace the
    // parameter with an actual Event subtype to avoid EventBus validation.
    @SuppressWarnings("unused")
    private static void onEntityJoinWorld(Object event) {
        LOGGER.debug("VillagerSpawnHandler.onEntityJoinWorld stub called: {}", event.getClass().getName());
    }
}