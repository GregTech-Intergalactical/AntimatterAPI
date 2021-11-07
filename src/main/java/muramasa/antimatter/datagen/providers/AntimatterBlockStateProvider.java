package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.fluid.AntimatterFluid;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.Map;

public class AntimatterBlockStateProvider extends BlockStateProvider implements IAntimatterProvider {

    protected final String providerDomain, providerName;
    protected final AntimatterBlockModelProvider blockModelProvider;

    public AntimatterBlockStateProvider(String providerDomain, String providerName, DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, providerDomain, exFileHelper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.blockModelProvider = new AntimatterBlockModelProvider(gen, providerDomain, providerName, exFileHelper);
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
    public void onCompletion() {
        models().generatedModels.forEach(DynamicResourcePack::addBlock);
        registeredBlocks.forEach((b, s) -> {
            if (b.getRegistryName() == null) {
                BlockBasic block = (BlockBasic) b;
                DynamicResourcePack.addState(new ResourceLocation(block.getDomain(), block.getId()), s);
            } else {
                DynamicResourcePack.addState(b.getRegistryName(), s);
            }
        });
    }

    @Override
    public BlockModelProvider models() {
        return blockModelProvider;
    }

    public Map<Block, IGeneratedBlockstate> getRegisteredBlocks() {
        return registeredBlocks;
    }

    public void processBlocks(String domain) {
        AntimatterAPI.all(Block.class, domain).forEach(b -> AntimatterModelManager.onBlockModelBuild(b, this));
        AntimatterAPI.all(AntimatterFluid.class, domain).forEach(f -> state(f.getFluidBlock(), getBuilder(f.getFluidBlock()).texture("particle", f.getFluid().getAttributes().getStillTexture())));
    }

    public AntimatterBlockModelBuilder getBuilder(Block block) {
        if (block.getRegistryName() == null) {
            return (AntimatterBlockModelBuilder) models().getBuilder(((BlockBasic) block).getId());
        }
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
        } else if (textures.length == 6) {
            horizontalBlock(block, getSimpleModel(block, textures));
        } else if (textures.length == 12) {
            horizontalBlock(block, getLayeredModel(block, textures));
        }
    }

    public BlockModelBuilder getSimpleModel(Block block, ResourceLocation texture) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/simple"))).texture("all", texture);
    }

    public BlockModelBuilder getSimpleModel(Block block, ResourceLocation... texture) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/simple"))).texture("down", texture[0]).texture("up", texture[1]).texture("south", texture[2]).texture("north", texture[3]).texture("west", texture[4]).texture("east", texture[5]).texture("particle", texture[1]);
    }

    public BlockModelBuilder getLayeredModel(Block block, ResourceLocation... texture) {
        return getBuilder(block).parent(models().getExistingFile(loc(Ref.ID, "block/preset/layered"))).texture("basedown", texture[0]).texture("baseup", texture[1]).texture("basesouth", texture[2]).texture("basenorth", texture[3]).texture("basewest", texture[4]).texture("baseeast", texture[5]).texture("overlaydown", texture[6]).texture("overlayup", texture[7]).texture("overlaysouth", texture[8]).texture("overlaynorth", texture[9]).texture("overlaywest", texture[10]).texture("overlayeast", texture[11]).texture("particle", texture[1]);
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
