package muramasa.antimatter.block;

import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;

public class BlockStone extends BlockBasic {

    protected StoneType type;

    public BlockStone(StoneType type) {
        super(type.getDomain(), type.getId(), getProps(type));
        this.type = type;
    }

    private static Properties getProps(StoneType type){
        Properties props = Block.Properties.create(type.getBlockMaterial()).sound(type.getSoundType()).harvestLevel(type.getHarvestLevel()).harvestTool(type.getToolType()).hardnessAndResistance(type.getHardness(), type.getResistence());
        if (type.doesRequireTool()){
            props.setRequiresTool();
        }
        return props;
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{type.getTexture()};
    }
}
