package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.*;

public class TextButtonWidget<T> extends ButtonWidget {
    T state;
    final Function<IGuiHandler, T> syncFunction;
    final Function<T, Component> textToRender;
    public TextButtonWidget(GuiInstance instance, IGuiElement parent, @NotNull Function<T, Component> textToRender, T defaultValue, Function<IGuiHandler, T> syncFunction, @Nullable Consumer<ButtonWidget> onPress) {
        super(instance, parent, ButtonOverlay.NO_OVERLAY, onPress);
        state = defaultValue;
        this.textToRender = textToRender;
        this.syncFunction = syncFunction;
    }

    @Override
    public void init() {
        super.init();
        if (state instanceof Integer){
            gui.syncInt(() -> (int)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof Long){
            gui.syncLong(() -> (long)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof Float){
            gui.syncFloat(() -> (float)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof Double){
            gui.syncDouble(() -> (double)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof Boolean){
            gui.syncBoolean(() -> (boolean)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof String){
            gui.syncString(() -> (String)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof ItemStack){
            gui.syncItemStack(() -> (ItemStack)syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else if (state instanceof FluidHolder){
            gui.syncFluidStack(() -> (FluidHolder) syncFunction.apply(gui.handler), i -> this.state = (T) i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        } else {
            String object = gui.handler instanceof BlockEntityMachine<?> machine ? machine.getMachineType().getLoc().toString() : gui.handler instanceof ICover cover ? cover.getLoc().toString() : gui.handler.getClass().toString();
            Antimatter.LOGGER.warn("Unknown sync type in text widget in: " + object);
        }
    }

    @Override
    protected void renderButtonBody(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        drawText(matrixStack, textToRender.apply(state), realX(), realY(), 4210752);
    }

    public static <T> WidgetSupplier build(Function<IGuiHandler, T> syncFunction, Function<T, Component> textToRender, T defaultValue, IGuiEvent.IGuiEventFactory ev, int id, boolean renderBackground) {
        return builder(((a, b) -> new TextButtonWidget<>(a, b, textToRender, defaultValue, syncFunction, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id)))).setRenderBackground(renderBackground)));
    }
}
