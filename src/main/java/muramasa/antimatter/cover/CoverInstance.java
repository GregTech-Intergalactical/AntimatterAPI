package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class CoverInstance implements INamedContainerProvider {
    private Cover cover;
    private CompoundNBT nbt;
    private TileEntity tile;

    public CoverInstance(Cover cover, TileEntity tile) {
        this.cover = Objects.requireNonNull(cover);
        this.tile = tile;
        this.nbt = new CompoundNBT();
    }
    //This allows you to instantiate a non-stateful cover, like COVER_EMPTY.
    //Using state with this is a runtime error.
    public CoverInstance(Cover cover) {
        this.cover = cover;
    }

    //Automatically calls onPlace.
    public CoverInstance(Cover cover, TileEntity tile, Direction side) {
        this(cover,tile);
        onPlace(side);
    }

    public void serialize(CompoundNBT nbt) {
        nbt.putString("ID",cover.getId());
    }

    public boolean isEqual(Cover cover) {
        return this.cover.getId().equals(cover.getId());
    }

    public boolean isEqual(CoverInstance cover) {
        return this.cover.getId().equals(cover.cover.getId());
    }

    public String getId() {
        return this.cover.getId();
    }

    public boolean isEmpty() {
        return cover == Data.COVERNONE;
    }

    //Gets the backing cover.
    //Because getCover().getCover() looks stupid
    public Cover backing() {
        return cover;
    }

    public TileEntity getTile() {
        return tile;
    }

    public Optional<TileEntityMachine> getMachine() {
        if (tile instanceof TileEntityMachine) {
            return Optional.of((TileEntityMachine)tile);
        }
        return Optional.empty();
    }

    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return cover.onInteract(this,player,hand,side,type);
    }

    public void onMachineEvent(TileEntityMachine tile, IMachineEvent event) {
        this.cover.onMachineEvent(this,tile,event);
    }


    public void onPlace(Direction side) {
        cover.onPlace(this, side);
    }

    public void onRemove(Direction side) {
        cover.onRemove(this, side);
        /*if (!tile.getWorld().isRemote) {
            BlockPos pos = tile.getPos();
            ItemEntity itementity = new ItemEntity(tile.getWorld(), pos.getX(), pos.getY() + 5D, pos.getZ(), cover.getDroppedStack());
            tile.getWorld().addEntity(itementity);
        }*/
    }


    public void onUpdate(Direction side) {
        cover.onUpdate(this, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("TODO");//TranslationTextComponent(cover.getId());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return cover.gui.getMenuHandler().getMenu(this, inv, windowId);
    }
}
