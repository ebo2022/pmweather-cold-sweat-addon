package dev.ebo2022.pmweather_cs.common.temperature.modifier;

import com.momosoftworks.coldsweat.api.temperature.modifier.BiomeTempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.ebo2022.pmweather_cs.core.PMWeatherCS;
import dev.ebo2022.pmweather_cs.core.PMWeatherCSConfig;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
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
                    GameBusEvents.MANAGERS.get(dimension),
                    entity.position(),
                    level,
                    null,
                    0
            );
            return temp -> {
                double ambientTemp = PMWeatherCSConfig.useApparentTemperature ? PMWeatherCS.getApparentTemperature(data.temperature(), data.dewpoint(), WindEngine.getWind(entity.position(), level)) : data.temperature();
                return temp + Temperature.convert(ambientTemp, Temperature.Units.C, Temperature.Units.MC, true);
            };
        }
        return super.calculate(entity, trait);
    }
}
