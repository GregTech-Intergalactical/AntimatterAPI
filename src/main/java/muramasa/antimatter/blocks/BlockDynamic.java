package muramasa.antimatter.blocks;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Arrays;

public abstract class BlockDynamic extends Block implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected ResourceLocation defaultModel = new ResourceLocation(Ref.MODID, "block/preset/simple");
    protected Texture[] defaultTextures;

    public BlockDynamic(Block.Properties properties, Texture... defaultTextures) {
        super(properties);
        this.defaultTextures = defaultTextures;
    }

    public BlockDynamic setDefaultModel(ResourceLocation model) {
        this.defaultModel = model;
        return this;
    }

    public ResourceLocation getDefaultModel() {
        return defaultModel;
    }

    @Override
    public Texture[] getTextures() {
        return defaultTextures;
    }

    /** Connection Logic **/
    public int[] getConfig(BlockState state, IBlockReader world, BlockPos.MutableBlockPos mut, BlockPos pos) {
        int[] ct = new int[1];
        for (int s = 0; s < 6; s++) {
            if (canConnect(world, mut.setPos(pos.offset(Ref.DIRECTIONS[s])))) ct[0] += 1 << s;
        }
        return ct;
    }

    public boolean canConnect(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        player.sendMessage(new StringTextComponent(Arrays.toString(getConfig(state, world, new BlockPos.MutableBlockPos(pos), pos))));
        return true;
    }
}
