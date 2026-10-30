package epsilon.aat.glowup;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * Built-in resource packs (src/main/resources/resourcepacks/...) that switch on only when the mods they support are installed.
 *
 * colorsplash_compat: horse/wolf armor trim textures for every Trim's - Color Splash color (for Animal Armor Trims),
 *   plus a corrected trimmable_tools.json so Color Splash colors work on tools. Colors used under CC-BY-4.0.
 * netherite_compat: gold-free netherite horse armor (Netherite Horse Armor, MIT) and wolf armor (More Wolf Armors, LGPL-2.1),
 *   matching vanilla netherite armor, whose gold lines would otherwise cross the trim lines.
 */
@EventBusSubscriber(modid = "aatglowup", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CompatPacks {
    private CompatPacks() {}

    @SubscribeEvent
    public static void addPacks(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
        ModList mods = ModList.get();

        if (mods.isLoaded("colorsplash")) {
            add(event, "colorsplash_compat", "Glow Trims: Color Splash Colors");
        }
        if (mods.isLoaded("netherite_horse_armor") || mods.isLoaded("more_wolf_armors")) {
            add(event, "netherite_compat", "Glow Trims: Trim-Friendly Netherite");
        }
    }

    private static void add(AddPackFindersEvent event, String folder, String displayName) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath("aatglowup", "resourcepacks/" + folder),
                PackType.CLIENT_RESOURCES,
                Component.literal(displayName),
                PackSource.BUILT_IN,
                true,               // always on, players can't accidentally disable it
                Pack.Position.TOP); // above other mods' own resources, so ours win
    }
}
