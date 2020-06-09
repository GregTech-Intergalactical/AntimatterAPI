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
    //Cached chargeable items from the energy handler. Updated on machine event as to not always extract caps.
    private List<IEnergyHandler> cachedItems;

    final int LOSS_ITEM = 2;

    public MachineEnergyHandler(TileEntityMachine tile, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.tile = tile;
        Tesseract.ELECTRIC.registerNode(tile.getDimention(), tile.getPos().toLong(), this);
        tile.itemHandler.ifPresent(handler -> cachedItems = handler.getChargeableItems());
    }

    public MachineEnergyHandler(TileEntityMachine tile) {
        this(tile, 0, tile.getMachineTier().getVoltage() * 64L, tile.getMachineTier().getVoltage(), 0, 1, 0);
    }

    public void onRemove() {
        Tesseract.ELECTRIC.remove(tile.getDimention(), tile.getPos().toLong());
    }
    //Transfers energy from internal buffer between items.
    public void onUpdate() {
        if (controller != null) controller.tick();
        if (cachedItems != null && cachedItems.size() > 0) {
            tile.itemHandler.ifPresent(handler -> {
                /*
                Charge internal buffer from slots.
                 */
                //TODO: Separate Amperage counter for items? To review

                //TODO: Review this. Avoid a problem with a charge loop, e.g. item charges machine & machine charges item.
                int consumedAmps = 0;

                if (canChargeFromItem()) {
                    for (IEnergyHandler ihandler : cachedItems) {
                        if (consumedAmps >= amperage_in) {
                            break;
                        }
                        long energy = -1;
                        long itemAmps = 0;
                        long itemCapAmps = ihandler.getOutputAmperage();
                        //Try to charge a given item until it can no longer consume amps. Then move on to the next one.
                        while (energy != 0 && consumedAmps < amperage_in && itemAmps < itemCapAmps) {
                            energy = Utils.transferEnergy(ihandler,this);
                            if (energy > 0) {
                                consumedAmps++;
                                itemAmps++;
                            }
                        }
                    }
                }
                /*
                Charges items from internal buffer. Regular machines cannot do this and charge straight
                from the ENet.
                 */
                //TODO: Else if? Do either but not both.
                if (canChargeItem() && consumedAmps == 0) {
                    consumedAmps = 0;
                    for (IEnergyHandler ihandler : cachedItems) {
                        if (consumedAmps >= amperage_out) {
                            break;
                        }
                        long energy = -1;
                        long itemAmps = 0;
                        long itemCapAmps = ihandler.getInputAmperage();
                        //Try to charge a given item until it can no longer consume amps. Then move on to the next one.
                        while (energy != 0 && consumedAmps < amperage_out && itemAmps < itemCapAmps) {
                            energy = Utils.transferEnergyWithLoss(this,ihandler,LOSS_ITEM);
                            if (energy > 0) {
                                consumedAmps++;
                                itemAmps++;
                            }
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

    public boolean canChargeItem() {
        return false;
    }

    public boolean canChargeFromItem() {
        return true;
    }

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