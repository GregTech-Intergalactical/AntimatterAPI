package muramasa.antimatter.blocks;

import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockStone extends BlockBaked {

    private StoneType type;

    public BlockStone(StoneType type) {
        super(Block.Properties.create(Material.ROCK).sound(type.getSoundType()), new TextureData().base(type.getTexture()));
        this.type = type;
        setRegistryName(getId());
        type.setBaseState(this.getDefaultState());
        GregTechAPI.register(BlockStone.class, this);
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public String getId() {
        return type.getId();
    }

    public static BlockStone get(StoneType stoneType) {
        return GregTechAPI.get(BlockStone.class, stoneType.getId());
    }
}
