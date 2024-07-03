package muramasa.antimatter.blockentity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class BlockEntityBase<T extends BlockEntityBase<T>> extends BlockEntity {

    protected final Dispatch dispatch;
    protected final Cache<Direction, BlockEntity> blockEntityCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        dispatch = new Dispatch();
    }

    public BlockEntity getCachedBlockEntity(Direction side){
        if (level == null) return null;
        try {
            BlockEntity entity;
            if (!blockEntityCache.asMap().containsKey(side)){
                entity = level.getBlockEntity(this.getBlockPos().relative(side));
                if (entity == null) return null;
            } else {
                entity = null;
            }
            return blockEntityCache.get(side, () -> entity);
        } catch (ExecutionException e) {
            Antimatter.LOGGER.error(e);
            return null;
        }
    }

    public void onBlockUpdate(BlockPos neighbor) {
        Direction facing = Utils.getOffsetFacing(this.getBlockPos(), neighbor);
        if (facing != null) {
            blockEntityCache.invalidate(facing);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        onRemove();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        BlockEntityCache.addBlockEntity(this.level, this.getBlockPos(), this);
    }

    public void onRemove() {
        BlockEntityCache.removeBlockEntity(this.level, this.getBlockPos());
    }

    public boolean isClientSide() {
        return level.isClientSide;
    }

    public boolean isServerSide() {
        return !level.isClientSide;
    }


    //TODO pass constant StringBuilder
    public List<String> getInfo(boolean simple) {
        List<String> info = new ObjectArrayList<>();
        if (!simple) info.add("Tile: " + getClass().getSimpleName());
        return info;
    }

    public void sidedSync(boolean renderUpdate) {
        if (this.remove) return;
        if (this.getLevel() == null) return;
        if (!this.getLevel().isClientSide) {
            this.setChanged();
            Utils.markTileForNBTSync(this);
        } else if (renderUpdate) {
            Utils.markTileForRenderUpdate(this);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //TODO figure this out
    //@Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag compoundtag = pkt.getTag();
        if (compoundtag != null) {
            load(compoundtag);
        }
        //handleUpdateTag(pkt.getTag());
        sidedSync(true);
    }

    public static class BlockEntityGetter<T extends BlockEntity, U> implements BlockEntityType.BlockEntitySupplier<T> {

        private final U value;
        private final BlockEntitySupplier<T, U> supplier;
        public BlockEntityGetter(BlockEntitySupplier<T, U> supp, U value) {
            this.value = value;
            this.supplier = supp;
        }
        @Override
        public T create(BlockPos p_155268_, BlockState p_155269_) {
            return this.supplier.create(value, p_155268_, p_155269_);
        }
    }

    public interface BlockEntitySupplier<T extends BlockEntity,U> {
        T create(U obj, BlockPos pos, BlockState state);
    }

    //TODO: implications of this.
    /*@NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.write(tag);
        return tag;
    }*/
}
