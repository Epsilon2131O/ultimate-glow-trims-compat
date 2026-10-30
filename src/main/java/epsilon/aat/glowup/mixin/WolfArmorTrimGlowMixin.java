package epsilon.aat.glowup.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import epsilon.aat.glowup.GlowTrimHelper;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** After the wolf armor layer draws, redraw a Glow Up-flagged trim at full brightness on top. */
@Mixin(WolfArmorLayer.class)
public class WolfArmorTrimGlowMixin {

    @Shadow @Final private WolfModel<Wolf> model;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Wolf;FFFFFF)V",
            at = @At("TAIL"))
    private void aatglowup$glowTrim(PoseStack poseStack, MultiBufferSource buffers, int packedLight, Wolf wolf,
                                    float limbSwing, float limbSwingAmount, float partialTicks,
                                    float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!wolf.hasArmor()) return;
        ItemStack armor = wolf.getBodyArmorItem();
        if (!(armor.getItem() instanceof AnimalArmorItem animalArmor)
                || animalArmor.getBodyType() != AnimalArmorItem.BodyType.CANINE) return;

        ArmorTrim trim = armor.get(DataComponents.TRIM);
        if (trim == null || !GlowTrimHelper.isGlowing(armor)) return;

        ResourceLocation texture = GlowTrimHelper.trimTexture("wolf_body", trim);
        GlowTrimHelper.renderGlowingTrim(model, poseStack, buffers, packedLight, armor, texture);
    }
}
