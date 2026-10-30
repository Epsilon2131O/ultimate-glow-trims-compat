package epsilon.aat.glowup.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import epsilon.aat.glowup.GlowTrimHelper;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** After the horse armor layer draws, redraw a Glow Up-flagged trim at full brightness on top. */
@Mixin(HorseArmorLayer.class)
public class HorseArmorTrimGlowMixin {

    // The layer's private horse model, already posed by vanilla during render()
    @Shadow @Final private HorseModel<Horse> model;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V",
            at = @At("TAIL"))
    private void aatglowup$glowTrim(PoseStack poseStack, MultiBufferSource buffers, int packedLight, Horse horse,
                                    float limbSwing, float limbSwingAmount, float partialTicks,
                                    float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack armor = horse.getBodyArmorItem();
        if (!(armor.getItem() instanceof AnimalArmorItem animalArmor)
                || animalArmor.getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) return;

        ArmorTrim trim = armor.get(DataComponents.TRIM);
        if (trim == null || !GlowTrimHelper.isGlowing(armor)) return;

        ResourceLocation texture = GlowTrimHelper.trimTexture("horse_body", trim);
        GlowTrimHelper.renderGlowingTrim(model, poseStack, buffers, packedLight, armor, texture);
    }
}
