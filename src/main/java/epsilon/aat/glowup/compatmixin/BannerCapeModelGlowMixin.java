package epsilon.aat.glowup.compatmixin;

import com.llamalad7.mixinextras.sugar.Local;
import epsilon.aat.glowup.BannerGlowBridge;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Banner Capes draws capes (worn normally or in a Curios slot) through vanilla's banner pattern renderer,
 * which Glow Up already hooks. Same fix as for mounted banners: announce which banner item is being drawn.
 * @Local(argsOnly) grabs the first ItemStack argument (the cape) without naming Banner Capes' own classes.
 */
@Pseudo
@Mixin(targets = "liedge.bannercapes.client.BannerCapeModel")
public class BannerCapeModelGlowMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void aatglowup$beginCapeGlow(CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) ItemStack cape) {
        BannerGlowBridge.begin(cape);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void aatglowup$endCapeGlow(CallbackInfo ci) {
        BannerGlowBridge.end();
    }
}
