package muramasa.gtu.api.recipe;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;

public class RecipeTag implements IGregTechObject {

    public static RecipeTag IGNORE_NBT = new RecipeTag("ignore_nbt");

    private String id;

    public RecipeTag(String id) {
        this.id = id;
        GregTechAPI.register(RecipeTag.class, this);
    }

    @Override
    public String getId() {
        return id;
    }
}
