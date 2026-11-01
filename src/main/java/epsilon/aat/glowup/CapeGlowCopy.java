package epsilon.aat.glowup;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Banner Capes' smithing recipes copy only the banner patterns onto the new cape.
 * Moog's Glow Up keeps which layers glow in the banner's block entity data ("GlowUpGlowMask"),
 * so we copy that along too. Layer order is unchanged, so every glowing layer stays on the right pattern.
 * Runs wherever crafting happens (the server, or singleplayer). Needs no Glow Up classes.
 */
public final class CapeGlowCopy {
    private CapeGlowCopy() {}

    public static void copy(ItemStack from, ItemStack to) {
        if (from == null || to == null || from.isEmpty() || to.isEmpty()) return;
        CustomData data = from.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null || !data.copyTag().contains("GlowUpGlowMask")) return;
        to.set(DataComponents.BLOCK_ENTITY_DATA, data);
    }
}
