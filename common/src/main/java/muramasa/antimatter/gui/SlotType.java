package muramasa.antimatter.gui;

import earth.terrarium.botarium.common.energy.util.EnergyHooks;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.EmptyContainer;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.gui.slot.*;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.TesseractCapUtils;
import tesseract.api.item.ExtendedItemContainer;

import java.util.Map;
import java.util.function.BiPredicate;

public class SlotType<T extends Slot> implements IAntimatterObject, IMachineEvent {

    public static SlotType<SlotInput> IT_IN = new SlotType<>("item_in", (type, gui, inv, i, d) -> new SlotInput(type, gui, inv.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), new ItIn(), true, false);
    public static SlotType<SlotOutput> IT_OUT = new SlotType<>("item_out", (type, gui, inv, i, d) -> new SlotOutput(type, gui, inv.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), (t, i) -> false, false, true);
    public static SlotType<SlotFake> DISPLAY = new SlotType<>("display", (type, gui, item, i, d) -> new SlotFake(type, gui, item.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY(), false), (t, i) -> false, false, false);
    public static SlotType<SlotFake> DISPLAY_SETTABLE = new SlotType<>("display_settable", (type, gui, item, i, d) -> new SlotFake(type, gui, item.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY(), true), (t, i) -> false, true, false);
    public static SlotType<SlotFake> FLUID_DISPLAY_SETTABLE = new SlotType<>("fluid_display_settable", (type, gui, item, i, d) -> new SlotFluidDisplaySettable(type, gui, item.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), (t, i) -> false, true, false, new ResourceLocation(Ref.ID, "fluid"));
    public static SlotType<AbstractSlot<?>> STORAGE = new SlotType<>("storage", (type, gui, item, i, d) -> new AbstractSlot<>(type, gui, item.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), (t, i) -> true);
    public static SlotType<SlotFakeFluid> FL_IN = new SlotType<>("fluid_in", (type, gui, inv, i, d) -> new SlotFakeFluid(type, gui, MachineFluidHandler.FluidDirection.INPUT, i, d.getX(), d.getY()), (t, i) -> false, false, false, new ResourceLocation(Ref.ID, "fluid_in"));
    //Cheat using same ID to get working counter.
    public static SlotType<SlotFakeFluid> FL_OUT = new SlotType<>("fluid_out", (type, gui, inv, i, d) -> new SlotFakeFluid(type, gui, MachineFluidHandler.FluidDirection.OUTPUT, i, d.getX(), d.getY()), (t, i) -> false, false, false, new ResourceLocation(Ref.ID, "fluid_out"));
    public static SlotType<SlotCell> CELL_IN = new SlotType<>("cell_in", (type, gui, inv, i, d) -> new SlotCell(type, gui, inv.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), (t, i) -> FluidHooks.isFluidContainingItem(i), true, false, new ResourceLocation(Ref.ID, "cell_in"));
    public static SlotType<SlotCell> CELL_OUT = new SlotType<>("cell_out", (type, gui, inv, i, d) -> new SlotCell(type, gui, inv.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), (t, i) -> FluidHooks.isFluidContainingItem(i), false, true, new ResourceLocation(Ref.ID, "cell_out"));
    public static SlotType<SlotEnergy> ENERGY = new SlotType<>("energy", (type, gui, inv, i, d) -> new SlotEnergy(type, gui, inv.getOrDefault(type, new EmptyContainer()), i, d.getX(), d.getY()), (t, i) -> {
        if (t instanceof BlockEntity tile) {
            return TesseractCapUtils.INSTANCE.getEnergyHandler(tile, null).map(eh -> TesseractCapUtils.INSTANCE.getEnergyHandlerItem(i).map(inner -> ((inner.getInputVoltage() | inner.getOutputVoltage()) == (eh.getInputVoltage() | eh.getOutputVoltage()))).orElse(EnergyHooks.isEnergyItem(i))).orElse(EnergyHooks.isEnergyContainer(tile, null) && EnergyHooks.isEnergyItem(i));
        }
        return true;
    }, true, false, new ResourceLocation(Ref.ID, "energy"));

    protected final String id;
    protected final ISlotSupplier<T> slotSupplier;
    public final boolean output;
    public final boolean input;
    public final ResourceLocation textureName;
    public final BiPredicate<IGuiHandler, ItemStack> tester;

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<IGuiHandler, ItemStack> validator) {
        this(id, slotSupplier, validator, true, true, new ResourceLocation(Ref.ID, "item"));
    }

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<IGuiHandler, ItemStack> validator, boolean input, boolean output) {
        this(id, slotSupplier, validator, input, output, new ResourceLocation(Ref.ID, "item"));
    }

    public SlotType(String id, ISlotSupplier<T> slotSupplier, BiPredicate<IGuiHandler, ItemStack> validator, boolean input, boolean output, ResourceLocation textureName) {
        this.id = id;
        this.slotSupplier = slotSupplier;
        this.output = output;
        this.tester = validator;
        this.input = input;
        this.textureName = textureName;
        AntimatterAPI.register(SlotType.class, this);
    }

    public String getId() {
        return id;
    }

    public ISlotSupplier<T> getSlotSupplier() {
        return slotSupplier;
    }

    public interface ISlotSupplier<T extends Slot> {
        T get(SlotType<T> type, IGuiHandler tile, Map<SlotType<?>, ExtendedItemContainer> slots, int index, SlotData<T> data);
    }

    public static void init() {

    }

    public static class ItIn implements BiPredicate<IGuiHandler, ItemStack> {

        @Override
        public boolean test(IGuiHandler iGuiHandler, ItemStack stack) {
            if (iGuiHandler instanceof BlockEntityMachine) {
                return (((BlockEntityMachine<?>) iGuiHandler).recipeHandler.map(rh -> rh.accepts(stack)).orElse(true));
            }
            return true;
        }
    }
}
