package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.function.Function;

public class AntimatterItemModelProvider extends ItemModelProvider implements IAntimatterProvider {

    protected final String providerDomain, providerName;

    public AntimatterItemModelProvider(String providerDomain, String providerName, DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, providerDomain, exFileHelper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        Function<ResourceLocation, ItemModelBuilder> factoryOverride = loc -> new AntimatterItemModelBuilder(loc, exFileHelper);
        ObfuscationReflectionHelper.setPrivateValue(ModelProvider.class, this, factoryOverride, "factory");
    }

    public AntimatterItemModelProvider(String providerDomain, String providerName, DataGenerator gen, String... domains) {
        this(providerDomain, providerName, gen, new ExistingFileHelperOverride(domains).addDomains(providerDomain));
    }

    @Override
    public String getName() {
        return providerName;
    }

    @Override
    public void run() {
        registerModels();
        generatedModels.forEach(DynamicResourcePack::addItem);
    }

    @Override
    public Dist getSide() {
        return Dist.CLIENT;
    }


    @Override
    protected void registerModels() {
        processItemModels(providerDomain);
    }

    public void processItemModels(String domain) {
        AntimatterAPI.all(Item.class, domain).forEach(i -> AntimatterModelManager.onItemModelBuild(i, this));
        AntimatterAPI.all(Block.class, domain).forEach(b -> AntimatterModelManager.onItemModelBuild(b, this));
        AntimatterAPI.all(IAntimatterTool.class, domain).forEach(t -> tex(t.getItem(), "item/handheld", t.getTextures()));
        AntimatterAPI.all(IAntimatterArmor.class, domain).forEach(t -> tex(t.getItem(), "item/handheld", t.getTextures()));
        AntimatterAPI.all(AntimatterFluid.class, domain).forEach(f -> {
            modelAndTexture(f.getContainerItem(), "forge", "item/bucket").bucketProperties(f.getFluid());
            modelAndTexture(f.getFluidBlock(), AntimatterBlockModelBuilder.getSimple()).tex(a -> a.put("all", f.getAttributes().getFlowingTexture().toString()));
        });
    }

    public ItemModelBuilder getBuilder(IItemProvider item) {
        return getBuilder(item.asItem().getRegistryName().getPath());
    }

    public ItemModelBuilder tex(IItemProvider item, ResourceLocation... textures) {
        return tex(item, "minecraft:item/generated", textures);
    }

    public ItemModelBuilder tex(IItemProvider item, String parent, ResourceLocation... textures) {
        ItemModelBuilder builder = getBuilder(item);
        builder.parent(new ModelFile.UncheckedModelFile(new ResourceLocation(parent)));
        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }

    public ItemModelBuilder blockItem(Block block) {
        return blockItem(block.asItem());
    }

    public ItemModelBuilder blockItem(IItemProvider item) {
        return withExistingParent(item.asItem().getRegistryName().getPath(), modLoc("block/" + item.asItem().getRegistryName().getPath()));
    }

    public ModelFile.ExistingModelFile existing(String domain, String path) {
        return getExistingFile(new ResourceLocation(domain, path));
    }

    public AntimatterItemModelBuilder getAntimatterBuilder(IItemProvider item) {
        return (AntimatterItemModelBuilder) getBuilder(item.asItem().getRegistryName().getPath());
    }

    public AntimatterItemModelBuilder modelAndTexture(IItemProvider item, String namespace, String path) {
        return (AntimatterItemModelBuilder) getAntimatterBuilder(item).parent(new ModelFile.UncheckedModelFile(new ResourceLocation(namespace, path)));
    }

    public AntimatterItemModelBuilder modelAndTexture(IItemProvider item, String resource) {
        return (AntimatterItemModelBuilder) getAntimatterBuilder(item).parent(new ModelFile.UncheckedModelFile(new ResourceLocation(resource)));
    }
}
