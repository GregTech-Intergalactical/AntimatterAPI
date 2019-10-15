package muramasa.gtu.api.blocks;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockCoil extends BlockBaked {

    private String id;
    private int heatCapacity;

    public BlockCoil(String id, int heatCapacity, Block.Properties properties) {
        super(properties, new TextureData().base(new Texture("blocks/coil/" + id)));
        this.id = id;
        this.heatCapacity = heatCapacity;
        setRegistryName(getId());
        GregTechAPI.register(BlockCoil.class, this);
    }

    public BlockCoil(String id, int heatCapacity) {
        this(id, heatCapacity, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
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
    public ToolType getHarvestTool(BlockState state) {
        return GregTechAPI.getWrenchToolType();
    }
}
