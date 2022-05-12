package muramasa.antimatter.datagen.providers.dummy;

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
