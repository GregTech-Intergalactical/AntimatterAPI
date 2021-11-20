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
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;

import java.util.List;

public class MachineEnergyHandler<T extends TileEntityMachine<T>> extends EnergyHandler implements IMachineHandler, Dispatch.Sided<IEnergyHandler> {

    protected final T tile;

    protected List<IEnergyHandler> cachedItems = new ObjectArrayList<>();
    protected int offsetInsert = 0;
    protected int offsetExtract = 0;

    public MachineEnergyHandler(T tile, long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(energy, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.tile = tile;
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
    }

    @Override
    protected boolean checkVoltage(GTTransaction.TransferData data) {
        if (data.getVoltage() > this.tile.getMachineTier().getVoltage()) {
            Utils.createExplosion(this.tile.getLevel(), tile.getBlockPos(), 4.0F, Explosion.Mode.BREAK);
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
    public boolean addEnergy(GTTransaction.TransferData data) {
        int j = 0;
        boolean ok = super.addEnergy(data);
        for (int i = offsetInsert; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            IEnergyHandler handler = cachedItems.get(i);
            if (handler.addEnergy(data)) {
                offsetInsert = (offsetInsert + 1) % cachedItems.size();
                ok = true;
            }
        }
        if (ok) {
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED);
        }
        return ok;
    }

    @Override
    public boolean extractEnergy(GTTransaction.TransferData data) {
        boolean ok = super.extractEnergy(data);
        int j = 0;
        for (int i = offsetExtract; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            IEnergyHandler handler = cachedItems.get(i);
            if (handler.extractEnergy(data)) {
                offsetExtract = (offsetExtract + 1) % cachedItems.size();
                ok = true;
            }
        }
        if (ok) {
            tile.onMachineEvent(MachineEvent.ENERGY_DRAINED);
        }
        return ok;
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
        for (Direction dir : Ref.DIRS) {
            if (canOutput(dir)) {
                TileEntity tile = this.tile.getLevel().getBlockEntity(this.tile.getBlockPos().relative(dir));
                if (tile == null) continue;
                LazyOptional<IEnergyHandler> handle = tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dir.getOpposite());
                if (!handle.isPresent()) {
                    LazyOptional<IEnergyStorage> cap = tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
                    if (!cap.isPresent()) continue;
                    handle = LazyOptional.of(() -> new EnergyTileWrapper(tile, cap.orElse(null)));
                }
                handle.ifPresent(eh -> Utils.transferEnergy(this, eh));
            }
        }
    }

    public void onRemove() {
        if (tile.isServerSide()) {
            // deregisterNet();
        }
    }

    @Override
    public boolean insert(GTTransaction transaction) {
        boolean ok = super.insert(transaction);
        if (transaction.canContinue()) {
            ok |= insertIntoItems(transaction);
        }
        return ok;
    }

    @Override
    public GTTransaction extract(GTTransaction.Mode mode) {
        GTTransaction transaction = super.extract(mode);
        extractFromItems(transaction);
        return transaction;
    }

    protected void extractFromItems(GTTransaction transaction) {
        for (IEnergyHandler cachedItem : this.cachedItems) {
            transaction.addAmps(cachedItem.availableAmpsOutput());
        }
    }

    protected boolean insertIntoItems(GTTransaction transaction) {
        for (IEnergyHandler cachedItem : this.cachedItems) {
            transaction.addAmps(cachedItem.availableAmpsInput());
        }
        return transaction.availableAmps > 0;
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
        Tesseract.GT_ENERGY.refreshNode(tile.getLevel(), tile.getBlockPos().asLong());
    }
}