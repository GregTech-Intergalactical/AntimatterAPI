package muramasa.antimatter.gui;

public interface IGuiElement {
    int getX();
    int getY();
    int getW();
    int getH();

    void setX(int x);
    void setY(int y);
    void setW(int w);
    void setH(int h);

    int realX();
    int realY();
    int depth();

    IGuiElement parent();
    default void onChildSizeChange(IGuiElement element) {

    }
}
