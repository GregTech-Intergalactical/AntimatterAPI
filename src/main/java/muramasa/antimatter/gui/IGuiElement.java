package muramasa.antimatter.gui;

public interface IGuiElement {
    int getX();
    int getY();
    int getW();
    int getH();
    IGuiElement parent();
    void setX(int x);
    void setY(int y);
    void setW(int w);
    void setH(int h);

    int depth();
}
