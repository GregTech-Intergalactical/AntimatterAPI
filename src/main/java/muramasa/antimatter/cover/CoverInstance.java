package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoverInstance<T extends TileEntity> implements INamedContainerProvider, IGuiHandler {

    public static final CoverInstance<?>[] EMPTY_COVER_ARRAY = new CoverInstance[0];

    private final Cover cover;
    private final T tile;
    private final Direction side;
    private CompoundNBT tag;

    public CoverInstance(Cover cover, T tile, Direction side) {
        this.cover = cover;
        this.tile = tile;
        this.side = side;
        this.tag = new CompoundNBT();
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

    public CompoundNBT serialize() {
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        this.tag = tag;
    }
}
