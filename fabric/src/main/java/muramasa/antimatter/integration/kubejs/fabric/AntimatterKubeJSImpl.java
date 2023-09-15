package muramasa.antimatter.integration.kubejs.fabric;

import muramasa.antimatter.fabric.AntimatterImpl;

public class AntimatterKubeJSImpl {
    public static void onRegister(){
        new AntimatterImpl().initialize(true);
    }
}
