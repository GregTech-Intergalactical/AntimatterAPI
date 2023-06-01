package muramasa.antimatter.capability.machine;

import com.google.common.collect.ImmutableList;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.energy.base.PlatformEnergyManager;
import earth.terrarium.botarium.common.energy.base.PlatformItemEnergyManager;
import earth.terrarium.botarium.common.energy.util.EnergyHooks;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.rf.RFHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.Optional;

public class MachineRFHandler<T extends TileEntityMachine<T>> extends RFHandler implements IMachineHandler, Dispatch.Sided<EnergyContainer> {
    protected final T tile;
    protected List<Pair<ItemStack, PlatformItemEnergyManager>> cachedItems = new ObjectArrayList<>();

    protected int offsetInsert = 0;
    protected int offsetExtract = 0;
    public MachineRFHandler(T tile, long energy, long capacity, int maxIn, int maxOut) {
        super(energy, capacity, maxIn, maxOut);
        this.tile = tile;
    }

    public MachineRFHandler(T tile, long capacity, boolean isGenerator) {
        this(tile, 0, capacity, isGenerator ? 0 : tile.getMachineTier().getVoltage(), isGenerator ? tile.getMachineTier().getVoltage() : 0);
    }

    @Override
    public void update(BlockEntity object) {
        for (Direction dir : Ref.DIRS) {
            if (canOutput(dir)) {
                BlockEntity tile = this.tile.getLevel().getBlockEntity(this.tile.getBlockPos().relative(dir));
                if (tile == null) continue;
                Optional<PlatformEnergyManager> handle = EnergyHooks.safeGetBlockEnergyManager(tile, dir.getOpposite());
                handle.ifPresent(eh -> Utils.transferEnergy(this, eh));
            }
        }
    }

    public void update(){
        this.update(tile);
    }

    @Override
    public void init() {
        this.cachedItems = tile.itemHandler.map(MachineItemHandler::getRFChargeableItems).map(ImmutableList::copyOf).orElse(ImmutableList.of());
    }

    @Override
    public long getMaxCapacity() {
        if (canChargeItem()) {
            return super.getMaxCapacity() + (cachedItems != null ? cachedItems.stream().map(Pair::right).mapToLong(PlatformItemEnergyManager::getCapacity).sum() : 0);
        }
        return super.getMaxCapacity();
    }

    @Override
    public long insertEnergy(long maxAmount, boolean simulate) {
        int j = 0;
        long inserted = super.insertEnergy(maxAmount, simulate);
        for (int i = offsetInsert; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            PlatformItemEnergyManager handler = cachedItems.get(i).right();
            if (!handler.supportsInsertion()) continue;
            ItemStack stack = cachedItems.get(i).left();
            ItemStackHolder holder = new ItemStackHolder(stack);
            long insert = handler.insert(holder, maxAmount, simulate);
            if (insert > 0) {
                if (holder.isDirty()){ //assumes the item itself did not change
                    stack.setTag(holder.getStack().getTag());
                    stack.setCount(holder.getStack().getCount());
                }
                offsetInsert = (offsetInsert + 1) % cachedItems.size();
                inserted += insert;
            }
        }
        if (inserted > 0) {
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED);
        }
        return inserted;
    }

    @Override
    public long extractEnergy(long maxAmount, boolean simulate) {
        int j = 0;
        long extracted = super.extractEnergy(maxAmount, simulate);
        for (int i = offsetInsert; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            PlatformItemEnergyManager handler = cachedItems.get(i).right();
            if (!handler.supportsExtraction()) continue;
            ItemStack stack = cachedItems.get(i).left();
            ItemStackHolder holder = new ItemStackHolder(stack);
            long extract = handler.extract(holder, maxAmount, simulate);
            if (extract > 0) {
                if (holder.isDirty()){ //assumes the item itself did not change
                    stack.setTag(holder.getStack().getTag());
                    stack.setCount(holder.getStack().getCount());
                }
                offsetInsert = (offsetInsert + 1) % cachedItems.size();
                extracted += extract;
            }
        }
        if (extracted > 0) {
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED);
        }
        return extracted;
    }

    @Override
    public long getStoredEnergy() {
        if (canChargeItem()) {
            return super.getStoredEnergy() + (cachedItems != null ? cachedItems.stream().map(Pair::right).mapToLong(PlatformItemEnergyManager::getStoredEnergy).sum() : 0);
        }
        return super.getStoredEnergy();
    }

    @Override
    public boolean canInput(Direction direction) {
        return super.canInput(direction) && tile.getFacing() != direction;
    }

    public boolean canChargeItem() {
        return true;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) {
            tile.itemHandler.ifPresent(h -> {
                cachedItems = h.getRFChargeableItems();
                offsetInsert = 0;
                offsetExtract = 0;
            });
            //refreshNet();
        }
    }

    @Override
    public LazyOptional<? extends EnergyContainer> forSide(Direction side) {
        return LazyOptional.of(() -> this);
    }

    @Override
    public LazyOptional<? extends EnergyContainer> forNullSide() {
        return LazyOptional.of(() -> this);
    }

    public void onRemove() {
    }
}
