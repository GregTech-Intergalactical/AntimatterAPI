package muramasa.antimatter.capability.machine;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
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
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IEnergyHandlerItem;

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
    protected boolean checkVoltage(long voltage) {
        if (voltage > this.getInputVoltage()) {
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
    public long insertAmps(long voltage, long amps, boolean simulate) {
        if (voltage < 0 || amps < 0) return 0;
        int loss = canInput() && canOutput() ? 1 : 0;
        if (getInputVoltage() == 0) return 0;
        amps = Math.min((getCapacity() - getEnergy()) / getInputVoltage(), amps);
        voltage -= loss;
        if (!simulate && !checkVoltage(voltage)) return amps;

        if (cachedItems.isEmpty()){
            return super.insertAmps(voltage, amps, simulate);
        }
        long energy = voltage * amps;
        insertInternal(energy, simulate);
        return amps;
    }

    @Override
    public long insertEu(long voltage, boolean simulate) {
        if (voltage < 0) return 0;
        if (!simulate && !checkVoltage(voltage)) return voltage;
        if (cachedItems.isEmpty()){
            long superInsert = super.insertEu(voltage, simulate);
            if (superInsert > 0 && !simulate){
                tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED);
            }
            return superInsert;
        }
        return insertInternal(voltage, simulate);
    }

    public long insertInternal(long energy, boolean simulate) {
        if (cachedItems.isEmpty()) return super.insertInternal(energy, simulate);
        long euPerBattery = energy / cachedItems.size();
        long leftover = energy % cachedItems.size();
        long euInserted = 0;
        for (int i = 0; i < cachedItems.size(); i++){
            IEnergyHandlerItem handler = cachedItems.get(i).right();
            long inserted = handler.insertEu(euPerBattery, simulate);
            if (inserted > 0){
                euInserted += inserted;
                if (!simulate){
                    cachedItems.get(i).left().setTag(handler.getContainer().getTag());
                }
            }
            if (euPerBattery - inserted > 0) leftover+= euPerBattery - inserted;
        }
        int unsuccessful = 0;
        int i = 0;
        while (leftover > 0){
            IEnergyHandlerItem handler = cachedItems.get(i).right();;
            if (handler.insertEu(1, simulate) > 0){
                if (!simulate) cachedItems.get(i).left().setTag(handler.getContainer().getTag());
                leftover--;
                euInserted++;
                unsuccessful = 0;
            } else {
                unsuccessful++;
            }
            i++;
            if (i == cachedItems.size()) i = 0;
            if (unsuccessful == cachedItems.size()) break;
        }
        if (leftover > 0){
            long insert = super.insertInternal(leftover, simulate);
            euInserted += insert;
        }
        if (euInserted > 0){
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED);
        }
        return euInserted;
    }

    @Override
    public long extractAmps(long voltage, long amps, boolean simulate) {
        if (amps < 0) {
            Antimatter.LOGGER.info(amps + " Amps at: " + tile.getBlockPos() + ", Simulate: " + simulate);
        }
        return super.extractAmps(voltage, amps, simulate);
    }

    @Override
    public long extractEu(long voltage, boolean simulate) {
        if (cachedItems.isEmpty()){
            long superExtract = super.extractEu(voltage, simulate);
            if (superExtract > 0 && !simulate){
                tile.onMachineEvent(MachineEvent.ENERGY_DRAINED);
            }
            return superExtract;
        }
        long superExtract = super.extractEu(voltage, simulate);
        voltage-= superExtract;
        long toExtract = Math.min(voltage, getEnergy());
        long euPerBattery = toExtract / cachedItems.size();
        long leftover = toExtract % cachedItems.size();
        long euExtracted = superExtract;
        for (int i = 0; i < cachedItems.size(); i++){
            IEnergyHandlerItem handler = cachedItems.get(i).right();
            long extracted = handler.extractEu(euPerBattery, simulate);
            if (extracted > 0){
                euExtracted += extracted;
                if (!simulate){
                    cachedItems.get(i).left().setTag(handler.getContainer().getTag());
                }
            }
            if (euPerBattery - extracted > 0) leftover+= euPerBattery - extracted;
        }
        int unsuccessful = 0;
        int i = 0;
        while (leftover > 0){
            IEnergyHandlerItem handler = cachedItems.get(i).right();;
            if (handler.extractEu(1, simulate) > 0){
                if (!simulate) cachedItems.get(i).left().setTag(handler.getContainer().getTag());
                leftover--;
                euExtracted++;
                unsuccessful = 0;
            } else {
                unsuccessful++;
            }
            i++;
            if (i == cachedItems.size()) i = 0;
            if (unsuccessful == cachedItems.size()) break;
        }
        if (leftover > 0){
            long extract = super.extractEu(leftover, simulate);
            euExtracted += extract;
        }
        if (euExtracted > 0 && !simulate){
            tile.onMachineEvent(MachineEvent.ENERGY_DRAINED);
        }
        return euExtracted;
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

    /*@Override
    public long availableAmpsOutput() {
        return super.availableAmpsOutput() + this.cachedItems.stream().map(Pair::right).mapToLong(IGTNode::availableAmpsOutput).sum();
    }*/

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