package muramasa.antimatter.block;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.texture.Texture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockMaterialType extends BlockBasic implements IColorHandler {

    protected Material material;
    protected MaterialType<?> type;
    protected String textureFolder = "";

    public BlockMaterialType(String domain, Material material, MaterialType<?> type, Properties properties) {
        super(domain, type.getId() + "_" + material.getId(), properties);
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
    public int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    public Texture[] getTextures() {
        return !textureFolder.isEmpty() ? new Texture[]{new Texture(domain, "block/" + textureFolder + "/" + material.getId())} : material.getSet().getTextures(type);
    }
}
