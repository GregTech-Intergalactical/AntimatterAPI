package muramasa.gtu.api.properties;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.ore.OreType;
import muramasa.gtu.api.pipe.PipeSize;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;

public class GTProperties {

    /** Block Machine Properties **/
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, Tier.getAll().size());
    public static final UnlistedInteger TYPE = new UnlistedInteger();
    public static final UnlistedInteger FACING = new UnlistedInteger();
    public static final UnlistedTextureData TEXTURE = new UnlistedTextureData();
    public static final UnlistedCovers COVER = new UnlistedCovers();

    /** Block Pipe Properties **/
    public static final PropertyInteger PIPE_SIZE = PropertyInteger.create("size", 0, PipeSize.VALUES.length);
    public static final UnlistedByte PIPE_CONNECTIONS = new UnlistedByte();

    /** Block Ore Properties **/
    public static final UnlistedInteger ORE_MATERIAL = new UnlistedInteger();
    public static final PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("ore_type", OreType.class);

    /** Block Storage Properties **/
    public static final PropertyInteger STORAGE_TYPE = PropertyInteger.create("storage_type", 0, 1);
}
