package muramasa.gtu.api.tileentities.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.impl.CoverHandler;
import muramasa.gtu.api.capability.impl.PipeConfigHandler;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.pipe.types.Pipe;
import muramasa.gtu.api.tileentities.TileEntityTickable;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.blocks.pipe.BlockPipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityPipe extends TileEntityTickable {

    protected Pipe type;
    protected PipeSize size;
    protected CoverHandler coverHandler;
    protected PipeConfigHandler configHandler;

    protected byte connections, disabledConnections;

    public TileEntityPipe() {
        configHandler = new PipeConfigHandler(this);
    }

    public final void init(Pipe type, PipeSize size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public void onFirstTick() {
        type = ((BlockPipe) getBlockType()).getType();
        size = size != null ? size : PipeSize.NORMAL;
        if (isServerSide()) refreshConnections();
    }

    public Pipe getType() {
        return type;
    }

    public PipeSize getSize() {
        if (size == null) size = PipeSize.NORMAL;
        return size;
    }

    public byte getConnections() {
        return connections;
    }

    public byte getDisabledConnections() {
        return disabledConnections;
    }

    public abstract boolean canConnect(TileEntity tile);

    public void refreshConnections() {
//        System.out.println("refresh");
        connections = 0;
        int sideMask, smallerPipes = 0;
        TileEntity adjTile;
        for (int s = 0; s < 6; s++) {
            adjTile = Utils.getTile(world, pos.offset(EnumFacing.VALUES[s]));
            if (adjTile == null) continue;
            sideMask = 1 << s;
            if ((disabledConnections & sideMask) == 0) { //Connection side has not been disabled
                if (canConnect(adjTile)) {
                    connections |=  sideMask;
                    //TODO check isFullCube to allow more culled connections?
                    if (((TileEntityPipe) adjTile).getSize().ordinal() < getSize().ordinal()) smallerPipes++;
                } else if (adjTile.hasCapability(GTCapabilities.ENERGY, EnumFacing.VALUES[s].getOpposite())) {
                    connections |= sideMask;
//                    machineConnections |= sideMask;
                }
            }
        }
        if (smallerPipes == 0) connections += 64; //Use culled models if there are no smaller pipes adjacent
        markForNBTSync();
    }

    public void toggleConnection(EnumFacing side) {
        int sideMask = 1 << side.getIndex();
        if ((disabledConnections & sideMask) != 0) { //Is Disabled, so remove mask
            disabledConnections &= ~sideMask;
            System.out.println("Enabled Connection for " + side);
        } else { //Is not disabled, so add mask
            disabledConnections |= sideMask;
            System.out.println("Disabled Connection for " + side);
        }
        refreshConnections();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COVERABLE || capability == GTCapabilities.CONFIGURABLE) return true;
        return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COVERABLE) {
            return GTCapabilities.COVERABLE.cast(coverHandler);
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return GTCapabilities.CONFIGURABLE.cast(configHandler);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Ref.KEY_PIPE_SIZE)) size = PipeSize.VALUES[tag.getInteger(Ref.KEY_PIPE_SIZE)];
        if (tag.hasKey(Ref.KEY_PIPE_CONNECTIONS)) connections = tag.getByte(Ref.KEY_PIPE_CONNECTIONS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger(Ref.KEY_PIPE_SIZE, size.ordinal());
        tag.setInteger(Ref.KEY_PIPE_CONNECTIONS, connections);
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Pipe Type: " + getType().getName());
        info.add("Pipe Size: " + getSize().getName());
        return info;
    }
}
