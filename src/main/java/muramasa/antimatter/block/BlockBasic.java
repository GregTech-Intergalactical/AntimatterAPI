package muramasa.antimatter.block;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import tesseract.electric.ElectricHandler;
import tesseract.electric.api.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nullable;

public class BlockBasic extends Block implements IAntimatterObject, ITextureProvider, IModelProvider, IElectricCable {

    public BlockBasic(Block.Properties properties) {
        super(properties);
    }

    private ElectricHandler electricHandler;

    public BlockBasic() {
        this(Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 1.0f).sound(SoundType.STONE));
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[0];
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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        electricHandler = new ElectricHandler(world.getDimension().getType().getId(), pos.toLong(), this);
    }

}
