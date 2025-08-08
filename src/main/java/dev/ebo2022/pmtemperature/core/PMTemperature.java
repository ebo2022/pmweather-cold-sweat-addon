package dev.ebo2022.pmtemperature.core;

import com.mojang.logging.LogUtils;
import com.momosoftworks.coldsweat.api.event.core.init.DefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.BiomeTempModifier;
import com.momosoftworks.coldsweat.api.temperature.modifier.CaveBiomeTempModifier;
import com.momosoftworks.coldsweat.api.temperature.modifier.ElevationTempModifier;
import com.momosoftworks.coldsweat.api.temperature.modifier.compat.SereneSeasonsTempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier.WrappedCaveBiomeTempModifier;
import dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier.WrappedClimateTempModifier;
import dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier.WrappedElevationTempModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(PMTemperature.MOD_ID)
public class PMTemperature {
    public static final String MOD_ID = "pmtemperature";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean COLD_SWEAT_LOADED = ModList.get().isLoaded("cold_sweat");

    public PMTemperature(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        modEventBus.register(PMTemperatureConfig.class);

        modContainer.registerConfig(ModConfig.Type.SERVER, PMTemperatureConfig.SPEC);
        if (dist.isClient())
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        if (COLD_SWEAT_LOADED) {
            NeoForge.EVENT_BUS.register(new Object() {
                @SubscribeEvent
                public void registerTempModifiers(TempModifierRegisterEvent event) {
                    event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "wrapped_climate"), WrappedClimateTempModifier::new);
                    event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "wrapped_elevation"), WrappedElevationTempModifier::new);
                    event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "wrapped_cave_biome"), WrappedCaveBiomeTempModifier::new);
                }

                @SubscribeEvent
                public void addDefaultTempModifiers(DefaultTempModifiersEvent event) {
                    // get rid of stock biome-related modifiers
                    event.getModifiers(Temperature.Trait.WORLD).removeIf(modifier -> modifier instanceof BiomeTempModifier || modifier instanceof ElevationTempModifier || modifier instanceof CaveBiomeTempModifier || modifier instanceof SereneSeasonsTempModifier);
                    event.addModifier(Temperature.Trait.WORLD, new WrappedClimateTempModifier());
                    event.addModifier(Temperature.Trait.WORLD, new WrappedElevationTempModifier());
                    event.addModifier(Temperature.Trait.WORLD, new WrappedCaveBiomeTempModifier());
                }
            });
        }
    }

    public static double getApparentTemperature(float airTemp, float dewPoint, Vec3 wind) {
        if (airTemp > 19)
            return airTemp + 0.5555 * (6.11 * Math.exp(5417.753 * (0.0036609921 - (1 / (273.15 + dewPoint)))) - 10);
        if (airTemp < 0)
            return 13.12 + 0.6215 * airTemp - 11.37 * Math.pow(wind.length(), 0.16);
        return airTemp;
    }
}
