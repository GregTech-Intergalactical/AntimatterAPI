package muramasa.antimatter.gui;

import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int4;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class GuiData<T extends AntimatterContainer> {

    protected ResourceLocation loc;
    protected ResourceLocation override = null;

    protected MenuHandler<T> menuHandler;
    protected boolean tieredGui = false;

    protected boolean enablePlayerSlots = true;
    protected int4 area = new int4(3, 3, 170, 80);
    protected int4 padding = new int4(0, 55, 0, 0);
    public final int4 progress = new int4(78, 24, 20, 18);
    public BarDir dir = BarDir.RIGHT;
   // protected int4 area = new int4(3, 3, 170, 80), padding = new int4(0, 55, 0, 0), progress = new int4(78, 24, 20, 18), state = new int4(84, 45, 8, 8), io = new int4(9, 64, 14, 14), item = new int4(35, 63, 16, 16), fluid = new int4(53, 63, 16, 16);
   //  protected int2 progressLocation = new int2(176, 0), stateLocation = new int2(176, 55);
   // protected ButtonOverlay itemLocation = new ButtonOverlay("item_eject", 177, 37, 16, 16), fluidLocation = new ButtonOverlay("fluid_eject", 177, 19, 16, 16);
   // protected BarDir side = BarDir.LEFT;
   // protected boolean barFill = true, hasIOButton = true;

    //don't use WidgetProvider as you shouldn't be forced to use AntimatterWidget.

    //This uses Object instead of Tier for instance, for mapping widgets to things other than a tier.
    protected final Map<Object, List<Function<AntimatterContainerScreen<? extends T>, Widget>>> objectWidgets = new Object2ObjectOpenHashMap<>();
    protected final List<Function<AntimatterContainerScreen<? extends T>, Widget>> widgets = new ObjectArrayList<>();


    private ISlotProvider<?> slots;

    public GuiData(String domain, String id) {
        this.loc = new ResourceLocation(domain, id);
    }

    public GuiData(String domain, String id, MenuHandler<T> menuHandler) {
        this(domain, id);
        this.menuHandler = menuHandler;
    }

    public GuiData(IAntimatterObject type, MenuHandler<T> menuHandler) {
        this(type.getDomain(), type.getId());
        this.menuHandler = menuHandler;
    }

    public GuiData<T> setSlots(ISlotProvider<?> slots) {
        this.slots = slots;
        return this;
    }

    public ISlotProvider<?> getSlots() {
        if (slots == null) throw new IllegalStateException("Called GuiData::getSlots without setting it first");
        return slots;
    }

    public MenuHandler<?> getMenuHandler() {
        return this.menuHandler;
    }

    public ResourceLocation getTexture(Tier tier, String type) {
        if (override != null) return override;
        if (tieredGui) {
            return new ResourceLocation(loc.getNamespace(), "textures/gui/" + type + "/" + loc.getPath() + "_" + tier.getId() + ".png");
        } else {
            return new ResourceLocation(loc.getNamespace(), "textures/gui/" + type + "/" + loc.getPath() + ".png");
        }
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    public int4 getArea() {
        return area;
    }

    public int4 getPadding() {
        return padding;
    }

    public void screenCreationCallBack(AntimatterContainerScreen<? extends T> screen, @Nullable Object lookup) {
        this.widgets.forEach(t -> screen.addWidget(t.apply(screen)));
        List<Function<AntimatterContainerScreen<? extends T>, Widget>> wid = this.objectWidgets.get(lookup);
        if (wid != null) wid.forEach(t -> t.apply(screen));
    }

    public boolean enablePlayerSlots() {
        return enablePlayerSlots;
    }

    public void setEnablePlayerSlots(boolean enablePlayerSlots) {
        this.enablePlayerSlots = enablePlayerSlots;
    }

    public GuiData<T> setArea(int x, int y, int z, int w) {
        area.set(x, y, z, w);
        return this;
    }

    public GuiData<T> setPadding(int x, int y, int z, int w) {
        padding.set(x, y, z, w);
        return this;
    }

    public GuiData<T> widget(WidgetSupplier<T> provider) {
        return widget(provider.cast(), null);
    }

    public GuiData<T> widget(WidgetSupplier.WidgetProvider<T> provider, Object data) {
        if (data == null) {
            this.widgets.add(provider::get);
        } else {
            this.objectWidgets.computeIfAbsent(data, k -> new ObjectArrayList<>()).add(provider::get);
        }
        return this;
    }

    public GuiData<T> widget(Function<AntimatterContainerScreen<? extends T>, Widget> provider) {
        return widget(provider, null);
    }

    public GuiData<T> widget(Function<AntimatterContainerScreen<? extends T>, Widget> provider, Object data) {
        if (data == null) {
            this.widgets.add(provider);
        } else {
            this.objectWidgets.computeIfAbsent(data, k -> new ObjectArrayList<>()).add(provider);
        }
        return this;
    }

    public GuiData<T> widget(WidgetSupplier.WidgetProvider<T> build) {
        widget(build, null);
        return this;
    }

    /*public GuiData setProgress(int x, int y, int l, int w) {
        progress.set(x, y, l, w);
        return this;
    }

    public GuiData setItemLocation(int x, int y, int l, int w) {
        itemLocation = new ButtonOverlay(itemLocation.id, x, y, l, w);
        return this;
    }

    public GuiData setFluidLocation(int x, int y, int l, int w) {
        fluidLocation = new ButtonOverlay(fluidLocation.id, x, y, l, w);
        return this;
    }

    public GuiData setDir(BarDir side) {
        this.side = side;
        return this;
    }

    public GuiData setBarFill(boolean barFill){
        this.barFill = barFill;
        return this;
    }

    public GuiData setOverrideLocation(ResourceLocation override) {
        this.override = override;
        return this;
    }*/
    /*
    public GuiData addButton(int x, int y, int w, int h, ButtonBody body) {
        BUTTON_LIST.add(new ButtonData(BUTTON_LIST.size(), EMPTY_BODY, x, y, w, h, body));
        return this;
    }

    public GuiData addButton(int x, int y, int w, int h, ButtonBody body, String text) {
        BUTTON_LIST.add(new ButtonData(BUTTON_LIST.size(), TEXT_ON_BODY, x, y, w, h, text, body));
        return this;
    }

    public GuiData addButton(int x, int y, int w, int h, ButtonBody body, ButtonOverlay overlay) {
        BUTTON_LIST.add(new ButtonData(BUTTON_LIST.size(), OVERLAY_ON_BODY, x, y, w, h, body, overlay));
        return this;
    }

    public GuiData addSwitch(int x, int y, int w, int h, ButtonBody on, ButtonBody off) {
        BUTTON_LIST.add(new ButtonData(BUTTON_LIST.size(), DOUBLE_SWITCH_BODY, x, y, w, h, on, off));
        return this;
    }

    public GuiData addSwitch(int x, int y, int w, int h, ButtonOverlay body) {
        BUTTON_LIST.add(new ButtonData(BUTTON_LIST.size(), SINGLE_SWITCH_BODY, x, y, w, h, body));
        return this;
    }

    public GuiData addSwitch(int x, int y, int w, int h, ButtonOverlay body, String text) {
        BUTTON_LIST.add(new ButtonData(BUTTON_LIST.size(), TEXT_ON_SWITCH, x, y, w, h, text, body));
        return this;
    }

    public List<ButtonData> getButtons() {
        return BUTTON_LIST;
    }

    public boolean hasButtons() {
        return !BUTTON_LIST.isEmpty();
    }

    public ResourceLocation getButtonLocation() {
        if (buttonLoc == null) buttonLoc = new ResourceLocation(loc.getNamespace(), "textures/gui/button/gui_buttons.png");
        return buttonLoc;
    }

    public boolean hasType(SlotType<?> type) {
        return getCount(null, type) > 0;
    }*/

}
