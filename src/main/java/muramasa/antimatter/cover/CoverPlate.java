package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

public class CoverPlate extends CoverMaterial {

    public CoverPlate(ICoverHandler<?> source, Tier tier, Direction side, CoverFactory factory, MaterialType<?> type,
                      Material material) {
        super(source, tier, side, factory);
        this.type = type;
        this.material = material;
        // TODO Auto-generated constructor stub
    }

    private final MaterialType<?> type;
    private final Material material;

    /*
     * public CoverPlate(String domain, MaterialType<?> type, Material material) {
     * this.type = type; this.material = material; this.domain = domain; register();
     * }
     */


    @Override
    public boolean ticks() {
        return false;
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
        if (type.equals("pipe"))
            return new ResourceLocation(Ref.ID + ":block/cover/cover_pipe_notint");
        return new ResourceLocation(Ref.ID + ":block/cover/basic_notint");
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

    /*
     * @Override public Cover onPlace(ItemStack stack) { Material material =
     * MaterialItem.getMaterial(stack); if (material != null) return new
     * CoverPlate(MaterialType.BLOCK, material); return super.onPlace(stack); }
     */

    @Override
    public void setTextures(BiConsumer<String, Texture> texer) {
        Texture[] tex = material.getSet().getTextures(Data.BLOCK);
        texer.accept("overlay", tex[0]);
    }

    @Override
    public List<BakedQuad> transformQuads(BlockState state, List<BakedQuad> quads) {
        quads.forEach(t -> RenderHelper.colorQuad(t, material.getRGB()));
        return quads;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{material.getSet().getTextures(Data.BLOCK)[0]};
    }
}
