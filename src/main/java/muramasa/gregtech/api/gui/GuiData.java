package muramasa.gregtech.api.gui;

import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.util.int4;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GuiData {

    private static final String ANY = "any";

    private String name;
    private Object instance;
    private int id;

    private int4 area = new int4(3, 3, 170, 80), padding = new int4(0, 55, 0, 0);
    private BarDir dir = BarDir.LEFT;

    private LinkedHashMap<String, ArrayList<SlotData>> SLOT_LOOKUP = new LinkedHashMap<>();
    private LinkedHashMap<SlotType, Integer> COUNT_LOOKUP = new LinkedHashMap<>();

    public GuiData(String name) {
        this.name = name;
    }

    public GuiData(Machine type, Object instance, int id) {
        this.name = type.getName();
        this.instance = instance;
        this.id = id;
    }

    public Object getInstance() {
        return instance;
    }

    public int getId() {
        return id;
    }

    public ResourceLocation getTexture(Tier tier) {
//        for (Tier t : Tier.getAll()) {
//            if (hasSlots(tier)) {
//                return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + name + "_" + tier.getName() + ".png");
//            }
//        }
//        return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + name + ".png");
        if (hasSlots(tier)) {
            return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + name + "_" + tier.getName() + ".png");
        } else {
            return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + name + ".png");
        }
    }

    public int4 getArea() {
        return area;
    }

    public int4 getPadding() {
        return padding;
    }

    public BarDir getDir() {
        return dir;
    }

    public GuiData add(String key, SlotData slot) {
        if (COUNT_LOOKUP.containsKey(slot.type)) {
            COUNT_LOOKUP.put(slot.type, COUNT_LOOKUP.get(slot.type) + 1);
        } else {
            COUNT_LOOKUP.put(slot.type, 1);
        }
        if (SLOT_LOOKUP.containsKey(key)) {
            SLOT_LOOKUP.get(key).add(slot);
        } else {
            ArrayList<SlotData> list = new ArrayList<>();
            list.add(slot);
            SLOT_LOOKUP.put(key, list);
        }
        return this;
    }

    public GuiData add(SlotType type, int x, int y) {
        return add(ANY, new SlotData(type, x, y));
    }

    public GuiData add(Tier tier, SlotType type, int x, int y) {
        return add(tier.getName(), new SlotData(type, x, y));
    }

    public GuiData add(GuiData data) {
        for (SlotData slot : data.getAnySlots()) {
            add(ANY, slot);
        }
        return this;
    }

    public GuiData add(Machine type) {
        for (SlotData slot : type.getGui().getAnySlots()) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from type into toTier slots **/
    public GuiData add(Tier toTier, Machine type) {
        for (SlotData slot : type.getGui().getAnySlots()) {
            add(toTier.getName(), slot);
        }
        return this;
    }

    /** Copies fromTier slots from type into toTier slots **/
    public GuiData add(Tier toTier, Machine type, Tier fromTier) {
        for (SlotData slot : type.getGui().getSlots(fromTier)) {
            add(toTier.getName(), slot);
        }
        return this;
    }

    public GuiData setArea(int x, int y, int z, int w) {
        area.set(x, y, z, w);
        return this;
    }

    public GuiData setPadding(int x, int y, int z, int w) {
        padding.set(x, y, z, w);
        return this;
    }

    public boolean hasSlots() {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(ANY);
        return slots != null && slots.size() > 0;
    }

    public boolean hasSlots(Tier tier) {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getName());
        return slots != null && slots.size() > 0;
    }

    public List<SlotData> getAnySlots() {
        return SLOT_LOOKUP.get(ANY);
    }

    public List<SlotData> getSlots(Tier tier) {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getName());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        return slots != null ? slots : new ArrayList<>();
    }

    public List<SlotData> getTypes(SlotType type, Tier tier) {
        ArrayList<SlotData> types = new ArrayList<>();
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getName());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        for (SlotData slot : slots) {
            if (slot.type == type) types.add(slot);
        }
        return types;
    }

    public boolean hasType(SlotType type) {
        return COUNT_LOOKUP.containsKey(type);
    }

    public int getCount(SlotType type) {
        return hasType(type) ? COUNT_LOOKUP.get(type) : 0;
    }
}
