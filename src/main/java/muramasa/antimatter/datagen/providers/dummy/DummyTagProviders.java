package muramasa.antimatter.datagen.providers.dummy;

import muramasa.antimatter.Ref;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.tags.TagCollection;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DummyTagProviders {

    public static final IDataProvider[] DUMMY_PROVIDERS = { new ForgeItemTagsProviderOverride(), new ForgeBlockTagsProviderOverride() };

    public static class ForgeItemTagsProviderOverride extends ForgeItemTagsProvider {

        public ForgeItemTagsProviderOverride() {
            super(Ref.BACKGROUND_GEN);
        }

        @Override
        public void act(DirectoryCache cache) {
            this.tagToBuilder.clear();
            this.registerTags();
            TagCollection<Item> collection = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
            collection.registerAll(this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(m -> m.getKey().getId(), Map.Entry::getValue)));
            this.setCollection(collection);
        }

    }

    public static class ForgeBlockTagsProviderOverride extends ForgeBlockTagsProvider {

        public ForgeBlockTagsProviderOverride() {
            super(Ref.BACKGROUND_GEN);
        }

        @Override
        public void act(DirectoryCache cache) {
            this.tagToBuilder.clear();
            this.registerTags();
            TagCollection<Block> collection = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
            collection.registerAll(this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(m -> m.getKey().getId(), Map.Entry::getValue)));
            this.setCollection(collection);
        }

    }

}
