package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.cover.CoverBehaviour;
import net.minecraft.util.EnumFacing;

public interface ICoverHandler {

    void update();

    boolean setCover(EnumFacing side, CoverBehaviour stack);

    CoverBehaviour get(EnumFacing side);

    CoverBehaviour[] getBehaviours();

    boolean hasCover(EnumFacing side, CoverBehaviour cover);

    boolean isBehaviourValid(CoverBehaviour stack);
}
