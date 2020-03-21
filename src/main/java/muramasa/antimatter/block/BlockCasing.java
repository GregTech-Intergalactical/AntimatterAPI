package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockCasing extends BlockDynamic  {

    public BlockCasing(String domain, String id, Block.Properties properties) {
        super(domain, "casing_" + id, properties, new Texture(domain, "block/casing/" + id));
        AntimatterAPI.register(BlockCasing.class, this);
    }

    public BlockCasing(String domain, String id) {
        this(domain, id, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return AntimatterAPI.WRENCH_TOOL_TYPE;
    }
}