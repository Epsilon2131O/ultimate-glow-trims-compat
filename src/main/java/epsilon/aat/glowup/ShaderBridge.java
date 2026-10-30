package epsilon.aat.glowup;

import net.neoforged.fml.ModList;

/** Talks to Iris (shaders) only when Iris is installed. Lives outside the mixin packages. */
public final class ShaderBridge {
    private ShaderBridge() {}

    private static Boolean irisLoaded;

    private static boolean irisLoaded() {
        if (irisLoaded == null) irisLoaded = ModList.get().isLoaded("iris");
        return irisLoaded;
    }

    /** True while Iris is drawing the first-person hand with a shaderpack active. */
    public static boolean inShaderHandPass() {
        return irisLoaded() && IrisCompat.inShaderHandPass();
    }

    /** Tags the next vertices as an emissive item for the shaderpack. Returns the previous tag to restore. */
    public static int tagAsEmissive() {
        return IrisCompat.tagAs("glowstone_dust");
    }

    public static void restore(int previous) {
        IrisCompat.restore(previous);
    }
}
