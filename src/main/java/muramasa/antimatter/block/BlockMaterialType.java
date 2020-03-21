package muramasa.antimatter.block;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockMaterialType extends BlockBasic implements IColorHandler {

    protected Material material;
    protected MaterialType<?> type;
    protected String textureFolder = "";

    public BlockMaterialType(String domain, Material material, MaterialType<?> type, Block.Properties properties) {
        super(domain, type.getId() + "_" + material.getId(), properties, material.getSet().getTextures(type));
        this.material = material;
        this.type = type;
    }

    public BlockMaterialType instancedTextures(String folder) {
        this.textureFolder = folder;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public MaterialType<?> getType() {
        return type;
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    public Texture[] getTextures() {
        return !textureFolder.isEmpty() ? new Texture[]{new Texture(domain, "block/" + textureFolder + "/" + material.getId())} : super.getTextures();
    }
}
