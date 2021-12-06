package muramasa.antimatter.datagen.providers.dummy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import java.util.HashMap;
import java.util.Map;

public class DummyTagProviders {
/*
    private static final BlockTagsProvider BLOCK = new ForgeBlockTagsProviderOverride();
    private static final ItemTagsProvider ITEM = new ForgeItemTagsProviderOverride();
    public static final DataProvider[] DUMMY_PROVIDERS = {BLOCK, ITEM};

    public static class ForgeItemTagsProviderOverride extends ItemTagsProvider {

        public ForgeItemTagsProviderOverride() {
            super(Ref.BACKGROUND_GEN, BLOCK, new ExistingFileHelperOverride());
        }

        @Override
        public void run(HashCache cache) {
            Map<ResourceLocation, Tag.Builder> b = new HashMap<>(this.builders);
            this.builders.clear();
            addTags();
            builders.forEach((a, bb) -> ItemTags.bind(a.toString()));
            b.forEach(builders::put);

        }

    }

    public static class ForgeBlockTagsProviderOverride extends ForgeBlockTagsProvider {

        public ForgeBlockTagsProviderOverride() {
            super(Ref.BACKGROUND_GEN, new ExistingFileHelperOverride());
        }

        @Override
        public void run(HashCache cache) {
            Map<ResourceLocation, Tag.Builder> b = new HashMap<>(this.builders);
            this.builders.clear();
            addTags();
            builders.forEach((a, bb) -> BlockTags.bind(a.toString()));
            b.forEach(builders::put);
        }
    }*/
}
