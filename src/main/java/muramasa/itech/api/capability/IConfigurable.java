package muramasa.itech.api.capability;

import net.minecraft.util.EnumFacing;

public interface IConfigurable {

    void onWrench(EnumFacing side);

    void onCrowbar(EnumFacing side);
}
