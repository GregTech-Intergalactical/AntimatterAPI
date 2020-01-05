package muramasa.antimatter.blocks;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockCoil extends BlockBasic {

    private String id;
    private int heatCapacity;

    public BlockCoil(String id, int heatCapacity, Block.Properties properties) {
        super(properties, new Texture("block/coil/" + id));
        this.id = id;
        this.heatCapacity = heatCapacity;
        setRegistryName(getId());
        AntimatterAPI.register(BlockCoil.class, this);
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
        return AntimatterAPI.WRENCH_TOOL_TYPE;
    }
}
