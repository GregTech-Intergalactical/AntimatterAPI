package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.io.IOException;

public class AntimatterItemModelProvider extends AntimatterModelProvider<AntimatterItemModelBuilder> implements IAntimatterProvider {

    protected final String providerName;

    public AntimatterItemModelProvider(String providerDomain, String providerName) {
        super(providerDomain, ITEM_FOLDER, AntimatterItemModelBuilder::new);
        this.providerName = providerName;
    }

    @Override
    public void run(HashCache cache) throws IOException {

    }

    @Override
    public String getName() {
        return providerName;
    }

    @Override
    public void run() {
        registerModels();
    }

    @Override
    public void onCompletion() {
        buildAll();
    }

    protected void registerModels() {
        processItemModels(modid);
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

    public AntimatterItemModelBuilder getBuilder(ItemLike item) {
        return getBuilder(AntimatterPlatformUtils.getIdFromItem(item.asItem()).getPath());
    }

    public AntimatterItemModelBuilder tex(ItemLike item, ResourceLocation... textures) {
        return tex(item, "minecraft:item/generated", textures);
    }

    public AntimatterItemModelBuilder tex(ItemLike item, String parent, ResourceLocation... textures) {
        AntimatterItemModelBuilder builder = getBuilder(item);
        builder.parent(new ResourceLocation(parent));
        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }

    public AntimatterItemModelBuilder blockItem(Block block) {
        return blockItem(block.asItem());
    }

    public AntimatterItemModelBuilder blockItem(ItemLike item) {
        return withParent(AntimatterPlatformUtils.getIdFromItem(item.asItem()).getPath(), modLoc("block/" + AntimatterPlatformUtils.getIdFromItem(item.asItem()).getPath()));
    }

    public ResourceLocation existing(String domain, String path) {
        return new ResourceLocation(domain, path);
    }

    public AntimatterItemModelBuilder getAntimatterBuilder(ItemLike item) {
        return getBuilder(AntimatterPlatformUtils.getIdFromItem(item.asItem()).getPath());
    }

    public AntimatterItemModelBuilder modelAndTexture(ItemLike item, String namespace, String path) {
        return getAntimatterBuilder(item).parent(new ResourceLocation(namespace, path));
    }

    public AntimatterItemModelBuilder modelAndTexture(ItemLike item, String resource) {
        return getAntimatterBuilder(item).parent(new ResourceLocation(resource));
    }
}
