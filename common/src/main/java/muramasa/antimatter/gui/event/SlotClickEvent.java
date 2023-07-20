package muramasa.antimatter.gui.event;

import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import earth.terrarium.botarium.common.fluid.base.PlatformFluidItemHandler;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.FluidHandler;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.wrapper.InvWrapper;
import tesseract.TesseractCapUtils;

import java.util.Optional;
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

    private PlatformFluidHandler tryGetCap(IGuiHandler handler) {
        if (handler instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) handler;
            return machine.fluidHandler.map(MachineFluidHandler::getGuiHandler).orElse(null);
        }
        if (handler instanceof BlockEntity be) {
            return FluidHooks.safeGetBlockFluidManager(be, null).orElse(null);
        }
        return null;
    }

    @Override
    public void handle(Player player, GuiInstance instance) {
        PlatformFluidHandler sink = tryGetCap(instance.handler);
        if (sink == null) return;
        ItemStack stack = player.containerMenu.getCarried();
        if (type == SlotType.FL_IN || type == SlotType.FL_OUT) {
            int max;
            if (stack.getItem() instanceof ItemFluidCell) {
                max = ((ItemFluidCell) stack.getItem()).getCapacity();
            } else {
                max = 1000;
            }
            Optional<PlatformFluidItemHandler> iHandler = FluidHooks.safeGetItemFluidManager(stack);
            boolean hasFluid = iHandler.map(t -> t.getTankAmount() > 0 && !t.getFluidInTank(0).isEmpty()).orElse(false);
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
