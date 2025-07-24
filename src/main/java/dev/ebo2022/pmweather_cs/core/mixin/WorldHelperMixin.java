package dev.ebo2022.pmweather_cs.core.mixin;

import com.momosoftworks.coldsweat.util.world.WorldHelper;
import dev.protomanly.pmweather.config.ServerConfig;
import dev.protomanly.pmweather.event.GameBusEvents;
import dev.protomanly.pmweather.weather.ThermodynamicEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
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
            ThermodynamicEngine.Precipitation type = ThermodynamicEngine.getPrecipitationType(
                    GameBusEvents.MANAGERS.get(dimension),
                    pos.getBottomCenter(),
                    level,
                    0
            );
            cir.setReturnValue(type == ThermodynamicEngine.Precipitation.RAIN && WorldHelper.canSeeSky(level, pos, level.getMaxBuildHeight()));
        }
    }
}
