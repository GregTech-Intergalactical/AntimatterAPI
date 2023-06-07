/*
package muramasa.antimatter.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.mixin.client.BlockModelAccessor;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class VanillaProxy implements ISimpleModelGeometry<VanillaProxy>
{
    private final List<BlockElement> elements;

    public VanillaProxy(List<BlockElement> list)
    {
        this.elements = list;
    }

    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
    {
        for(BlockElement blockpart : elements) {
            for(Direction direction : blockpart.faces.keySet()) {
                BlockElementFace blockpartface = blockpart.faces.get(direction);
                TextureAtlasSprite textureatlassprite1 = spriteGetter.apply(owner.resolveTexture(blockpartface.texture));
                if (blockpartface.cullForDirection == null) {
                    modelBuilder.addGeneralQuad(BlockModelAccessor.invokeBakeFace(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
                } else {
                    modelBuilder.addFaceQuad(
                            Direction.rotate(modelTransform.getRotation().getMatrix(), blockpartface.cullForDirection),
                            BlockModelAccessor.invokeBakeFace(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
                }
            }
        }
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> textures = Sets.newHashSet();

        for(BlockElement part : elements) {
            for(BlockElementFace face : part.faces.values()) {
                Material texture = owner.resolveTexture(face.texture);
                if (Objects.equals(texture, MissingTextureAtlasSprite.getLocation().toString())) {
                    missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                }

                textures.add(texture);
            }
        }

        return textures;
    }

    public static class Loader implements IModelLoader<VanillaProxy>
    {
        public static final VanillaProxy.Loader INSTANCE = new VanillaProxy.Loader();

        private Loader()
        {
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager)
        {

        }

        @Override
        public VanillaProxy read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            List<BlockElement> list = this.getModelElements(deserializationContext, modelContents);
            return new VanillaProxy(list);
        }

        private List<BlockElement> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
            List<BlockElement> list = Lists.newArrayList();
            if (object.has("elements")) {
                for(JsonElement jsonelement : GsonHelper.getAsJsonArray(object, "elements")) {
                    list.add(deserializationContext.deserialize(jsonelement, BlockElement.class));
                }
            }

            return list;
        }
    }
}
*/
