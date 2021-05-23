package muramasa.antimatter.gui;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.slot.*;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import tesseract.api.capability.TesseractGTCapability;

import java.util.function.BiPredicate;

public class SlotType<T extends AbstractSlot> implements IAntimatterObject {

    public static SlotType<SlotInput> IT_IN = new SlotType<>("item_in", (type, t, i, d) -> new SlotInput(type, t,t.itemHandler.map(MachineItemHandler::getInputHandler).orElse(null), i, d.getX(), d.getY()), (t,i) -> t.recipeHandler.map(rh -> rh.accepts(i)).orElse(true),ContentEvent.ITEM_INPUT_CHANGED, true, false);
    public static SlotType<SlotOutput> IT_OUT = new SlotType<>("item_out", (type, t, i, d) -> new SlotOutput(type, t,t.itemHandler.map(MachineItemHandler::getOutputHandler).orElse(null), i, d.getX(), d.getY()),(t, i) -> false,ContentEvent.ITEM_OUTPUT_CHANGED, false, true);
    public static SlotType<SlotFakeFluid> FL_IN = new SlotType<>("fluid_in", (type, t, i, d) -> new SlotFakeFluid(type, t, MachineFluidHandler.FluidDirection.INPUT,i, d.getX(), d.getY()), (t,i) -> false,ContentEvent.FLUID_INPUT_CHANGED, false, false);
    //Cheat using same ID to get working counter.
    public static SlotType<SlotFakeFluid> FL_OUT = new SlotType<>("fluid_out", (type, t, i, d) -> new SlotFakeFluid(type, t,MachineFluidHandler.FluidDirection.OUTPUT, i, d.getX(), d.getY()), (t,i) -> false, ContentEvent.FLUID_OUTPUT_CHANGED, false, false);
    public static SlotType<SlotCell> CELL_IN = new SlotType<>("cell_in", (type, t, i, d) -> new SlotCell(type, t, t.itemHandler.map(MachineItemHandler::getCellInputHandler).orElse(null), i, d.getX(), d.getY()), (t,i) -> i.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent(), ContentEvent.ITEM_CELL_CHANGED,true, false);
    public static SlotType<SlotCell> CELL_OUT = new SlotType<>("cell_out", (type, t, i, d) -> new SlotCell(type, t, t.itemHandler.map(MachineItemHandler::getCellOutputHandler).orElse(null), i, d.getX(), d.getY()), (t,i) -> i.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent(), ContentEvent.ITEM_CELL_CHANGED,false, true);
    public static SlotType<SlotEnergy> ENERGY = new SlotType<>("energy", (type, t, i, d) -> new SlotEnergy(type, t, t.itemHandler.map(MachineItemHandler::getChargeHandler).orElse(null), i, d.getX(), d.getY()), (t,i) ->  t.energyHandler.map(eh -> i.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(inner -> inner.getInputVoltage() == eh.getOutputVoltage()).orElse(false)).orElse(false), ContentEvent.ENERGY_SLOT_CHANGED, true, true);

    protected final String id;
    protected final ISlotSupplier<T> slotSupplier;
    public final boolean output;
    public final boolean input;
    public final BiPredicate<TileEntityMachine<?>, ItemStack> tester;
    public final ContentEvent ev;

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<TileEntityMachine<?>, ItemStack> validator, ContentEvent ev) {
        this(id, slotSupplier, validator, ev,true, true);
    }

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<TileEntityMachine<?>, ItemStack> validator, ContentEvent ev, boolean input, boolean output) {
        this.id = id;
        this.slotSupplier = slotSupplier;
        this.output = output;
        this.tester = validator;
        this.input = input;
        this.ev = ev;
        AntimatterAPI.register(SlotType.class, this);
    }

    public String getId() {
        return id;
    }

    public ISlotSupplier<T> getSlotSupplier() {
        return slotSupplier;
    }

    public interface ISlotSupplier<T extends AbstractSlot> {
        T get(SlotType<T> type, TileEntityMachine<?> tile, int index, SlotData<T> data);
    }
}
