package muramasa.antimatter.cover;


import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collection;
import java.util.Set;

;

public abstract class CoverFilter<T> extends BaseCover {

    // TODO: Add black/white lister filter mode
    protected Set<T> filter = new ObjectOpenHashSet<>();


    @Override
    public String getId() {
        return "filter";
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
