package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.cover.CoverBehaviour;
import net.minecraft.util.EnumFacing;

public interface ICoverHandler {

    void update();

    boolean setCover(EnumFacing side, CoverBehaviour cover);

    CoverBehaviour get(EnumFacing side);

    CoverBehaviour[] getCovers();

    boolean hasCover(EnumFacing side, CoverBehaviour cover);

    boolean isCoverValid(EnumFacing side, CoverBehaviour cover);
}
