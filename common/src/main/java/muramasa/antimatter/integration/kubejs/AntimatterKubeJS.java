package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.MaterialEvent;
import net.minecraft.resources.ResourceLocation;

public class AntimatterKubeJS extends KubeJSPlugin {


    @Override
    public void initStartup() {
        super.initStartup();
        new AMCreationEvent().post(ScriptType.STARTUP, "antimatter.creation");}

    @Override
    public void addBindings(BindingsEvent event) {
        event.add("antimatter", new KubeJSBindings());
    }

    @Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register(new ResourceLocation(Ref.ID, "machine"), KubeJSRecipe::new);
    }

    public static void loadMaterialEvent(){
        new AMMaterialEvent(new MaterialEvent()).post(ScriptType.STARTUP, "antimatter.material_event");
    }

    public static void loadWorldgenScripts() {
        //new AMWorldEvent().post(ScriptType.STARTUP, "antimatter.world");
    }
}
