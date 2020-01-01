package muramasa.gtu.data.resources;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

import java.util.Collections;

public class GTModelBuilder {

    public static ExistingFileHelper EXISTING_FILE_HELPER = new ExistingFileHelper(Collections.emptyList(), false);

    public static ItemModelBuilder getItemBuilder() {
        return new ItemModelBuilder(new ResourceLocation("dummy"), EXISTING_FILE_HELPER);
    }
}
