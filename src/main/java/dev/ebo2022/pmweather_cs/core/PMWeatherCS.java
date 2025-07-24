package dev.ebo2022.pmweather_cs.core;

import com.mojang.logging.LogUtils;
import com.momosoftworks.coldsweat.api.event.core.init.DefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.BiomeTempModifier;
import com.momosoftworks.coldsweat.api.temperature.modifier.ElevationTempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.ebo2022.pmweather_cs.common.temperature.modifier.WrappedClimateTempModifier;
import dev.ebo2022.pmweather_cs.common.temperature.modifier.WrappedElevationTempModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(PMWeatherCS.MOD_ID)
public class PMWeatherCS {
    public static final String MOD_ID = "pmweather_cs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PMWeatherCS(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        NeoForge.EVENT_BUS.register(PMWeatherCS.class);
        modEventBus.register(PMWeatherCSConfig.class);

        modContainer.registerConfig(ModConfig.Type.SERVER, PMWeatherCSConfig.SPEC);
        if (dist.isClient())
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    private static void registerTempModifiers(TempModifierRegisterEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "wrapped_climate"), WrappedClimateTempModifier::new);
        event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "wrapped_elevation"), WrappedElevationTempModifier::new);
    }

    @SubscribeEvent
    private static void addDefaultTempModifiers(DefaultTempModifiersEvent event) {
        // get rid of stock biome-related modifiers
        event.getModifiers(Temperature.Trait.WORLD).removeIf(modifier -> modifier instanceof BiomeTempModifier || modifier instanceof ElevationTempModifier);
        event.addModifier(Temperature.Trait.WORLD, new WrappedClimateTempModifier());
        event.addModifier(Temperature.Trait.WORLD, new WrappedElevationTempModifier());
    }

    public static double getApparentTemperature(float airTemp, float dewPoint, Vec3 wind) {
        if (airTemp > 19)
            return airTemp + 0.5555 * (6.11 * Math.exp(5417.753 * (0.0036609921 - (1 / (273.15 + dewPoint)))) - 10);
        if (airTemp < 0)
            return 13.12 + 0.6215 * airTemp - 11.37 * Math.pow(wind.length(), 0.16);
        return airTemp;
    }
}
