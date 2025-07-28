package dev.ebo2022.pmtemperature.core.mixin;

import com.google.common.collect.ImmutableMap;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class PMTemperatureMixinPlugin implements IMixinConfigPlugin {

    private static final String MIXIN_PACKAGE = "dev.ebo2022.pmtemperature.core.mixin.";
    private static final Map<String, BooleanSupplier> CONDITIONS = ImmutableMap.of(
            MIXIN_PACKAGE + "WorldHelperMixin", () -> modLoaded("cold_sweat"),
            MIXIN_PACKAGE + "TemperatureHelperImplMixin", () -> modLoaded("toughasnails")
    );

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, () -> true).getAsBoolean();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public static boolean modLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}
