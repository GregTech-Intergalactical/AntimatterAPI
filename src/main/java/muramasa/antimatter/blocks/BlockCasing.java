package muramasa.antimatter.blocks;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockCasing extends BlockDynamic {

    protected String namespace, id;

    public BlockCasing(String namespace, String id, Block.Properties properties) {
        super(properties, new Texture(namespace, "block/casing/" + id));
        this.namespace = namespace;
        this.id = "casing_" + id;
        setRegistryName(getNamespace(), getId());
        AntimatterAPI.register(BlockCasing.class, this);
    }

    public BlockCasing(String namespace, String id) {
        this(namespace, id, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return AntimatterAPI.WRENCH_TOOL_TYPE;
    }
}