package muramasa.antimatter.cover;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterRemapping;
import muramasa.antimatter.Data;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class CoverFactory implements IAntimatterObject {

    final String id;
    final String domain;

    private final CoverSupplier supplier;
    private Map<Tier, Item> itemStacks = Collections.emptyMap();
    private Item itemStack;
    private List<Texture> textures;
    @Getter
    private MenuHandler<?> menuHandler = Data.COVER_MENU_HANDLER;

    protected boolean gui = false;

    protected CoverFactory(String domain, String id, CoverSupplier supplier) {
        this.id = id;
        this.supplier = supplier;
        this.domain = domain;
        AntimatterAPI.register(CoverFactory.class, this);
    }

    public final CoverSupplier get() {
        return this.supplier;
    }

    public ItemStack getItem(Tier tier) {
        return tier == null ? getItem() : itemStacks.getOrDefault(tier, Items.AIR).getDefaultInstance();
    }

    public Tier getValidTier() {
        return itemStacks.size() > 1 ? itemStacks.keySet().iterator().next() : null;
    }

    public List<Texture> getTextures() {
        return textures == null ? Collections.emptyList() : textures;
    }

    public ItemStack getItem() {
        return itemStack == null ? ItemStack.EMPTY : itemStack.getDefaultInstance();
    }

    void setItems(Map<Tier, Item> stacks) {
        this.itemStack = stacks.remove(null);
        if (itemStack == null)
            itemStack = Items.AIR;
        this.itemStacks = ImmutableMap.copyOf(stacks);
    }

    void addTextures(List<Texture> textures) {
        this.textures = textures;
    }

    void setHasGui() {
        this.gui = true;
    }

    void setMenuHandler(MenuHandler<?> handler) {
        this.menuHandler = handler;
    }

    public boolean hasGui() {
        return this.gui;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public static Builder builder(final CoverSupplier supplier) {
        return new Builder(supplier);
    }

    @Override
    public String getId() {
        return id;
    }

    public static CompoundTag writeCover(CompoundTag nbt, ICover cover) {
        CoverFactory factory = cover.getFactory();
        nbt.putString(cover.side().get3DDataValue() + "d", factory.getDomain());
        nbt.putString(cover.side().get3DDataValue() + "i", factory.getId());
        if (cover.getTier() != null)
            nbt.putString(cover.side().get3DDataValue() + "t", cover.getTier().getId());
        CompoundTag inner = cover.serialize();
        if (!inner.isEmpty())
            nbt.put(cover.side().get3DDataValue() + "c", inner);
        return nbt;
    }

    public static ICover readCover(ICoverHandler<?> source, Direction dir, CompoundTag nbt) {
        if (!nbt.contains(dir.get3DDataValue() + "d"))
            return null;
        String domain = nbt.getString(dir.get3DDataValue() + "d");
        String id = nbt.getString(dir.get3DDataValue() + "i");
        ResourceLocation location = new ResourceLocation(domain, id);
        if (AntimatterRemapping.getCoverRemappingMap().containsKey(location)) location = AntimatterRemapping.getCoverRemappingMap().get(location);
        CoverFactory factory = AntimatterAPI.get(CoverFactory.class, location);
        if (factory == null) {
            throw new IllegalStateException("Reading a cover with null factory, game in bad state");
        }
        Tier tier = nbt.contains(dir.get3DDataValue() + "t")
                ? AntimatterAPI.get(Tier.class, nbt.getString(dir.get3DDataValue() + "t"))
                : null;
        ICover cover = factory.supplier.get(source, tier, dir, factory);
        cover.onCreate();
        if (nbt.contains(dir.get3DDataValue() + "c"))
            cover.deserialize((CompoundTag) nbt.get(dir.get3DDataValue() + "c"));
        return cover;
    }

    public static class Builder {
        List<Tier> tiers = Collections.singletonList(null);

        final CoverSupplier supplier;
        BiFunction<CoverFactory, Tier, Item> itemBuilder;
        boolean gui = false;
        List<Texture> textures;
        MenuHandler<?> menuHandler;

        public Builder(final CoverSupplier supplier) {
            this.supplier = supplier;
        }

        public Builder setTiers(Tier... tiers) {
            this.tiers = Arrays.asList(tiers);
            return this;
        }

        public Builder item(BiFunction<CoverFactory, Tier, Item> item) {
            this.itemBuilder = item;
            return this;
        }

        public Builder gui() {
            this.gui = true;
            return this;
        }

        public Builder setMenuHandler(MenuHandler<?> handler) {
            this.menuHandler = handler;
            return this;
        }

        public Builder addTextures(List<Texture> textures) {
            this.textures = textures;
            return this;
        }

        public Builder addTextures(Texture... textures) {
            this.textures = Arrays.asList(textures);
            return this;
        }

        public CoverFactory build(String domain, String id) {
            CoverFactory factory = new CoverFactory(domain, id, this.supplier);
            if (this.itemBuilder != null) {
                Map<Tier, Item> map = new Object2ObjectOpenHashMap<>();
                for (Tier tier : this.tiers) {
                    Item stack = this.itemBuilder.apply(factory, tier);
                    map.put(tier, stack);
                }
                factory.setItems(map);
            }
            if (gui) {
                factory.setHasGui();
                if (menuHandler != null) {
                    factory.setMenuHandler(menuHandler);
                }
            }
            if (textures != null)
                factory.addTextures(textures);
            return factory;
        }

    }

    public interface CoverSupplier {
        ICover get(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory);
    }

}
