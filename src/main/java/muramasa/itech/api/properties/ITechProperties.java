package muramasa.itech.api.properties;

import muramasa.itech.api.enums.HatchTexture;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class ITechProperties {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
//    public static final PropertyInteger STATE = PropertyInteger.create("state", 0, MachineState.values().length);
    public static final PropertyEnum<HatchTexture> HATCH_TEXTURE = PropertyEnum.create("hatch_texture", HatchTexture.class);

    public static final UnlistedInteger TYPE = new UnlistedInteger();
    public static final UnlistedInteger TIER = new UnlistedInteger();
    public static final UnlistedMachineState STATE = new UnlistedMachineState();
    public static final UnlistedInteger TINT = new UnlistedInteger();
    public static final UnlistedInteger TEXTURE = new UnlistedInteger();
    public static final UnlistedCoverType COVERS = new UnlistedCoverType();
}
