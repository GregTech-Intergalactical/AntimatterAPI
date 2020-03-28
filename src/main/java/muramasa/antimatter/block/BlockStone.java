package muramasa.antimatter.block;

import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModLoadingContext;
import tesseract.electric.Electric;
import tesseract.electric.api.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nullable;

public class BlockStone extends BlockBasic implements IElectricCable {

    protected StoneType type;
    protected Electric electric;

    public BlockStone(StoneType type) {
        super(type.getDomain(), type.getId(), Block.Properties.create(Material.ROCK).sound(type.getSoundType()));
        this.type = type;
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{type.getTexture()};
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        electric = Electric.ofCable(world.getDimension().getType().getId(), pos.toLong(), this);
    }

    @Override
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosionIn) {
        if (electric != null) electric.remove();
    }

    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
        if (electric != null) electric.remove();
    }

    @Override
    public long getVoltage() {
        return 32;
    }

    @Override
    public int getLoss() {
        return 1;
    }

    @Override
    public int getAmps() {
        return 4;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }
}
