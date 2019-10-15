package muramasa.gtu.api.items;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;

//TODO replace with vanilla tag system
public class ItemTag implements IGregTechObject {

    public static ItemTag SHOW_EXTENDED_HIGHLIGHT = new ItemTag("show_extended_highlight");

    private String id;

    public ItemTag(String id) {
        this.id = id;
        GregTechAPI.register(ItemTag.class, this);
    }

    @Override
    public String getId() {
        return id;
    }
}
