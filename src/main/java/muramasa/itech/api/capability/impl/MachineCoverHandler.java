package muramasa.itech.api.capability.impl;

import muramasa.itech.api.capability.ICoverable;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.util.SoundList;
import muramasa.itech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Arrays;

public class MachineCoverHandler implements ICoverable {

    private TileEntityBase tile;
    private ArrayList<CoverType> validCovers;

    private CoverType[] covers = new CoverType[] {
        CoverType.NONE, CoverType.NONE, CoverType.NONE, CoverType.NONE, CoverType.NONE, CoverType.NONE
    };

    public MachineCoverHandler(TileEntityBase tile, CoverType... covers) {
        this.tile = tile;
        validCovers = new ArrayList<>(Arrays.asList(covers));
    }

    @Override
    public boolean setCover(EnumFacing side, CoverType coverType) {
        if (tile == null) return false;
        if (coverType == null) coverType = CoverType.NONE;
        if (isCoverValid(coverType) && side != EnumFacing.NORTH && covers[side.getIndex()] != coverType) {
            covers[side.getIndex()] = coverType;
            SoundList.PLACE_METAL.play(tile.getWorld(), tile.getPos());
            tile.markForRenderUpdate();
            return true;
        }
        return false;
    }

    @Override
    public CoverType getCover(EnumFacing side) {
        if (side == null || side == EnumFacing.NORTH) {
            return CoverType.NONE;
        } else {
            return covers[side.ordinal()];
        }
    }

    @Override
    public boolean hasCover(EnumFacing side, CoverType coverType) {
        return getCover(side) == coverType;
    }

    @Override
    public boolean isCoverValid(CoverType coverType) {
        return coverType == CoverType.NONE || validCovers.contains(coverType);
    }
}
