package epsilon.aat.glowup.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import epsilon.aat.glowup.ToolTrimGlow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * render(...) knows the buffers and where the item is shown (hand, ground, inventory...).
 * Inside it, renderModelLists(...) draws the item's quads, already positioned.
 *
 * We wrap the CALL to renderModelLists (inside render) instead of hooking renderModelLists itself,
 * because Sodium replaces renderModelLists' body with its own faster code and returns early,
 * so a hook at the end of that method never runs. The call site in render(...) is untouched by Sodium.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererTrimGlowMixin {

    private static final String RENDER =
            "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V";

    @Inject(method = RENDER, at = @At("HEAD"))
    private void aatglowup$beginItem(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack,
                                     MultiBufferSource buffers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        ToolTrimGlow.begin(stack, context, buffers);
    }

    @Inject(method = RENDER, at = @At("RETURN"))
    private void aatglowup$endItem(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack,
                                   MultiBufferSource buffers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        ToolTrimGlow.end(stack);
    }

    @WrapOperation(method = RENDER, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void aatglowup$glowTrim(ItemRenderer self, BakedModel model, ItemStack stack, int light, int overlay,
                                    PoseStack poseStack, VertexConsumer buffer, Operation<Void> original) {
        original.call(self, model, stack, light, overlay, poseStack, buffer); // draw the item normally (vanilla or Sodium)
        ToolTrimGlow.renderTrim(model, stack, light, overlay, poseStack, buffer); // then our glowing trim on top
    }
}
