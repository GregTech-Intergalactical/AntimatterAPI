package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockProxy;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class TileEntityFakeBlock extends TileEntityBase {

    private BlockState state;

    private final Set<TileEntityBasicMultiMachine> controllers = new ObjectOpenHashSet<>();
    private Map<Direction, ICover> covers = new EnumMap<>(Direction.class);
    public Direction facing;

    public final Map<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;

    public TileEntityFakeBlock(BlockProxy block) {
        super(block.TYPE);
        coverTexturer = new Object2ObjectOpenHashMap<>();
    }

    public void addController(TileEntityBasicMultiMachine controller) {
        controllers.add(controller);
    }

    public void removeController(TileEntityBasicMultiMachine controller) {
        controllers.remove(controller);
    }

    public TileEntityFakeBlock setCovers(Map<Direction, ICover> covers) {
        this.covers = covers;
        for (Map.Entry<Direction, ICover> entry : covers.entrySet()) {
            Direction dir = entry.getKey();
            covers.put(Utils.coverRotateFacing(dir, facing.getAxis() == Direction.Axis.X ? facing.getOpposite() : facing), entry.getValue());
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
        return coverTexturer.computeIfAbsent(side, dir -> new DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        for (TileEntityBasicMultiMachine controller : controllers) {
            LazyOptional<T> opt = controller.getCapabilityFromFake(cap, getPos(), side, covers.get(side));
            if (opt.isPresent()) return opt;
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
            String id = c.getString(dir.getName2());
            if (id.isEmpty()) continue;
            covers.put(dir, AntimatterAPI.get(ICover.class, id));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        nbt.put("B", NBTUtil.writeBlockState(state));
        nbt.putInt("F", facing.ordinal());
        CompoundNBT n = new CompoundNBT();
        this.covers.forEach((k,v) -> n.putString(k.getName2(), v.getId()));
        compound.put("C", n);
        return nbt;
    }

    @Nonnull
    public BlockState getState() {
        return state;
    }

    public TileEntityFakeBlock setState(BlockState state) {
        this.state = state;
        return this;
    }
}
