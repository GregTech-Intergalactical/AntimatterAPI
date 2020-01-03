package muramasa.antimatter.capability.impl;

import muramasa.gtu.Ref;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.tools.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class MachineCoverHandler extends RotatableCoverHandler {

    protected int outputSide = 3;

    public MachineCoverHandler(TileEntityMachine tile) {
        //TODO add valid covers to Machine class
        super(tile, AntimatterAPI.CoverPlate, AntimatterAPI.CoverOutput);
        covers = new Cover[] {
            AntimatterAPI.CoverNone, AntimatterAPI.CoverNone, AntimatterAPI.CoverNone, AntimatterAPI.CoverOutput, AntimatterAPI.CoverNone, AntimatterAPI.CoverNone
        };
    }

    public Direction getOutputFacing() {
        return Utils.rotateFacingAlt(Ref.DIRECTIONS[outputSide], getTileFacing());
    }

    public boolean setOutputFacing(Direction side) {
        if (set(side, AntimatterAPI.CoverOutput)) {
            if (covers[outputSide].isEqual(AntimatterAPI.CoverOutput)) covers[outputSide] = AntimatterAPI.CoverNone;
            outputSide = Utils.rotateFacing(side, getTileFacing()).getIndex();
            return true;
        }
        return false;
    }

    @Override
    public boolean set(Direction side, Cover cover) {
        if (cover.isEqual(AntimatterAPI.CoverNone) && Utils.rotateFacing(side, getTileFacing()).getIndex() == outputSide) {
            super.set(side, AntimatterAPI.CoverNone);
            return super.set(side, AntimatterAPI.CoverOutput);
        }
        return super.set(side, cover);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type) {
        if (type == AntimatterToolType.CROWBAR && get(side).isEqual(AntimatterAPI.CoverOutput)) return false;
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public boolean isValid(Direction side, Cover existing, Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        return (existing.isEqual(AntimatterAPI.CoverOutput) && !replacement.isEqual(AntimatterAPI.CoverNone)) || super.isValid(side, existing, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return ((TileEntityMachine) getTile()).getFacing();
    }
}
