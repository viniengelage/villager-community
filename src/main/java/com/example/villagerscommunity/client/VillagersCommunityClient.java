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

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = VillagersCommunity.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = VillagersCommunity.MODID, value = Dist.CLIENT)
public class VillagersCommunityClient {
    private static net.minecraft.world.entity.npc.Villager lastLookedVillager = null;

    public VillagersCommunityClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        VillagersCommunity.LOGGER.info("HELLO FROM CLIENT SETUP");
        VillagersCommunity.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.hitResult != null) {
                if (mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
                    net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) mc.hitResult;
                    if (entityHit.getEntity() instanceof net.minecraft.world.entity.npc.Villager villager) {
                        if (villager != lastLookedVillager) {
                            // Remove name from previous villager
                            if (lastLookedVillager != null) {
                                lastLookedVillager.setCustomName(null);
                                lastLookedVillager.setCustomNameVisible(false);
                            }
                            VillagerData data = VillagerDataProvider.get(villager);
                            String name = data.getName();
                            if (name.isEmpty()) {
                                String[] temp = VillagerNameGenerator.generateNameAndGender();
                                name = temp[0];
                                VillagerData newData = new VillagerData(name, temp[1], data.isHasAssignedBed());
                                VillagerDataProvider.set(villager, newData);
                            }
                            // Set custom name
                            villager.setCustomName(net.minecraft.network.chat.Component.literal(name));
                            villager.setCustomNameVisible(true);
                            lastLookedVillager = villager;
                        }
                    } else {
                        if (lastLookedVillager != null) {
                            lastLookedVillager.setCustomName(null);
                            lastLookedVillager.setCustomNameVisible(false);
                            lastLookedVillager = null;
                        }
                    }
                } else {
                    if (lastLookedVillager != null) {
                        lastLookedVillager.setCustomName(null);
                        lastLookedVillager.setCustomNameVisible(false);
                        lastLookedVillager = null;
                    }
                }
            }
        }
    }
}
