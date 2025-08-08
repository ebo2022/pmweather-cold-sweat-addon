package dev.ebo2022.pmtemperature.core;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class PMTemperatureConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue USE_APPARENT_TEMPERATURE = BUILDER
            .comment("Accounts for factors such as dew point and wind speed when calculating the temperature to supply to Cold Sweat.")
            .comment("This option may increase gameplay difficulty and should be disabled for casual play.")
            .define("use_apparent_temperature", false);
    public static boolean useApparentTemperature;
    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    private static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && !(event instanceof ModConfigEvent.Unloading)) {
            PMTemperature.LOGGER.info("Loading PMWTemperature config");
            useApparentTemperature = USE_APPARENT_TEMPERATURE.getAsBoolean();
        }
    }
}
