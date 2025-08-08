package dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier;

import com.momosoftworks.coldsweat.api.temperature.modifier.BiomeTempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.ebo2022.pmtemperature.core.PMTemperature;
import dev.ebo2022.pmtemperature.core.PMTemperatureConfig;
import dev.ebo2022.pmtemperature.core.util.PMUtil;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.weather.ThermodynamicEngine;
import dev.protomanly.pmweather.weather.WindEngine;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class WrappedClimateTempModifier extends BiomeTempModifier {

    @Override
    public Function<Double, Double> calculate(LivingEntity entity, Temperature.Trait trait) {
        Level level = entity.level();
        ResourceKey<Level> dimension = level.dimension();
        if (ServerConfig.validDimensions.contains(dimension)) {
            ThermodynamicEngine.AtmosphericDataPoint data  = ThermodynamicEngine.samplePoint(
                    PMUtil.getWeatherHandler(level),
                    entity.position(),
                    level,
                    null,
                    0
            );
            return temp -> {
                double ambientTemp = PMTemperatureConfig.useApparentTemperature ? PMTemperature.getApparentTemperature(data.temperature(), data.dewpoint(), WindEngine.getWind(entity.position(), level)) : data.temperature();
                return temp + Temperature.convert(ambientTemp, Temperature.Units.C, Temperature.Units.MC, true);
            };
        }
        return super.calculate(entity, trait);
    }
}
