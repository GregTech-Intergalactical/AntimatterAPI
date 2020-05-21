package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;

public class CoverInstance {
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
        onPlace(tile,side);
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

    public Cover getCover() {
        return cover;
    }

    public boolean onInteract(TileEntity tile, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return cover.onInteract(this,tile,player,hand,side,type);
    }

    public void onMachineEvent(TileEntityMachine tile, IMachineEvent event) {
        this.cover.onMachineEvent(this,tile,event);
    }


    public void onPlace(TileEntity tile, Direction side) {
        cover.onPlace(this, tile, side);
    }

    public void onRemove(TileEntity tile, Direction side) {
        cover.onRemove(this, tile, side);
        /*if (!tile.getWorld().isRemote) {
            BlockPos pos = tile.getPos();
            ItemEntity itementity = new ItemEntity(tile.getWorld(), pos.getX(), pos.getY() + 5D, pos.getZ(), cover.getDroppedStack());
            tile.getWorld().addEntity(itementity);
        }*/
    }


    public void onUpdate(TileEntity tile, Direction side) {
        cover.onUpdate(this, tile, side);
    }
}
