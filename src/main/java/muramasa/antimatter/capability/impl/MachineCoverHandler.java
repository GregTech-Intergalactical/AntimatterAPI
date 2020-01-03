package muramasa.antimatter.capability.impl;

import muramasa.gtu.Ref;
import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.tools.GregTechToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class MachineCoverHandler extends RotatableCoverHandler {

    protected int outputSide = 3;

    public MachineCoverHandler(TileEntityMachine tile) {
        //TODO add valid covers to Machine class
        super(tile, GregTechAPI.CoverPlate, GregTechAPI.CoverOutput);
        covers = new Cover[] {
            GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverOutput, GregTechAPI.CoverNone, GregTechAPI.CoverNone
        };
    }

    public Direction getOutputFacing() {
        return Utils.rotateFacingAlt(Ref.DIRECTIONS[outputSide], getTileFacing());
    }

    public boolean setOutputFacing(Direction side) {
        if (set(side, GregTechAPI.CoverOutput)) {
            if (covers[outputSide].isEqual(GregTechAPI.CoverOutput)) covers[outputSide] = GregTechAPI.CoverNone;
            outputSide = Utils.rotateFacing(side, getTileFacing()).getIndex();
            return true;
        }
        return false;
    }

    @Override
    public boolean set(Direction side, Cover cover) {
        if (cover.isEqual(GregTechAPI.CoverNone) && Utils.rotateFacing(side, getTileFacing()).getIndex() == outputSide) {
            super.set(side, GregTechAPI.CoverNone);
            return super.set(side, GregTechAPI.CoverOutput);
        }
        return super.set(side, cover);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, GregTechToolType type) {
        if (type == GregTechToolType.CROWBAR && get(side).isEqual(GregTechAPI.CoverOutput)) return false;
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public boolean isValid(Direction side, Cover existing, Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        return (existing.isEqual(GregTechAPI.CoverOutput) && !replacement.isEqual(GregTechAPI.CoverNone)) || super.isValid(side, existing, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return ((TileEntityMachine) getTile()).getFacing();
    }
}
