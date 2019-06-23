package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.texture.Texture;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CoverPlate extends CoverMaterial {

    private MaterialType type;
    private Material material;

    public CoverPlate() {
        //NOOP
    }

    public CoverPlate(MaterialType type, Material material) {
        this.type = type;
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
    public MaterialType getType() {
        return type;
    }

    @Override
    public ItemStack getDroppedStack() {
        return material.getPlate(1);
    }

    @Override
    public Cover onPlace(ItemStack stack) {
        Material material = MaterialItem.getMaterial(stack);
        if (material != null) return new CoverPlate(MaterialType.BLOCK, material);
        return super.onPlace(stack);
    }

    @Override
    public Texture[] getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (TextureSet set : TextureSet.getAll()) {
            textures.add(set.getBlockTexture(MaterialType.BLOCK));
        }
        return textures.toArray(new Texture[0]);
    }
}
