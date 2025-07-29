package dev.ebo2022.pmtemperature.core.mixin;

import com.momosoftworks.coldsweat.util.world.WorldHelper;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.ThermodynamicEngine;
import dev.protomanly.pmweather.weather.WeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldHelper.class)
public class WorldHelperMixin {

    @Inject(method = "isRainingAt", at = @At("HEAD"), cancellable = true)
    private static void isRainingAt(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ResourceKey<Level> dimension = level.dimension();
        if (ServerConfig.validDimensions.contains(dimension)) {
            WeatherHandler handler = GameBusEvents.MANAGERS.get(dimension);
            Vec3 vec3 = new Vec3(pos.getX(), pos.getY() + 1, pos.getZ());
            ThermodynamicEngine.Precipitation type = ThermodynamicEngine.getPrecipitationType(
                    handler,
                    vec3,
                    level,
                    0
            );
            cir.setReturnValue(handler.getPrecipitation(vec3) > 0D && type == ThermodynamicEngine.Precipitation.RAIN && WorldHelper.canSeeSky(level, pos, level.getMaxBuildHeight()));
        }
    }
}
