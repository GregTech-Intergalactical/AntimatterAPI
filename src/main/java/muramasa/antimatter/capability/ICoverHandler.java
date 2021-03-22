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
import java.util.function.Function;

public interface ICoverHandler<T extends TileEntity> extends ICapabilitySerializable<CompoundNBT> {

    /** Getters/Setters **/
    boolean set(Direction side, ICover cover);

    CoverStack<T> get(Direction side);

    CoverStack<?>[] getAll();

    Direction getTileFacing();

    T getTile();
    //Returns a lambda that, given a direction returns the given Cover.
    default Function<Direction, CoverStack> getCoverFunction() {
        return this::get;
    }

    /** Events **/
    void onRemove();

    void onUpdate();

    //If the player uses a cover in hand -> place cover if none exists.. Otherwises interact with the cover, if present.
    boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type);

    /** Helpers **/
    boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover);

    boolean removeCover(PlayerEntity player, Direction side, boolean drop);

    boolean hasCover(Direction side, ICover cover);

    boolean isValid(Direction side, ICover replacement);

    boolean moveCover(PlayerEntity entity, Direction oldSide, Direction newSide);
}
