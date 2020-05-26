package muramasa.antimatter.datagen.providers.forge;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ForgeDummyTagProviders {

    public static final IDataProvider[] DUMMY_FORGE_PROVIDERS = { new ForgeItemTagProviderOverride(), new ForgeBlockTagProviderOverride() };

    public static class ForgeItemTagProviderOverride extends ForgeItemTagsProvider {

        public ForgeItemTagProviderOverride() {
            super(Ref.BACKGROUND_DATA_GENERATOR);
        }

        @Override
        public void act(DirectoryCache cache) {
            this.tagToBuilder.clear();
            this.registerTags();
            TagCollection<Item> collection = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
            Map<ResourceLocation, Tag.Builder<Item>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(m -> m.getKey().getId(), Map.Entry::getValue));
            collection.registerAll(map);
            this.setCollection(collection);
        }

    }

    public static class ForgeBlockTagProviderOverride extends ForgeBlockTagsProvider {

        public ForgeBlockTagProviderOverride() {
            super(Ref.BACKGROUND_DATA_GENERATOR);
        }

        @Override
        public void act(DirectoryCache cache) {
            this.tagToBuilder.clear();
            this.registerTags();
            TagCollection<Block> collection = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
            Map<ResourceLocation, Tag.Builder<Block>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(m -> m.getKey().getId(), Map.Entry::getValue));
            collection.registerAll(map);
            this.setCollection(collection);
        }

    }

}
