package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.ProxyBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class ProxyModel implements IAntimatterModel<ProxyModel> {

    public ProxyModel() {
        super();
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return new ProxyBakedModel(getter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation())));
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        return Collections.emptyList();
    }
}
