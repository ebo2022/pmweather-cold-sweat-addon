package dev.ebo2022.pmtemperature.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ebo2022.pmtemperature.core.PMTemperature;
import dev.ebo2022.pmtemperature.core.PMTemperatureConfig;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusClientEvents;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.ThermodynamicEngine;
import dev.protomanly.pmweather.weather.WeatherHandler;
import dev.protomanly.pmweather.weather.WeatherHandlerClient;
import dev.protomanly.pmweather.weather.WindEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.init.ModConfig;
import toughasnails.temperature.TemperatureHelperImpl;

@Mixin(TemperatureHelperImpl.class)
public class TemperatureHelperImplMixin {

    @Inject(method = "getBiomeTemperatureLevel", at = @At("HEAD"), cancellable = true)
    private static void getBiomeTemperatureLevel(Level level, BlockPos pos, CallbackInfoReturnable<TemperatureLevel> cir) {
        ResourceKey<Level> dimension = level.dimension();
        if (ServerConfig.validDimensions.contains(dimension)) {
            ThermodynamicEngine.AtmosphericDataPoint data  = ThermodynamicEngine.samplePoint(
                    GameBusEvents.MANAGERS.get(dimension),
                    pos.getBottomCenter(),
                    level,
                    null,
                    0
            );

            double temperature = PMTemperatureConfig.useApparentTemperature ? PMTemperature.getApparentTemperature(data.temperature(), data.dewpoint(), WindEngine.getWind(pos, level)) : data.temperature();
            temperature = temperature / 25d;

            if (temperature < 0.15D) cir.setReturnValue(TemperatureLevel.ICY);
            if (temperature >= 0.15F && temperature < 0.45F) cir.setReturnValue(TemperatureLevel.COLD);
            if (temperature >= 0.45F && temperature < 0.85F) cir.setReturnValue(TemperatureLevel.NEUTRAL);
            if (temperature >= 0.85F && temperature < 1.0F) cir.setReturnValue(TemperatureLevel.WARM);
            if (temperature >= 1.0F) cir.setReturnValue(TemperatureLevel.HOT);
        }
    }

    @Inject(method = "nightModifier", at = @At("HEAD"), cancellable = true)
    private static void nightModifier(Level level, BlockPos pos, TemperatureLevel current, CallbackInfoReturnable<TemperatureLevel> cir) {
        if (ServerConfig.validDimensions.contains(level.dimension()))
            cir.setReturnValue(current);
    }

    @Inject(method = "altitudeModifier", at = @At("HEAD"), cancellable = true)
    private static void altitudeModifier(Level level, BlockPos pos, TemperatureLevel current, CallbackInfoReturnable<TemperatureLevel> cir) {
        if (ServerConfig.validDimensions.contains(level.dimension()))
            cir.setReturnValue(current);
    }

    @Inject(method = "rainModifier", at = @At("HEAD"), cancellable = true)
    private static void rainModifier(Level level, BlockPos pos, TemperatureLevel current, CallbackInfoReturnable<TemperatureLevel> cir) {
        ResourceKey<Level> dimension = level.dimension();
        if (ServerConfig.validDimensions.contains(dimension)) {
            double precip;
            WeatherHandler weatherHandler;
            Vec3 vec3 = new Vec3(pos.getX(), pos.getY() + 1, pos.getZ());

            if (level.isClientSide()) {
                GameBusClientEvents.getClientWeather();
                weatherHandler = GameBusClientEvents.weatherHandler;
                precip = weatherHandler.getPrecipitation(vec3);
            } else {
                weatherHandler = GameBusEvents.MANAGERS.get(level.dimension());
                precip = weatherHandler.getPrecipitation(pos.getCenter());
            }

            if (precip >= 0.2F && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
                ThermodynamicEngine.Precipitation type = ThermodynamicEngine.getPrecipitationType(
                        weatherHandler,
                        vec3,
                        level,
                        0
                );
                if (type == ThermodynamicEngine.Precipitation.RAIN || type == ThermodynamicEngine.Precipitation.FREEZING_RAIN || type == ThermodynamicEngine.Precipitation.HAIL)
                    cir.setReturnValue(current.increment(ModConfig.temperature.wetTemperatureChange));
                if (type == ThermodynamicEngine.Precipitation.SNOW || type == ThermodynamicEngine.Precipitation.SLEET || type == ThermodynamicEngine.Precipitation.WINTRY_MIX)
                    cir.setReturnValue(current.increment(ModConfig.temperature.snowTemperatureChange));
                else
                    cir.setReturnValue(current);
            } else  {
                cir.setReturnValue(current);
            }
        }
    }

    @WrapOperation(method = "immersionModifier", at = @At(value = "INVOKE", target = "Ltoughasnails/temperature/TemperatureHelperImpl;isExposedToRain(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean immersionModifier(Level level, BlockPos pos, Operation<Boolean> original) {
        ResourceKey<Level> dimension = level.dimension();
        if (ServerConfig.validDimensions.contains(dimension)) {
            ThermodynamicEngine.Precipitation type = ThermodynamicEngine.getPrecipitationType(
                    GameBusEvents.MANAGERS.get(dimension),
                    new Vec3(pos.getX(), pos.getY() + 1, pos.getZ()),
                    level,
                    0
            );
            return type != null && pos.getY() >= level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).below().getY();
        }
        return original.call(level, pos);
    }
}
