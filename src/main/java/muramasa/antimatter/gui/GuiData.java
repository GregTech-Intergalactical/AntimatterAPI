package muramasa.antimatter.gui;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int4;
import net.minecraft.util.ResourceLocation;

//@OnlyIn(Dist.CLIENT)
public class GuiData {

    protected ResourceLocation loc;
    protected ResourceLocation override = null;

    protected MenuHandler<?> menuHandler;
    protected ImmutableMap<Tier, Tier> guiTiers;

    protected boolean enablePlayerSlots = true;
    protected int4 area = new int4(3, 3, 170, 80);
    protected int4 padding = new int4(0, 55, 0, 0);
    public BarDir dir = BarDir.RIGHT;

    private int buttons = 0;
    private ISlotProvider<?> slots;

    public GuiData(String domain, String id) {
        this.loc = new ResourceLocation(domain, id);
    }

    public GuiData(String domain, String id, MenuHandler menuHandler) {
        this(domain, id);
        this.menuHandler = menuHandler;
    }

    public GuiData(IAntimatterObject type, MenuHandler menuHandler) {
        this(type.getDomain(), type.getId());
        this.menuHandler = menuHandler;
    }

    public GuiData setSlots(ISlotProvider<?> slots) {
        this.slots = slots;
        return this;
    }

    public GuiData setTieredGui(ImmutableMap.Builder<Tier, Tier> guiTiers) {
        this.guiTiers = guiTiers.build();
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
        if (guiTiers != null && guiTiers.get(tier) != null) {
            return new ResourceLocation(loc.getNamespace(), "textures/gui/" + type + "/" + loc.getPath() + "_" + guiTiers.get(tier).getId() + ".png");
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

    /*public void screenCreationCallBack(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler, @Nullable Object lookup) {
        this.widgets.forEach(t -> screen.addWidget(t.apply(screen, handler)));
        List<BiFunction<AntimatterContainerScreen<? extends T>, IGuiHandler, Widget>> wid = this.objectWidgets.get(lookup);
        if (wid != null) wid.forEach(t -> t.apply(screen, handler));
    }*/

    public boolean enablePlayerSlots() {
        return enablePlayerSlots;
    }

    public void setEnablePlayerSlots(boolean enablePlayerSlots) {
        this.enablePlayerSlots = enablePlayerSlots;
    }

    public GuiData setArea(int x, int y, int z, int w) {
        area.set(x, y, z, w);
        return this;
    }

    public GuiData setPadding(int x, int y, int z, int w) {
        padding.set(x, y, z, w);
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
    }*/

    public GuiData setOverrideLocation(ResourceLocation override) {
        this.override = override;
        return this;
    }
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
