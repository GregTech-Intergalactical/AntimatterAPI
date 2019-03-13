package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.util.Sounds;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class ConfigHandler implements IConfigHandler {

    private TileEntity tile;

    public ConfigHandler(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public boolean onWrench(EnumFacing side) {
        if (getTile().hasCapability(GTCapabilities.COVERABLE, side)) { //Side has cover, configure.
            ICoverHandler coverHandler = getTile().getCapability(GTCapabilities.COVERABLE, side);
            if (coverHandler == null) return false;
            Cover cover = coverHandler.get(side);
            if (cover.isEmpty() || !cover.onInteract(getTile(), ToolType.WRENCH)) return false;
            Sounds.WRENCH.play(getTile().getWorld(), getTile().getPos());
            return true;
        }
        return false;
    }

    @Override
    public boolean onCrowbar(EnumFacing side) {
        if (getTile().hasCapability(GTCapabilities.COVERABLE, side)) { //Side has cover
            ICoverHandler coverHandler = getTile().getCapability(GTCapabilities.COVERABLE, side);
            if (coverHandler == null) return false;
            Cover cover = coverHandler.get(side);
            if (cover.isEmpty() || !cover.onInteract(getTile(), ToolType.CROWBAR)) return false;
            coverHandler.set(side, GregTechAPI.CoverNone);
            Sounds.BREAK.play(getTile().getWorld(), getTile().getPos());
            return true;
        }
        return false;
    }

    @Override
    public boolean onScrewdriver(EnumFacing side) {
        if (getTile().hasCapability(GTCapabilities.COVERABLE, side)) { //Side has cover
            ICoverHandler coverHandler = getTile().getCapability(GTCapabilities.COVERABLE, side);
            if (coverHandler == null) return false;
            Cover cover = coverHandler.get(side);
            if (cover.isEmpty() || !cover.onInteract(getTile(), ToolType.SCREWDRIVER)) return false;
            Sounds.WRENCH.play(getTile().getWorld(), getTile().getPos());
            return true;
        }
        return false;
    }

    @Override
    public TileEntity getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
