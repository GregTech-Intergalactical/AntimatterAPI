package muramasa.itech.api.capability;

import muramasa.itech.api.enums.CoverType;
import net.minecraft.util.EnumFacing;

public interface ICoverable {

    void setCover(EnumFacing side, CoverType coverType);

    CoverType getCover(EnumFacing side);

    boolean hasCover(EnumFacing side, CoverType coverType);

    boolean isCoverValid(CoverType coverType);
}
