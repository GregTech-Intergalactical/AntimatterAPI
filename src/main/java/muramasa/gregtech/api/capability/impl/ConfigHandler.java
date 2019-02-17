package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.cover.CoverBehaviour;
import muramasa.gregtech.api.util.SoundList;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class ConfigHandler implements IConfigHandler {

    protected TileEntityBase tile;

    public ConfigHandler(TileEntityBase tile) {
        this.tile = tile;
    }

    @Override
    public boolean onWrench(EnumFacing side) {
        if (tile == null) return false;
        if (tile.hasCapability(GTCapabilities.COVERABLE, side)) { //Side has cover, configure.
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
            if (coverHandler == null) return false;
            CoverBehaviour cover = coverHandler.get(side);
            if (!cover.isEmpty()) {
                cover.onWrench(tile);
                SoundList.WRENCH.play(tile.getWorld(), tile.getPos());
                return true;
            }
        } else { //Used wrench on side with no cover, rotate.
            if (tile instanceof TileEntityMachine) {
                ((TileEntityMachine) tile).setFacing(side);
            }
        }
        return true;
    }

    @Override
    public boolean onCrowbar(EnumFacing side) {
        if (tile == null) return false;
        if (tile.hasCapability(GTCapabilities.COVERABLE, side)) { //Side has cover
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
            if (coverHandler != null && !coverHandler.get(side).isEmpty()) {
                coverHandler.get(side).onCrowbar(tile);
                coverHandler.setCover(side, GregTechAPI.CoverBehaviourNone);
                SoundList.BREAK.play(tile.getWorld(), tile.getPos());
                return true;
            }
        } else { //Used crowbar on side with no cover
            //NOOP
        }
        return false;
    }

    @Override
    public boolean onScrewdriver(EnumFacing side) {
        if (tile == null) return false;
        if (tile.hasCapability(GTCapabilities.COVERABLE, side)) { //Side has cover
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
            if (coverHandler != null && !coverHandler.get(side).isEmpty()) {
                coverHandler.get(side).onScrewdriver(tile);
                SoundList.WRENCH.play(tile.getWorld(), tile.getPos());
            }
        } else { //Used screwdriver on side with no cover
            //NOOP
        }
        return false;
    }
}
