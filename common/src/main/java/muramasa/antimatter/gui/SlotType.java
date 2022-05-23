package muramasa.antimatter.gui;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.gui.slot.AbstractSlot;
import muramasa.antimatter.gui.slot.SlotCell;
import muramasa.antimatter.gui.slot.SlotEnergy;
import muramasa.antimatter.gui.slot.SlotFake;
import muramasa.antimatter.gui.slot.SlotFakeFluid;
import muramasa.antimatter.gui.slot.SlotInput;
import muramasa.antimatter.gui.slot.SlotOutput;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import tesseract.TesseractPlatformUtils;
import tesseract.api.TesseractCaps;

import java.util.Map;
import java.util.function.BiPredicate;

public class SlotType<T extends Slot> implements IAntimatterObject {

    public static SlotType<SlotInput> IT_IN = new SlotType<>("item_in", (type, gui, inv, i, d) -> new SlotInput(type, gui, inv.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY()), new ItIn(), ContentEvent.ITEM_INPUT_CHANGED, true, false);

    public static SlotType<SlotOutput> IT_OUT = new SlotType<>("item_out", (type, gui, inv, i, d) -> new SlotOutput(type, gui, inv.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY()), (t, i) -> false, ContentEvent.ITEM_OUTPUT_CHANGED, false, true);
    public static SlotType<SlotFake> DISPLAY = new SlotType<>("display", (type, gui, item, i, d) -> new SlotFake(type, gui, item.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY(), false), (t, i) -> false, ContentEvent.ITEM_INPUT_CHANGED, false, false);
    public static SlotType<SlotFake> DISPLAY_SETTABLE = new SlotType<>("display_settable", (type, gui, item, i, d) -> new SlotFake(type, gui, item.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY(), true), (t, i) -> false, ContentEvent.ITEM_INPUT_CHANGED, true, false);
    public static SlotType<AbstractSlot<?>> STORAGE = new SlotType<>("storage", (type, gui, item, i, d) -> new AbstractSlot<>(type, gui, item.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY()), (t, i) -> true, ContentEvent.ITEM_INPUT_CHANGED);
    public static SlotType<SlotFakeFluid> FL_IN = new SlotType<>("fluid_in", (type, gui, inv, i, d) -> new SlotFakeFluid(type, gui, MachineFluidHandler.FluidDirection.INPUT, i, d.getX(), d.getY()), (t, i) -> false, ContentEvent.FLUID_INPUT_CHANGED, false, false);
    //Cheat using same ID to get working counter.
    public static SlotType<SlotFakeFluid> FL_OUT = new SlotType<>("fluid_out", (type, gui, inv, i, d) -> new SlotFakeFluid(type, gui, MachineFluidHandler.FluidDirection.OUTPUT, i, d.getX(), d.getY()), (t, i) -> false, ContentEvent.FLUID_OUTPUT_CHANGED, false, false);
    public static SlotType<SlotCell> CELL_IN = new SlotType<>("cell_in", (type, gui, inv, i, d) -> new SlotCell(type, gui, inv.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY()), (t, i) -> TesseractPlatformUtils.getFluidHandlerItem(i).isPresent(), ContentEvent.ITEM_CELL_CHANGED, true, false);
    public static SlotType<SlotCell> CELL_OUT = new SlotType<>("cell_out", (type, gui, inv, i, d) -> new SlotCell(type, gui, inv.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY()), (t, i) -> TesseractPlatformUtils.getFluidHandlerItem(i).isPresent(), ContentEvent.ITEM_CELL_CHANGED, false, true);
    public static SlotType<SlotEnergy> ENERGY = new SlotType<>("energy", (type, gui, inv, i, d) -> new SlotEnergy(type, gui, inv.getOrDefault(type, new EmptyHandler()), i, d.getX(), d.getY()), (t, i) -> {
        if (t instanceof ICapabilityProvider tile) {
            return tile.getCapability(TesseractCaps.getENERGY_HANDLER_CAPABILITY()).map(eh -> TesseractPlatformUtils.getEnergyHandlerItem(i).map(inner -> ((inner.getInputVoltage() | inner.getOutputVoltage()) == (eh.getInputVoltage() | eh.getOutputVoltage()))).orElse(false)).orElse(false);
        }
        return true;
    }, ContentEvent.ENERGY_SLOT_CHANGED, true, false);

    protected final String id;
    protected final ISlotSupplier<T> slotSupplier;
    public final boolean output;
    public final boolean input;
    public final BiPredicate<IGuiHandler, ItemStack> tester;
    public final ContentEvent ev;

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent ev) {
        this(id, slotSupplier, validator, ev, true, true);
    }

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent ev, boolean input, boolean output) {
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

    public interface ISlotSupplier<T extends Slot> {
        T get(SlotType<T> type, IGuiHandler tile, Map<SlotType<?>, IItemHandler> slots, int index, SlotData<T> data);
    }

    public static void init() {

    }

    public static class ItIn implements BiPredicate<IGuiHandler, ItemStack> {

        @Override
        public boolean test(IGuiHandler iGuiHandler, ItemStack stack) {
            if (iGuiHandler instanceof TileEntityMachine) {
                return (((TileEntityMachine<?>) iGuiHandler).recipeHandler.map(rh -> rh.accepts(stack)).orElse(true));
            }
            return true;
        }
    }
}
