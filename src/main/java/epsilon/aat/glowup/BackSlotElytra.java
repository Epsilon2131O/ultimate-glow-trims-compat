package epsilon.aat.glowup;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * Vanilla only lets you fly with an elytra in the CHEST slot. Our mixins route every
 * "what's in the chest slot?" flight check through here: if the chest item can't fly
 * (say, a chestplate), we hand back a Banner Capes elytra cape worn in the Curios back slot instead.
 */
public final class BackSlotElytra {
    private BackSlotElytra() {}

    private static Boolean enabled;

    private static boolean enabled() {
        if (enabled == null) enabled = ModList.get().isLoaded("curios") && ModList.get().isLoaded("bannercapes");
        return enabled;
    }

    public static ItemStack chestOrBackCape(LivingEntity entity, ItemStack chest) {
        if (chest.canElytraFly(entity) || !enabled()) return chest;
        ItemStack cape = CuriosCapeCompat.findFlyingCape(entity);
        return cape.isEmpty() ? chest : cape;
    }
}
