package dev.ebo2022.pmtemperature.core;

import com.momosoftworks.coldsweat.api.event.core.init.DefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.temperature.modifier.BiomeTempModifier;
import com.momosoftworks.coldsweat.api.temperature.modifier.ElevationTempModifier;
import com.momosoftworks.coldsweat.api.temperature.modifier.compat.SereneSeasonsTempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier.WrappedClimateTempModifier;
import dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier.WrappedElevationTempModifier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class PMTemperatureEventHandler {



    public static void init() {

    }
}
