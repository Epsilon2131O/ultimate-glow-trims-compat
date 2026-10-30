package epsilon.aat.glowup;

import com.finndog.client.GlowRenderType;
import com.finndog.client.TrimLight;
import com.finndog.util.FeatureGates;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** The only class touching Glow Up's trim rendering (loaded only when Glow Up is installed). */
final class GlowUpCompat {
    private GlowUpCompat() {}

    private static final int FULL_BRIGHT = 15728880;

    /** Horse and wolf armor trims. */
    static void render(Model model, PoseStack poseStack, MultiBufferSource buffers, int packedLight, ItemStack armor, ResourceLocation texture) {
        int light = TrimLight.resolve(armor, packedLight);
        model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.armorCutoutNoCull(texture)), light, OverlayTexture.NO_OVERLAY);
        float strength = TrimLight.glowStrength(armor);
        if (strength > 0f) {
            model.renderToBuffer(poseStack, buffers.getBuffer(GlowRenderType.additive(texture)), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, GlowRenderType.tint(strength));
        }
    }

    /** Tool and weapon trims (Trimmable Tools): same two passes, but with item model quads. */
    static void renderItemTrim(List<BakedQuad> quads, PoseStack poseStack, MultiBufferSource buffers, VertexConsumer itemBuffer,
                               int packedLight, int overlay, ItemStack stack, boolean additive, boolean shaderHand) {
        // How far through the resonant pulse we are (always 1 for a plain glowing trim)
        float maxStrength = FeatureGates.glowStrength();
        float strength = TrimLight.glowStrength(stack);
        float pulse = maxStrength > 0f ? strength / maxStrength : 1f;

        // Pass 1: redraw the trim layer at Glow Up's light level (full bright, or the resonant pulse).
        // In the shader hand pass the shader decides brightness from the emissive tag, so we fade the
        // vertex color with the pulse instead to keep resonant trims breathing.
        int light = TrimLight.resolve(stack, packedLight);
        float shade = shaderHand ? 0.35f + 0.65f * pulse : 1f;
        for (BakedQuad quad : quads) {
            itemBuffer.putBulkData(lifted(poseStack, quad), quad, shade, shade, shade, 1f, light, overlay);
            poseStack.popPose();
        }

        // Pass 2: Glow Up's additive halo, tinted by its glow strength setting
        if (!additive || strength <= 0f) return;
        int tint = GlowRenderType.tint(strength);
        float r = ((tint >> 16) & 255) / 255f, g = ((tint >> 8) & 255) / 255f, b = (tint & 255) / 255f;
        VertexConsumer glow = buffers.getBuffer(GlowRenderType.additive(quads.get(0).getSprite().atlasLocation()));
        for (BakedQuad quad : quads) {
            glow.putBulkData(lifted(poseStack, quad), quad, r, g, b, 1f, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    /**
     * How far each trim face is lifted off the item, along the direction that face points
     * (front faces forward, back faces backward, side faces out their own side).
     * In item units: 1/16 = one pixel. Raise it if any speckling remains; lower it if the trim looks like it floats.
     */
    private static final float LIFT = 0.0001f;

    /** Pushes a copy of the pose, nudged outward along this quad's facing. Caller must popPose() after drawing. */
    private static PoseStack.Pose lifted(PoseStack poseStack, BakedQuad quad) {
        poseStack.pushPose();
        Direction facing = quad.getDirection();
        poseStack.translate(facing.getStepX() * LIFT, facing.getStepY() * LIFT, facing.getStepZ() * LIFT);
        return poseStack.last();
    }
}
