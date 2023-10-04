package muramasa.antimatter.capability;

import earth.terrarium.botarium.util.Serializable;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface ICoverHandler<T extends BlockEntity> extends Serializable {

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
    InteractionResult onInteract(Player player, InteractionHand hand, Direction side, @Nullable AntimatterToolType type);

    /**
     * Helpers
     **/
    boolean placeCover(Player player, Direction side, ItemStack stack, ICover cover);

    /**
     * Removes a cover.
     *
     * @param player entity.
     * @param side   which side to remove.
     * @return
     */
    boolean removeCover(Player player, Direction side, boolean onlyRemove);

    boolean hasCover(Class<? extends ICover> clazz);

    boolean isValid(Direction side, ICover replacement);

    boolean moveCover(Player entity, Direction oldSide, Direction newSide);

    public static ICoverHandler<?> empty(BlockEntity tile) {
        return new EmptyHandler(tile);
    }

    static class EmptyHandler implements ICoverHandler<BlockEntity> {

        BlockEntity tile;

        private final ICover[] COVERS = new ICover[]{ICover.empty, ICover.empty, ICover.empty, ICover.empty,
                ICover.empty, ICover.empty};

        EmptyHandler(BlockEntity tile) {
            this.tile = tile;
        }

        @Override
        public CompoundTag serialize(CompoundTag nbt) {
            return new CompoundTag();
        }

        @Override
        public void deserialize(CompoundTag nbt) {

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
        public BlockEntity getTile() {
            return tile;
        }

        @Override
        public void onRemove() {

        }

        @Override
        public void onUpdate() {

        }

        @Override
        public InteractionResult onInteract(Player player, InteractionHand hand, Direction side, AntimatterToolType type) {
            return InteractionResult.PASS;
        }

        @Override
        public boolean placeCover(Player player, Direction side, ItemStack stack, ICover cover) {
            return false;
        }

        @Override
        public boolean removeCover(Player player, Direction side, boolean onlyRemove) {
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
        public boolean moveCover(Player entity, Direction oldSide, Direction newSide) {
            return false;
        }
    }
}
