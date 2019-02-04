package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.enums.CoverType;
import net.minecraft.util.EnumFacing;

public interface ICoverable {

    boolean setCover(EnumFacing side, CoverType coverType);

    CoverType getCover(EnumFacing side);

    boolean hasCover(EnumFacing side, CoverType coverType);

    boolean isCoverValid(CoverType coverType);
}
