package muramasa.antimatter.gui;

import muramasa.antimatter.gui.event.IGuiEvent;

public class ButtonData {

    private int id;
    private ButtonType type;
    private IGuiEvent event;
    private int x, y, w, h;
    private ButtonOverlay[] data;
    private String text;

    public ButtonData(int id, ButtonType type, IGuiEvent event, int x, int y, int w, int h, String text, ButtonOverlay... data) {
        this.id = id;
        this.type = type;
        this.event = event;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
        this.data = data;
    }

    public ButtonData(int id, ButtonType type, IGuiEvent event, int x, int y, int w, int h, ButtonOverlay... data) {
        this(id, type, event, x, y, w, h, "", data);
    }

    public int getId() {
        return id;
    }

    public ButtonType getType() {
        return type;
    }

    public IGuiEvent getEvent() {
        return event;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public String getText() {
        return text;
    }

    public ButtonOverlay getOverlay(int i) {
        return data[i];
    }

    public ButtonBody getBody(int i) {
        return (ButtonBody) data[i];
    }
}
