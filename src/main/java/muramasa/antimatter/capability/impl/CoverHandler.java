package muramasa.antimatter.capability.impl;

import muramasa.gtu.Ref;
import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.machines.MachineEvent;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.tools.GregTechToolType;
import muramasa.antimatter.util.SoundType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import java.util.ArrayList;

public class CoverHandler implements ICoverHandler {

    private TileEntity tile;
    protected ArrayList<String> validCovers;

    //TODO
    protected Cover[] covers = new Cover[] {
        GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone
    };

    public CoverHandler(TileEntity tile, Cover... covers) {
        this.tile = tile;
        //TODO fix valid covers
        validCovers = new ArrayList<>();
        validCovers.add(GregTechAPI.CoverNone.getId());
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            validCovers.add(cover.getId());
        }
    }

    @Override
    public void update() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(getTile(), Ref.DIRECTIONS[i]);
        }
    }

    @Override
    public boolean set(Direction side, Cover cover) {
        if (!isValid(side, covers[side.getIndex()], cover)) return false;
        covers[side.getIndex()] = cover;
        //TODO add cover.onPlace and cover.onRemove to customize sounds
        SoundType.PLACE_METAL.play(getTile().getWorld(), getTile().getPos());
        Utils.markTileForRenderUpdate(getTile());
        return true;
    }

    @Override
    public Cover get(Direction side) {
        return covers[side.getIndex()];
    }

    public Cover[] getAll() {
        return covers;
    }

    @Override /** Fires ones per hand **/
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, GregTechToolType type) {
        Cover cover = get(side);
        if (cover.isEmpty() || !cover.onInteract(getTile(), player, hand, side, type)) return false;
        if (type == null) return false;
        switch (type) {
            case CROWBAR: return GregTechAPI.removeCover(player, this, side);
            default: return false;
        }
    }

    @Override
    public void onMachineEvent(MachineEvent event) {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onMachineEvent((TileEntityMachine) getTile(), event);
        }
    }

    @Override
    public boolean hasCover(Direction side, Cover cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isValid(Direction side, Cover existing, Cover replacement) {
        return (existing.isEmpty() || replacement.isEqual(GregTechAPI.CoverNone)) && validCovers.contains(replacement.getId());
    }

    @Override
    public Direction getTileFacing() {
        return Direction.NORTH;
    }

    @Override
    public TileEntity getTile() {
        if (tile == null) throw new NullPointerException("CoverHandler cannot have a null tile");
        return tile;
    }
}
