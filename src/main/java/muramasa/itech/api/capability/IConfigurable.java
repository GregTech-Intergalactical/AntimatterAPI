package muramasa.itech.api.capability;

import net.minecraft.util.EnumFacing;

public interface IConfigurable {

    boolean onWrench(EnumFacing side);

    boolean onCrowbar(EnumFacing side);
}
