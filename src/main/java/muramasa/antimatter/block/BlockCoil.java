package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockCoil extends BlockBasic {

    protected int heatCapacity;

    public BlockCoil(String domain, String id, int heatCapacity, Block.Properties properties) {
        super(domain, "coil_" + id, properties, new Texture(domain, "block/coil/" + id));
        this.heatCapacity = heatCapacity;
        AntimatterAPI.register(BlockCoil.class, this);
    }

    public BlockCoil(String domain, String id, int heatCapacity) {
        this(domain, id, heatCapacity, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
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
