package muramasa.antimatter.client.model.loader.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.model.MachineModel;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MachineModelLoader extends AntimatterModelLoader<MachineModel> {

    public MachineModelLoader(ResourceLocation loc) {
        super(loc);
    }

    @Override
    public MachineModel read(JsonDeserializationContext context, JsonObject json) {
        ResourceLocation particle = json.has("particle") ? new ResourceLocation(json.get("particle").getAsString()) : MissingTextureAtlasSprite.getLocation();
        Map<MachineState, UnbakedModel[]> m = new HashMap<>();
        AntimatterAPI.all(MachineState.class, t -> {
            if (json.has(t.getDisplayName())) {
                JsonArray arr = json.get(t.getDisplayName()).getAsJsonArray();
                UnbakedModel[] a = new UnbakedModel[6];
                for (int i = 0; i < 6; i++) {
                    a[i] = context.deserialize(arr.get(i), BlockModel.class);
                }
                m.put(t, a);
            }
        });
        return new MachineModel(m, particle);
    }
    
}
