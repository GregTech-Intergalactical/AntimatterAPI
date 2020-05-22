package muramasa.antimatter.item.types;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class ItemType<T extends ItemType<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected String domain, id;
    protected ImmutableSet<Tier> tiers = ImmutableSet.of();
    protected ImmutableSet<Tier> layers = ImmutableSet.of();
    protected ImmutableMap<Tier, String> tooltips = ImmutableMap.of();
    protected BiFunction<ItemType<?>, Tier, ? extends ItemBasic<?>> itemFunc;

    public ItemType(String domain, String id) {
        this.domain = domain;
        this.id = id;
        tiers(Tier.getAllElectric());
        AntimatterAPI.register(ItemType.class, getId(), this);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain) || registry != ForgeRegistries.ITEMS) return;
        for (Tier t : tiers) itemFunc.apply(this, t);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public String getTooltip(Tier tier) {
        return tooltips.get(tier);
    }

    public boolean isLayered(Tier tier) {
        return layers.contains(tier);
    }

    public T tiers(Tier... tiers) {
        this.tiers = ImmutableSet.copyOf(tiers);
        return (T) this;
    }

    public T layers(Tier... layers) {
        this.layers = ImmutableSet.copyOf(layers);
        return (T) this;
    }

    public T tips(String... tooltips) {
        IntFunction<String> func = i -> i < tooltips.length ? tooltips[i] : "";
        ImmutableMap.Builder<Tier, String> map = new ImmutableMap.Builder<>(); int i = 0;
        for (Tier tier : tiers) map.put(tier, func.apply(i++));
        this.tooltips = map.build();
        return (T) this;
    }

    public T tipAll(String tooltip) {
        ImmutableMap.Builder<Tier, String> map = new ImmutableMap.Builder<>();
        for (Tier tier : tiers) map.put(tier, tooltip);
        this.tooltips = map.build();
        return (T) this;
    }

    public T setItem(BiFunction<ItemType<?>, Tier, ? extends ItemBasic<?>> func) {
        this.itemFunc = func;
        return (T) this;
    }
}
