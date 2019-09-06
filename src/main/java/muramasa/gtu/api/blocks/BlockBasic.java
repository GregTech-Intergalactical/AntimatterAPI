package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockBasic extends BlockBaked {

    private String type;

    public BlockBasic(String type) {
        super(Material.ROCK, new TextureData().base(new Texture("blocks/basic/" + type)));
        this.type = type;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setHardness(1.0F);
        setResistance(1.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.STONE);
        register(BlockBasic.class, this);
    }

    @Override
    public String getId() {
        return "basic_" + type;
    }

    public String getType() {
        return type;
    }
}
