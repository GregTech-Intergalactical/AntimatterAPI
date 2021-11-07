package muramasa.antimatter.datagen;

import net.minecraft.data.IDataProvider;
import net.minecraftforge.api.distmarker.Dist;

public interface IAntimatterProvider extends IDataProvider {

    // Only runs when dynamically generating assets/data
    void run();

    default boolean async() {
        return true;
    }

    default void onCompletion() {

    }
}
