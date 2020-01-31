package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;

public class AntimatterItemModelProvider extends ItemModelProvider {

    protected String providerDomain, providerName;

    public AntimatterItemModelProvider(String providerDomain, String providerName, DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, providerDomain, exFileHelper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    public AntimatterItemModelProvider(String providerDomain, String providerName, DataGenerator gen, String... domains) {
        this(providerDomain, providerName, gen, new ExistingFileHelperOverride(domains).addDomains(providerDomain));
    }

    @Override
    public String getName() {
        return providerName;
    }

    @Override
    protected void registerModels() {
        processItemModels(providerDomain);
    }

    public void processItemModels(String domain) {
        if (AntimatterAPI.RESOURCE_METHOD != ResourceMethod.PROVIDER_GEN) return;
        AntimatterAPI.all(Item.class)
            .stream().filter(i -> i instanceof IModelProvider && i.getRegistryName().getNamespace().equals(domain))
            .forEach(i -> ((IModelProvider) i).onItemModelBuild(i, this));
        AntimatterAPI.all(Block.class)
            .stream().filter(b -> b instanceof IModelProvider && b.getRegistryName().getNamespace().equals(domain))
            .forEach(b -> ((IModelProvider) b).onItemModelBuild(b, this));
    }

    public ItemModelBuilder getBuilder(IItemProvider item) {
        return getBuilder(item.asItem().getRegistryName().getPath());
    }

    public ItemModelBuilder tex(IItemProvider item, ResourceLocation... textures) {
        ItemModelBuilder builder = getBuilder(item);
        builder.parent(new UncheckedModelFile("item/generated"));
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
}
