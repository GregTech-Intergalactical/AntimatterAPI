package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeBakedModel extends DynamicBakedModel {

    public static final Int2ObjectOpenHashMap<IBakedModel[]> CONFIGS = new Int2ObjectOpenHashMap<>();

    public PipeBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<IBakedModel[]> map) {
        super(particle, map);
        onlyGeneralQuads();
    }

    @Override
    public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData data) {
        data = super.getModelData(world, pos, state, data);
        data.setData(AntimatterProperties.TILE_PROPERTY, ((TileEntityPipe) world.getTileEntity(pos)));
        return data;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, Random rand, IModelData data) {
        if (!data.hasProperty(AntimatterProperties.TILE_PROPERTY))
            return super.getBlockQuads(state, side, rand, data);
        TileEntityPipe<?> pipe = (TileEntityPipe<?>) data.getData(AntimatterProperties.TILE_PROPERTY);
        PipeCoverHandler<?> covers = pipe.coverHandler.orElse(null);
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, data);
        List<BakedQuad> coverQuads = new LinkedList<>();
        if (covers == null)
            return quads;
        if (side == null) {
            for (Direction dir : Ref.DIRS) {
                Texture tex = ((BlockPipe<?>) state.getBlock()).getFace();
                ICover c = covers.get(dir);
                if (c.isEmpty())
                    continue;
                // Depth model only causes z fighting of sizes larger than tiny.
                if (pipe.canConnect(dir.getIndex()) && pipe.getPipeSize().compareTo(PipeSize.TINY) > 0) {
                    int index = RenderHelper.findPipeFront(pipe.getPipeSize(), quads, dir);
                    if (index != -1) {
                        quads.remove(index);
                    }
                }
                coverQuads = covers.getTexturer(side).getQuads("pipe", coverQuads, state, c,
                        new BaseCover.DynamicKey(dir, tex, c.getId()), dir.getIndex(), CoverBakedModel.addCoverModelData(dir, covers, data));
            }
        }
        quads.addAll(coverQuads);
        return quads;
    }

}
