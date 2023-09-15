package muramasa.antimatter.gui;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int4;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

//@Environment(EnvType.CLIENT)
public class GuiData {

    protected ResourceLocation loc;
    protected ResourceLocation override = null;

    protected MenuHandler<?> menuHandler;
    protected ImmutableMap<Tier, Tier> guiTiers;

    protected Map<String, ResourceLocation> backgroundTextures = new Object2ObjectOpenHashMap<>();

    protected boolean enablePlayerSlots = true;
    protected int4 area = new int4(3, 3, 170, 80);

    protected MachineWidgetData machineWidgetData = new MachineWidgetData(this);

    private final int buttons = 0;
    private ISlotProvider<?> slots;

    public GuiData(String domain, String id) {
        this.loc = new ResourceLocation(domain, id);
        this.backgroundTextures.put("", new ResourceLocation(Ref.ID, "textures/gui/background/machine_basic.png"));
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
       if (backgroundTextures.containsKey(tier.getId())) return backgroundTextures.get(tier.getId());
       return backgroundTextures.get("");
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    public int4 getArea() {
        return area;
    }

    public MachineWidgetData getMachineData() {
        return machineWidgetData;
    }

    /*public void screenCreationCallBack(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler, @Nullable Object lookup) {
        this.widgets.forEach(t -> screen.addWidget(t.apply(screen, handler)));
        List<BiFunction<AntimatterContainerScreen<? extends T>, IGuiHandler, Widget>> wid = this.objectWidgets.get(lookup);
        if (wid != null) wid.forEach(t -> t.apply(screen, handler));
    }*/

    public boolean enablePlayerSlots() {
        return enablePlayerSlots;
    }

    public GuiData setEnablePlayerSlots(boolean enablePlayerSlots) {
        this.enablePlayerSlots = enablePlayerSlots;
        return this;
    }

    public GuiData setArea(int x, int y, int z, int w) {
        area.set(x, y, z, w);
        return this;
    }

    public GuiData setOverrideLocation(ResourceLocation override) {
        this.override = override;
        return this;
    }

    public GuiData setBackgroundTexture(String textureName){
        this.backgroundTextures.put("", new ResourceLocation(loc.getNamespace(), "textures/gui/background/" + textureName + ".png"));
        return this;
    }
    public GuiData setBackgroundTexture(Tier tier, String textureName){
        this.backgroundTextures.put(tier.getId(), new ResourceLocation(loc.getNamespace(), "textures/gui/background/" + textureName + ".png"));
        return this;
    }
}
