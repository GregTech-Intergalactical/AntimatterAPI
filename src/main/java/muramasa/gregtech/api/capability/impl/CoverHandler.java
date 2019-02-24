package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.cover.CoverBehaviour;
import muramasa.gregtech.api.util.SoundList;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;

public class CoverHandler implements ICoverHandler {

    protected TileEntityBase tile;
    protected ArrayList<String> validCovers;

    private CoverBehaviour[] covers = new CoverBehaviour[] {
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone
    };

    public CoverHandler(TileEntityBase tile, CoverBehaviour... covers) {
        this.tile = tile;
        validCovers = new ArrayList<>();
        validCovers.add(GregTechAPI.CoverBehaviourNone.getName());
        for (CoverBehaviour cover : covers) {
            validCovers.add(cover.getName());
        }
    }

    @Override
    public void tick() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(tile);
        }
    }

    @Override
    public boolean setCover(EnumFacing side, CoverBehaviour cover) {
        if (tile == null) return false;
        if (isCoverValid(side, cover) && covers[side.getIndex()] != cover) {
            covers[side.getIndex()] = cover;
            SoundList.PLACE_METAL.play(tile.getWorld(), tile.getPos());
            tile.markForRenderUpdate();
            return true;
        }
        return false;
    }

    @Override
    public CoverBehaviour get(EnumFacing side) {
        return covers[side.ordinal()];
    }

    public CoverBehaviour[] getCovers() {
        return covers;
    }

    @Override
    public boolean hasCover(EnumFacing side, CoverBehaviour cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isCoverValid(EnumFacing side, CoverBehaviour cover) {
        return validCovers.contains(cover.getName());
    }
}
