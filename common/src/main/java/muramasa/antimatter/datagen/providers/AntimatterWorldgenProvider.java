package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.Map;

public class AntimatterWorldgenProvider implements IAntimatterProvider {
    protected final String providerDomain, providerName;
    protected final String subDir;
    protected final Map<ResourceLocation, JsonObject> JSOM_MAP = new Object2ObjectOpenHashMap<>();

    public AntimatterWorldgenProvider(String providerDomain, String providerName, String subDir) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.subDir = subDir;
    }

    @Override
    public void run() {

    }

    @Override
    public void onCompletion() {
        JSOM_MAP.forEach((r, j) -> {
            AntimatterDynamics.RUNTIME_DATA_PACK.addData(fix(r, "worldgen/" + subDir), j.toString().getBytes());
        });
    }
    private static ResourceLocation fix(ResourceLocation identifier, String prefix) {
        return new ResourceLocation(identifier.getNamespace(), prefix + '/' + identifier.getPath() + ".json");
    }

    public void addJsonObject(ResourceLocation id, JsonObject object){
        JSOM_MAP.put(id, object);
    }

    @Override
    public void run(CachedOutput cache) throws IOException {

    }

    @Override
    public String getName() {
        return null;
    }
}
