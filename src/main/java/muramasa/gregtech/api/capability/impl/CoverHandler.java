package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.tools.ToolType;
import muramasa.gregtech.api.util.Sounds;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

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
        validCovers.add(GregTechAPI.CoverNone.getName());
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            validCovers.add(cover.getName());
        }
    }

    @Override
    public void tick() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(getTile());
        }
    }

    @Override
    public boolean set(EnumFacing side, Cover cover) {
        side = Utils.rotateFacing(side, getTileFacing());
        if (!isValid(side, cover) || covers[side.getIndex()] == cover) return false;
        covers[side.getIndex()] = cover;
        Sounds.PLACE_METAL.play(getTile().getWorld(), getTile().getPos());
        Utils.markTileForRenderUpdate(getTile());
        return true;
    }

    @Override
    public boolean onInteract(EntityPlayer player, EnumHand hand, EnumFacing side, ToolType type) {
        Cover cover = get(side);
        if (cover.isEmpty() || !cover.onInteract(getTile(), player, hand, side, type)) return false;
        if (type == null) return false;
        switch (type) {
            case CROWBAR:
                player.dropItem(cover.getCatalystUsed(), false);
                set(side, GregTechAPI.CoverNone);
//                Utils.spawnItem(tile, side, cover.getCatalystUsed());
                return true;
            default: return false;
        }
    }

    @Override
    public Cover get(EnumFacing side) {
        return covers[Utils.rotateFacing(side, getTileFacing()).getIndex()];
    }

    public Cover[] getAll() {
        return covers;
    }

    @Override
    public boolean hasCover(EnumFacing side, Cover cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isValid(EnumFacing side, Cover cover) {
        return validCovers.contains(cover.getName());
    }

    @Override
    public EnumFacing getTileFacing() {
        return EnumFacing.NORTH;
    }

    @Override
    public TileEntity getTile() {
        if (tile == null) throw new NullPointerException("CoverHandler cannot have a null tile");
        return tile;
    }
}
