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
    //Includes possible tagged inputs as well.
    public ItemWrapper[] taggedItems;
    public ItemStack[] rootItems;
    protected Int2IntOpenHashMap itemMap = new Int2IntOpenHashMap();

    public FluidStack[] rootFluids;
    protected FluidWrapper[] fluids;
    protected Int2IntOpenHashMap fluidMap = new Int2IntOpenHashMap();

    protected int itemHash;
    protected int fluidHash;
    //temporary hash, tagged items that are treated as untagged.
    protected int tagHash;

    protected long bitfilter = ~0L;

    public RecipeInput(ItemStack[] items, FluidStack[] fluids, Set<RecipeTag> tags) {
        if (items != null && items.length > 64) {
            throw new RuntimeException("time to add support for arbitrary size bitmaps");
        }
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

        //Ensure this one isn't changed.
        itemHash = (int) (tempHash ^ (tempHash >>> 32));
        if (fluids != null && fluids.length > 0) {
            this.fluids = new FluidWrapper[fluids.length];
            for (int i = 0; i < fluids.length; i++) {
                this.fluids[i] = new FluidWrapper(fluids[i], tags);
                fluidMap.put(this.fluids[i].hashCode(), i);
                fluidHash += this.fluids[i].hashCode();
            }
        }
    }

    public RecipeInput(ItemStack[] items, FluidStack[] fluids) {
        this(items, fluids, Collections.emptySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInput)) return false;
        RecipeInput other = (RecipeInput) obj;
        if (items != null) {
            if (other.items == null) {
                if (taggedItems == null && bitfilter == 0) return false;
            } else {
                for (ItemWrapper item : items) {
                    if (!other.items[other.itemMap.get(item.hashCode())].equals(item)) return false;
                }
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
        } else return other.fluids == null;

        return true;
    }

    @Override
    public int hashCode() {
        return itemHash + fluidHash + tagHash;
    }
}
