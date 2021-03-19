package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;


public class CoverPlate extends CoverMaterial {

    private final MaterialType<?> type;
    private final Material material;
    private final String domain;

    @Override
    public String getDomain() {
        return domain;
    }

    public CoverPlate(String domain, MaterialType<?> type, Material material) {
        this.type = type;
        this.material = material;
        this.domain = domain;
        register();
    }

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getBasicModel();
    }

    @Override
    public String getId() {
        return "plate_"+material.getId();
    }

    public MaterialType<?> getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public ItemStack getDroppedStack() {
        return Data.PLATE.get(material, 1);
    }

    /*@Override
    public Cover onPlace(ItemStack stack) {
        Material material = MaterialItem.getMaterial(stack);
        if (material != null) return new CoverPlate(MaterialType.BLOCK, material);
        return super.onPlace(stack);
    }*/

    @Override
    public void setTextures(BiConsumer<String, Texture> texer) {
        Texture[] tex = material.getSet().getTextures(Data.BLOCK);
        texer.accept("overlay",tex[0]);
    }

    @Override
    public List<BakedQuad> transformQuads(List<BakedQuad> quads) {
        quads.forEach(t -> RenderHelper.colorQuad(t, material.getRGB()));
        return quads;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{material.getSet().getTextures(Data.BLOCK)[0]};
    }
}
