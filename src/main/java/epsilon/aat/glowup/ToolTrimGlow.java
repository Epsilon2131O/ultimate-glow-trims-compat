package epsilon.aat.glowup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes Glow Up's glowing/resonant trims glow on tools and weapons trimmed with Trimmable Tools.
 * Trimmable Tools draws a trim as an extra texture layer whose sprite lives at "trims/items/...",
 * so we find those pieces of the item model and redraw them glowing, on top of the normal item.
 */
public final class ToolTrimGlow {
    private ToolTrimGlow() {}

    /** Flip to true if you also want the additive glow on item icons in inventories and hotbar. */
    private static final boolean GLOW_IN_INVENTORY = false;

    private static final Direction[] SIDES = {null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private static final RandomSource RANDOM = RandomSource.create();

    // What ItemRenderer is drawing right now (render thread only)
    private static ItemStack currentStack = ItemStack.EMPTY;
    private static MultiBufferSource currentBuffers;
    private static ItemDisplayContext currentContext;

    public static void begin(ItemStack stack, ItemDisplayContext context, MultiBufferSource buffers) {
        if (!GlowTrimHelper.glowUpLoaded()) return;
        if (stack.isEmpty() || !stack.has(DataComponents.TRIM) || !GlowTrimHelper.isGlowing(stack)) return;
        currentStack = stack;
        currentBuffers = buffers;
        currentContext = context;
    }

    public static void end(ItemStack stack) {
        if (stack != currentStack) return;
        currentStack = ItemStack.EMPTY;
        currentBuffers = null;
        currentContext = null;
    }

    public static void renderTrim(BakedModel model, ItemStack stack, int light, int overlay, PoseStack poseStack, VertexConsumer itemBuffer) {
        if (stack != currentStack || currentBuffers == null) return;

        List<BakedQuad> trimQuads = new ArrayList<>();
        for (Direction side : SIDES) {
            RANDOM.setSeed(42L); // same seed vanilla uses, so we get the same quads it drew
            for (BakedQuad quad : model.getQuads(null, side, RANDOM, ModelData.EMPTY, null)) {
                if (quad.getSprite().contents().name().getPath().startsWith("trims/items/")) trimQuads.add(quad);
            }
        }
        if (trimQuads.isEmpty()) return;

        // First-person view with a shaderpack: the pack dims held items, so we use its own emissive system instead
        boolean shaderHand = ShaderBridge.inShaderHandPass();
        boolean additive = !shaderHand && (GLOW_IN_INVENTORY || currentContext != ItemDisplayContext.GUI);

        int previous = shaderHand ? ShaderBridge.tagAsEmissive() : 0;
        try {
            GlowUpCompat.renderItemTrim(trimQuads, poseStack, currentBuffers, itemBuffer, light, overlay, stack, additive, shaderHand);
        } finally {
            if (shaderHand) ShaderBridge.restore(previous);
        }
    }
}
