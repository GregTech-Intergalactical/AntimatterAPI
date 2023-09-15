package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

;

public abstract class CoverFilter<T> extends BaseCover {

    // TODO: Add black/white lister filter mode
    protected Set<T> filter = new ObjectOpenHashSet<>();

    public CoverFilter(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    public void addToFilter(T value) {
        filter.add(value);
    }

    public void addToFilter(Collection<? extends T> values) {
        filter.addAll(values);
    }

    public Set<T> getFilter() {
        return filter;
    }

    public void clearFilter() {
        filter.clear();
    }
}
