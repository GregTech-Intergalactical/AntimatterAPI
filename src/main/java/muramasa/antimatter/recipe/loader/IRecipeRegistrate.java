package muramasa.antimatter.recipe.loader;

public interface IRecipeRegistrate {

    void add(IRecipeLoader load);

    interface IRecipeLoader {
        void init();
    }

}

