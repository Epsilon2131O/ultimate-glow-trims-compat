package epsilon.aat.glowup.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import epsilon.aat.glowup.BackSlotElytra;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Your own client decides when you pressed jump mid-air to glide; let it see the back-slot elytra cape too. */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerBackElytraMixin {

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aatglowup$backSlotElytra(LocalPlayer self, EquipmentSlot slot, Operation<ItemStack> original) {
        ItemStack stack = original.call(self, slot);
        return slot == EquipmentSlot.CHEST ? BackSlotElytra.chestOrBackCape(self, stack) : stack;
    }
}
