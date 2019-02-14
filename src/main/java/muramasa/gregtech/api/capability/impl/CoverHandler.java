package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.cover.CoverBehaviour;
import muramasa.gregtech.api.util.SoundList;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;

public class CoverHandler implements ICoverHandler {

    private TileEntityBase tile;
    private ArrayList<String> validCovers;

    private CoverBehaviour[] behaviours = new CoverBehaviour[] {
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone,
        GregTechAPI.CoverBehaviourNone
    };

    public CoverHandler(TileEntityBase tile, CoverBehaviour... behaviours) {
        this.tile = tile;
        validCovers = new ArrayList<>();
        for (CoverBehaviour behaviour : behaviours) {
            validCovers.add(behaviour.getName());
        }
    }

    @Override
    public void update() {
        for (int i = 0; i < behaviours.length; i++) {
            if (behaviours[i].isEmpty()) continue;
            behaviours[i].onUpdate(tile);
        }
    }

    @Override
    public boolean setCover(EnumFacing side, CoverBehaviour behaviour) {
        if (tile == null) return false;
        if ((isBehaviourValid(behaviour) && behaviours[side.getIndex()] != behaviour) || behaviour.isEmpty()) {
            behaviours[side.getIndex()] = behaviour;
            SoundList.PLACE_METAL.play(tile.getWorld(), tile.getPos());
            tile.markForRenderUpdate();
            return true;
        }
        return false;
    }

    @Override
    public CoverBehaviour get(EnumFacing side) {
        return behaviours[side.ordinal()];
    }

    public CoverBehaviour[] getBehaviours() {
        return behaviours;
    }

    @Override
    public boolean hasCover(EnumFacing side, CoverBehaviour behaviour) {
        return get(side).isEqual(behaviour);
    }

    @Override
    public boolean isBehaviourValid(CoverBehaviour behaviour) {
        return validCovers.contains(behaviour.getName());
    }
}
