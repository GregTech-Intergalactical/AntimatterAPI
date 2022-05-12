package muramasa.antimatter.datagen;

import net.minecraft.data.DataProvider;

public interface IAntimatterProvider extends DataProvider {

    // Only runs when dynamically generating assets/data
    void run();

    default boolean async() {
        return true;
    }

    default void onCompletion() {

    }
}
