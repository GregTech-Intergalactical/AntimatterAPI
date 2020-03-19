package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.IAntimatterProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;

public class AntimatterItemModelProvider extends ItemModelProvider implements IAntimatterProvider {

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

    @Override
    public void run() {
        registerModels();
    }

    public void processItemModels(String domain) {
        AntimatterAPI.all(Item.class)
            .stream().filter(i -> i.getRegistryName().getNamespace().equals(domain))
            .forEach(i -> AntimatterModelManager.onItemModelBuild(i, this));
        AntimatterAPI.all(Block.class)
            .stream().filter(b -> b.getRegistryName().getNamespace().equals(domain))
            .forEach(b -> AntimatterModelManager.onItemModelBuild(b, this));
    }

    public ItemModelBuilder getBuilder(IItemProvider item) {
        return getBuilder(item.asItem().getRegistryName().getPath());
    }

    public ItemModelBuilder tex(IItemProvider item, ResourceLocation... textures) {
        return tex(item, "minecraft:item/generated", textures);
    }

    public ItemModelBuilder tex(IItemProvider item, String parent, ResourceLocation... textures) {
        ItemModelBuilder builder = getBuilder(item);
        builder.parent(new UncheckedModelFile(new ResourceLocation(parent)));
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
}
