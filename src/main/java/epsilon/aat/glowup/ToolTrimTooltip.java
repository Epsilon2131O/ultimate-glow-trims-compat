package epsilon.aat.glowup;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Glow Up only adds its "Bioluminescent" / "Resonant" tooltip line to armor and elytra.
 * This adds the same line (same text, same color) to anything else with a glowing trim, like tools and weapons.
 */
@EventBusSubscriber(modid = "aatglowup", value = Dist.CLIENT)
public final class ToolTrimTooltip {
    private ToolTrimTooltip() {}

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!GlowTrimHelper.glowUpLoaded()) return;
        if (stack.getItem() instanceof ArmorItem || stack.getItem() instanceof ElytraItem) return; // Glow Up handles these
        if (!stack.has(DataComponents.TRIM)) return;

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;
        CompoundTag tag = data.copyTag();

        String key = null;
        if (tag.getBoolean("GlowUp_EchoTrim")) key = "tooltip.glow_up.resonant";
        else if (tag.getBoolean("GlowUp_GlowingTrim")) key = "tooltip.glow_up.bioluminescent";
        if (key == null) return;

        var lines = event.getToolTip();
        lines.add(Math.min(1, lines.size()), Component.translatable(key).withStyle(ChatFormatting.AQUA)); // right under the name
    }
}
