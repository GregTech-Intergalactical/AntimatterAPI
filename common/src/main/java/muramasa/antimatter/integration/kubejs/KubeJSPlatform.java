package muramasa.antimatter.integration.kubejs;

import muramasa.antimatter.util.ImplLoader;

public interface KubeJSPlatform {
    KubeJSPlatform INSTANCE = ImplLoader.load(KubeJSPlatform.class);
    void onRegister();
}
