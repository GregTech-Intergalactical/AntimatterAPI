package muramasa.antimatter.datagen.providers.dummy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DummyTagProviders {

    private static final BlockTagsProvider BLOCK = new ForgeBlockTagsProviderOverride();
    private static final ItemTagsProvider ITEM = new ForgeItemTagsProviderOverride();
    public static final IDataProvider[] DUMMY_PROVIDERS = { BLOCK, ITEM };

    public static class ForgeItemTagsProviderOverride extends ForgeItemTagsProvider {

        public ForgeItemTagsProviderOverride() {
            super(Ref.BACKGROUND_GEN, BLOCK, new ExistingFileHelperOverride());
        }

        @Override
        public void act(DirectoryCache cache) {
            Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.tagToBuilder);
            this.tagToBuilder.clear();
            registerTags();
            tagToBuilder.forEach((a,bb) -> ItemTags.makeWrapperTag(a.toString()));
            b.forEach(tagToBuilder::put);

        }

    }

    public static class ForgeBlockTagsProviderOverride extends ForgeBlockTagsProvider {

        public ForgeBlockTagsProviderOverride() {
            super(Ref.BACKGROUND_GEN, new ExistingFileHelperOverride());
        }

        @Override
        public void act(DirectoryCache cache) {
            Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.tagToBuilder);
            this.tagToBuilder.clear();
            registerTags();
            tagToBuilder.forEach((a,bb) -> BlockTags.makeWrapperTag(a.toString()));
            b.forEach(tagToBuilder::put);
        }
    }
}
