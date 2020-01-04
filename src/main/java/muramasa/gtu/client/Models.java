package muramasa.gtu.client;

import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.model.ModelDynamic;
import muramasa.gtu.client.render.models.ModelNichrome;
import muramasa.gtu.data.Textures;

import static muramasa.gtu.common.Data.*;

public class Models {

    public static void init() {

    }

    static {
        AntimatterModelLoader.register(COIL_NICHROME, new ModelNichrome());
        AntimatterModelLoader.register(CASING_FUSION_3, new ModelDynamic(CASING_FUSION_3).setConfig((b) -> b.basic(b, Textures.FUSION_3_CT)));
    }
}
