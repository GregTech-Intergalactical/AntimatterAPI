package muramasa.antimatter.item;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Function;

public class ItemFactory<T extends ItemFactory<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected String domain, id;
    protected int layers = 1;
    protected ImmutableSet<Tier> tiers = ImmutableSet.of();
    //protected ImmutableSet<String> tooltips = ImmutableSet.of();
    protected Function<ItemFactory<?>, Class<?>> itemFunc;

    public ItemFactory(String domain, String id) {
        this.domain = domain;
        this.id = id;
        tiers(Tier.getAllElectric());
        AntimatterAPI.register(ItemFactory.class, getId(), this);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain) || registry != ForgeRegistries.ITEMS) return;
        for (Tier tier : tiers) AntimatterAPI.register(ItemTiered.class, String.join("_", getId(), tier.getId()), new ItemTiered(this, tier));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public int getLayers() {
        return layers;
    }

    public T tiers(Tier... tiers) {
        this.tiers = ImmutableSet.copyOf(tiers);
        return (T) this;
    }

    /*public T tips(String... tooltips) {
        this.tooltips = ImmutableSet.copyOf(tooltips);
        return (T) this;
    }*/

    public T setLayers(int layers) {
        this.layers = layers;
        return (T) this;
    }

    public T setItem(Function<ItemFactory<?>, Class<?>> func) {
        this.itemFunc = func;
        return (T) this;
    }

    public T setItem(Class<?> item) {
        setItem(m -> item);
        return (T) this;
    }
}
