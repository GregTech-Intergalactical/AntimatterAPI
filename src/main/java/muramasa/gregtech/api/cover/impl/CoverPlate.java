package muramasa.gregtech.api.cover.impl;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.MaterialSet;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.texture.Texture;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CoverPlate extends CoverMaterial {

    private Prefix prefix;
    private Material material;

    public CoverPlate() {
        //NOOP
    }

    public CoverPlate(Prefix prefix, Material material) {
        this.prefix = prefix;
        this.material = material;
    }

    @Override
    public String getName() {
        return "cover_plate";
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Prefix getPrefix() {
        return prefix;
    }

    @Override
    public Cover onPlace(ItemStack stack) {
        Material material = MaterialItem.getMaterial(stack);
        if (material != null) return new CoverPlate(Prefix.Block, material);
        return this;
    }

    @Override
    public Texture[] getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (MaterialSet set : MaterialSet.values()) {
            textures.add(set.getBlockTexture(Prefix.Block));
        }
        return textures.toArray(new Texture[0]);
    }
}
