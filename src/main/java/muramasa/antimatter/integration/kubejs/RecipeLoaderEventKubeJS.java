package muramasa.antimatter.integration.kubejs;

import java.util.Set;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;

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
