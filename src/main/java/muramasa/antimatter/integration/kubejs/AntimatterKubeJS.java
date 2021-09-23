package muramasa.antimatter.integration.kubejs;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import muramasa.antimatter.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class AntimatterKubeJS {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(AntimatterKubeJS::onBindings);
        MinecraftForge.EVENT_BUS.addListener(AntimatterKubeJS::registerRecipeHandlers);
    }
    public static void onBindings(BindingsEvent event) {
        event.add("antimatter", new KubeJSBindings());
    }

    public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
    {
        event.register(new ResourceLocation(Ref.ID, "machine"), KubeJSRecipe::new);
    }

    public static void loadStartupScripts(){
        new AMCreationEvent().post(ScriptType.STARTUP, "antimatter.creation");
    }
    public static void loadWorldgenScripts(){
        new AMWorldEvent().post(ScriptType.STARTUP, "antimatter.world");
    }
}
