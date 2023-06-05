package muramasa.antimatter.client.model.loader.forge;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.PipeBakedModel;
import muramasa.antimatter.client.model.AntimatterGroupedModel;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.client.model.ProxyModel;
import muramasa.antimatter.client.model.VanillaProxy;
import muramasa.antimatter.dynamic.DynamicModel;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AntimatterModelLoader<T extends IAntimatterModel> implements IModelLoader, IAntimatterObject {

    private final ResourceLocation loc;

    public AntimatterModelLoader(ResourceLocation loc) {
        this.loc = loc;
        AntimatterAPI.register(AntimatterModelLoader.class, this);
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    @Override
    public String getId() {
        return getLoc().getPath();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }

    @Override
    public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        UnbakedModel model = readModel(deserializationContext, modelContents);
        return new IModelGeometry() {
            @Override
            public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
                return model.bake(bakery, spriteGetter, modelTransform, loc);
            }

            @Override
            public Collection<Material> getTextures(IModelConfiguration owner, Function modelGetter, Set missingTextureErrors) {
                return model.getMaterials(modelGetter, missingTextureErrors);
            }
        };
    }

    public abstract T readModel(JsonDeserializationContext context, JsonObject json);

    public int[] buildRotations(JsonObject e) {
        int[] rotations = new int[3];
        if (e.has("rotation") && e.get("rotation").isJsonArray()) {
            JsonArray array = e.get("rotation").getAsJsonArray();
            for (int i = 0; i < Math.min(rotations.length, array.size()); i++) {
                if (array.get(i).isJsonPrimitive() && array.get(i).getAsJsonPrimitive().isNumber()) {
                    rotations[i] = array.get(i).getAsJsonPrimitive().getAsInt();
                }
            }
        }
        return rotations;
    }

    public static class BlockBenchLoader extends AntimatterModelLoader<AntimatterGroupedModel> {

        public BlockBenchLoader(ResourceLocation loc) {
            super(loc);
        }

        @Override
        public AntimatterGroupedModel readModel(JsonDeserializationContext context, JsonObject json) {
            try {
                ResourceLocation particle = json.has("particle") ? new ResourceLocation(json.get("particle").getAsString()) : MissingTextureAtlasSprite.getLocation();
                Map<Integer, String> offsets = new Object2ObjectOpenHashMap<>();
                if (json.has("groups")) {
                    int index = 0;
                    for (JsonElement jsonelement : GsonHelper.getAsJsonArray(json, "groups")) {
                        if (jsonelement.isJsonObject()) {
                            JsonObject obj = jsonelement.getAsJsonObject();
                            String name = obj.get("name").getAsString();
                            JsonArray children = obj.get("children").getAsJsonArray();
                            for (JsonElement child : children) {
                                offsets.put(child.getAsInt(), name);
                            }
                        }
                    }
                }
                Map<String, List<BlockElement>> map = new Object2ObjectOpenHashMap<>();
                if (json.has("elements")) {
                    int index = 0;
                    for (JsonElement jsonelement : GsonHelper.getAsJsonArray(json, "elements")) {
                        String name = offsets.get(index++);
                        map.computeIfAbsent(name == null ? "" : name, a -> new ObjectArrayList<>()).add(context.deserialize(jsonelement, BlockElement.class));
                    }
                }
                return new AntimatterGroupedModel(particle, /*map.entrySet().stream().collect(Collectors.toMap(t -> t.getKey(), k -> new VanillaProxy(k.getValue())))*/ImmutableMap.of());
            } catch (Exception e) {
                throw new RuntimeException("Caught error deserializing model : " + e);
            }
        }
    }

    public static class DynamicModelLoader extends AntimatterModelLoader<DynamicModel> {

        public DynamicModelLoader(ResourceLocation loc) {
            super(loc);
        }

        @Override
        public DynamicModel readModel(JsonDeserializationContext context, JsonObject json) {
            try {
                Int2ObjectOpenHashMap<UnbakedModel[]> configs = new Int2ObjectOpenHashMap<>();
                for (JsonElement e : json.getAsJsonArray("config")) {
                    if (!e.isJsonObject() || !e.getAsJsonObject().has("id") || !e.getAsJsonObject().has("models"))
                        continue;
                    int id = e.getAsJsonObject().get("id").getAsInt();
                    configs.put(id, buildModels(context, e.getAsJsonObject().get("models").getAsJsonArray()));
                }
                String staticMapId = "";
                if (json.has("staticMap") && json.get("staticMap").isJsonPrimitive())
                    staticMapId = json.get("staticMap").getAsString();
                ResourceLocation particle = json.has("particle") ? new ResourceLocation(json.get("particle").getAsString()) : MissingTextureAtlasSprite.getLocation();
                return new DynamicModel(particle, configs, staticMapId);
            } catch (Exception e) {
                throw new RuntimeException("Caught error deserializing model : " + e);
            }
        }

        public UnbakedModel[] buildModels(JsonDeserializationContext context, JsonArray array) {
            UnbakedModel[] models = new UnbakedModel[array.size()];
            for (int i = 0; i < array.size(); i++) {
                if (!array.get(i).isJsonObject()) continue;
                models[i] = new AntimatterModel(context.deserialize(array.get(i).getAsJsonObject(), BlockModel.class), buildRotations(array.get(i).getAsJsonObject()));
            }
            return models;
        }
    }

    public static class ProxyModelLoader extends AntimatterModelLoader<ProxyModel> {

        public ProxyModelLoader(ResourceLocation location) {
            super(location);
        }

        @Override
        public ProxyModel readModel(JsonDeserializationContext context, JsonObject json) {
            return new ProxyModel();
        }
    }

    public static class PipeModelLoader extends DynamicModelLoader{
        public PipeModelLoader(ResourceLocation location) {
            super(location);
        }

        @Override

        public DynamicModel readModel(JsonDeserializationContext context, JsonObject json) {
            return new DynamicModel(super.readModel(context, json)) {
                @Override
                public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc) {
                    return new PipeBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, particle)), getBakedConfigs(bakery, getter, transform, loc));
                }
            };
        }
    }
}
