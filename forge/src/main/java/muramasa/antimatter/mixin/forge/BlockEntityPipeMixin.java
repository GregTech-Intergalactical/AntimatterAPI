package muramasa.antimatter.mixin.forge;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.forge.energy.ForgeEnergyContainer;
import earth.terrarium.botarium.forge.fluid.ForgeFluidContainer;
import muramasa.antimatter.blockentity.BlockEntityTickable;
import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import muramasa.antimatter.blockentity.pipe.BlockEntityItemPipe;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.forge.AntimatterCaps;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tesseract.api.forge.wrapper.ExtendedContainerWrapper;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.rf.IRFNode;

import java.util.Optional;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@Mixin(BlockEntityPipe.class)
public abstract class BlockEntityPipeMixin<T extends PipeType<T>> extends BlockEntityTickable<BlockEntityPipe<T>> {
    @Shadow
    protected Holder pipeCapHolder;
    @Shadow
    @Final
    private Optional<PipeCoverHandler<?>> coverHandler;

    @Shadow
    abstract boolean connects(Direction direction);
    @Shadow
    abstract Class<?> getCapClass();

    @Unique
    protected LazyOptional[] pipeCaps = new LazyOptional[7];
    public BlockEntityPipeMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        if (side != null && !connects(side)) return LazyOptional.empty();
        if (!pipeCapHolder.isPresent()) return LazyOptional.empty();
        if (cap == FLUID_HANDLER_CAPABILITY && getCapClass() == FluidContainer.class){
            int index = side == null ? 6 : side.get3DDataValue();
            if (pipeCaps[index] == null || !pipeCaps[index].isPresent()){
                pipeCaps[index] = fromFluidHolder(pipeCapHolder, side).cast();
            }
            return pipeCaps[index].cast();
        }
        if (cap == ITEM_HANDLER_CAPABILITY && getCapClass() == ExtendedItemContainer.class){
            int index = side == null ? 6 : side.get3DDataValue();
            if (pipeCaps[index] == null || !pipeCaps[index].isPresent()){
                pipeCaps[index] = fromItemHolder(pipeCapHolder, side).cast();
            }
            return pipeCaps[index].cast();
        }
        if (side == null) return LazyOptional.empty();
        if (cap == CapabilityEnergy.ENERGY && getCapClass() == IRFNode.class) {
            if (pipeCaps[side.get3DDataValue()] == null || !pipeCaps[side.get3DDataValue()].isPresent()){
                pipeCaps[side.get3DDataValue()] = fromEnergyHolder(pipeCapHolder, side).cast();
            }
            return pipeCaps[side.get3DDataValue()].cast();
        }
        try {
            if (cap == AntimatterCaps.CAP_MAP.get(getCapClass())){
                if (pipeCaps[side.get3DDataValue()] == null || !pipeCaps[side.get3DDataValue()].isPresent()){
                    pipeCaps[side.get3DDataValue()] = fromHolder(pipeCapHolder, side);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return LazyOptional.empty();
        }
        if (pipeCaps[side.get3DDataValue()] != null && cap == AntimatterCaps.CAP_MAP.get(getCapClass())){
            return pipeCaps[side.get3DDataValue()].cast();
        }
        return LazyOptional.empty();
    }

    private  <U> LazyOptional<U> fromHolder(Holder<U, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<U> opt = LazyOptional.of(() -> holder.side(side).get());
        boolean add = holder.addListener(side, opt::invalidate);
        if (!add) return LazyOptional.empty();
        return opt;
    }

    private LazyOptional<IItemHandler> fromItemHolder(Holder<ExtendedItemContainer, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<IItemHandler> opt = LazyOptional.of(() -> new ExtendedContainerWrapper(holder.side(side).get()));
        boolean add = holder.addListener(side, opt::invalidate);
        if (!add) return LazyOptional.empty();
        return opt;
    }

    private LazyOptional<IFluidHandler> fromFluidHolder(Holder<FluidContainer, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<IFluidHandler> opt = LazyOptional.of(() -> new ForgeFluidContainer(holder.side(side).get()));
        boolean add = holder.addListener(side, opt::invalidate);
        if (!add) return LazyOptional.empty();
        return opt;
    }

    private LazyOptional<IEnergyStorage> fromEnergyHolder(Holder<IRFNode, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<IEnergyStorage> opt = LazyOptional.of(() -> new ForgeEnergyContainer<>(holder.side(side).get(), this));
        boolean add = holder.addListener(side, opt::invalidate);
        if (!add) return LazyOptional.empty();
        return opt;
    }
}
