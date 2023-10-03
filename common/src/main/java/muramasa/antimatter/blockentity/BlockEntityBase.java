package muramasa.antimatter.blockentity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BlockEntityBase<T extends BlockEntityBase<T>> extends BlockEntity {

    protected final Dispatch dispatch;

    public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        dispatch = new Dispatch();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        onRemove();
    }

    public void onRemove() {
        //NOOP
    }

    public boolean isClientSide() {
        return level.isClientSide;
    }

    public boolean isServerSide() {
        return !level.isClientSide;
    }


    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getSimpleName());
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