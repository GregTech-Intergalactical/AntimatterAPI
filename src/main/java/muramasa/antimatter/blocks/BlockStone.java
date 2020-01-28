package muramasa.antimatter.blocks;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockStone extends BlockBasic {

    protected String namespace, id;
    protected StoneType type;

    public BlockStone(String namespace, StoneType type) {
        super(Block.Properties.create(Material.ROCK).sound(type.getSoundType()), type.getTexture());
        this.type = type;
        this.namespace = namespace;
        this.id = getType().getId();
        setRegistryName(getNamespace(), getId());
        type.setBaseState(this.getDefaultState());
        AntimatterAPI.register(BlockStone.class, this);
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public static BlockStone get(StoneType stoneType) {
        return AntimatterAPI.get(BlockStone.class, stoneType.getId());
    }
}
