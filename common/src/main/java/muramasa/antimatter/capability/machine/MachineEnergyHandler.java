package muramasa.antimatter.capability.machine;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.TesseractCapUtils;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IEnergyHandlerItem;
import tesseract.api.gt.IGTNode;

import java.util.List;
import java.util.Optional;

public class MachineEnergyHandler<T extends TileEntityMachine<T>> extends EnergyHandler implements IMachineHandler, Dispatch.Sided<IEnergyHandler> {

    protected final T tile;
    protected long capacty;

    protected List<Pair<ItemStack, IEnergyHandlerItem>> cachedItems = new ObjectArrayList<>();
    protected int offsetInsert = 0;
    protected int offsetExtract = 0;

    public MachineEnergyHandler(T tile, long energy, long capacity, long voltageIn, long voltageOut, int amperageIn, int amperageOut) {
        super(energy, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.capacty = capacity;
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
        this.cachedItems = tile.itemHandler.map(MachineItemHandler::getChargeableItems).map(ImmutableList::copyOf).orElse(ImmutableList.of());
    }

    public void setCapacty(long capacty) {
        this.capacty = capacty;
    }

    public List<Pair<ItemStack, IEnergyHandlerItem>> getCachedEnergyItems() {
        return this.cachedItems;
    }

    public void onRemove() {

    }

    @Override
    protected boolean checkVoltage(GTTransaction.TransferData data) {
        if (data.getVoltage() > this.getInputVoltage()) {
            if (AntimatterConfig.GAMEPLAY.MACHINES_EXPLODE) {
                Utils.createExplosion(this.tile.getLevel(), tile.getBlockPos(), 4.0F, Explosion.BlockInteraction.DESTROY);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getCapacity() {
        if (canChargeItem()) {
            return capacity + (cachedItems != null ? cachedItems.stream().map(Pair::right).mapToLong(IEnergyHandler::getCapacity).sum() : 0);
        }
        return capacity;
    }

    @Override
    public boolean addEnergy(GTTransaction.TransferData data) {
        int loss = canInput() && canOutput() ? 1 : 0;
        data.setLoss(loss);
        //int amps = (int) Math.min((getCapacity() - getEnergy()) / getInputVoltage(), data.getAmps(true));
        //GTTransaction internal = new GTTransaction(data.getEnergy(amps, true), (t) -> {});

        int j = 0;
        boolean ok = super.addEnergy(data);
        for (int i = offsetInsert; j < cachedItems.size(); j++, i = (i == cachedItems.size() - 1 ? 0 : (i + 1))) {
            IEnergyHandlerItem handler = cachedItems.get(i).right();
            if (handler.addEnergy(data)) {
                cachedItems.get(i).left().setTag(handler.getContainer().getTag());
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
            IEnergyHandlerItem handler = cachedItems.get(i).right();
            if (handler.extractEnergy(data)) {
                cachedItems.get(i).left().setTag(handler.getContainer().getTag());
                offsetExtract = (offsetExtract + 1) % cachedItems.size();
                ok = true;
            }
        }
        if (ok) {
            tile.onMachineEvent(MachineEvent.ENERGY_DRAINED);
        }
        return ok;
    }

    public boolean extractInternal(long power, boolean simulate){
        GTTransaction transaction = tile.energyHandler.map(eh -> eh.extract(GTTransaction.Mode.INTERNAL)).orElse(null);
        if (transaction != null && transaction.isValid()) {
            if (simulate) {
                return transaction.eu >= power;
            } else if (transaction.eu >= power) {
                transaction.addData(power, Utils.sink());
                transaction.commit();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public long getEnergy() {
        if (canChargeItem()) {
            return super.getEnergy() + (cachedItems != null ? cachedItems.stream().map(Pair::right).mapToLong(IEnergyHandler::getEnergy).sum() : 0);
        }
        return super.getEnergy();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        cachedItems.forEach(t -> t.right().getState().onTick());
        for (Direction dir : Ref.DIRS) {
            if (canOutput(dir)) {
                BlockEntity tile = this.tile.getLevel().getBlockEntity(this.tile.getBlockPos().relative(dir));
                if (tile == null) continue;
                Optional<IEnergyHandler> handle = TesseractCapUtils.getEnergyHandler(tile, dir.getOpposite());
                handle.ifPresent(eh -> Utils.transferEnergy(this, eh));
            }
        }
    }

    @Override
    public long availableAmpsOutput() {
        return super.availableAmpsOutput() + this.cachedItems.stream().map(Pair::right).mapToLong(IGTNode::availableAmpsOutput).sum();
    }

    /*@Override
    public long availableAmpsInput(long voltage) {
        return super.availableAmpsInput(voltage) + this.cachedItems.stream().map(Pair::right).mapToLong(node -> node.availableAmpsInput(voltage)).sum();
    }*/

    @Override
    public boolean canInput(Direction direction) {
        return super.canInput(direction) && (tile.getFacing() != direction || tile.getMachineType().allowsFrontIO());
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
    public Optional<IEnergyHandler> forSide(Direction side) {
        return Optional.of(this);
    }

    @Override
    public Optional<? extends IEnergyHandler> forNullSide() {
        return Optional.of(this);
    }
}