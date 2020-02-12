package muramasa.antimatter.advancement.trigger;

import net.minecraft.advancements.CriteriaTriggers;

public class AntimatterTriggers {

    public static TagSensitiveInventoryChangedTrigger TAG_SENSITIVE_INVENTORY_CHANGED_TRIGGER;

    public static void init() {
        TAG_SENSITIVE_INVENTORY_CHANGED_TRIGGER = CriteriaTriggers.register(new TagSensitiveInventoryChangedTrigger());
    }

}
