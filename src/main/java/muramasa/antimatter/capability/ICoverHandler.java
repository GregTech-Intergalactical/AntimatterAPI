package muramasa.antimatter.capability;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface ICoverHandler<T extends TileEntity> extends ICapabilitySerializable<CompoundNBT> {

    /**
     * Getters/Setters
     **/
    boolean set(Direction side, ICover cover, boolean sync);

    ICover get(Direction side);

    ICover[] getAll();

    T getTile();

    /**
     * Events
     **/
    void onRemove();

    void onUpdate();

    // If the player uses a cover in hand -> place cover if none exists.. Otherwises
    // interact with the cover, if present.
    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type);

    /**
     * Helpers
     **/
    boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover);

    /**
     * Removes a cover.
     *
     * @param player entity.
     * @param side   which side to remove.
     * @return
     */
    boolean removeCover(PlayerEntity player, Direction side, boolean onlyRemove);

    boolean hasCover(Class<? extends ICover> clazz);

    boolean isValid(Direction side, ICover replacement);

    boolean moveCover(PlayerEntity entity, Direction oldSide, Direction newSide);

    public static ICoverHandler<?> empty(TileEntity tile) {
        return new EmptyHandler(tile);
    }

    static class EmptyHandler implements ICoverHandler<TileEntity> {

        TileEntity tile;

        private final ICover[] COVERS = new ICover[]{ICover.empty, ICover.empty, ICover.empty, ICover.empty,
                ICover.empty, ICover.empty};

        EmptyHandler(TileEntity tile) {
            this.tile = tile;
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return new CompoundNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {

        }

        @Override
        public boolean set(Direction side, ICover cover, boolean sync) {
            return false;
        }

        @Override
        public ICover get(Direction side) {
            return ICover.empty;
        }

        @Override
        public ICover[] getAll() {
            return COVERS;
        }

        @Override
        public TileEntity getTile() {
            return tile;
        }

        @Override
        public void onRemove() {

        }

        @Override
        public void onUpdate() {

        }

        @Override
        public boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type) {
            return false;
        }

        @Override
        public boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover) {
            return false;
        }

        @Override
        public boolean removeCover(PlayerEntity player, Direction side, boolean onlyRemove) {
            return false;
        }

        @Override
        public boolean hasCover(Class<? extends ICover> clazz) {
            return false;
        }

        @Override
        public boolean isValid(Direction side, ICover replacement) {
            return false;
        }

        @Override
        public boolean moveCover(PlayerEntity entity, Direction oldSide, Direction newSide) {
            return false;
        }
    }
}
