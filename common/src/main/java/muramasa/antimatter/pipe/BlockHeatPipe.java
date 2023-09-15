package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.HeatPipe;
import muramasa.antimatter.tile.pipe.TileEntityHeatPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockHeatPipe<T extends HeatPipe<T>> extends BlockPipe<T> {

    public BlockHeatPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
    }

    @Override
    //@ParametersAreNotNullByDefault
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (!(entityIn instanceof LivingEntity)) return;
        TileEntityHeatPipe<?> pipe = (TileEntityHeatPipe) worldIn.getBlockEntity(pos);
        int temp = pipe.getTemperature();
        if (temp > 50) {
            entityIn.hurt(DamageSource.GENERIC, Mth.clamp((temp-10)/2, 2, 20));
        }
    }
}
