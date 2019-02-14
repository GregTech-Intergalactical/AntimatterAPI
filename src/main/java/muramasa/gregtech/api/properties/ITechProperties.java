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
    public static final UnlistedCovers COVERS = new UnlistedCovers();

    /** Ore Properties **/
    public static final UnlistedString MATERIAL = new UnlistedString();
    public static final PropertyInteger STONE = PropertyInteger.create("stone", 0, 6);
}
