package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

import javax.annotation.Nonnull;
import java.util.Map;

public class AntimatterBlockStateProvider extends BlockStateProvider implements IAntimatterProvider {

    protected String providerDomain, providerName;
    protected AntimatterBlockModelProvider blockModelProvider;

    public AntimatterBlockStateProvider(String providerDomain, String providerName, DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, providerDomain, exFileHelper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.blockModelProvider = new AntimatterBlockModelProvider(gen, providerDomain, exFileHelper) {
            @Override
            protected void registerModels() {
                //NOOP
            }

            @Override
            public String getName() {
                return AntimatterBlockStateProvider.this.getName();
            }
        };
    }

    public AntimatterBlockStateProvider(String providerDomain, String providerName, DataGenerator gen, String... excludedDomains) {
        this(providerDomain, providerName, gen, new ExistingFileHelperOverride(excludedDomains).addDomains(providerDomain));
    }

    @Nonnull
    @Override
    public String getName() {
        return providerName;
    }

    @Override
    protected void registerStatesAndModels() {
        processBlocks(providerDomain);
    }

    @Override
    public void run() {
        registerStatesAndModels();
    }

    @Override
    public BlockModelProvider models() {
        return blockModelProvider;
    }

    public Map<Block, IGeneratedBlockstate> getRegisteredBlocks() {
        return registeredBlocks;
    }

    public void processBlocks(String domain) {
        AntimatterAPI.all(Block.class)
            .stream().filter(b -> b.getRegistryName().getNamespace().equals(domain))
            .forEach(b -> AntimatterModelManager.onBlockModelBuild(b, this));
    }

    public AntimatterBlockModelBuilder getBuilder(Block block) {
        return (AntimatterBlockModelBuilder) models().getBuilder(block.getRegistryName().getPath());
    }

    public BlockModelBuilder cubeAll(Block block, ResourceLocation texture) {
        return models().cubeAll(block.getRegistryName().toString(), texture);
    }

    public void state(Block block, ModelFile model) {
        simpleBlock(block, model);
    }

    public void state(Block block, ResourceLocation... textures) {
        if (textures.length == 1) {
            simpleBlock(block, getSimpleModel(block, textures[0]));
        } else if (textures.length == 2) {
            simpleBlock(block, getLayeredModel(block, textures[0], textures[1]));
        }
    }

    public BlockModelBuilder getSimpleModel(Block block, ResourceLocation texture) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/simple"))).texture("all", texture);
    }

    public BlockModelBuilder getLayeredModel(Block block, ResourceLocation base, ResourceLocation overlay) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/layered"))).texture("base", base).texture("overlay", overlay);
    }

    public ModelFile.ExistingModelFile existing(String domain, String path) {
        return models().getExistingFile(loc(domain, path));
    }

    public ResourceLocation loc(String domain, String path) {
        return new ResourceLocation(domain, path);
    }
}
