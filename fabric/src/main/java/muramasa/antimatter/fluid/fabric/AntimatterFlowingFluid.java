package muramasa.antimatter.fluid.fabric;

import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class AntimatterFlowingFluid extends FlowingFluid {
    final FluidData data;

    protected AntimatterFlowingFluid(FluidData data) {
        this.data = data;
    }

    public Fluid getFlowing() {
        return this.data.getFlowingFluid().get();
    }

    public Fluid getSource() {
        return this.data.getStillFluid().get();
    }

    protected boolean canConvertToSource() {
        return data.getProperties().canConvertToSource();
    }

    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    protected int getSlopeFindDistance(LevelReader level) {
        return this.data.getProperties().slopeFindDistance();
    }

    protected int getDropOff(LevelReader level) {
        return this.data.getProperties().dropOff();
    }

    public Item getBucket() {
        return this.data.getBucket() != null ? this.data.getBucket().get() : Items.AIR;
    }

    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !this.isSame(fluid);
    }

    public int getTickDelay(LevelReader level) {
        return this.data.getProperties().tickDelay();
    }

    protected float getExplosionResistance() {
        return this.data.getProperties().explosionResistance();
    }

    protected BlockState createLegacyBlock(FluidState state) {
        return this.data.getBlock() != null ? this.data.getBlock().get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state)) : Blocks.AIR.defaultBlockState();
    }

    public boolean isSame(Fluid fluid) {
        return fluid == this.data.getFlowingFluid().get() || fluid == this.data.getStillFluid().get();
    }


    public static class Source extends AntimatterFlowingFluid {
        public Source(FluidData data) {
            super(data);
            data.setStillFluid(() -> this);
        }

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends AntimatterFlowingFluid {
        public Flowing(FluidData data) {
            super(data);
            data.setFlowingFluid(() -> this);
            this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
