package epsilon.aat.glowup;

import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * Lets Moog's Glow Up know which banner ITEM is being drawn, so its existing banner-layer glow
 * also works on banners worn by mounts (More Banner Features).
 * Lives outside the mixin packages; only touches Glow Up when it is installed.
 */
public final class BannerGlowBridge {
    private BannerGlowBridge() {}

    private static Boolean glowUpLoaded;

    private static boolean glowUpLoaded() {
        if (glowUpLoaded == null) glowUpLoaded = ModList.get().isLoaded("glow_up");
        return glowUpLoaded;
    }

    public static void begin(ItemStack banner) {
        if (glowUpLoaded()) GlowUpBannerCompat.begin(banner);
    }

    public static void end() {
        if (glowUpLoaded()) GlowUpBannerCompat.end();
    }
}
