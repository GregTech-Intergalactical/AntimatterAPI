package muramasa.antimatter.recipe;

import net.minecraft.item.Item;
import net.minecraft.tags.Tag;

public class TagInput {
    Tag<Item> tag;
    int count;

    public TagInput(Tag<Item> tag, int count) {
        this.tag = tag;
        this.count = count;
    }
}