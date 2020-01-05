package muramasa.gtu.client;

import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.model.ModelDynamic;
import muramasa.antimatter.client.model.ModelTextured;
import muramasa.gtu.client.render.models.ModelNichrome;
import muramasa.gtu.data.Textures;

import static muramasa.gtu.common.Data.CASING_FUSION_3;
import static muramasa.gtu.common.Data.COIL_NICHROME;

public class Models {

    public static void init() {
        AntimatterModelLoader.register(COIL_NICHROME, new ModelNichrome());
        AntimatterModelLoader.register(CASING_FUSION_3, new ModelDynamic(CASING_FUSION_3)
            .setConfig((b) -> b.basic(b, Textures.FUSION_3_CT))
            .add(32, new ModelTextured("block/preset/simple").of(m -> m.tex("all", "minecraft:block/diamond_block")))
        );
    }
}
