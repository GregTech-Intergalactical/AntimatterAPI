package muramasa.antimatter.recipe;

import net.minecraft.item.Item;
import net.minecraft.tags.Tag;

public class TagInput {
    public final Tag<Item> tag;
    public final int count;

    public TagInput(Tag<Item> tag, int count) {
        this.tag = tag;
        this.count = count;
    }

    public static TagInput of(Tag<Item> item, int count) {
        return new TagInput(item, count);
    }
}