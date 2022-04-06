package muramasa.antimatter.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityTickable<T extends TileEntityTickable<T>> extends TileEntityBase<T> {

    private boolean hadFirstTick;

    public TileEntityTickable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void serverTick(Level level, BlockPos pos, BlockState state) {

    }
    protected void clientTick(Level level, BlockPos pos, BlockState state) {

    }

    protected void tick(Level level, BlockPos pos, BlockState state) {
        level.getProfiler().push("AntimatterTileTick");
        if (!hadFirstTick) {
            onFirstTick();
            hadFirstTick = true;
        }
        if (level.isClientSide()) {
            clientTick(level, pos, state);
        } else {
            serverTick(level, pos, state);
        }
        level.getProfiler().pop();
    }

    public static <T extends BlockEntity> void commonTick(Level level, BlockPos pos, BlockState state, T tile) {
        if (tile instanceof TileEntityTickable tick) {
            tick.tick(level, pos, state);
        }
    }

    public boolean hadFirstTick() {
        return hadFirstTick;
    }

    /**
     * Override this to do any initialization that requires the World and/or BlockState reference.
     */
    public void onFirstTick() {
        //NOOP
    }
}
