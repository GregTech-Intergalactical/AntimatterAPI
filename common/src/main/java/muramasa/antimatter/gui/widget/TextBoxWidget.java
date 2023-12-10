package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TextBoxWidget extends Widget {

    EditBox textBox;
    BiConsumer<IGuiHandler, String> consumer;

    protected TextBoxWidget(@NotNull GuiInstance gui, @Nullable IGuiElement parent, BiConsumer<IGuiHandler, String> consumer) {
        super(gui, parent);
        this.consumer = consumer;
    }

    public static WidgetSupplier build(BiConsumer<IGuiHandler, String> consumer) {
        return builder((i, p) -> new ProgressWidget(i, p));
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        textBox.render(matrixStack, (int) mouseX, (int) mouseY, partialTicks);
    }

    @Override
    public void init() {
        super.init();
        this.gui.syncString(() -> textBox.getValue(), s -> consumer.accept(gui.handler, s), ICanSyncData.SyncDirection.CLIENT_TO_SERVER);
        if (gui.isRemote){
            initTextBox();
        }
    }

    @Environment(EnvType.CLIENT)
    private void initTextBox(){
        textBox = new EditBox(Minecraft.getInstance().font, this.getX(), this.getY(), this.getW(), this.getH(), Utils.literal(""));
        this.textBox.setMaxLength(32500);
    }
}
