package epsilon.aat.glowup;

import com.finndog.api.GlowingBannerAccess;
import com.finndog.util.BannerGlowUtil;
import com.finndog.util.GlowRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** The only class touching Glow Up's banner internals (loaded only when Glow Up is installed). */
final class GlowUpBannerCompat {
    private GlowUpBannerCompat() {}

    static void begin(ItemStack banner) {
        if (banner.isEmpty()) return;
        int mask = glowMask(banner);
        if (mask == 0) return; // nothing glowing: leave Glow Up's state untouched
        // Glow Up asks "the banner currently being drawn" for its glow mask, layer by layer
        GlowingBannerAccess access = () -> mask;
        GlowRenderState.set(access);
    }

    static void end() {
        GlowRenderState.clear();
    }

    /**
     * Glow Up's own reader only accepts real banner items (BannerItem), so for anything else that carries
     * banner patterns (like Banner Capes' capes) we read the same stored value ourselves.
     */
    private static int glowMask(ItemStack stack) {
        int mask = BannerGlowUtil.readGlowMask(stack);
        if (mask != 0) return mask;
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data == null ? 0 : data.copyTag().getInt("GlowUpGlowMask");
    }
}
