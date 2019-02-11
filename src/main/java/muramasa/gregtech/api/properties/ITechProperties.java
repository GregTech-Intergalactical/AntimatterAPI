package muramasa.gregtech.api.properties;

import net.minecraft.block.properties.PropertyInteger;

public class ITechProperties {

    /** Machine Properties **/
    public static final UnlistedInteger TYPE = new UnlistedInteger();
    public static final UnlistedInteger TIER = new UnlistedInteger();
    public static final UnlistedInteger FACING = new UnlistedInteger();
    public static final UnlistedInteger OVERLAY = new UnlistedInteger();
    public static final UnlistedInteger TINT = new UnlistedInteger();
    public static final UnlistedResourceLocation TEXTURE = new UnlistedResourceLocation();
    public static final UnlistedCoverType COVERS = new UnlistedCoverType();

    /** Ore Properties **/
    public static final UnlistedInteger MATERIAL = new UnlistedInteger();
    public static final PropertyInteger STONE = PropertyInteger.create("stone", 0, 6);
}
