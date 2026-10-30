package epsilon.aat.glowup;

import com.finndog.api.GlowingBannerAccess;
import com.finndog.util.BannerGlowUtil;
import com.finndog.util.GlowRenderState;
import net.minecraft.world.item.ItemStack;

/** The only class touching Glow Up's banner internals (loaded only when Glow Up is installed). */
final class GlowUpBannerCompat {
    private GlowUpBannerCompat() {}

    static void begin(ItemStack banner) {
        if (banner.isEmpty()) return;
        int mask = BannerGlowUtil.readGlowMask(banner);
        if (mask == 0) return; // nothing glowing: leave Glow Up's state untouched
        // Glow Up asks "the banner currently being drawn" for its glow mask, layer by layer
        GlowingBannerAccess access = () -> mask;
        GlowRenderState.set(access);
    }

    static void end() {
        GlowRenderState.clear();
    }
}
