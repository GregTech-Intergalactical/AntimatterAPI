package muramasa.antimatter.gui;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int4;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static muramasa.antimatter.gui.ButtonType.*;

//GuiData with a type parameter T representing a GUI-able type,
//e.g. machine o r cover.
public class GuiData {

    //TODO This whole class needs rethought
    //TODO make sure addons can redirect gui opening now that Mod instance isn't used

    private static final String ANY = "any";

    protected ResourceLocation loc;
    protected MenuHandler<?> menuHandler;

    protected Tier highestTier = Tier.LV;
    protected boolean enablePlayerSlots = true;
    protected ResourceLocation buttonLoc;

    protected int4 area = new int4(3, 3, 170, 80), padding = new int4(0, 55, 0, 0), progress = new int4(78, 24, 20, 18), state = new int4(84, 45, 8, 8);
    protected int2 progressLocation = new int2(176, 0), stateLocation = new int2(176, 55);
    protected BarDir side = BarDir.LEFT;
    protected boolean barFill = true;

    protected Object2ObjectMap<String, List<SlotData<?>>> SLOT_LOOKUP = new Object2ObjectLinkedOpenHashMap<>();
    protected Object2ObjectMap<String, Object2IntOpenHashMap<SlotType<?>>> COUNT_LOOKUP = new Object2ObjectLinkedOpenHashMap<>();
    protected List<ButtonData> BUTTON_LIST = new ObjectArrayList<>();

    public GuiData(String domain, String id) {
        this.loc = new ResourceLocation(domain, id);
    }

    public GuiData(String domain, String id, MenuHandler<?> menuHandler) {
        this(domain, id);
        this.menuHandler = menuHandler;
    }

    public GuiData(IAntimatterObject type, MenuHandler<?> menuHandler) {
        this(type.getDomain(), type.getId());
        this.menuHandler = menuHandler;
    }

    public MenuHandler<?> getMenuHandler() {
        return this.menuHandler;
    }

    //Type represents what type of texture this data is representing.
    //TODO: store this in e.g. IAntimatterobject instead of hardcoded.
    public ResourceLocation getTexture(Tier tier, String type) {
        if (hasSlots(tier) && type.equals("machine")) {
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

    public int4 getProgress() {
        return progress;
    }

    public int4 getState() {
        return state;
    }

    public int2 getProgressLocation() {
        return progressLocation;
    }

    public int2 getStateLocation() {
        return stateLocation;
    }

    public BarDir getDir() {
        return side;
    }

    public boolean isBarFill(){
        return barFill;
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

    public GuiData setProgress(int x, int y, int l, int w) {
        progress.set(x, y, l, w);
        return this;
    }

    public GuiData setState(int x, int y, int l, int w) {
        state.set(x, y, l, w);
        return this;
    }

    public GuiData setProgressLocation(int x, int y) {
        progressLocation.set(x, y);
        return this;
    }

    public GuiData setStateLocation(int x, int y) {
        stateLocation.set(x, y);
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

    /** Adds a slot for ANY **/
    public GuiData add(SlotType<?> type, int x, int y) {
        return add(ANY, new SlotData<>(type, x, y));
    }

    /** Adds a slot for the given Tier **/
    public GuiData add(Tier tier, SlotType<?> type, int x, int y) {
        return add(tier.getId(), new SlotData<>(type, x, y));
    }

    /** Copies ALL slots from an existing GuiData **/
    public GuiData add(GuiData data) {
        List<SlotData<?>> list = data.getAnySlots();
        for (SlotData<?> slot : list) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from an existing Machine **/
    public GuiData add(Machine<?> type) {
        List<SlotData<?>> list = type.getGui().getAnySlots();
        for (SlotData<?> slot : list) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from type into toTier slots **/
    public GuiData add(Tier toTier, Machine<?> type) {
        List<SlotData<?>> list = type.getGui().getAnySlots();
        for (SlotData<?> slot : list) {
            add(toTier.getId(), slot);
        }
        return this;
    }

    /** Copies fromTier slots from type into toTier slots **/
    public GuiData add(Tier toTier, Machine<?> type, Tier fromTier) {
        List<SlotData<?>> list = type.getGui().getSlots(fromTier);
        for (SlotData<?> slot : list) {
            add(toTier.getId(), slot);
        }
        return this;
    }

    public GuiData add(String key, SlotData<?> slot) {
        //TODO figure out better way to do this
        Tier tier = AntimatterAPI.get(Tier.class, key);
        if (tier != null && tier.getVoltage() > highestTier.getVoltage()) highestTier = tier;

        if (!COUNT_LOOKUP.containsKey(key)) COUNT_LOOKUP.put(key, new Object2IntOpenHashMap<>());

        COUNT_LOOKUP.get(key).addTo(slot.getType(), 1);
        if (SLOT_LOOKUP.containsKey(key)) {
            SLOT_LOOKUP.get(key).add(slot);
        } else {
            List<SlotData<?>> list = new ObjectArrayList<>();
            list.add(slot);
            SLOT_LOOKUP.put(key, list);
        }
        return this;
    }

    public boolean hasSlots() {
        List<SlotData<?>> slots = SLOT_LOOKUP.get(ANY);
        return slots != null && !slots.isEmpty();
    }

    public boolean hasSlots(Tier tier) {
        List<SlotData<?>> slots = SLOT_LOOKUP.get(tier.getId());
        return slots != null && !slots.isEmpty();
    }

    public List<SlotData<?>> getAnySlots() {
        return SLOT_LOOKUP.get(ANY);
    }

    public Tier getHighestTier() {
        return highestTier;
    }

    public List<SlotData<?>> getSlots(Tier tier) {
        List<SlotData<?>> slots = SLOT_LOOKUP.get(tier.getId());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        return slots != null ? slots : new ObjectArrayList<>();
    }

    public List<SlotData<?>> getSlots(SlotType<?> type, Tier tier) {
        List<SlotData<?>> types = new ObjectArrayList<>();
        List<SlotData<?>> slots = SLOT_LOOKUP.get(tier.getId());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        if (slots == null) return types; //No slots found
        for (SlotData<?> slot : slots) {
            if (slot.getType() == type) types.add(slot);
        }
        return types;
    }

    public List<SlotData<?>> getSlots(SlotType<?> type) {
        List<SlotData<?>> types = new ObjectArrayList<>();
        List<SlotData<?>> slots = SLOT_LOOKUP.get(ANY);
        if (slots == null) return types; //No slots found
        for (SlotData<?> slot : slots) {
            if (slot.getType() == type) types.add(slot);
        }
        return types;
    }

    public boolean hasType(SlotType<?> type) {
        return getCount(null, type) > 0;
    }

    public boolean hasAnyItem(Tier tier) {
        return !getSlots(SlotType.IT_IN, tier).isEmpty() || !getSlots(SlotType.IT_OUT, tier).isEmpty() || !getSlots(SlotType.CELL_IN,tier).isEmpty() || !getSlots(SlotType.CELL_OUT,tier).isEmpty();
    }

    public boolean hasAnyFluid(Tier tier) {
        return !getSlots(SlotType.FL_IN, tier).isEmpty() || !getSlots(SlotType.FL_OUT, tier).isEmpty();
    }

    //TODO broken
    public int getCount(Tier tier, SlotType<?> type) {
        String id = tier == null || !COUNT_LOOKUP.containsKey(tier.getId()) ? ANY : tier.getId();
        return COUNT_LOOKUP.get(id).getInt(type);
    }
}
