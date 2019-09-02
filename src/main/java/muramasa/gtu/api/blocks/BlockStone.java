package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.texture.TextureData;

public class BlockStone extends BlockBaked {

    private StoneType type;

    public BlockStone(StoneType type) {
        super(net.minecraft.block.material.Material.ROCK, new TextureData().base(type.getTexture()));
        this.type = type;
        setSoundType(type.getSoundType());
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_BLOCKS);
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
