package muramasa.antimatter.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TagUtils {

    //Initialized in onResourceReload.
    //private static TagContainer TAG_GETTER;

    //A list of all registered tags for all Antimatter mods.
    private static final Map<Class, Map<ResourceLocation, TagKey>> TAG_MAP = new Object2ObjectOpenHashMap<>();

    /**
     * Redirects an ItemTag to a BlockTag
     *
     * @param tag a ItemTag, preferably already created
     * @return BlockTag variant of the ItemTag
     */
    public static TagKey<Block> itemToBlockTag(TagKey<Item> tag) {
        return createTag(tag.location(), Block.class, BlockTags::create);
    }

    /**
     * Redirects an BlockTag to a ItemTag
     *
     * @param tag a BlockTag, preferably already created
     *            This is NOT safe to use for recipes outside of Antimatter recipe builders,
     *            call nc() to get content. (NamedToContent)
     * @return ItemTag variant of the BlockTag
     */
    public static TagKey<Item> blockToItemTag(TagKey<Block> tag) {
        return createTag(tag.location(), Item.class, ItemTags::create);
    }

    /**
     * @param loc ResourceLocation of a BlockTag, can be new or old
     * @return BlockTag
     */
    public static TagKey<Block> getBlockTag(ResourceLocation loc) {
        return createTag(loc, Block.class, BlockTags::create);
    }

    public static Map<ResourceLocation, TagKey> getTags(Class clazz) {
        return TAG_MAP.getOrDefault(clazz, Collections.emptyMap());
    }

    /**
     * @param name name of a BlockTag, can be new or old, has the namespace "forge" attached
     * @return BlockTag
     */
    public static TagKey<Block> getForgeBlockTag(String name) {
        return getBlockTag(new ResourceLocation("forge", name));
    }

    /**
     * @param loc ResourceLocation of a ItemTag, can be new or old
     *            This is NOT safe to use for recipes outside of Antimatter recipe builders,
     *            call nc() to get content. (NamedToContent)
     * @return ItemTag
     */
    public static TagKey<Item> getItemTag(ResourceLocation loc) {
        return createTag(loc, Item.class, ItemTags::create);
    }

    /**
     * @param name name of a ItemTag, can be new or old, has the namespace "forge" attached.
     *             This is NOT safe to use for recipes outside of Antimatter recipe builders,
     *             call nc() to get content. (NamedToContent)
     * @return ItemTag
     */
    public static TagKey<Item> getForgeItemTag(String name) {
        // TODO: Change "wood" -> "wooden", forge recognises "wooden"
        return getItemTag(new ResourceLocation("forge", name));
    }

    /**
     * @param name name of a FluidTag, can be new or old, has the namespace "forge" attached
     * @return FluidTag
     */
    public static TagKey<Fluid> getForgeFluidTag(String name) {
        return createTag(new ResourceLocation("forge", name), Fluid.class, FluidTags::create);
    }

    /**
     * @param name name of a FluidTag, can be new or old, has the namespace "forge" attached
     * @return FluidTag
     */
    public static TagKey<Fluid> getFluidTag(ResourceLocation name) {
        return createTag(name, Fluid.class, FluidTags::create);
    }

    /**
     * NamedToContent
     * In order to use a named tag in recipes outside antimatter(e.g. for furnace recipes)
     * you have to convert the tag into a safe one, this method returns a safe tag.
     *
     * @param tag
     * @return
     */
    public static Tag<Item> nc(TagKey<Item> tag) {
        List<Item> list = new ObjectArrayList<>();
         Registry.ITEM.getTagOrEmpty(tag).iterator().forEachRemaining(t ->list.add(t.value()));
         return new Tag<Item>(list);
    }

    /**
     * NamedToContent
     * In order to use a named tag in recipes outside antimatter(e.g. for furnace recipes)
     * you have to convert the tag into a safe one, this method returns a safe tag.
     *
     * @param tag
     * @return
     */
    public static TagKey<Item> nc(ResourceLocation tag) {
        return new TagKey<>(Registry.ITEM_REGISTRY, tag);
    }

    protected static <T> TagKey<T> createTag(ResourceLocation loc, Class<T> clazz, Function<ResourceLocation, TagKey<T>> fn) {
        TagKey<T>[] tag = new TagKey[1];
        synchronized (TAG_MAP) {
            TAG_MAP.compute(clazz, (k, v) -> {
                if (v == null) v = new Object2ObjectOpenHashMap<>();
                tag[0] = v.computeIfAbsent(loc, a -> fn.apply(loc));
                return v;
            });
        }

        return tag[0];
    }
}
