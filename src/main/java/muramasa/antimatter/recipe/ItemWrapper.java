package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemWrapper {

    public final ItemStack item;
    //protected boolean count, nbt;
    // protected int hash;
    public final int count;
    public final List<ResourceLocation> possibleTags;

    private int hash;

    public ItemWrapper(ItemStack item, Set<ResourceLocation> tagsPresent, Set<RecipeTag> tags) {
        this.item = item;
        this.count = item.getCount();
        this.possibleTags = this.item.getItem().getTags().stream().filter(tagsPresent::contains).collect(Collectors.toList());
        //  count = item.getCount() > 1;
        //  nbt = item.hasTag() && !tags.contains(RecipeTag.IGNORE_NBT);
        //     long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        //    tempHash = 31 * tempHash + item.getItem().getRegistryName().toString().hashCode();
        //  if (nbt) tempHash = 31 * tempHash + item.getTag().hashCode();
        //  hash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision
    }

    public ItemWrapper(AntimatterIngredient ingredient, int count, Set<RecipeTag> tags) {
        ItemStack item1;
        this.possibleTags = new ObjectArrayList<>();
        this.count = count;
        if (ingredient.tag != null) {
            this.possibleTags.add(ingredient.getTagResource());
            item1 = null;
        } else if (ingredient.item != null) {
            item1 = ingredient.item;
        }
        item1 = ingredient.getMatchingStacks().length == 1 ? ingredient.getMatchingStacks()[0] : null;
        this.item = item1;
    }
}
