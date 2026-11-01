package epsilon.aat.glowup.compatmixin;

import epsilon.aat.glowup.CapeGlowCopy;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Keeps Glow Up's glowing layers when crafting banner → cape: the banner sits in the template slot. */
@Pseudo
@Mixin(targets = "liedge.bannercapes.BannerToCapeRecipe")
public class BannerToCapeGlowMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"))
    private void aatglowup$keepGlow(SmithingRecipeInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        CapeGlowCopy.copy(input.template(), cir.getReturnValue());
    }
}
