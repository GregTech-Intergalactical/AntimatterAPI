package muramasa.antimatter.integration.kubejs;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.resources.ResourceLocation;

public class AntimatterKubeJS extends KubeJSPlugin {

    @Override
    public void initStartup() {
        super.initStartup();
        if (AntimatterPlatformUtils.INSTANCE.isFabric()) {
            KubeJSPlatform.INSTANCE.onRegister();
        }
    }


    @Override
    public void addBindings(BindingsEvent event) {
        event.add("antimatter", new KubeJSBindings());
    }

    @Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register(new ResourceLocation(Ref.ID, "machine"), KubeJSRecipe::new);
    }

    public static void loadStartup(){
        AMCreationEvent.init();
        new AMCreationEvent().post(ScriptType.STARTUP, "antimatter.creation");
    }

    public static void loadMaterialEvent(MaterialEvent event){
        new AMMaterialEvent(event).post(ScriptType.STARTUP, "antimatter.material_event");
    }
}
