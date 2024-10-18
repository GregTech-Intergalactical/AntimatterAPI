package muramasa.antimatter.integration.kubejs.fabric;

import muramasa.antimatter.fabric.AntimatterImpl;
import muramasa.antimatter.integration.kubejs.KubeJSPlatform;

public class KubeJSPlatformImpl implements KubeJSPlatform {
    @Override
    public void onRegister(){
        new AntimatterImpl().initialize(true);
    }
}
