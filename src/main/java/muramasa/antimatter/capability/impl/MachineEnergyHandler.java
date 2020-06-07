package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.api.electric.IElectricNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.List;

public class MachineEnergyHandler extends EnergyHandler implements IMachineHandler {

    protected TileEntityMachine tile;
    protected ITickingController controller;
    //Cached charge items from the energy handler. Updated on machine event as to not always extract caps.
    private List<IEnergyHandler> cachedItems;

    final int LOSS_ITEM = 2;

    public MachineEnergyHandler(TileEntityMachine tile, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.tile = tile;
        Tesseract.ELECTRIC.registerNode(tile.getDimention(), tile.getPos().toLong(), this);
    }

    public MachineEnergyHandler(TileEntityMachine tile) {
        this(tile, 0, tile.getMachineTier().getVoltage() * 64L, tile.getMachineTier().getVoltage(), 0, 1, 0);
        tile.itemHandler.ifPresent(handler -> cachedItems = handler.getChargeableItems());
    }


    @Override
    public boolean canInput() {
        //TODO: Somehow cache this as it might be slow
        boolean canInput = super.canInput();
        return canInput || cachedItems.stream().anyMatch(IEnergyHandler::canInput);
    }

    public void onRemove() {
        Tesseract.ELECTRIC.remove(tile.getDimention(), tile.getPos().toLong());
    }
    //Transfers energy from internal buffer between items.
    public void onUpdate() {
        if (controller != null) controller.tick();
        if (cachedItems != null && cachedItems.size() > 0) {
            tile.itemHandler.ifPresent(handler -> {
                //TODO: Consume amperage.
                if (canInput()) {
                    int amps = 0;
                    for (IEnergyHandler ihandler : cachedItems) {
                        if (amps == amperage_in) {
                            break;
                        }
                        long energy = Utils.transferEnergy(ihandler,this);
                        if (energy > 0) {
                            amps++;
                        }
                    }
                }
                if (canExtract()) {
                    int amps = 0;
                    for (IEnergyHandler ihandler : cachedItems) {
                        if (amps == amperage_out) {
                            break;
                        }
                        long energy = Utils.transferEnergyWithLoss(this,ihandler,LOSS_ITEM);
                        if (energy > 0) {
                            amps++;
                        }
                    }
                }
            });
        }
    }

    public void setOutputAmperage(int amp) {
        amperage_out = amp;
    }

    public void setInputAmperage(int amp) {
        amperage_in = amp;
    }

    /*public void onReset() {
        if (tile.isServerSide()) {
            TesseractAPI.removeElectric(tile.getDimention(), tile.getPos().toLong());
            TesseractAPI.registerElectricNode(tile.getDimention(), tile.getPos().toLong(), this);
        }
    }*/

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && tile.getCover(Ref.DIRECTIONS[direction.getIndex()]).isEqual(Data.COVER_EMPTY)*/;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return false;
    }

    /** NBT **/
    // TODO: Finish
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong("Energy", energy);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        energy = tag.getLong("Energy");
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        //TODO: Check if item event
        tile.itemHandler.ifPresent(handler -> cachedItems = handler.getChargeableItems());
    }
}