package muramasa.antimatter.gui;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.tier.VoltageTier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int4;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;

//GuiData with a type parameter T representing a GUI-able type,
//e.g. machine o r cover.
public class GuiData<T extends IAntimatterObject> {

    //TODO This whole class needs rethought

    //TODO make sure addons can redirect gui opening now that Mod instance isn't used

    private static final String ANY = "any";

    protected ResourceLocation loc;
    protected IMenuHandler menuHandler;

    protected VoltageTier highestTier = VoltageTier.LV;
    protected boolean enablePlayerSlots = true;

    protected int4 area = new int4(3, 3, 170, 80), padding = new int4(0, 55, 0, 0);
    protected BarDir side = BarDir.LEFT;
    protected IInfoRenderer infoRenderer;

    protected LinkedHashMap<String, List<SlotData>> SLOT_LOOKUP = new LinkedHashMap<>();
    protected Object2IntOpenHashMap<SlotType> COUNT_LOOKUP = new Object2IntOpenHashMap<>();

    public GuiData(String domain, String id) {
        this.loc = new ResourceLocation(domain, id);
    }

    public GuiData(String domain, String id, IMenuHandler menuHandler) {
        this(domain, id);
        this.menuHandler = menuHandler;
    }

    public GuiData(T type) {
        this(type.getDomain(), type.getId());
    }

    public GuiData(T type, IMenuHandler menuHandler) {
        this(type);
        this.menuHandler = menuHandler;
    }

    public IMenuHandler getMenuHandler() {
        return this.menuHandler;
    }

    //Type represents what type of texture this data is representing.
    //TODO: store this in e.g. IAntimatterobject instead of hardcoded.
    public ResourceLocation getTexture(VoltageTier tier, String type) {
        if (hasSlots(tier) && type.equals("machine")) {
            return new ResourceLocation(loc.getNamespace(), "textures/gui/" + type + '/' + loc.getPath() + '_' + tier.getId() + ".png");
        } else {
            return new ResourceLocation(loc.getNamespace(), "textures/gui/" + type + '/' + loc.getPath() + ".png");
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

    public BarDir getDir() {
        return side;
    }

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

    public GuiData setDir(BarDir side) {
        this.side = side;
        return this;
    }

    public IInfoRenderer getInfoRenderer() {
        return infoRenderer;
    }

    public GuiData setInfoRenderer(IInfoRenderer infoRenderer) {
        this.infoRenderer = infoRenderer;
        return this;
    }

    /** Adds a slot for ANY **/
    public GuiData add(SlotType type, int x, int y) {
        return add(ANY, new SlotData(type, x, y));
    }

    /** Adds a slot for the given Tier **/
    public GuiData add(VoltageTier tier, SlotType type, int x, int y) {
        return add(tier.getId(), new SlotData(type, x, y));
    }

    /** Copies ALL slots from an existing GuiData **/
    public GuiData add(GuiData data) {
        List<SlotData> list = data.getAnySlots();
        for (SlotData slot : list) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from an existing Machine **/
    public GuiData add(Machine type) {
        List<SlotData> list = type.getGui().getAnySlots();
        for (SlotData slot : list) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from type into toTier slots **/
    public GuiData add(VoltageTier toTier, Machine type) {
        List<SlotData> list = type.getGui().getAnySlots();
        for (SlotData slot : list) {
            add(toTier.getId(), slot);
        }
        return this;
    }

    /** Copies fromTier slots from type into toTier slots **/
    public GuiData add(VoltageTier toTier, Machine type, VoltageTier fromTier) {
        List<SlotData> list = type.getGui().getSlots(fromTier);
        for (SlotData slot : list) {
            add(toTier.getId(), slot);
        }
        return this;
    }

    public GuiData add(String key, SlotData slot) {
        //TODO figure out better way to do this
        VoltageTier tier = AntimatterAPI.get(VoltageTier.class, key);
        if (tier != null && tier.getVoltage() > highestTier.getVoltage()) highestTier = tier;

        COUNT_LOOKUP.addTo(slot.type, 1);
        if (SLOT_LOOKUP.containsKey(key)) {
            SLOT_LOOKUP.get(key).add(slot);
        } else {
            List<SlotData> list = new ObjectArrayList<>();
            list.add(slot);
            SLOT_LOOKUP.put(key, list);
        }
        return this;
    }

    public boolean hasSlots() {
        List<SlotData> slots = SLOT_LOOKUP.get(ANY);
        return slots != null && slots.size() > 0;
    }

    public boolean hasSlots(VoltageTier tier) {
        List<SlotData> slots = SLOT_LOOKUP.get(tier.getId());
        return slots != null && slots.size() > 0;
    }

    public List<SlotData> getAnySlots() {
        return SLOT_LOOKUP.get(ANY);
    }

    public VoltageTier getHighestTier() {
        return highestTier;
    }

    public List<SlotData> getSlots(VoltageTier tier) {
        List<SlotData> slots = SLOT_LOOKUP.get(tier.getId());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        return slots != null ? slots : new ObjectArrayList<>();
    }

    public List<SlotData> getSlots(SlotType type, VoltageTier tier) {
        List<SlotData> types = new ObjectArrayList<>();
        List<SlotData> slots = SLOT_LOOKUP.get(tier.getId());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        if (slots == null) return types; //No slots found
        for (SlotData slot : slots) {
            if (slot.type == type) types.add(slot);
        }
        return types;
    }

    public boolean hasType(SlotType type) {
        return getCount(type) > 0;
    }

    public boolean hasAnyItem(VoltageTier tier) {
        return getSlots(SlotType.IT_IN, tier).size() > 0 || getSlots(SlotType.IT_OUT, tier).size() > 0;
    }

    public boolean hasAnyFluid(VoltageTier tier) {
        return getSlots(SlotType.FL_IN, tier).size() > 0 || getSlots(SlotType.FL_OUT, tier).size() > 0;
    }

    //TODO broken
    public int getCount(SlotType type) {
        return COUNT_LOOKUP.get(type);
    }
}
