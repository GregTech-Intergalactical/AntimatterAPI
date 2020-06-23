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
}
