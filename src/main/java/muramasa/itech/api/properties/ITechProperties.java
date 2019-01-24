package muramasa.itech.api.properties;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

public class ITechProperties {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public static final UnlistedString TYPE = new UnlistedString();
    public static final UnlistedString TIER = new UnlistedString();
    public static final UnlistedBoolean ACTIVE = new UnlistedBoolean(); //TODO replce with MachineState
    public static final UnlistedCoverType COVERS = new UnlistedCoverType();
}
