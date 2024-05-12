package muramasa.antimatter.integration.kubejs;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.MaterialEvent;
import net.minecraft.resources.ResourceLocation;

public class AntimatterKubeJS extends KubeJSPlugin {

    @Override
    public void initStartup() {
        super.initStartup();
        onRegister();
    }

    @ExpectPlatform
    private static void onRegister(){
        throw new AssertionError();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("antimatter", new KubeJSBindings());
    }

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(new ResourceLocation(Ref.ID, "machine"), MachineRecipeSchema.SCHEMA);
    }

    public static void loadStartup(){
        AMCreationEvent.init();
        new AMCreationEvent().post(ScriptType.STARTUP, "antimatter.creation");
    }

    public static void loadMaterialEvent(MaterialEvent event){
        new AMMaterialEvent(event).post(ScriptType.STARTUP, "antimatter.material_event");
    }
}
