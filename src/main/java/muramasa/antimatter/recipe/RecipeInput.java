package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

//Reminder: A simplified version of a HashMap get
//if (e.hash == hash && ((k = e.key) == key || key.equals(k))) return e.value;
public class RecipeInput {

    protected ItemWrapper[] items;
    public ItemStack[] rootItems;
    protected Int2IntOpenHashMap itemMap = new Int2IntOpenHashMap();

    public FluidStack[] rootFluids;
    protected FluidWrapper[] fluids;
    protected Int2IntOpenHashMap fluidMap = new Int2IntOpenHashMap();

    protected int itemHash;
    protected int fluidHash;

    protected long bitfilter;

    public RecipeInput(ItemStack[] items, FluidStack[] fluids, Set<RecipeTag> tags) {
        rootFluids = fluids;
        rootItems = items;
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        if (items != null && items.length > 0) {
            this.items = new ItemWrapper[items.length];
            for (int i = 0; i < items.length; i++) {
                this.items[i] = new ItemWrapper(items[i], tags);
                itemMap.put(this.items[i].hashCode(), i);
                tempHash += this.items[i].hashCode();
            }
        }
        itemHash = (int) (tempHash ^ (tempHash >>> 32));
        if (fluids != null && fluids.length > 0) {
            this.fluids = new FluidWrapper[fluids.length];
            for (int i = 0; i < fluids.length; i++) {
                this.fluids[i] = new FluidWrapper(fluids[i], tags);
                fluidMap.put(this.fluids[i].hashCode(), i);
                fluidHash += this.fluids[i].hashCode();
            }
        }
        this.bitfilter = ~0L;
    }

    public RecipeInput(ItemStack[] items, FluidStack[] fluids) {
        this(items, fluids, Collections.emptySet());
    }

    public void clearJunk(Set<ItemWrapper> filter) {
        if (items == null || filter == null) return;
        this.items = Arrays.stream(items).filter(filter::contains).collect(Collectors.toList()).toArray(new ItemWrapper[0]);
    }

    public List<ItemWrapper> trimAllTags(Optional<Set<ResourceLocation>> filter) {
        Set<ResourceLocation> fil = filter.orElse(null);
        List<ItemWrapper> withTag = new ArrayList<>();
        List<ItemWrapper> withoutTag = new ArrayList<>();
        if (items != null) {
            for (ItemWrapper wrapper : items) {
                Set<ResourceLocation> itemTags = wrapper.item.getItem().getTags();
                if (!itemTags.isEmpty() && (fil == null || itemTags.stream().anyMatch(fil::contains)) ) {
                    withTag.add(wrapper);
                } else {
                    withoutTag.add(wrapper);
                }
            }
        }
        int oldLength = (this.items == null ? 0 : this.items.length);
        this.items = withoutTag.toArray(new ItemWrapper[0]);
        if (this.items.length != oldLength && withTag.size() > 0) {
            itemMap.clear();
            for (int i = 0; i < items.length; i++) {
                itemMap.put(this.items[i].hashCode(), i);
            }
            rehash(0);
        }
        return withTag;
    }

    public void rehash(long bitFilter) {
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        if (items != null && items.length > 0 && Long.bitCount(bitFilter) < items.length) {
            for (int i = 0; i < items.length; i++) {
                if ((bitFilter & (1 << i)) == 0) tempHash += this.items[i].hashCode();
            }
        }
       /*f (fluids != null && fluids.length > 0) {
            for (int i = 0; i < fluids.length; i++) {
                tempHash += this.fluids[i].hashCode();
            }
        }*/
        itemHash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision

        if (items != null && items.length == 0) items = null;

        this.bitfilter = bitFilter;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInput)) return false;
        RecipeInput other = (RecipeInput) obj;
        if (items != null) {
            if (other.items == null) {
                return false;
            }
            for (int i = 0; ((bitfilter & (1 << i)) != 0) && i < items.length; i++) {
                ItemWrapper item = items[i];
                if (!other.items[other.itemMap.get(item.hashCode())].equals(item)) return false;
            }
        } else if (other.items != null) {
            return false;
        }
        if (fluids != null) {
            if (other.fluids == null) {
                return false;
            }
            for (FluidWrapper fluid : fluids) {
                if (!other.fluids[other.fluidMap.get(fluid.hashCode())].equals(fluid)) return false;
            }
        } else if (other.fluids != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return itemHash + fluidHash;
    }
}
