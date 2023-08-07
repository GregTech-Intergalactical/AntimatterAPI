package muramasa.antimatter.mixin.forge;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.forge.energy.ForgeEnergyContainer;
import earth.terrarium.botarium.forge.fluid.ForgeFluidContainer;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.forge.AntimatterCaps;
import muramasa.antimatter.capability.machine.*;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.forge.duck.IFakeTileCap;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tesseract.api.forge.TesseractCaps;
import tesseract.api.forge.wrapper.ExtendedContainerWrapper;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.rf.IRFNode;

@Mixin(TileEntityMachine.class)
public abstract class TileEntityMachineMixin<T extends TileEntityMachine<T>> extends TileEntityTickable<T> implements IFakeTileCap {

    @Shadow
    public Holder<ExtendedItemContainer, MachineItemHandler<T>> itemHandler;
    @Shadow
    public Holder<FluidContainer, MachineFluidHandler<T>> fluidHandler;
    @Shadow
    public Holder<ICoverHandler<?>, MachineCoverHandler<T>> coverHandler;
    @Shadow
    public Holder<IEnergyHandler, MachineEnergyHandler<T>> energyHandler;
    @Shadow
    public Holder<IRFNode, MachineRFHandler<T>> rfHandler;

    @Shadow
    abstract Direction getFacing();
    @Shadow
    abstract boolean blocksCapability(Class<?> aClass, Direction side);
    @Shadow
    abstract boolean allowsFrontIO();

    @Unique
    private LazyOptional<IEnergyHandler>[] energyHandlerLazyOptional = new LazyOptional[7];
    @Unique
    private LazyOptional<IFluidHandler>[] fluidHandlerLazyOptional = new LazyOptional[7];
    @Unique
    private LazyOptional<IItemHandler>[] itemHandlerLazyOptional = new LazyOptional[7];
    @Unique
    private LazyOptional<ICoverHandler<?>>[] coverHandlerLazyOptional = new LazyOptional[7];
    @Unique
    private LazyOptional<IEnergyStorage>[] rfHandlerLazyOptional = new LazyOptional[7];

    public TileEntityMachineMixin(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        int index = side == null ? 6 : side.get3DDataValue();
        if (side == getFacing() && !allowsFrontIO()) return LazyOptional.empty();
        if (blocksCapability(AntimatterCaps.CAP_MAP.inverse().get(cap), side)) return LazyOptional.empty();
        return getCap(cap, side);
    }

    @Override
    public <U> LazyOptional<U> getCapabilityFromFake(@NotNull Capability<U> cap, @Nullable Direction side, ICover cover) {
        if (((Object)this) instanceof TileEntityBasicMultiMachine<?> basicMultiMachine){
            if (!basicMultiMachine.allowsFakeTiles()) return LazyOptional.empty();
            if ((cap == CapabilityEnergy.ENERGY || cap == TesseractCaps.ENERGY_HANDLER_CAPABILITY) && !(cover instanceof CoverDynamo || cover instanceof CoverEnergy)) return LazyOptional.empty();
            return getCap(cap, side);
        }
        return super.getCapability(cap, side);
    }

    private <U> LazyOptional<U> getCap(@NotNull Capability<U> cap, @Nullable Direction side) {
        int index = side == null ? 6 : side.get3DDataValue();
        if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && coverHandler.isPresent()) {
            if (coverHandlerLazyOptional[index] == null || !coverHandlerLazyOptional[index].isPresent()){
                coverHandlerLazyOptional[index] = fromHolder(coverHandler, side);
            }
            return coverHandlerLazyOptional[index].cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) {
            if (fluidHandlerLazyOptional[index] == null || !fluidHandlerLazyOptional[index].isPresent()){
                fluidHandlerLazyOptional[index] = fromFluidHolder(fluidHandler, side);
            }
            return fluidHandlerLazyOptional[index].cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) {
            if (itemHandlerLazyOptional[index] == null || !itemHandlerLazyOptional[index].isPresent()){
                itemHandlerLazyOptional[index] = fromItemHolder(itemHandler, side);
            }
            return itemHandlerLazyOptional[index].cast();
        }
        if (cap == TesseractCaps.ENERGY_HANDLER_CAPABILITY || cap == CapabilityEnergy.ENERGY){
            if (cap == CapabilityEnergy.ENERGY && rfHandler.isPresent()){
                if (rfHandlerLazyOptional[index] == null || !rfHandlerLazyOptional[index].isPresent()){
                    rfHandlerLazyOptional[index] = fromEnergyHolder(rfHandler, side);
                }
                return rfHandlerLazyOptional[index].cast();
            } else if (energyHandler.isPresent()){
                if (energyHandlerLazyOptional[index] == null || !energyHandlerLazyOptional[index].isPresent()){
                    energyHandlerLazyOptional[index] = fromHolder(energyHandler, side);
                }
                return energyHandlerLazyOptional[index].cast();
            }
        }
        return super.getCapability(cap, side);
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
