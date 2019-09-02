package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;

public class BlockCasing extends BlockBaked {

    private String type;
    protected String harvestTool = "wrench";

    public BlockCasing(String type) {
        super(net.minecraft.block.material.Material.IRON, new TextureData().base(new Texture("blocks/casing/" + type)));
        this.type = type;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setHardness(1.0F);
        setResistance(10.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.METAL);
        register(BlockCasing.class, this);
    }

    public BlockCasing(String type, String harvestTool) {
        this(type);
        this.harvestTool = harvestTool;
    }

    @Override
    public String getId() {
        return "casing_" + type;
    }

    public String getType() {
        return type;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return harvestTool;
    }
}