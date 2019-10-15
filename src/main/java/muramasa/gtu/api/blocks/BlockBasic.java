package muramasa.gtu.api.blocks;

import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockBasic extends BlockBaked {

    private String type;

    public BlockBasic(String type, Block.Properties properties) {
        super(properties, new TextureData().base(new Texture("blocks/basic/" + type)));
        this.type = type;
        setRegistryName(getId());
        register(BlockBasic.class, this);
    }

    public BlockBasic(String type) {
        this(type, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 1.0f).sound(SoundType.STONE));
    }

    @Override
    public String getId() {
        return "basic_" + type;
    }

    public String getType() {
        return type;
    }
}
