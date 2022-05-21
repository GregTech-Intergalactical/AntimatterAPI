package muramasa.antimatter.recipe;

public class RecipeTag implements IAntimatterObject {

    public static RecipeTag IGNORE_NBT = new RecipeTag("ignore_nbt");

    private String id;

    public RecipeTag(String id) {
        this.id = id;
        AntimatterAPI.register(RecipeTag.class, this);
    }

    @Override
    public String getId() {
        return id;
    }
}
