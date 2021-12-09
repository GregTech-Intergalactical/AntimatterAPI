package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
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

    public final Set<TileEntityBasicMultiMachine<?>> controllers = new ObjectOpenHashSet<>();
    private Map<Direction, ICover> covers = new EnumMap<>(Direction.class);
    public Direction facing;

    public final Map<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;

    private List<BlockPos> controllerPos; 

    public TileEntityFakeBlock(BlockProxy proxy, BlockPos pos, BlockState state) {
        super(proxy.TYPE, pos, state);
        coverTexturer = new Object2ObjectOpenHashMap<>();
    }

    public void addController(TileEntityBasicMultiMachine<?> controller) {
        if (level != null)
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        controllers.add(controller);
    }

    public ICover[] covers() {
        ICover[] ret = new ICover[6];
        for (Direction dir : Ref.DIRS) {
            ICover c = this.covers.get(dir);
            ret[dir.get3DDataValue()] = c == null ? ICover.empty : c;
        }
        return ret;
    }

    public void removeController(TileEntityBasicMultiMachine<?> controller) {
        if (level != null)
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        controllers.remove(controller);
    }

    public TileEntityFakeBlock setCovers(Map<Direction, CoverFactory> covers) {
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

    public TileEntityFakeBlock setFacing(Direction facing) {
        this.facing = facing;
        setChanged();
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
            controllerPos.forEach(t -> controllers.add((TileEntityBasicMultiMachine<?>) level.getBlockEntity(t)));
            controllerPos = null;
        }
        for (TileEntityBasicMultiMachine<?> controller : controllers) {
            LazyOptional<T> opt = controller.getCapabilityFromFake(cap, getBlockPos(), side, covers.get(side));
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
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.state = NbtUtils.readBlockState(nbt.getCompound("B"));
        this.facing = Direction.from3DDataValue(nbt.getInt("F"));
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
            ListTag list = nbt.getList("P", 4);
            this.controllerPos = new ObjectArrayList<>(list.size());
            list.forEach(n -> controllerPos.add(BlockPos.of(((LongTag) n).getAsLong())));
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
    public CompoundTag getUpdateTag() {
        return this.writeTag(new CompoundTag(), true);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        writeTag(nbt, false);
    }

    private CompoundTag writeTag(CompoundTag compound, boolean send) {
        CompoundTag nbt = super.save(compound);
        nbt.put("B", NbtUtils.writeBlockState(state));
        nbt.putInt("F", facing.ordinal());
        CompoundTag n = new CompoundTag();
        this.covers.forEach((k, v) -> CoverFactory.writeCover(n, v));
        compound.put("C", n);
        if (!send) {
            ListTag list = new ListTag();
            for (TileEntityBasicMultiMachine<?> controller : controllers) {
                list.add(LongTag.valueOf(controller.getBlockPos().asLong()));
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
            list.add("Facing: " + facing.getName());
        covers.forEach((k, v) -> {
            list.add("Cover on " + k.getName() + ": " + v.getId());
        });
        if (controllers.size() > 0) {
            list.add("Controller positions: "
                    + controllers.stream().map(t -> t.getBlockPos().toString()).reduce((k, v) -> k + ", " + v).orElse(""));
        }
        return list;
    }
}
