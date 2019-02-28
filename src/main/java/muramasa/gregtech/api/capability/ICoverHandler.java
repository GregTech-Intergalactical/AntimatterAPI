package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.cover.Cover;
import net.minecraft.util.EnumFacing;

public interface ICoverHandler {

    void tick();

    boolean setCover(EnumFacing side, Cover cover);

    Cover get(EnumFacing side);

    Cover[] getCovers();

    boolean hasCover(EnumFacing side, Cover cover);

    boolean isCoverValid(EnumFacing side, Cover cover);
}
