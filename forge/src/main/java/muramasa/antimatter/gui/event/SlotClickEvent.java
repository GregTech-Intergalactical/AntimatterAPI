package muramasa.antimatter.gui.event;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.function.BiFunction;

public class SlotClickEvent implements IGuiEvent {

    public static final IGuiEventFactory SLOT_CLICKED = AntimatterAPI.register(IGuiEventFactory.class, new IGuiEventFactory() {
        @Override
        public BiFunction<IGuiEventFactory, FriendlyByteBuf, IGuiEvent> factory() {
            return SlotClickEvent::new;
        }

        @Override
        public String getId() {
            return "slot_click";
        }
    });

    public final SlotType<?> type;
    public final int index;

    public SlotClickEvent(IGuiEventFactory factory, FriendlyByteBuf buffer) {
        this.index = buffer.readVarInt();
        ResourceLocation loc = buffer.readResourceLocation();
        this.type = AntimatterAPI.get(SlotType.class, loc.getPath(), loc.getNamespace());
    }

    public SlotClickEvent(int slotIndex, SlotType<?> type) {
        this.index = slotIndex;
        this.type = type;
    }

    @Override
    public boolean forward() {
        return false;
    }

    private IFluidHandler tryGetCap(IGuiHandler handler) {
        if (handler instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) handler;
            return machine.fluidHandler.map(MachineFluidHandler::getGuiHandler).orElse(null);
        }
        if (handler instanceof ICapabilityProvider) {
            return ((ICapabilityProvider) handler).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
        }
        return null;
    }

    @Override
    public void handle(Player player, GuiInstance instance) {
        IFluidHandler sink = tryGetCap(instance.handler);
        if (sink == null) return;
        ItemStack stack = player.containerMenu.getCarried();
        if (type == SlotType.FL_IN || type == SlotType.FL_OUT) {
            int max;
            if (stack.getItem() instanceof ItemFluidCell) {
                max = ((ItemFluidCell) stack.getItem()).getCapacity();
            } else {
                max = 1000;
            }
            LazyOptional<IFluidHandlerItem> iHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
            boolean hasFluid = iHandler.map(t -> t.getTanks() > 0 && !t.getFluidInTank(0).isEmpty()).orElse(false);
            FluidActionResult res;
            if (hasFluid && type == SlotType.FL_IN) {
                res = FluidUtil.tryEmptyContainerAndStow(stack, sink, new InvWrapper(player.getInventory()), max, player, true);
            } else {
                res = FluidUtil.tryFillContainerAndStow(stack, sink, new InvWrapper(player.getInventory()), max, player, true);
            }
            if (res.isSuccess() && !player.isCreative()) {
                player.containerMenu.setCarried(res.getResult());
            }
        }
    }

    @Override
    public IGuiEventFactory getFactory() {
        return SLOT_CLICKED;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.index);
        buffer.writeResourceLocation(new ResourceLocation(type.getDomain(), type.getId()));
    }

    public static void init() {

    }
}
