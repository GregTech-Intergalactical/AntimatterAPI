package muramasa.antimatter.gui;

import muramasa.antimatter.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class ButtonType {
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("textures/gui/button/gui_buttons.png");

    public static ButtonType GREY = new ButtonType("button_grey", (x, y, w, h, text, press) -> new ButtonWidget(x, y, w, h, 0, 0, 32, 0, BUTTON_TEXTURE, press));
    public static ButtonType BLUE = new ButtonType("button_blue", (x, y, w, h, text, press) -> new ButtonWidget(x, y, w, h, 64, 64, 96, 0, BUTTON_TEXTURE, press));
    public static ButtonType NO_HOVER = new ButtonType("button_nohover", (x, y, w, h, text, press) -> new ButtonWidget(x, y, w, h, 128, 128, 128, 0, BUTTON_TEXTURE, press));


    protected String id;
    protected IButtonSupplier buttonSupplier;

    public ButtonType(String id, IButtonSupplier buttonSupplier) {
        this.id = id;
        this.buttonSupplier = buttonSupplier;
    }

    public String getId() {
        return id;
    }

    public IButtonSupplier getButtonSupplier() {
        return buttonSupplier;
    }

    public interface IButtonSupplier {

        AbstractButton get(int x, int y, int w, int h, String text, Button.IPressable press);
    }
}
