package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeFluids {
    private int hash;
    public FluidStack[] rootFluids;
    public Set<FluidWrapper> fluids = new ObjectOpenHashSet<>();

    public RecipeFluids(@Nullable FluidStack[] fluids, Set<RecipeTag> tags) {
        initHash(fluids, tags);
    }

    private void initHash(FluidStack[] fluids, Set<RecipeTag> tags) {
        int fluidHash = 0;
        if (fluids != null && fluids.length > 0) {
            for (FluidStack fluid : fluids) {
                FluidWrapper fw = new FluidWrapper(fluid, tags);
                this.fluids.add(fw);
                fluidHash += fw.hashCode();
            }
        }
        this.hash = fluidHash;
    }

    public RecipeFluids(Recipe recipe) {
        long fluidHash = 0;
        FluidStack[] fluids = recipe.getInputFluids();
        if (fluids != null && fluids.length > 0) {
            this.fluids = new ObjectOpenHashSet<>();
            for (FluidStack fluid : fluids) {
                FluidWrapper fw = new FluidWrapper(fluid, recipe.getTags());
                this.fluids.add(fw);
                fluidHash += fw.hashCode();
            }
        }
        hash = (int)(fluidHash^(fluidHash >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeFluids)) return false;
        RecipeFluids other = (RecipeFluids) obj;
        return fluids.containsAll(other.fluids);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (fluids != null && fluids.size() > 0) {
            builder.append("\nInput Fluids: { ");
            for (FluidWrapper fluid : fluids) {
                builder.append(fluid.fluid.getDisplayName().getFormattedText()).append(" x").append(fluid.fluid.getAmount());
                builder.append(", ");
            }
            builder.append(" }\n");
        }
        return builder.toString();
    }
}
