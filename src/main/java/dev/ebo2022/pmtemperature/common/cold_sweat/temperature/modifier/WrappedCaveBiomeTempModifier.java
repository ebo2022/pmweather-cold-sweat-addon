package dev.ebo2022.pmtemperature.common.cold_sweat.temperature.modifier;

import com.momosoftworks.coldsweat.api.temperature.modifier.CaveBiomeTempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.protomanly.pmweather.config.ServerConfig;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class WrappedCaveBiomeTempModifier extends CaveBiomeTempModifier {

    @Override
    protected Function<Double, Double> calculate(LivingEntity entity, Temperature.Trait trait) {
        if (ServerConfig.validDimensions.contains(entity.level().dimension()))
            return Function.identity();
        return super.calculate(entity, trait);
    }
}
