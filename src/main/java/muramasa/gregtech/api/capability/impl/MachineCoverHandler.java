package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.ICoverable;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.cover.CoverStack;
import muramasa.gregtech.api.util.SoundList;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;

public class MachineCoverHandler implements ICoverable {

    private TileEntityBase tile;
    private ArrayList<String> validCovers;

    private CoverStack[] covers = new CoverStack[] {
        new CoverStack(Cover.NONE),
        new CoverStack(Cover.NONE),
        new CoverStack(Cover.NONE),
        new CoverStack(Cover.NONE),
        new CoverStack(Cover.NONE),
        new CoverStack(Cover.NONE)
    };

    public MachineCoverHandler(TileEntityBase tile, Cover... covers) {
        this.tile = tile;
        validCovers = new ArrayList<>();
        for (Cover cover : covers) {
            validCovers.add(cover.getName());
        }
    }

    @Override
    public boolean setCover(EnumFacing side, CoverStack stack) {
        if (tile == null) return false;
        if (stack == null) stack = new CoverStack(Cover.NONE);
        if (isCoverValid(stack) && side != EnumFacing.NORTH && covers[side.getIndex()] != stack) {
            covers[side.getIndex()] = stack;
            SoundList.PLACE_METAL.play(tile.getWorld(), tile.getPos());
            tile.markForRenderUpdate();
            return true;
        }
        return false;
    }

    @Override
    public CoverStack get(EnumFacing side) {
        return covers[side.ordinal()];
    }

    @Override
    public CoverStack[] getCovers() {
        return covers;
    }

    @Override
    public boolean hasCover(EnumFacing side, Cover cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isCoverValid(CoverStack stack) {
        return stack.isEqual(Cover.NONE) || validCovers.contains(stack.getCover().getName());
    }
}
