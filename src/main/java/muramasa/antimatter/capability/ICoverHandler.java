package muramasa.antimatter.capability;

import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public interface ICoverHandler<T extends TileEntity> extends ICapabilitySerializable<CompoundNBT> {

    /** Getters/Setters **/
    boolean set(Direction side, ICover cover, boolean sync);

    CoverStack<T> get(Direction side);

    CoverStack<?>[] getAll();

    Direction getTileFacing();

    T getTile();

    /** Events **/
    void onRemove();

    void onUpdate();

    //If the player uses a cover in hand -> place cover if none exists.. Otherwises interact with the cover, if present.
    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type);

    /** Helpers **/
    boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover);

    /**
     * Removes a cover.
     * @param player entity.
     * @param side which side to remove.
     * @return
     */
    boolean removeCover(PlayerEntity player, Direction side, boolean onlyRemove);

    boolean hasCover(Direction side, ICover cover);

    boolean isValid(Direction side, ICover replacement);

    boolean moveCover(PlayerEntity entity, Direction oldSide, Direction newSide);
}
