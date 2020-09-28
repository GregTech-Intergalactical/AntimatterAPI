package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

//Reminder: A simplified version of a HashMap get
//if (e.hash == hash && ((k = e.key) == key || key.equals(k))) return e.value;
public class RecipeInput {


    protected Set<AntimatterIngredient> items;
    protected AntimatterIngredient[] taggedItems;
    public List<Set<ResourceLocation>> allInputTags;

    public FluidStack[] rootFluids;
    protected Set<FluidWrapper> fluids;
    //Static item + fluid hash.
    protected int staticHash;
    //Combination of staticHash and possible Accumulated tags.
    protected int hash;
    //The items selected as non-tagged get their hash put here.
    protected int tagHash;

    protected long bitfilter = ~0L;

    public int special = 0;

    //Called from Recipe.
    public RecipeInput(@Nullable List<AntimatterIngredient> items, @Nullable FluidStack[] fluids, Set<RecipeTag> tags) {
        if (items != null && items.size() > 64) {
            throw new RuntimeException("time to add support for arbitrary size bitmaps");
        }
        rootFluids = fluids;
        if (items != null) {
            this.items = items.stream().filter(t -> t.tag == null).collect(Collectors.toSet());
            this.taggedItems = items.stream().filter(t -> t.tag != null).toArray(AntimatterIngredient[]::new);
        }
        initHash(this.items, fluids, tags);
    }

    private void initHash(Collection<AntimatterIngredient> items, FluidStack[] fluids, Set<RecipeTag> tags) {
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        if (items != null && items.size() > 0) {
            this.items = new ObjectLinkedOpenHashSet<>(items.size());
            for (AntimatterIngredient item : items) {
                this.items.add(item);
                tempHash += item.hashCode();
            }
        }

        //Ensure this one isn't changed.
        long itemHash = (int) (tempHash ^ (tempHash >>> 32));
        long fluidHash = 0;
        if (fluids != null && fluids.length > 0) {
            this.fluids = new ObjectOpenHashSet<>();
            for (FluidStack fluid : fluids) {
                FluidWrapper fw = new FluidWrapper(fluid, tags);
                this.fluids.add(fw);
                 fluidHash += fw.hashCode();
            }
        }
        staticHash = combineHash((int)itemHash,(int)(fluidHash^(fluidHash >>> 32)));
        if (hash == 0) hash = staticHash;
    }

    public void setAccumulatedTagHash(long hash) {
        this.hash = combineHash(this.staticHash, (int)(hash ^(hash >>> 32)));
    }

    public RecipeInput(AntimatterIngredient[] items, FluidStack[] fluids, Set<RecipeTag> tags) {
        this(Arrays.asList(items), fluids, tags);
    }

    public RecipeInput(List<AntimatterIngredient> items, FluidStack[] fluids) {
        this(items, fluids, Collections.emptySet());
    }

    public RecipeInput(ItemStack[] stacks, FluidStack[] fluids, Set<RecipeTag> tags, Set<ResourceLocation> filter, Set<AntimatterIngredient> itemFilter) {
        List<AntimatterIngredient> withTag = new ArrayList<>(stacks == null ? 0 : stacks.length);
        List<AntimatterIngredient> withoutTag = new ArrayList<>(stacks == null ? 0 : stacks.length);

        this.allInputTags = new LinkedList<>();
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                Set<ResourceLocation> itemTags = stack.getItem().getTags();
                if (!itemTags.isEmpty() && (filter == null || itemTags.stream().anyMatch(filter::contains))) {
                    allInputTags.add(stack.getItem().getTags());
                    withTag.add(AntimatterIngredient.fromStack(stack));
                } else {
                    withoutTag.add(AntimatterIngredient.fromStack(stack));
                }
            }
        }
        this.items = withoutTag.stream().filter(itemFilter::contains).collect(Collectors.toSet());
        initHash(this.items, fluids, tags);
        this.taggedItems = withTag.toArray(new AntimatterIngredient[0]);
    }

    protected boolean itemEquals(RecipeInput other) {
        //How many non-tagged items are matched. This has to be equal to other.items.length,or 0 if it is null.
        int correctAmount = 0;
        int len = other.items == null ? 0 : other.items.size();
        //First, try regular items.
        if (items != null && other.items != null && correctAmount < len) {
            for (AntimatterIngredient item : this.items) {
                if (other.items.contains(item)) correctAmount++;
            }
        }
        //Otherwise, try the tagged items treated as non-tagged.
        if (correctAmount < len && taggedItems != null && bitfilter != 0) {
            for (int i = 0; ((bitfilter & (1 << i)) != 0) && i < taggedItems.length; i++) {
                if (other.items.contains(taggedItems[i])) correctAmount++;
            }
        }
        return correctAmount >= len;
    }

    protected boolean fluidEquals(RecipeInput other) {
        if (fluids != null) {
            if (other.fluids == null) {
                return false;
            }
            return other.fluids.containsAll(this.fluids);
        }
        return true;
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
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInput)) return false;
        RecipeInput other = (RecipeInput) obj;
        if (other.special != this.special) return false;
        return itemEquals(other) && fluidEquals(other);
    }

    @Override
    public int hashCode() {
        return combineHash(combineHash(hash,tagHash),Integer.hashCode(special));
    }

    private int combineHash(int hash1, int hash2) {
        hash1 ^= hash2 + 0x9e3779b9 + (hash1 << 6) + (hash2 >> 2);
        return hash1;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (items != null && items.size() > 0) {
            builder.append("\nInput Items (default): { ");
            for (AntimatterIngredient item : items) {
                builder.append(item.getMatchingStacks()[0].getDisplayName().getFormattedText()).append(" x").append(item.getMatchingStacks()[0].getCount());
                builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (taggedItems != null && taggedItems.length > 0) {
            builder.append("\nInput Items (tag): { ");
            for (AntimatterIngredient item : taggedItems) {
                builder.append(item.getMatchingStacks()[0].getDisplayName().getFormattedText()).append(" x").append(item.getMatchingStacks()[0].getCount());
                builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (fluids != null && fluids.size() > 0) {
            builder.append("\nInput Fluids: { ");
            for (FluidWrapper fluid : fluids) {
                builder.append(fluid.fluid.getDisplayName().getFormattedText()).append(" x").append(fluid.fluid.getAmount());
                builder.append(", ");
            }
            builder.append(" }\n");
        }
        builder.append("Special: ").append(special).append("\n");
        return builder.toString();
    }
}
