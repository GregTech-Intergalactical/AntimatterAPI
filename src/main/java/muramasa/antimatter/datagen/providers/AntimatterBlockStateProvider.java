package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import javax.annotation.Nonnull;

public class AntimatterBlockStateProvider extends BlockStateProvider {

    protected String providerNamespace, providerName;

    public AntimatterBlockStateProvider(String providerNamespace, String providerName, DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, providerNamespace, exFileHelper);
        this.providerNamespace = providerNamespace;
        this.providerName = providerName;
    }

    @Nonnull
    @Override
    public String getName() {
        return providerName;
    }

    @Override
    protected void registerStatesAndModels() {
        processBlocks(providerNamespace);
    }

    public void processBlocks(String namespace) {
        AntimatterAPI.all(Block.class)
            .stream().filter(b -> b instanceof IModelProvider && b.getRegistryName().getNamespace().equals(namespace))
            .forEach(b -> ((IModelProvider) b).onBlockModelBuild(b, this));
    }

    public BlockModelBuilder getBuilder(Block block) {
        return models().getBuilder(block.getRegistryName().getPath());
    }

    public BlockModelBuilder cubeAll(Block block, ResourceLocation texture) {
        return models().cubeAll(block.getRegistryName().toString(), texture);
    }

    public void simpleState(Block block, ResourceLocation texture) {
        simpleBlock(block, getSimpleModel(block, texture));
    }

    public void layeredState(Block block, ResourceLocation base, ResourceLocation overlay) {
        simpleBlock(block, getLayeredModel(block, base, overlay));
    }

    public void texturedState(Block block, ResourceLocation[] textures) {
        if (textures.length == 1) {
            simpleState(block, textures[0]);
        } else if (textures.length == 2) {
            layeredState(block, textures[0], textures[1]);
        }
    }

    public BlockModelBuilder getSimpleModel(Block block, ResourceLocation texture) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/simple"))).texture("all", texture);
    }

    public BlockModelBuilder getLayeredModel(Block block, ResourceLocation base, ResourceLocation overlay) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/layered"))).texture("base", base).texture("overlay", overlay);
    }

    public ResourceLocation loc(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
}
