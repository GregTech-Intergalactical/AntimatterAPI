package muramasa.antimatter.item.types;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.tier.VoltageTier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.BiFunction;

public class ItemFactory<T extends ItemFactory<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected String domain, id;
    protected int layers = 1;
    protected ImmutableSet<VoltageTier> tiers = ImmutableSet.of();
    protected BiFunction<ItemFactory<?>, VoltageTier, ? extends ItemBasic<?>> itemFunc;

    public ItemFactory(String domain, String id) {
        this.domain = domain;
        this.id = id;
        tiers(VoltageTier.getAllElectric());
        AntimatterAPI.register(ItemFactory.class, getId(), this);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain) || registry != ForgeRegistries.ITEMS) return;
        for (VoltageTier t : tiers) itemFunc.apply(this, t);
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

    public T tiers(VoltageTier... tiers) {
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

    public T setItem(BiFunction<ItemFactory<?>, VoltageTier, ? extends ItemBasic<?>> func) {
        this.itemFunc = func;
        return (T) this;
    }
}
