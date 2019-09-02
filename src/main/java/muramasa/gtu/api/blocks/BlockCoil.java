package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;

public class BlockCoil extends BlockBaked {

    private String id;
    private int heatCapacity;

    public BlockCoil(String id, int heatCapacity) {
        super(net.minecraft.block.material.Material.IRON, new TextureData().base(new Texture("blocks/coil/" + id)));
        this.id = id;
        this.heatCapacity = heatCapacity;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setHardness(1.0F);
        setResistance(10.0F);
        setCreativeTab(Ref.TAB_BLOCKS);
        setSoundType(SoundType.METAL);
        GregTechAPI.register(BlockCoil.class, this);
    }

    @Override
    public String getId() {
        return "coil_" + id;
    }

    public int getHeatCapacity() {
        return heatCapacity;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wrench";
    }
}
