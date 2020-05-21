package muramasa.antimatter.item.types;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemType<T extends ItemType<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected String domain, id;
    protected int layers = 1;
    protected ImmutableSet<Tier> tiers = ImmutableSet.of();
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
        for (Tier t : tiers) {
            Object item = itemFunc.apply(this, t);
            AntimatterAPI.register(item.getClass(), getId() + '_' + t.getId(), item);
        }
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

    public T setItem(BiFunction<ItemType<?>, Tier, ? extends ItemBasic<?>> func) {
        this.itemFunc = func;
        return (T) this;
    }
}
