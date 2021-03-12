package muramasa.antimatter.datagen;

import net.minecraft.data.IDataProvider;
import net.minecraftforge.api.distmarker.Dist;

public interface IAntimatterProvider extends IDataProvider {

    // Only runs when dynamically generating assets/data
    void run();

    /**
     * Return {@link Dist#CLIENT} for providers that should only run on clients
     * Return {@link Dist#DEDICATED_SERVER} for providers that should only run on servers
     */
    Dist getSide();

    /**
     * @return Whether to only run this provider during dynamic data generation.
     */
    default Types staticDynamic() {
        return Types.STATIC_AND_DYNAMIC;
    }

    enum Types {
        DYNAMIC,
        STATIC,
        STATIC_AND_DYNAMIC;

        public boolean isStatic() {
            return this == STATIC || this == STATIC_AND_DYNAMIC;
        }

        public boolean isDynamic() {
            return this == STATIC_AND_DYNAMIC || this == DYNAMIC;
        }
    }

    default void onCompletion() {

    }
}
