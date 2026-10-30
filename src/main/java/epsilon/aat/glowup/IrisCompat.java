package epsilon.aat.glowup;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;

/**
 * The only class touching Iris (loaded only when Iris is installed).
 * Iris writes a "which item is this" ID into every vertex it receives, and shaderpacks read it to pick materials.
 * Complementary treats glowstone dust as strongly emissive, so tagging our trim quads with that ID makes them glow.
 */
final class IrisCompat {
    private IrisCompat() {}

    static boolean inShaderHandPass() {
        return Iris.isPackInUseQuick() && HandRenderer.INSTANCE.isActive();
    }

    static int tagAs(String vanillaItem) {
        int previous = CapturedRenderingState.INSTANCE.getCurrentRenderedItem();
        Object2IntFunction<NamespacedId> ids = WorldRenderingSettings.INSTANCE.getItemIds();
        if (ids != null) {
            int id = ids.applyAsInt(new NamespacedId("minecraft", vanillaItem));
            if (id != ids.defaultReturnValue()) CapturedRenderingState.INSTANCE.setCurrentRenderedItem(id);
        }
        return previous;
    }

    static void restore(int previous) {
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(previous);
    }
}
