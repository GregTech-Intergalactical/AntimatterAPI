package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.ProxyBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
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
    public BakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        return new ProxyBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> getter, Set<Pair<String, String>> errors) {
        return Collections.emptyList();
    }
}
