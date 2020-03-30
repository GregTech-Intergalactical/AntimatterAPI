package muramasa.antimatter.recipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;

public class RecipeTag implements IAntimatterObject {

    public static RecipeTag IGNORE_NBT = new RecipeTag("ignore_nbt");

    private String id;

    public RecipeTag(String id) {
        this.id = id;
        AntimatterAPI.register(RecipeTag.class, id, this);
    }

    @Override
    public String getId() {
        return id;
    }
}
