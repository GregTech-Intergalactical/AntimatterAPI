package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tesseract.EnergyTileWrapper;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

import java.util.Arrays;
import java.util.List;

public class MachineEnergyHandler<T extends TileEntityMachine<T>> extends EnergyHandler implements IMachineHandler, Dispatch.Sided<IEnergyHandler> {

    protected final T tile;

    protected List<IEnergyHandler> cachedItems = new ObjectArrayList<>();
    protected int offsetInsert = 0;
    protected int offsetExtract = 0;
    private final List<LazyOptional<IEnergyHandler>> cache = new ObjectArrayList<>(6);

    public MachineEnergyHandler(T tile, long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(energy, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.tile = tile;
        Arrays.stream(Ref.DIRS).forEach(dir -> cache.add(LazyOptional.empty()));
    }

    public MachineEnergyHandler(T tile, boolean isGenerator) {
        this(tile, 1, isGenerator);
    }

    public MachineEnergyHandler(T tile, int amps, boolean isGenerator) {
        this(tile, 0L, tile.getMachineTier().getVoltage() * (isGenerator ? 40L : 66L), isGenerator ? 0 : tile.getMachineTier().getVoltage(), isGenerator ? tile.getMachineTier().getVoltage() : 0, isGenerator ? 0 : amps, isGenerator ? amps : 0);
    }

    @Override
    public void init() {
        cachedItems = tile.itemHandler.map(MachineItemHandler::getChargeableItems).orElse(cachedItems);
        //registerNet();
    }

    @Override
    protected boolean checkVoltage(long receive, boolean simulate) {
        if (receive > this.getInputVoltage()) {
            if (!this.tile.recipeHandler.map(t -> t.generator).orElse(false)) {
                if (!simulate)
                    Utils.createExplosion(tile.getWorld(), tile.getPos(), 4.0F, Explosion.Mode.BREAK);
                return false;
            }
        }
        return true;
    }

    @Override
    public long getCapacity() {
        if (canChargeItem()) {
            return super.getCapacity() + (cachedItems != null ? cachedItems.stream().mapToLong(IEnergyHandler::getCapacity).sum() : 0);
        }
        return super.getCapacity();
    }

    @Override
    public long getEnergy() {
        if (canChargeItem()) {
            return super.getEnergy() + (cachedItems != null ? cachedItems.stream().mapToLong(IEnergyHandler::getEnergy).sum() : 0);
        }
        return super.getEnergy();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        cachedItems.forEach(t -> t.getState().onTick());
        cache.forEach(v -> v.ifPresent(t -> {
            if (t instanceof EnergyTileWrapper) {
                t.tesseractTick();
            }
        }));
        for (Direction dir : Ref.DIRS) {
            if (canOutput(dir)) {
                LazyOptional<IEnergyHandler> handle = cache.get(dir.getIndex());
                if (!handle.isPresent()) {
                    TileEntity tile = this.tile.getWorld().getTileEntity(this.tile.getPos().offset(dir));
                    if (tile == null) continue;
                    handle = tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dir.getOpposite());
                    if (!handle.isPresent()) {
                        LazyOptional<IEnergyStorage> cap = tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
                        if (!cap.isPresent()) continue;
                        handle = LazyOptional.of(() -> new EnergyTileWrapper(tile, cap.orElse(null)));
                        LazyOptional<IEnergyHandler> finalHandle = handle;
                        cap.addListener(list -> finalHandle.invalidate());
                    }
                    cache.add(dir.getIndex(), handle);
                    handle.addListener(h -> cache.add(dir.getIndex(), LazyOptional.empty()));
                }
                boolean ok = true;
                while (ok) {
                    if (!getState().extract(true, 1, 0)) {
                        break;
                    }
                    ok = handle.map(eh -> Utils.transferEnergy(this, eh)).orElse(false);
                }
            }
        }
    }

    public void onRemove() {
        if (tile.isServerSide()) {
            // deregisterNet();
        }
    }

    @Override
    public boolean canInput(Direction direction) {
        return super.canInput(direction) && tile.getFacing() != direction;
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        return this.insertInternal(maxReceive, simulate, false);
    }

    @Override
    public long insertInternal(long maxReceive, boolean simulate, boolean force) {
        long inserted = super.insertInternal(maxReceive, simulate, force);
        if (inserted == 0 && canChargeItem()) {
            inserted = insertIntoItems(maxReceive, simulate);
        }
        if (!simulate) {
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED, inserted);
        }
        return inserted;
    }

    protected long insertIntoItems(long maxReceive, boolean simulate) {
        int j = 0;
        for (int i = offsetInsert; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            IEnergyHandler handler = cachedItems.get(i);
            long inserted = handler.insert(maxReceive, true);
            if (inserted > 0) {
                if (!simulate) {
                    offsetInsert = offsetInsert == cachedItems.size() - 1 ? 0 : offsetInsert + 1;
                    return handler.insert(maxReceive, false);
                } else {
                    return inserted;
                }
            }
        }
        return 0;
    }

    public boolean canChargeItem() {
        return true;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return extractInternal(maxExtract, simulate, false);
    }

    @Override
    public long extractInternal(long maxExtract, boolean simulate, boolean force) {
        long extracted = super.extractInternal(maxExtract, simulate, force);
        if (extracted == 0) {
            extracted = extractFromItems(maxExtract, simulate);
        }
        if (!simulate) {
            tile.onMachineEvent(MachineEvent.ENERGY_DRAINED, extracted);
            // tile.markDirty();
        }
        return extracted;
    }

    protected long extractFromItems(long maxExtract, boolean simulate) {
        int j = 0;
        for (int i = offsetExtract; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            IEnergyHandler handler = cachedItems.get(i);
            long extracted = handler.extract(maxExtract, true);
            if (extracted > 0) {
                if (!simulate) {
                    offsetExtract = offsetExtract == cachedItems.size() - 1 ? 0 : offsetExtract + 1;
                    return handler.extract(maxExtract, false);
                } else {
                    return extracted;
                }
            }
        }
        return 0;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) {
            tile.itemHandler.ifPresent(h -> {
                cachedItems = h.getChargeableItems();
                offsetInsert = 0;
                offsetExtract = 0;
            });
            //refreshNet();
        }
    }


    @Override
    public LazyOptional<IEnergyHandler> forSide(Direction side) {
        return LazyOptional.of(() -> this);
    }

    @Override
    public LazyOptional<? extends IEnergyHandler> forNullSide() {
        return LazyOptional.of(() -> this);
    }

    @Override
    public void refresh() {
        Tesseract.GT_ENERGY.refreshNode(tile.getWorld(), tile.getPos().toLong());
    }
}