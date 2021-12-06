package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class RecipeLoaderEventKubeJS extends EventJS {

    public final Set<ResourceLocation> forMachines = new ObjectOpenHashSet<>();
    public final Set<ResourceLocation> forLoaders = new ObjectOpenHashSet<>();

    public RecipeLoaderEventKubeJS() {

    }

    public void disableMap(ResourceLocation loc) {
        forMachines.add(loc);
    }

    public void disableLoader(ResourceLocation loc) {
        forLoaders.add(loc);
    }

    public static RecipeLoaderEventKubeJS createAndPost(boolean server) {
        RecipeLoaderEventKubeJS ev = new RecipeLoaderEventKubeJS();
        ev.post(server ? ScriptType.SERVER : ScriptType.CLIENT, "antimatter.recipes");
        return ev;
    }
}
