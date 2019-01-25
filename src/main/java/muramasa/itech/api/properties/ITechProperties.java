package muramasa.itech.api.properties;

import muramasa.itech.api.enums.HatchTexture;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class ITechProperties {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<HatchTexture> HATCH_TEXTURE = PropertyEnum.create("hatch_texture", HatchTexture.class);

    public static final UnlistedString TYPE = new UnlistedString();
    public static final UnlistedString TIER = new UnlistedString();
    public static final UnlistedBoolean ACTIVE = new UnlistedBoolean(); //TODO replce with MachineState
    public static final UnlistedCoverType COVERS = new UnlistedCoverType();
}
