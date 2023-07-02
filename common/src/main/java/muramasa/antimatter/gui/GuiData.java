package muramasa.antimatter.gui;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int4;
import net.minecraft.resources.ResourceLocation;

//@Environment(EnvType.CLIENT)
public class GuiData {

    protected ResourceLocation loc;
    protected ResourceLocation override = null;

    protected MenuHandler<?> menuHandler;
    protected ImmutableMap<Tier, Tier> guiTiers;

    protected boolean enablePlayerSlots = true;
    protected int4 area = new int4(3, 3, 170, 80);
    public BarDir dir = BarDir.LEFT;
    public boolean barFill = true;

    private final int buttons = 0;
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

    public GuiData setOverrideLocation(ResourceLocation override) {
        this.override = override;
        return this;
    }

    public void setDir(BarDir dir) {
        this.dir = dir;
    }

    public void setBarFill(boolean barFill) {
        this.barFill = barFill;
    }
}
