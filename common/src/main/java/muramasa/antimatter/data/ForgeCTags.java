package muramasa.antimatter.data;

import muramasa.antimatter.util.TagUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class ForgeCTags {
    public static final TagKey<Item> CROPS = tag("crops");
    public static final TagKey<Item> SEEDS = tag("seeds");
    public static final TagKey<Item> MUSHROOMS = tag("mushrooms");

    public static final TagKey<Item> GLASS = tag("glass");
    public static final TagKey<Item> LEATHER = tag("leather");
    public static final TagKey<Item> CHESTS = tag("chests");
    public static final TagKey<Item> CHESTS_WOODEN = tag("chests/wooden");
    public static final TagKey<Item> GLASS_PANES = tag("glass_panes");
    public static final TagKey<Item> RODS_WOODEN = tag("rods/wooden");
    public static final TagKey<Item> COBBLESTONE = tag("cobblestone");
    public static final TagKey<Item> STONE = tag("stone");
    public static final TagKey<Item> SAND = tag("sand");
    public static final TagKey<Item> GRAVEL = tag("gravel");
    public static final TagKey<Item> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
    public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");

    public static final TagKey<Item> DUSTS_REDSTONE = tag("dusts/redstone");
    public static final TagKey<Item> GEMS_QUARTZ_ALL = tag("gems/quartz_all");

    public static final TagKey<Item> DYES = tag("dyes");
    public static final TagKey<Item> DYES_BLACK = DyeColor.BLACK.getTag();
    public static final TagKey<Item> DYES_RED = DyeColor.RED.getTag();
    public static final TagKey<Item> DYES_GREEN = DyeColor.GREEN.getTag();
    public static final TagKey<Item> DYES_BROWN = DyeColor.BROWN.getTag();
    public static final TagKey<Item> DYES_BLUE = DyeColor.BLUE.getTag();
    public static final TagKey<Item> DYES_PURPLE = DyeColor.PURPLE.getTag();
    public static final TagKey<Item> DYES_CYAN = DyeColor.CYAN.getTag();
    public static final TagKey<Item> DYES_LIGHT_GRAY = DyeColor.LIGHT_GRAY.getTag();
    public static final TagKey<Item> DYES_GRAY = DyeColor.GRAY.getTag();
    public static final TagKey<Item> DYES_PINK = DyeColor.PINK.getTag();
    public static final TagKey<Item> DYES_LIME = DyeColor.LIME.getTag();
    public static final TagKey<Item> DYES_YELLOW = DyeColor.YELLOW.getTag();
    public static final TagKey<Item> DYES_LIGHT_BLUE = DyeColor.LIGHT_BLUE.getTag();
    public static final TagKey<Item> DYES_MAGENTA = DyeColor.MAGENTA.getTag();
    public static final TagKey<Item> DYES_ORANGE = DyeColor.ORANGE.getTag();
    public static final TagKey<Item> DYES_WHITE = DyeColor.WHITE.getTag();

    private static TagKey<Item> tag(String id){
        return TagUtils.getForgelikeItemTag(id);
    }

    public static void init(){
        AntimatterTags.init();
    }
}
