package muramasa.antimatter.integration.kubejs;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.MaterialEvent;
import net.minecraft.resources.ResourceLocation;

public class AntimatterKubeJS extends KubeJSPlugin {
    private static final EventGroup ANTIMATTER = EventGroup.of("antimatter");

    private static final EventHandler CREATION = ANTIMATTER.startup("creation", () -> AMCreationEvent.class);
    private static final EventHandler MATERIAL_EVENT = ANTIMATTER.startup("material_event", () -> AMMaterialEvent.class);
    public static final EventHandler WORLDGEN = ANTIMATTER.server("worldgen", () -> AMWorldEvent.class);
    public static final EventHandler RECIPE_LOADER = ANTIMATTER.server("recipes", () -> RecipeLoaderEventKubeJS.class);

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
        CREATION.post(ScriptType.STARTUP, new AMCreationEvent());
    }

    public static void loadMaterialEvent(MaterialEvent event){
        MATERIAL_EVENT.post(ScriptType.STARTUP, new AMMaterialEvent(event));
    }
}
