package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
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
    private final Set<Object> filter = new ObjectLinkedOpenHashSet<>();
    private CompoundNBT tag = new CompoundNBT();

    public CoverInstance(Cover cover, T tile, Direction side) {
        this.cover = cover;
        this.tile = tile;
        this.side = side;
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

    public void addToFilter(Object value) {
        filter.add(value);
    }

    public void addToFilter(Collection<? extends Object> values) {
        filter.addAll(values);
    }

    public Set<?> getFilter() {
        return filter;
    }

    public void clearFilter() {
        filter.clear();
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

    public CompoundNBT getTag() {
        return tag;
    }

    public CompoundNBT serialize() {
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
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
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
        this.tag = tag;
    }
}
