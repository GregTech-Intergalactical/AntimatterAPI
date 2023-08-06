package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.forge.duck.IFakeTileCap;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumMap;
import java.util.Map;

@Mixin(TileEntityFakeBlock.class)
public class TileEntityFakeBlockMixin extends TileEntityTickable<TileEntityFakeBlock> {
    @Shadow
    private TileEntityBasicMultiMachine<?> controller = null;
    @Shadow
    public Map<Direction, ICover> covers;

    public TileEntityFakeBlockMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (controller != null){
            return ((IFakeTileCap)controller).getCapabilityFromFake(cap, side, side == null ? ICover.empty : covers.get(side));
        }
        return super.getCapability(cap, side);
    }
}
