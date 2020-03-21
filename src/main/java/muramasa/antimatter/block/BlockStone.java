package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockStone extends BlockBasic {

    protected String id;
    protected StoneType type;

    public BlockStone(String domain, StoneType type) {
        super(domain, type.getId(), Block.Properties.create(Material.ROCK).sound(type.getSoundType()), type.getTexture());
        this.type = type;
        AntimatterAPI.register(BlockStone.class, this);
    }

    public StoneType getType() {
        return type;
    }

    public static BlockStone get(StoneType stoneType) {
        return AntimatterAPI.get(BlockStone.class, stoneType.getId());
    }
}
