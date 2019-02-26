package muramasa.gregtech.api.gui;

import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiData {

    private Object instance;
    private int id;

    private HashMap<String, ArrayList<SlotData>> SLOT_LOOKUP = new HashMap<>();
    private int itemInputs, itemOutputs, fluidInputs, fluidOutputs;

    public GuiData(Object instance, int id) {
        this.instance = instance;
        this.id = id;
    }

    public Object getInstance() {
        return instance;
    }

    public int getId() {
        return id;
    }

    public GuiData add(String key, SlotData slot) {
        switch (slot.type) {
            case IT_IN: itemInputs++; break;
            case IT_OUT: itemOutputs++; break;
            case FL_IN: fluidInputs++; break;
            case FL_OUT: fluidOutputs++; break;
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
        return add("any", new SlotData(type, x, y));
    }

    public GuiData add(Tier tier, SlotType type, int x, int y) {
        return add(tier.getName(), new SlotData(type, x, y));
    }

    public GuiData add(Machine type) {
        for (SlotData slot : type.getGui().getSlots()) {
            add("any", slot);
        }
        return this;
    }

    public boolean hasSlots() {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get("any");
        return slots != null && slots.size() > 0;
    }

    public boolean hasSlots(Tier tier) {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getName());
        return slots != null && slots.size() > 0;
    }

    public ArrayList<SlotData> getSlots() {
        return SLOT_LOOKUP.get("any");
    }

    public ArrayList<SlotData> getSlots(Tier tier) {
        return SLOT_LOOKUP.get(tier.getName());
    }

    public int getItemInputs() {
        return itemInputs;
    }

    public int getItemOutputs() {
        return itemOutputs;
    }

    public int getFluidInputs() {
        return fluidInputs;
    }

    public int getFluidOutputs() {
        return fluidOutputs;
    }
}
