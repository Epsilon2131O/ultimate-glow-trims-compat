package epsilon.aat.glowup.compatmixin;

import com.mojang.blaze3d.vertex.PoseStack;
import epsilon.aat.glowup.BannerGlowBridge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * More Banner Features draws mount banners through vanilla's banner pattern renderer, which Glow Up already
 * hooks. We just announce which banner item is being drawn (and clear it afterwards).
 * @Pseudo = the target belongs to another mod; if More Banner Features isn't installed, this mixin is skipped.
 */
@Pseudo
@Mixin(targets = "dev.svrt.domiirl.mbf.layer.side.SideBannerRenderer")
public class SideBannerRendererGlowMixin {

    private static final String RENDER_BANNER =
            "renderBanner(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;FFFFF)V";

    @Inject(method = RENDER_BANNER, at = @At("HEAD"))
    private void aatglowup$beginBannerGlow(PoseStack poseStack, MultiBufferSource buffers, int light, LivingEntity entity,
                                           ItemStack banner, float a, float b, float c, float d, float e, CallbackInfo ci) {
        BannerGlowBridge.begin(banner);
    }

    @Inject(method = RENDER_BANNER, at = @At("RETURN"))
    private void aatglowup$endBannerGlow(PoseStack poseStack, MultiBufferSource buffers, int light, LivingEntity entity,
                                         ItemStack banner, float a, float b, float c, float d, float e, CallbackInfo ci) {
        BannerGlowBridge.end();
    }
}
