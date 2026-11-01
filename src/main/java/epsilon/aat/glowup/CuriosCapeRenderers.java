package epsilon.aat.glowup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.lang.reflect.Constructor;

/**
 * Banner Capes registers its Curios renderer for normal capes only. Its renderer already draws any cape type,
 * so we register the same renderer for the elytra capes too. We create Banner Capes' renderer by name
 * (reflection), so we don't need Banner Capes' code to build this mod.
 */
@EventBusSubscriber(modid = "aatglowup", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CuriosCapeRenderers {
    private CuriosCapeRenderers() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!ModList.get().isLoaded("curios") || !ModList.get().isLoaded("bannercapes")) return;
        event.enqueueWork(CuriosCapeRenderers::register);
    }

    private static void register() {
        try {
            Constructor<?> rendererCtor = Class.forName("liedge.bannercapes.client.BannerCapeCurioRenderer").getConstructor();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item.getClass().getName().equals("liedge.bannercapes.BannerElytraCapeItem")) {
                    CuriosRendererRegistry.register(item, () -> {
                        try {
                            return (ICurioRenderer) rendererCtor.newInstance();
                        } catch (ReflectiveOperationException e) {
                            throw new IllegalStateException("Couldn't create Banner Capes' curio renderer", e);
                        }
                    });
                }
            }
        } catch (ReflectiveOperationException e) {
            // Banner Capes changed its internals: elytra capes still work in the back slot, they just won't be drawn there.
            com.mojang.logging.LogUtils.getLogger().warn("[aatglowup] Couldn't register elytra cape curio renderers", e);
        }
    }
}
