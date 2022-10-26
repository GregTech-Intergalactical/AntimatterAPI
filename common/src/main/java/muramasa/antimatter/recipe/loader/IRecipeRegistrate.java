package muramasa.antimatter.recipe.loader;

public interface IRecipeRegistrate {

    void add(String domain, String id, IRecipeLoader load);

    interface IRecipeLoader {
        void init();
    }

}

