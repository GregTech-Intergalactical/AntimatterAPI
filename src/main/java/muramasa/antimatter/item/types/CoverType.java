package muramasa.antimatter.item.types;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.machine.Tier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CoverType<T extends CoverType<T>> extends ItemType<T> {

    protected Map<Tier, Cover> covers = new Object2ObjectOpenHashMap<>();
    protected Function<Tier, ? extends Cover> coverFunc;

    public CoverType(String domain, String id) {
        super(domain, id);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain) || registry != ForgeRegistries.ITEMS) return;
        for (Tier t : tiers) covers.put(t, registryCover(t, itemFunc.apply(this, t)));
    }

    private Cover registryCover(Tier t, ItemBasic<?> item) {
        Cover cover = coverFunc.apply(t);
        cover.onRegister();
        cover.setItem(item);
        return cover;
    }

    public T setItem(BiFunction<ItemType<?>, Tier, ? extends ItemBasic<?>> itemFunc, Function<Tier, ? extends Cover> coverFunc) {
        this.coverFunc = coverFunc;
        return super.setItem(itemFunc);
    }

    public Cover getCover(Tier tier) {
        return covers.get(tier);
    }
}
