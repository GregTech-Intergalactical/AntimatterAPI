package muramasa.antimatter.tool;

import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IAntimatterTool extends IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider {

    @Nonnull AntimatterToolType getType();

    @Nonnull Material getPrimaryMaterial(@Nonnull ItemStack stack);

    @Nonnull Material getSecondaryMaterial(@Nonnull ItemStack stack);

    @Nonnull Item asItem();

    @Nonnull ItemStack asItemStack(@Nonnull Material primary, @Nonnull Material secondary);

    @Override default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? getPrimaryMaterial(stack).getRGB() : getSecondaryMaterial(stack) != null ? getSecondaryMaterial(stack).getRGB() : -1;
    }

    @Override default Texture[] getTextures() {
        List<Texture> textures = new ArrayList<>();
        int layers = getType().getOverlayLayers();
        textures.add(new Texture(getDomain(), "item/tool/".concat(getType().getId())));
        if (layers == 1) textures.add(new Texture(getDomain(), "item/tool/overlay/".concat(getType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/tool/overlay/", getType().getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }

}
