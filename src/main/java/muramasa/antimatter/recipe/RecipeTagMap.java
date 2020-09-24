package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeTagMap {

    protected static class TagItemWrapper extends ItemWrapper {

        public TagItemWrapper(ItemStack item, Set<RecipeTag> tags) {
            super(item, tags);
        }

        public TagItemWrapper(ItemStack item) {
            super(item);
        }

        //Ignore count but otherwise reuse the comparison!
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ItemWrapper)) return false;
            ItemWrapper other = (ItemWrapper) obj;
            return (ItemStack.areItemsEqual(item, other.item) && (!nbt || ItemStack.areItemStackTagsEqual(item, other.item)));
        }
    }
    //TagMapInput is a more complicated RecipeInput that allows for dynamic hashing & preparing the input.
    protected static class TagMapInput extends RecipeInput {
        //Unlike taggedItems, this is the accumulated hash for the actual tags present, not
        //regular items.
        private int accumulatedTagHash = 0;

        public TagMapInput(ItemStack[] items, FluidStack[] fluids, Set<RecipeTag> tags) {
            super(items, fluids, tags);
        }

        public TagMapInput(ItemStack[] items, FluidStack[] fluids) {
            super(items, fluids);
        }

        public void setTagHash(long hash) {
            this.accumulatedTagHash = (int)(hash ^ (hash >>> 32));
        }

        public List<ItemWrapper> prepare(Set<ResourceLocation> filter, Set<ItemWrapper> itemFilter) {
            List<ItemWrapper> withTag = new ArrayList<>();
            List<ItemWrapper> withoutTag = new ArrayList<>();
            if (items != null) {
                for (ItemWrapper wrapper : items) {
                    Set<ResourceLocation> itemTags = wrapper.item.getItem().getTags();
                    if (!itemTags.isEmpty() && (filter == null || itemTags.stream().anyMatch(filter::contains)) ) {
                        withTag.add(wrapper);
                    } else {
                        withoutTag.add(wrapper);
                    }
                }
            }
            int oldLength = (this.items == null ? 0 : this.items.length);
            this.items = withoutTag.stream().filter(itemFilter::contains).toArray(ItemWrapper[]::new);
            if (this.items.length != oldLength) {
                itemMap.clear();
                for (int i = 0; i < items.length; i++) {
                    itemMap.put(this.items[i].hashCode(), i);
                }
            }

            long hash = Arrays.stream(this.items).mapToLong(ItemWrapper::hashCode).sum() + 1;
            this.itemHash = (int) (hash ^ (hash >>> 32));

            this.taggedItems = withTag.toArray(new ItemWrapper[0]);
            return withTag;
        }
        //Rehash!
        public void rehash(long bitfilter) {
            long tempHash = 0; //long hash used to handle many inputs with nbt hashes
            if (taggedItems != null && taggedItems.length > 0 && bitfilter != 0) {
                for (int i = 0; i < taggedItems.length; i++) {
                    if ((bitfilter & (1 << i)) > 0) tempHash += this.taggedItems[i].hashCode();
                }
            }

            tagHash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision

            this.bitfilter = bitfilter;
        }

        @Override
        protected boolean itemEquals(RecipeInput other) {
            //How many non-tagged items are matched. This has to be equal to other.items.length,or 0 if it is null.
            int correctAmount = 0;
            int len = other.items == null ? 0 : other.items.length;

            //First, try regular items.
            if (items != null && other.items != null && correctAmount < len) {
                for (ItemWrapper item : this.items) {
                    if (other.items[other.itemMap.get(item.hashCode())].equals(item)) correctAmount++;
                }
            }
            if (correctAmount < len && taggedItems != null && other.items != null && bitfilter != 0) {
                for (int i = 0; ((bitfilter & (1 << i)) != 0) && i < taggedItems.length; i++) {
                    ItemWrapper item = taggedItems[i];
                    if (other.items[other.itemMap.get(item.hashCode())].equals(item)) correctAmount++;
                }
            }

            if (other instanceof TagMapInput) {
                TagMapInput tm = (TagMapInput) other;
                //Make sure tags match.
                return accumulatedTagHash == tm.accumulatedTagHash && correctAmount >= len;
            } else {
                return correctAmount >= len;
            }
        }
    }
    //First, match the non-tagged items.
    private final Object2ObjectMap<TagMapInput, Recipe> LOOKUP_TAG = new Object2ObjectLinkedOpenHashMap<>();
    //Tags present, others are ignored.
    private final Set<ResourceLocation> tagsPresent = new ObjectOpenHashSet<>();
    //Set of items present.
    private final Set<ItemWrapper> itemsPresent = new ObjectOpenHashSet<>();

    public RecipeTagMap() {

    }

    public Recipe find(TagMapInput input) {
        //Items that do not have tags present in the map are split to a separate list.
        //Remove items that don't exist at all in the map.
        List<ItemWrapper> tagged = input.prepare(tagsPresent, itemsPresent);

        return recursiveHash(input,tagged.stream().map(t -> t.item.getItem().getTags()).collect(Collectors.toList()), 0,0,0);
    }

    /**
     * Recursively finds a recipe.
     * @param input the input recipe.
     * @param arrayList all the items tags.
     * @param element current element in the list.
     * @param acc accumulated hash.
     * @param whichNonTagged bitmap of items to ignore tags at current level.
     * @return a found recipe.
     */
    Recipe recursiveHash(TagMapInput input, List<Set<ResourceLocation>> arrayList, int element, long acc, long whichNonTagged) {
        if (element > arrayList.size()) {
            return null;
        }
        for (ListIterator<Set<ResourceLocation>> it = arrayList.listIterator(element); it.hasNext(); ) {
            Set<ResourceLocation> tags = it.next();
            for (ResourceLocation r : tags) {
                if (!this.tagsPresent.contains(r)) {
                    continue;
                }
                Recipe ok = recursiveHash(input,arrayList, element + 1, acc + r.hashCode(),whichNonTagged);
                if (ok != null) {
                    return ok;
                }
            }
        }
        Recipe ok = recursiveHash(input,arrayList, element + 1, acc, whichNonTagged | (1 << element));
        if (ok != null) {
            return ok;
        }
        input.rehash(whichNonTagged);
        input.setTagHash(acc);
        return LOOKUP_TAG.get(input);
    }

    public void add(Recipe recipe) {
        TagMapInput input = new TagMapInput(recipe.getInputItems(), recipe.getInputFluids(), recipe.getTags());
            /*
            Sanity check, no input item can match the tag inputs, or it is undefined behaviour. Tags.length > 0 here.
             */
        if (recipe.getInputItems() != null) {
            for (ItemWrapper inputItem : input.items) {
                for (TagInput tag : recipe.getTagInputs()) {
                    if (tag.tag.contains(inputItem.item.getItem())) {
                        Utils.onInvalidData("INVALID RECIPE! Item added that is also a part of the tags of the recipe.");
                        return;
                    }
                }
            }
        }
        long code = 0;
        for (TagInput wr : recipe.getTagInputs()) {
            code += wr.tag.getId().hashCode();
            tagsPresent.add(wr.tag.getId());
        }
        if (recipe.getInputItems() != null) {
            for (ItemStack wr : recipe.getInputItems()) {
                itemsPresent.add(new TagItemWrapper(wr));
            }
        }
        input.setTagHash(code);
        LOOKUP_TAG.put(input, recipe);
    }
}