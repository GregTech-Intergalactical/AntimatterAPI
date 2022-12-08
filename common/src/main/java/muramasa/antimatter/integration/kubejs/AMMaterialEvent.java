package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.resources.ResourceLocation;

public class AMMaterialEvent extends EventJS {
    final MaterialEvent event;
    public AMMaterialEvent(MaterialEvent event){
        this.event = event;
    }

    public MaterialEvent getEvent() {
        return event;
    }

    public MaterialType type(String type) {
        return AntimatterAPI.get(MaterialType.class, type);
    }

    public void setReplacement(String material, String item, MaterialType type){
        Material material1 = Material.get(material);
        if (material1 == Material.NULL){
            return;
        }
        if (!AntimatterPlatformUtils.itemExists(new ResourceLocation(item))){
            return;
        }
        type.replacement(material1, () -> AntimatterPlatformUtils.getItemFromID(new ResourceLocation(item)));
    }
}
