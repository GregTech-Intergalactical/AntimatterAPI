package muramasa.antimatter.gui.event;

import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.FluidPlatformUtils;

import java.util.function.BiFunction;
import java.util.function.Consumer;

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
        if (stack.isEmpty()) return;
        if (type == SlotType.FL_IN || type == SlotType.FL_OUT) {
            Consumer<ItemStack> consumer = s -> {
                if (player.isCreative()) return;
                boolean single = stack.getCount() == 1;
                stack.shrink(1);
                if (single){
                    player.containerMenu.setCarried(s);
                } else {
                    if (!player.addItem(s)){
                        player.drop(s, true);
                    }
                }
            };
            if (type == SlotType.FL_IN){
                FluidPlatformUtils.emptyItemIntoContainer(Utils.ca(1, stack), sink, consumer);
            } else {
                FluidPlatformUtils.fillItemFromContainer(Utils.ca(1, stack), sink, consumer);
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
