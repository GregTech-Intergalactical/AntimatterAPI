package muramasa.antimatter.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public abstract class PublicBlockStateProvider extends BlockStateProvider {

    public PublicBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    /** BlockStateProvider **/


    /** Model Provider **/
//    public BlockModelBuilder getBuilder(String path) {
//        return super.getBuilder(path);
//    }
//
//    public BlockModelBuilder withExistingParent(String name, String parent) {
//        return super.withExistingParent(name, parent);
//    }
//
//    public BlockModelBuilder withExistingParent(String name, ResourceLocation parent) {
//        return super.withExistingParent(name, parent);
//    }
//
//    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
//        return super.getExistingFile(path);
//    }
}
