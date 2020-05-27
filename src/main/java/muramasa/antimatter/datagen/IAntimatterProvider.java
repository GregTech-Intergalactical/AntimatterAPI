package muramasa.antimatter.datagen;

import muramasa.antimatter.datagen.resources.ResourceMethod;
import net.minecraft.data.IDataProvider;

public interface IAntimatterProvider extends IDataProvider {

    void run(ResourceMethod method);

}
