//package muramasa.antimatter.tileentities.pipe;
//
//import muramasa.gtu.Ref;
//import muramasa.antimatter.blocks.pipe.BlockPipe;
//import muramasa.antimatter.capability.GTCapabilities;
//import muramasa.antimatter.capability.impl.CoverHandler;
//import muramasa.antimatter.capability.impl.PipeConfigHandler;
//import muramasa.antimatter.pipe.PipeSize;
//import muramasa.antimatter.GregTechProperties;
//import muramasa.antimatter.tileentities.TileEntityTickable;
//import muramasa.antimatter.util.Utils;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//import net.minecraftforge.client.model.data.IModelData;
//import net.minecraftforge.client.model.data.ModelDataMap;
//import net.minecraftforge.common.capabilities.Capability;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Optional;
//
//public abstract class TileEntityPipe extends TileEntityTickable {
//
//    /** Pipe Data **/
//    protected BlockPipe type;
//    protected PipeSize size;
//
//    /** Capabilities **/
//    public Optional<CoverHandler> coverHandler = Optional.empty();
//    public Optional<PipeConfigHandler> configHandler = Optional.empty();
//
//    protected byte connections, disabledConnections;
//
//    //TODO needed in load?
//    @Override
//    public void onLoad() {
//        coverHandler = Optional.of(new CoverHandler(this));
//        configHandler = Optional.of(new PipeConfigHandler(this));
//    }
//
//    @Override
//    public void onFirstTick() {
//        if (isServerSide()) refreshConnections();
//    }
//
//    public BlockPipe getType() {
//        return type != null ? type : (type = (BlockPipe) getBlockType());
//    }
//
//    public PipeSize getSize() {
//        return size != null ? size : (size = getState().getValue(getType().getSizeProp()));
//    }
//
//    public byte getConnections() {
//        return connections;
//    }
//
//    public byte getDisabledConnections() {
//        return disabledConnections;
//    }
//
//    public abstract boolean canConnect(TileEntity tile);
//
//    public void refreshConnections() {
////        System.out.println("refresh");
//        connections = 0;
//        int sideMask, smallerPipes = 0;
//        TileEntity adjTile;
//        for (int s = 0; s < 6; s++) {
//            adjTile = Utils.getTile(world, pos.offset(Ref.DIRECTIONS[s]));
//            if (adjTile == null) continue;
//            sideMask = 1 << s;
//            if ((disabledConnections & sideMask) == 0) { //Connection side has not been disabled
//                if (canConnect(adjTile)) {
//                    connections |= sideMask;
//                    //TODO check isFullCube to allow more culled connections?
//                    if (((TileEntityPipe) adjTile).getSize().ordinal() < getSize().ordinal()) smallerPipes++;
//                } else if (adjTile.hasCapability(GTCapabilities.ENERGY, Ref.DIRECTIONS[s].getOpposite())) {
//                    connections |= sideMask;
////                    machineConnections |= sideMask;
//                }
//            }
//        }
//        if (smallerPipes == 0) connections += 64; //Use culled models if there are no smaller pipes adjacent
//        markForNBTSync();
//    }
//
//    public void toggleConnection(Direction side) {
//        int sideMask = 1 << side.getIndex();
//        if ((disabledConnections & sideMask) != 0) { //Is Disabled, so remove mask
//            disabledConnections &= ~sideMask;
//            System.out.println("Enabled Connection for " + side);
//        } else { //Is not disabled, so add mask
//            disabledConnections |= sideMask;
//            System.out.println("Disabled Connection for " + side);
//        }
//        refreshConnections();
//    }
//
//    @Override
//    public boolean hasCapability(Capability<?> capability, @Nullable Direction side) {
//        if (capability == GTCapabilities.COVERABLE && coverHandler.isPresent()) {
//            return true;
//        } else if (capability == GTCapabilities.CONFIGURABLE && configHandler.isPresent()) {
//            return true;
//        }
//        return super.hasCapability(capability, side);
//    }
//
//    @Nullable
//    @Override
//    public <T> T getCapability(Capability<T> capability, @Nullable Direction side) {
//        if (capability == GTCapabilities.COVERABLE && coverHandler.isPresent()) {
//            return GTCapabilities.COVERABLE.cast(coverHandler.get());
//        } else if (capability == GTCapabilities.CONFIGURABLE && configHandler.isPresent()) {
//            return GTCapabilities.CONFIGURABLE.cast(configHandler.get());
//        }
//        return super.getCapability(capability, side);
//    }
//
//    @Override
//    public void readFromNBT(CompoundNBT tag) {
//        super.readFromNBT(tag);
//        if (tag.hasKey(Ref.KEY_PIPE_CONNECTIONS)) connections = tag.getByte(Ref.KEY_PIPE_CONNECTIONS);
//    }
//
//    @Override
//    public CompoundNBT writeToNBT(CompoundNBT tag) {
//        super.writeToNBT(tag);
//        tag.setInteger(Ref.KEY_PIPE_CONNECTIONS, connections);
//        return tag;
//    }
//
//    @Nonnull
//    @Override
//    public IModelData getModelData() {
//        return new ModelDataMap.Builder().withInitial(GregTechProperties.PIPE_SIZE, size).withInitial(GregTechProperties.PIPE_CONNECTIONS, connections).build();
//    }
//
//    @Override
//    public List<String> getInfo() {
//        List<String> info = super.getInfo();
//        info.add("Pipe Type: " + getType().getId());
//        info.add("Pipe Size: " + getSize().getName());
//        return info;
//    }
//}
