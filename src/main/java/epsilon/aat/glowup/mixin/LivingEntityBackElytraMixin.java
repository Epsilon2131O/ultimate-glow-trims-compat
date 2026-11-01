package epsilon.aat.glowup.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import epsilon.aat.glowup.BackSlotElytra;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Keeps a glide going each tick (and wears down the cape) when the elytra cape is in the back slot. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityBackElytraMixin {

    @WrapOperation(method = "updateFallFlying", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aatglowup$backSlotElytra(LivingEntity self, EquipmentSlot slot, Operation<ItemStack> original) {
        ItemStack stack = original.call(self, slot);
        return slot == EquipmentSlot.CHEST ? BackSlotElytra.chestOrBackCape(self, stack) : stack;
    }
}
