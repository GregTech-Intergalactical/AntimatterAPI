package muramasa.antimatter.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class TagUtils {
    //A list of all registered tags for all Antimatter mods.
    private static final Map<Class, Map<ResourceLocation, ITag.INamedTag>> TAG_MAP = new Object2ObjectOpenHashMap<>();
    /**
     * Redirects an ItemTag to a BlockTag
     * @param tag a ItemTag, preferably already created
     * @return BlockTag variant of the ItemTag
     */
    public static ITag.INamedTag<Block> itemToBlockTag(ITag.INamedTag<Item> tag) {
        return createTag(tag.getName(), Block.class, BlockTags::makeWrapperTag);
    }

    /**
     * Redirects an BlockTag to a ItemTag
     * @param tag a BlockTag, preferably already created
     *             This is NOT safe to use for recipes outside of Antimatter recipe builders,
     *             call nc() to get content. (NamedToContent)
     * @return ItemTag variant of the BlockTag
     */
    public static ITag.INamedTag<Item> blockToItemTag(ITag.INamedTag<Block> tag) {
        return createTag(tag.getName(), Item.class, ItemTags::makeWrapperTag);
    }

    /**
     * @param loc ResourceLocation of a BlockTag, can be new or old
     * @return BlockTag
     */
    public static ITag.INamedTag<Block> getBlockTag(ResourceLocation loc) {
        return createTag(loc, Block.class, BlockTags::makeWrapperTag);
    }

    public static Map<ResourceLocation, ITag.INamedTag> getTags(Class clazz) {
        return TAG_MAP.getOrDefault(clazz, Collections.emptyMap());
    }

    /**
     * @param name name of a BlockTag, can be new or old, has the namespace "forge" attached
     *
     * @return BlockTag
     */
    public static ITag.INamedTag<Block> getForgeBlockTag(String name) {
        return getBlockTag(new ResourceLocation("forge", name));
    }

    /**
     * @param loc ResourceLocation of a ItemTag, can be new or old
     *             This is NOT safe to use for recipes outside of Antimatter recipe builders,
     *             call nc() to get content. (NamedToContent)
     * @return ItemTag
     */
    public static ITag.INamedTag<Item> getItemTag(ResourceLocation loc) {
        return createTag(loc, Item.class, ItemTags::makeWrapperTag);
    }

    /**
     * @param name name of a ItemTag, can be new or old, has the namespace "forge" attached.
     *             This is NOT safe to use for recipes outside of Antimatter recipe builders,
     *             call nc() to get content. (NamedToContent)
     * @return ItemTag
     */
    public static ITag.INamedTag<Item> getForgeItemTag(String name) {
        // TODO: Change "wood" -> "wooden", forge recognises "wooden"
        return getItemTag(new ResourceLocation("forge", name));
    }

    /**
     * @param name name of a FluidTag, can be new or old, has the namespace "forge" attached
     * @return FluidTag
     */
    public static ITag.INamedTag<Fluid> getForgeFluidTag(String name) {
        return createTag(new ResourceLocation("forge", name), Fluid.class, FluidTags::makeWrapperTag);
    }

    /**
     * NamedToContent
     * In order to use a named tag in recipes outside antimatter(e.g. for furnace recipes)
     * you have to convert the tag into a safe one, this method returns a safe tag.
     * @param tag
     * @return
     */
    public static ITag<Item> nc(ITag.INamedTag<Item> tag) {
        return TagCollectionManager.getManager().getItemTags().get(tag.getName());
    }

    protected static <T> ITag.INamedTag<T> createTag(ResourceLocation loc, Class<T> clazz, Function<String, ITag.INamedTag<T>> fn) {
        ITag.INamedTag<T>[] tag = new ITag.INamedTag[1];
        TAG_MAP.compute(clazz, (k,v) -> {
            if (v == null) v = new Object2ObjectOpenHashMap<>();
            tag[0] = v.computeIfAbsent(loc, a -> fn.apply(loc.toString()));
            return v;
        });
        return tag[0];
    }
}
