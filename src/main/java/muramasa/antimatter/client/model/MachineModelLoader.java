package muramasa.antimatter.client.model;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.ResourceLocation;

public class MachineModelLoader extends AntimatterModelLoader<MachineModel> {

    public MachineModelLoader(ResourceLocation loc) {
        super(loc);
    }

    @Override
    public MachineModel read(JsonDeserializationContext context, JsonObject json) {
        ResourceLocation particle = json.has("particle") ? new ResourceLocation(json.get("particle").getAsString()) : MissingTextureSprite.getLocation();
        Map<MachineState, IUnbakedModel[]> m = new HashMap<>();
        AntimatterAPI.all(MachineState.class, t -> {
            if (json.has(t.getDisplayName())) {
                JsonArray arr = json.get(t.getDisplayName()).getAsJsonArray();
                IUnbakedModel[] a = new IUnbakedModel[6];
                for (int i = 0; i < 6; i++) {
                    a[i] = context.deserialize(arr.get(i), BlockModel.class);
                }
                m.put(t, a);
            }
        });
        return new MachineModel(m, particle);
    }
    
}
