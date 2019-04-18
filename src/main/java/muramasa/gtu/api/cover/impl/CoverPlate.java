package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.texture.Texture;
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
        return "plate";
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
        return super.onPlace(stack);
    }

    @Override
    public Texture[] getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (TextureSet set : TextureSet.getAll()) {
            textures.add(set.getBlockTexture(Prefix.Block));
        }
        return textures.toArray(new Texture[0]);
    }
}
