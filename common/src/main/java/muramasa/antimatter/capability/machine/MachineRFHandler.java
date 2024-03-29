package muramasa.antimatter.capability.machine;

import com.google.common.collect.ImmutableList;
import earth.terrarium.botarium.common.energy.base.PlatformEnergyManager;
import earth.terrarium.botarium.common.energy.base.PlatformItemEnergyManager;
import earth.terrarium.botarium.common.energy.util.EnergyHooks;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.rf.RFHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.api.rf.IRFNode;

import java.util.List;
import java.util.Optional;

public class MachineRFHandler<T extends BlockEntityMachine<T>> extends RFHandler implements IMachineHandler, Dispatch.Sided<IRFNode> {
    protected final T tile;
    protected List<Pair<ItemStack, PlatformItemEnergyManager>> cachedItems = new ObjectArrayList<>();

    protected int offsetInsert = 0;
    protected int offsetExtract = 0;
    public MachineRFHandler(T tile, long energy, long capacity, long maxIn, long maxOut) {
        super(energy, capacity, maxIn, maxOut);
        this.tile = tile;
    }

    public MachineRFHandler(T tile, long capacity, boolean isGenerator) {
        this(tile, 0, capacity, isGenerator ? 0 : tile.getMachineTier().getVoltage(), isGenerator ? tile.getMachineTier().getVoltage() : 0);
    }

    public void onUpdate(){
        for (Direction dir : Ref.DIRS) {
            if (canOutput(dir)) {
                BlockEntity tile = this.tile.getCachedBlockEntity(dir);
                if (tile == null) continue;
                Optional<PlatformEnergyManager> handle = EnergyHooks.safeGetBlockEnergyManager(tile, dir.getOpposite());
                handle.ifPresent(eh -> Utils.transferEnergy(this, eh));
            }
        }
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
        return super.canInput(direction) && (tile.getFacing() != direction || tile.getMachineType().allowsFrontIO());
    }

    public boolean canChargeItem() {
        return true;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == SlotType.ENERGY) {
            tile.itemHandler.ifPresent(h -> {
                cachedItems = h.getRFChargeableItems();
                offsetInsert = 0;
                offsetExtract = 0;
            });
            //refreshNet();
        }
    }

    @Override
    public Optional<? extends IRFNode> forSide(Direction side) {
        return Optional.of(this);
    }

    @Override
    public Optional<? extends IRFNode> forNullSide() {
        return Optional.of(this);
    }

    public void onRemove() {
    }
}
