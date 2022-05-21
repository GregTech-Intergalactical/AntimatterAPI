package muramasa.antimatter.item;

//TODO replace with vanilla tag system
public class ItemTag implements IAntimatterObject {

    public static ItemTag SHOW_EXTENDED_HIGHLIGHT = new ItemTag("show_extended_highlight");

    private final String id;

    public ItemTag(String id) {
        this.id = id;
        AntimatterAPI.register(ItemTag.class, this);
    }

    @Override
    public String getId() {
        return id;
    }
}
