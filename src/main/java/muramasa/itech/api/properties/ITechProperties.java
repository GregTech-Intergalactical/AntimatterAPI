package muramasa.itech.api.properties;

public class ITechProperties {

//    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
//    public static final PropertyInteger STATE = PropertyInteger.create("state", 0, MachineState.values().length);
//    public static final PropertyEnum<HatchTexture> HATCH_TEXTURE = PropertyEnum.create("hatch_texture", HatchTexture.class);

    /** Machine Properties **/
    public static final UnlistedInteger TYPE = new UnlistedInteger();
    public static final UnlistedInteger TIER = new UnlistedInteger();
    public static final UnlistedInteger FACING = new UnlistedInteger();
    public static final UnlistedInteger STATE = new UnlistedInteger();
    public static final UnlistedInteger TINT = new UnlistedInteger();
    public static final UnlistedInteger TEXTURE = new UnlistedInteger();
    public static final UnlistedCoverType COVERS = new UnlistedCoverType();

    /** Ore Properties **/
    public static final UnlistedInteger MATERIAL = new UnlistedInteger();
    public static final UnlistedInteger STONE = new UnlistedInteger();
}
