package epsilon.aat.glowup.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import epsilon.aat.glowup.BackSlotElytra;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Lets a glide start (server and client side) when the elytra cape is in the back slot. */
@Mixin(Player.class)
public abstract class PlayerBackElytraMixin {

    @WrapOperation(method = "tryToStartFallFlying", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aatglowup$backSlotElytra(Player self, EquipmentSlot slot, Operation<ItemStack> original) {
        ItemStack stack = original.call(self, slot);
        return slot == EquipmentSlot.CHEST ? BackSlotElytra.chestOrBackCape(self, stack) : stack;
    }
}
