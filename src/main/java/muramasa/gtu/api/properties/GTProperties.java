package muramasa.gtu.api.properties;

import muramasa.gtu.api.machines.Tier;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;

public class GTProperties {

    /** Block Machine Properties **/
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, Tier.getAll().size());
    public static final UnlistedInteger TYPE = new UnlistedInteger();
    public static final UnlistedInteger FACING = new UnlistedInteger();
    public static final UnlistedTextureData TEXTURE = new UnlistedTextureData();
    public static final UnlistedCovers COVER = new UnlistedCovers();

    /** Block Pipe Properties **/
    public static PropertyBool PIPE_INSULATED = PropertyBool.create("insulated");
    public static PropertyBool PIPE_RESTRICTIVE = PropertyBool.create("restrictive");
    public static final UnlistedByte PIPE_CONNECTIONS = new UnlistedByte();

    /** Block Ore Properties **/
    //public static final UnlistedInteger ORE_SET = new UnlistedInteger();
    //public static final PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("ore_type", OreType.class);

    /** Block Rock Properties **/
    public static final UnlistedInteger ROCK_MODEL = new UnlistedInteger();
}
