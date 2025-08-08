package dev.ebo2022.pmtemperature.core.util;

import dev.protomanly.pmweather.event.GameBusClientEvents;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.WeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PMUtil {

    public static Vec3 overheadPos(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY() + 1, pos.getZ());
    }

    public static WeatherHandler getWeatherHandler(Level level) {
        if (level.isClientSide()) {
            GameBusClientEvents.getClientWeather();
            return GameBusClientEvents.weatherHandler;
        } else {
            return GameBusEvents.MANAGERS.get(level.dimension());
        }
    }
}
