package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockProxy;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TileEntityFakeBlock extends TileEntityBase<TileEntityFakeBlock> {

    private BlockState state;

    private final Set<TileEntityBasicMultiMachine<?>> controllers = new ObjectOpenHashSet<>();
    private Map<Direction, ICover> covers = new EnumMap<>(Direction.class);
    public Direction facing;

    public final Map<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;

    private List<BlockPos> controllerPos;

    public TileEntityFakeBlock(BlockProxy block) {
        super(block.TYPE);
        coverTexturer = new Object2ObjectOpenHashMap<>();
    }

    public void addController(TileEntityBasicMultiMachine<?> controller) {
        if (world != null)
            world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
        controllers.add(controller);
    }

    public void removeController(TileEntityBasicMultiMachine<?> controller) {
        if (world != null)
            world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
        controllers.remove(controller);
    }

    public TileEntityFakeBlock setCovers(Map<Direction, CoverFactory> covers) {
        ICoverHandler<?> handler = ICoverHandler.empty(this);
        for (Map.Entry<Direction, CoverFactory> entry : covers.entrySet()) {
            Direction dir = entry.getKey();
            CoverFactory factory = entry.getValue();
            ICover cover = factory.get().get(handler, null, dir, factory);
            this.covers.put(
                    Utils.coverRotateFacing(dir, facing.getAxis() == Direction.Axis.X ? facing.getOpposite() : facing),
                    cover);
        }
        markDirty();
        return this;
    }

    public TileEntityFakeBlock setFacing(Direction facing) {
        this.facing = facing;
        markDirty();
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public DynamicTexturer<ICover, ICover.DynamicKey> getTexturer(Direction side) {
        return coverTexturer.computeIfAbsent(side,
                dir -> new DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (controllerPos != null) {
            controllerPos.forEach(t -> controllers.add((TileEntityBasicMultiMachine<?>) world.getTileEntity(t)));
            controllerPos = null;
        }
        for (TileEntityBasicMultiMachine<?> controller : controllers) {
            LazyOptional<T> opt = controller.getCapabilityFromFake(cap, getPos(), side, covers.get(side));
            if (opt.isPresent())
                return opt;
        }
        return LazyOptional.empty();
    }

    @Nullable
    public ICover getCover(Direction side) {
        return covers.get(side);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.state = NBTUtil.readBlockState(nbt.getCompound("B"));
        this.facing = Direction.byIndex(nbt.getInt("F"));
        if (world != null && world.isRemote) {
            Utils.markTileForRenderUpdate(this);
        }
        this.covers = new EnumMap<>(Direction.class);
        CompoundNBT c = nbt.getCompound("C");
        for (Direction dir : Ref.DIRS) {
            ICover cover = CoverFactory.readCover(ICoverHandler.empty(this), dir, c);
            if (cover != null)
                covers.put(dir, cover);
        }
        if (nbt.contains("P")) {
            ListNBT list = nbt.getList("P", 4);
            this.controllerPos = new ObjectArrayList<>(list.size());
            list.forEach(n -> controllerPos.add(BlockPos.fromLong(((LongNBT) n).getLong())));
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(AntimatterProperties.STATE_MODEL_PROPERTY, getState())
                .withInitial(AntimatterProperties.TILE_PROPERTY, this).build();
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return this.writeTag(new CompoundNBT(), true);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeTag(compound, false);
    }

    private CompoundNBT writeTag(CompoundNBT compound, boolean send) {
        CompoundNBT nbt = super.write(compound);
        nbt.put("B", NBTUtil.writeBlockState(state));
        nbt.putInt("F", facing.ordinal());
        CompoundNBT n = new CompoundNBT();
        this.covers.forEach((k, v) -> CoverFactory.writeCover(n, v));
        compound.put("C", n);
        if (!send) {
            ListNBT list = new ListNBT();
            for (TileEntityBasicMultiMachine<?> controller : controllers) {
                list.add(LongNBT.valueOf(controller.getPos().toLong()));
            }
            compound.put("P", list);
        }
        return nbt;
    }

    public BlockState getState() {
        return state;
    }

    public TileEntityFakeBlock setState(BlockState state) {
        this.state = state;
        return this;
    }

    @Override
    public List<String> getInfo() {
        List<String> list = super.getInfo();
        if (getState() != null)
            list.add("State: " + getState().toString());
        if (facing != null)
            list.add("Facing: " + facing.getName2());
        covers.forEach((k, v) -> {
            list.add("Cover on " + k.getName2() + ": " + v.getId());
        });
        if (controllers.size() > 0) {
            list.add("Controller positions: "
                    + controllers.stream().map(t -> t.getPos().toString()).reduce((k, v) -> k + ", " + v).orElse(""));
        }
        return list;
    }
}
