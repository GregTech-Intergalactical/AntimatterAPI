package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeBakedModel extends DynamicBakedModel {

    public static final Int2ObjectOpenHashMap<BakedModel[]> CONFIGS = new Int2ObjectOpenHashMap<>();

    public PipeBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<BakedModel[]> map) {
        super(particle, map);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, Random rand, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TileEntityPipe<?> pipe)) return super.getBlockQuads(state, side, rand, level, pos);
        if (side != null && pipe.getPipeSize().ordinal() < 6) return Collections.emptyList();
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, level, pos);
        PipeCoverHandler<?> covers = pipe.coverHandler.orElse(null);
        List<BakedQuad> coverQuads = new LinkedList<>();
        if (covers == null)
            return quads;
        if (side == null && pipe.getPipeSize().ordinal() < 6) {
            for (Direction dir : Ref.DIRS) {
                Texture tex = ((BlockPipe<?>) state.getBlock()).getFace();
                ICover c = covers.get(dir);
                if (c.isEmpty())
                    continue;
                // Depth model only causes z fighting of sizes larger than tiny.
                if (pipe.canConnect(dir.get3DDataValue())) { //&& pipe.getPipeSize().compareTo(PipeSize.TINY) > 0) {
                    int index = RenderHelper.findPipeFront(pipe.getPipeSize(), quads, dir);
                    if (index != -1) {
                        quads.remove(index);
                    }
                }
                coverQuads = covers.getTexturer(dir).getQuads("pipe", coverQuads, state, c,
                        new BaseCover.DynamicKey(dir, tex, c.getId()), dir.get3DDataValue(), level, pos);//CoverBakedModel.addCoverModelData(dir, covers));
            }
        } else if (side != null){
            Texture tex = pipe.connects(side) ? ((BlockPipe<?>) state.getBlock()).getFace() : ((BlockPipe<?>) state.getBlock()).getSide();
            ICover c = covers.get(side);
            if (!c.isEmpty()){
                coverQuads = covers.getTexturer(side).getQuads("pipe_full", coverQuads, state, c,
                        new BaseCover.DynamicKey(side, tex, c.getId()), side.get3DDataValue(), level, pos);
                return coverQuads;
            }
        }
        quads.addAll(coverQuads);
        return quads;
    }

}
