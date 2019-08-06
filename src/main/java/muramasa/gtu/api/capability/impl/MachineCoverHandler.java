package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class MachineCoverHandler extends RotatableCoverHandler {

    protected int outputSide = 3;

    public MachineCoverHandler(TileEntityMachine tile) {
        //TODO add valid covers to Machine class
        super(tile, GregTechAPI.CoverPlate, GregTechAPI.CoverOutput);
        covers = new Cover[] {
            GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverOutput, GregTechAPI.CoverNone, GregTechAPI.CoverNone
        };
    }

    public EnumFacing getOutputFacing() {
        return Utils.rotateFacingAlt(EnumFacing.VALUES[outputSide], getTileFacing());
    }

    public boolean setOutputFacing(EnumFacing side) {
        if (set(side, GregTechAPI.CoverOutput)) {
            if (covers[outputSide].isEqual(GregTechAPI.CoverOutput)) covers[outputSide] = GregTechAPI.CoverNone;
            outputSide = Utils.rotateFacing(side, getTileFacing()).getIndex();
            return true;
        }
        return false;
    }

    @Override
    public boolean set(EnumFacing side, Cover cover) {
        if (cover.isEqual(GregTechAPI.CoverNone) && Utils.rotateFacing(side, getTileFacing()).getIndex() == outputSide) {
            super.set(side, GregTechAPI.CoverNone);
            return super.set(side, GregTechAPI.CoverOutput);
        }
        return super.set(side, cover);
    }

    @Override
    public boolean onInteract(EntityPlayer player, EnumHand hand, EnumFacing side, ToolType type) {
        if (type == ToolType.CROWBAR && get(side).isEqual(GregTechAPI.CoverOutput)) return false;
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public boolean isValid(EnumFacing side, Cover existing, Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        return (existing.isEqual(GregTechAPI.CoverOutput) && !replacement.isEqual(GregTechAPI.CoverNone)) || super.isValid(side, existing, replacement);
    }

    @Override
    public EnumFacing getTileFacing() {
        return ((TileEntityMachine) getTile()).getFacing();
    }
}
