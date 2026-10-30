package epsilon.aat.glowup;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.ModList;

/**
 * Shared helper for the glow mixins.
 * Lives OUTSIDE the mixin package: Mixin forbids normal code from referencing classes in a mixin package.
 */
public final class GlowTrimHelper {
    private GlowTrimHelper() {}

    private static Boolean glowUpLoaded;

    static boolean glowUpLoaded() {
        if (glowUpLoaded == null) glowUpLoaded = ModList.get().isLoaded("glow_up");
        return glowUpLoaded;
    }

    /** Glow Up stores its flags in the item's custom data. */
    public static boolean isGlowing(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;
        CompoundTag tag = data.copyTag();
        return tag.getBoolean("GlowUp_GlowingTrim") || tag.getBoolean("GlowUp_EchoTrim");
    }

    /** Same texture path Animal Armor Trims uses: trims/entity/<body>/<pattern>_<material>.png */
    public static ResourceLocation trimTexture(String body, ArmorTrim trim) {
        String pattern = trim.pattern().value().assetId().getPath();
        String material = trim.material().value().assetName();
        return ResourceLocation.withDefaultNamespace(
                "textures/trims/entity/" + body + "/" + pattern + "_" + material + ".png");
    }

    /**
     * Draws the glowing trim the same way Glow Up draws player armor trims:
     * pass 1 = trim at Glow Up's light level (full-bright, or pulsing for resonant),
     * pass 2 = additive glow on top, at Glow Up's configured strength.
     */
    public static void renderGlowingTrim(Model model, PoseStack poseStack, MultiBufferSource buffers,
                                         int packedLight, ItemStack armor, ResourceLocation texture) {
        if (!glowUpLoaded()) return;
        // Only touch Glow Up's classes when it is installed (keeps it an optional dependency)
        GlowUpCompat.render(model, poseStack, buffers, packedLight, armor, texture);
    }
}
