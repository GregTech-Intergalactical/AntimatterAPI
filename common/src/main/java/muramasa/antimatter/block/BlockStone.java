package muramasa.antimatter.block;

import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.world.level.block.Block;

public class BlockStone extends BlockBasic implements ISharedAntimatterObject {

    protected StoneType type;
    protected String suffix;

    public BlockStone(StoneType type) {
        super(type.getDomain(), type.getId(), getProps(type));
        this.type = type;
        this.suffix = "";
    }

    public BlockStone(StoneType type, String suffix) {
        super(type.getDomain(), type.getId() + "_" + suffix, getProps(type));
        this.type = type;
        this.suffix = suffix;
    }

    private static Properties getProps(StoneType type) {
        Properties props = Properties.of(type.getBlockMaterial()).sound(type.getSoundType()).strength(type.getHardness(), type.getResistence());
        if (type.doesRequireTool()) {
            props.requiresCorrectToolForDrops();
        }
        return props;
    }

    public StoneType getType() {
        return type;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public Texture[] getTextures() {
        if (type instanceof CobbleStoneType c && !suffix.isEmpty()) {
            return new Texture[]{new Texture(type.getDomain(), c.getBeginningPath() + type.getId() + "/" + suffix)};
        }
        return new Texture[]{type.getTexture()};
    }
}
