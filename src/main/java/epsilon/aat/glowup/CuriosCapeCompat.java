package epsilon.aat.glowup;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

/** The only (common-side) class touching Curios. Loaded only when Curios and Banner Capes are installed. */
final class CuriosCapeCompat {
    private CuriosCapeCompat() {}

    private static final TagKey<Item> ELYTRA_CAPES =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("bannercapes", "elytra_capes"));

    /** A Banner Capes elytra cape in the "back" curio slot that is currently able to fly (not broken), or EMPTY. */
    static ItemStack findFlyingCape(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.is(ELYTRA_CAPES) && stack.canElytraFly(entity), "back"))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }
}
