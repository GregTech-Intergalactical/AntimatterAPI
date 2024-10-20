package muramasa.antimatter.blockentity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockFakeTile;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.packets.FakeTilePacket;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BlockEntityFakeBlock extends BlockEntityTickable<BlockEntityFakeBlock> {

    private BlockEntityBasicMultiMachine<?> controller = null;
    public Map<Direction, ICover> covers = new EnumMap<>(Direction.class);
    public Direction facing;
    private BlockPos controllerPos = null;

    public final Map<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;

    public BlockEntityFakeBlock(BlockPos pos, BlockState state) {
        super(BlockFakeTile.TYPE, pos, state);
        coverTexturer = new Object2ObjectOpenHashMap<>();
    }

    public void setController(BlockEntityBasicMultiMachine<?> controller) {
        this.controller = controller;
        if (level != null) {
            if (controller != null && !level.isClientSide){
                AntimatterNetwork.NETWORK.sendToAllLoaded(new FakeTilePacket(this.getBlockPos(), controller.getBlockPos()), level, this.getBlockPos());
            }
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    public BlockEntityBasicMultiMachine<?> getController() {
        return controller;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (controllerPos != null){
            if (level.getBlockEntity(pos) instanceof BlockEntityBasicMultiMachine<?> basicMultiMachine && basicMultiMachine.allowsFakeTiles()){
                setController(basicMultiMachine);
            }
            controllerPos = null;
        }
        covers.forEach((s, c) -> {
            if (c.ticks()) {
                c.onUpdate();
            }
        });
    }

    public ICover[] covers() {
        ICover[] ret = new ICover[6];
        for (Direction dir : Ref.DIRS) {
            ICover c = this.covers.get(dir);
            ret[dir.get3DDataValue()] = c == null ? ICover.empty : c;
        }
        return ret;
    }

    public void removeController(BlockEntityBasicMultiMachine<?> controller) {
        if (level != null)
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        this.controller = null;
    }

    public BlockEntityFakeBlock setCovers(Map<Direction, CoverFactory> covers) {
        ICoverHandler<?> handler = ICoverHandler.empty(this);
        for (Map.Entry<Direction, CoverFactory> entry : covers.entrySet()) {
            Direction dir = entry.getKey();
            CoverFactory factory = entry.getValue();
            Direction rot = Utils.coverRotateFacing(dir, facing);
            if (rot.getAxis() == Axis.X) rot = rot.getOpposite();
            ICover cover = factory.get().get(handler, null, rot, factory);
            this.covers.put(
                rot,
                    cover);
        }
        setChanged();
        return this;
    }

    public BlockEntityFakeBlock setFacing(Direction facing) {
        this.facing = facing;
        setChanged();
        return this;
    }

    @Environment(EnvType.CLIENT)
    public DynamicTexturer<ICover, ICover.DynamicKey> getTexturer(Direction side) {
        return coverTexturer.computeIfAbsent(side,
                dir -> new DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @Nullable
    public ICover getCover(Direction side) {
        return covers.get(side);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("F")) {
            this.facing = Direction.from3DDataValue(nbt.getInt("F"));
        } else {
            this.facing = Direction.NORTH;
        }
        if (level != null && level.isClientSide) {
            Utils.markTileForRenderUpdate(this);
        }
        this.covers = new EnumMap<>(Direction.class);
        CompoundTag c = nbt.getCompound("C");
        for (Direction dir : Ref.DIRS) {
            ICover cover = CoverFactory.readCover(ICoverHandler.empty(this), dir, c);
            if (cover != null)
                covers.put(dir, cover);
        }
        if (nbt.contains("P")) {
            controllerPos = BlockPos.of(nbt.getLong("P"));
        }
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        writeTag(nbt, true);
        return nbt;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        writeTag(nbt, false);
    }

    private void writeTag(CompoundTag compound, boolean send) {
        if (facing != null) {
            compound.putInt("F", facing.ordinal());
        }
        CompoundTag n = new CompoundTag();
        this.covers.forEach((k, v) -> CoverFactory.writeCover(n, v, v.side()));
        compound.put("C", n);
        if (!send && controller != null) {
            compound.putLong("P", controller.getBlockPos().asLong());
        }
    }

    public BlockState getState() {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public List<String> getInfo(boolean simple) {
        List<String> list = super.getInfo(simple);
        if (!simple){
            if (getState() != null)
                list.add("State: " + getState().toString());
            if (facing != null)
                list.add("Facing: " + facing.getName());
            covers.forEach((k, v) -> {
                list.add("Cover on " + k.getName() + ": " + v.getId());
            });
            if (controller != null) {
                list.add("Controller position: "
                        + controller.getBlockPos());
            }
        }
        if (controller != null){
            controller.getInfo(simple);
        }
        return list;
    }
}
