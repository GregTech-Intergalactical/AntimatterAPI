package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.cover.CoverStack;
import net.minecraft.util.EnumFacing;

public interface ICoverable {

    boolean setCover(EnumFacing side, CoverStack stack);

    CoverStack get(EnumFacing side);

    CoverStack[] getCovers();

    boolean hasCover(EnumFacing side, Cover cover);

    boolean isCoverValid(CoverStack stack);
}
