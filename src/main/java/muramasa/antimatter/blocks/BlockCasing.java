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

    private String type;
    private Texture[] textures;

    public BlockCasing(String type, Block.Properties properties) {
        super(properties, new Texture("block/casing/" + type));
        this.type = type;
        setRegistryName(getId());
        AntimatterAPI.register(BlockCasing.class, this);
    }

    public BlockCasing(String type) {
        this(type, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
    }

    public BlockCasing(String type, Texture[] textures) {
        this(type);
        this.textures = textures;
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
    public ToolType getHarvestTool(BlockState state) {
        return AntimatterAPI.WRENCH_TOOL_TYPE;
    }

    @Override
    public Texture[] getConfigTextures() {
        return textures != null ? textures : new Texture[0];
    }

    @Override
    public void onConfigBuild() {
        if (textures != null) buildBasic(this, textures);
    }
}