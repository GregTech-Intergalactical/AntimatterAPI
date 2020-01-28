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

    protected String namespace, id;
    protected int heatCapacity;

    public BlockCoil(String namespace, String id, int heatCapacity, Block.Properties properties) {
        super(properties, new Texture(namespace, "block/coil/" + id));
        this.namespace = namespace;
        this.id = "coil_" + id;
        this.heatCapacity = heatCapacity;
        setRegistryName(getNamespace(), getId());
        AntimatterAPI.register(BlockCoil.class, this);
    }

    public BlockCoil(String namespace, String id, int heatCapacity) {
        this(namespace, id, heatCapacity, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
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
