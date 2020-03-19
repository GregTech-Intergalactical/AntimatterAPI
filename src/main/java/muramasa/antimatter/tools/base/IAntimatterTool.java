package muramasa.antimatter.tools.base;

import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IAntimatterTool extends IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider {

    String getDomain();

    AntimatterToolType getType();

    IItemTier getTier();

    Material getPrimaryMaterial();

    @Nullable Material getSecondaryMaterial();

    Item asItem();

    @Override default String getId() {
        return getPrimaryMaterial().getId() + "_" + (getSecondaryMaterial() == null ? "" : getSecondaryMaterial().getId() + "_") + getType().getId();
    }

    @Override default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? getPrimaryMaterial().getRGB() : getSecondaryMaterial() != null ? getSecondaryMaterial().getRGB() : -1;
    }

    @Override default Texture[] getTextures() {
        List<Texture> textures = new ArrayList<>();
        int layers = getType().getOverlayLayers();
        textures.add(new Texture(getDomain(), "item/tool/".concat(getType().getId())));
        if (layers == 1) textures.add(new Texture(getDomain(), "item/tool/overlay/".concat(getType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/tool/overlay/" + getType().getId() + "_" + i)));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }
}
