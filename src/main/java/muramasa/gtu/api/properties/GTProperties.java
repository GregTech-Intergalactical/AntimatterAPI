package muramasa.gtu.api.properties;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.pipe.PipeSize;
import net.minecraft.block.properties.PropertyInteger;

public class GTProperties {

    /** Machine Properties **/
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, Tier.getAll().size());
    public static final UnlistedInteger TYPE = new UnlistedInteger();
    public static final UnlistedInteger FACING = new UnlistedInteger();
    public static final UnlistedTextureData TEXTURE = new UnlistedTextureData();
    public static final UnlistedCovers COVER = new UnlistedCovers();


    /** Pipe Properties **/
    public static final PropertyInteger SIZE = PropertyInteger.create("size", 0, PipeSize.values().length);
    public static final UnlistedByte CONNECTIONS = new UnlistedByte();
}
