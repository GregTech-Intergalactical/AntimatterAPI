package muramasa.antimatter.gui.slot;


import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.Tier;

import java.util.List;
import java.util.Map;

public interface ISlotProvider<T extends ISlotProvider<T>> {
    Map<String, Object2IntOpenHashMap<SlotType<?>>> getCountLookup();

    Map<String, List<SlotData<?>>> getSlotLookup();

    /**
     * Adds a slot for ANY
     **/
    default T add(SlotType<?> type, int x, int y) {
        return add("", new SlotData<>(type, x, y));
    }

    /**
     * Adds a slot for the given Tier
     **/
    default T add(Tier tier, SlotType<?> type, int x, int y) {
        return add(tier.getId(), new SlotData<>(type, x, y));
    }

    /**
     * Copies ALL slots from an existing Machine
     **/
    default T add(ISlotProvider<?> provider) {
        List<SlotData<?>> list = provider.getAnySlots();
        for (SlotData<?> slot : list) {
            add("", slot);
        }
        return (T) this;
    }

    /**
     * Copies ALL slots from type into toTier slots
     **/
    default T add(Tier toTier, ISlotProvider<?> provider) {
        List<SlotData<?>> list = provider.getAnySlots();
        for (SlotData<?> slot : list) {
            add(toTier.getId(), slot);
        }
        return (T) this;
    }

    /**
     * Copies fromTier slots from type into toTier slots
     **/
    default T add(Tier toTier, ISlotProvider<?> type, Tier fromTier) {
        List<SlotData<?>> list = type.getSlots(fromTier);
        for (SlotData<?> slot : list) {
            add(toTier.getId(), slot);
        }
        return (T) this;
    }

    default T add(String key, SlotData<?> slot) {
        Tier tier = AntimatterAPI.get(Tier.class, key);
        //if (tier != null && tier.getVoltage() > h.getVoltage()) highestTier = tier;
        Map<String, Object2IntOpenHashMap<SlotType<?>>> count = getCountLookup();
        Map<String, List<SlotData<?>>> slotLookup = getSlotLookup();
        if (!count.containsKey(key)) count.put(key, new Object2IntOpenHashMap<>());

        count.get(key).addTo(slot.getType(), 1);
        if (slotLookup.containsKey(key)) {
            slotLookup.get(key).add(slot);
        } else {
            List<SlotData<?>> list = new ObjectArrayList<>();
            list.add(slot);
            slotLookup.put(key, list);
        }
        return (T) this;
    }

    default boolean hasType(SlotType<?> type) {
        return getCount(null, type) > 0;
    }

    //TODO broken
    default int getCount(Tier tier, SlotType<?> type) {
        String id = tier == null || !getCountLookup().containsKey(tier.getId()) ? "" : tier.getId();
        return getCountLookup().get(id).getInt(type);
    }

    default boolean hasSlots() {
        List<SlotData<?>> slots = getSlotLookup().get("");
        return slots != null && !slots.isEmpty();
    }

    default boolean hasSlots(Tier tier) {
        List<SlotData<?>> slots = getSlotLookup().get(tier.getId());
        return slots != null && !slots.isEmpty();
    }

    default List<SlotData<?>> getAnySlots() {
        return getSlotLookup().get("");
    }

    default List<SlotData<?>> getSlots(Tier tier) {
        List<SlotData<?>> slots = getSlotLookup().get(tier.getId());
        if (slots == null) slots = getSlotLookup().get("");
        return slots != null ? slots : new ObjectArrayList<>();
    }

    default List<SlotData<?>> getSlots(SlotType<?> type, Tier tier) {
        List<SlotData<?>> types = new ObjectArrayList<>();
        List<SlotData<?>> slots = getSlotLookup().get(tier.getId());
        if (slots == null) slots = getSlotLookup().get("");
        if (slots == null) return types; //No slots found
        for (SlotData<?> slot : slots) {
            if (slot.getType() == type) types.add(slot);
        }
        return types;
    }

    default List<SlotData<?>> getSlots(SlotType<?> type) {
        List<SlotData<?>> types = new ObjectArrayList<>();
        List<SlotData<?>> slots = getSlotLookup().get("");
        if (slots == null) return types; //No slots found
        for (SlotData<?> slot : slots) {
            if (slot.getType() == type) types.add(slot);
        }
        return types;
    }

    static ISlotProvider<?> DEFAULT() {
        return new Provider();
    }

    class Provider implements ISlotProvider<Provider> {

        protected Provider() {
        }

        Map<String, Object2IntOpenHashMap<SlotType<?>>> count = new Object2ObjectOpenHashMap<>();
        Map<String, List<SlotData<?>>> slot = new Object2ObjectOpenHashMap<>();

        @Override
        public Map<String, Object2IntOpenHashMap<SlotType<?>>> getCountLookup() {
            return count;
        }

        @Override
        public Map<String, List<SlotData<?>>> getSlotLookup() {
            return slot;
        }
    }
}
