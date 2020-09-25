package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.ints.Int2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMaps;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public class CoverInstance<T extends TileEntity> implements INamedContainerProvider, IGuiHandler {

    private final Cover cover;
    private final T tile;
    private final Direction side;
    private Int2BooleanMap cache;
    private Set<ForgeRegistryEntry<?>> filter;
    private CompoundNBT tag;

    public CoverInstance(Cover cover, T tile, Direction side) {
        this.cover = cover;
        this.tile = tile;
        this.side = side;
        if (cover.hasFilter()) filter = new ObjectLinkedOpenHashSet<>();
        if (cover.hasGui()) {
            int size = cover.getGui().getButtons().size();
            if (size > 0) cache = new Int2BooleanLinkedOpenHashMap(size);
        }
    }

    /** Events **/
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return cover.onInteract(this, player, hand, side, type);
    }

    public void onPlace(Direction side) {
        cover.onPlace(this, side);
    }

    public void onRemove(Direction side) {
        cover.onRemove(this, side);
    }

    //Called on update of the world.
    public void onUpdate(Direction side) {
        cover.onUpdate(this, side);
    }

    public void onMachineEvent(IMachineEvent event, Object... data) {
        cover.onMachineEvent(this, tile, event, data);
    }

    public void onGuiEvent(IGuiEvent event, int... data) {
        if (data.length >= 4) cache.put(data[0], data[3] != 0);
        cover.onGuiEvent(this, tile, event, data);
    }

    public boolean openGui(PlayerEntity player, Direction side) {
        return cover.openGui(this, player, side);
    }

    public void serialize(CompoundNBT nbt) {
        nbt.putString("id", cover.getId());
    }

    public boolean isEqual(Cover cover) {
        return this.cover.getId().equals(cover.getId());
    }

    public boolean isEqual(CoverInstance<T> cover) {
        return this.cover.getId().equals(cover.cover.getId());
    }

    public String getId() {
        return this.cover.getId();
    }

    public boolean isEmpty() {
        return cover == Data.COVER_NONE;
    }

    public boolean shouldRender() {
        return isEmpty(); //|| cover == Data.COVEROUTPUT;
    }

    //Gets the backing cover.
    //Because getCover().getCover() looks stupid
    public Cover getCover() {
        return cover;
    }

    public T getTile() {
        return tile;
    }

    public Direction getSide() {
        return side;
    }

    public void addToFilter(ForgeRegistryEntry<?> value) {
        filter.add(value);
    }

    public void addToFilter(Collection<? extends ForgeRegistryEntry<?>> values) {
        filter.addAll(values);
    }

    public void clearFilter() {
        filter.clear();
    }

    public Set<?> getFilter() {
        return filter != null ? filter : ObjectSets.EMPTY_SET;
    }

    public Int2BooleanMap getButtonsCache() {
        return cache != null ? cache : Int2BooleanMaps.EMPTY_MAP;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("TODO");//TranslationTextComponent(cover.getId());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return cover.getGui() != null && cover.getGui().getMenuHandler() != null ? cover.getGui().getMenuHandler().getMenu(this, inv, windowId) : null;
    }

    public CompoundNBT getOrCreateTag() {
        if (tag == null) tag = new CompoundNBT();
        return tag;
    }

    public CompoundNBT serialize() {
        if (filter != null) {
            ListNBT item = new ListNBT();
            ListNBT fluid = new ListNBT();
            for (Object o : filter) {
                StringNBT nbt = StringNBT.valueOf(((ForgeRegistryEntry<?>) o).getRegistryName().toString());
                if (o instanceof Item) {
                    item.add(nbt);
                } else if (o instanceof Fluid) {
                    fluid.add(nbt);
                }
            }
            tag.put(Ref.TAG_COVER_ITEM, item);
            tag.put(Ref.TAG_COVER_FLUID, fluid);
        }
        if (cache != null) {
            ListNBT list = new ListNBT();
            for (Int2BooleanMap.Entry e : cache.int2BooleanEntrySet()) {
                list.add(LongNBT.valueOf((long) e.getIntKey() << 32 | (e.getBooleanValue() ? 1 : 0)));
            }
            tag.put(Ref.TAG_COVER_BUTTON, list);
        }
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        if (filter != null) {
            ListNBT item = tag.getList(Ref.TAG_COVER_ITEM, Constants.NBT.TAG_STRING);
            for (int i = 0; i < item.size(); i++) {
                ResourceLocation itemName = new ResourceLocation(item.getString(i));
                Item itemValue = ForgeRegistries.ITEMS.getValue(itemName);
                if (itemValue != null) {
                    filter.add(itemValue);
                }
            }
            ListNBT fluid = tag.getList(Ref.TAG_COVER_FLUID, Constants.NBT.TAG_STRING);
            for (int i = 0; i < fluid.size(); i++) {
                ResourceLocation fluidName = new ResourceLocation(fluid.getString(i));
                Fluid fluidValue = ForgeRegistries.FLUIDS.getValue(fluidName);
                if (fluidValue != null) {
                    filter.add(fluidValue);
                }
            }
        }
        if (cache != null) {
            ListNBT list = tag.getList(Ref.TAG_COVER_BUTTON, Constants.NBT.TAG_LONG);
            for (INBT nbt : list) {
                long pack = ((LongNBT) nbt).getLong();
                cache.put((int) (pack >> 32), (int) (pack) != 0);
            }
        }
        this.tag = tag;
    }
}
