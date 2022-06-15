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
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeBakedModel extends DynamicBakedModel {

    public static final Int2ObjectOpenHashMap<BakedModel[]> CONFIGS = new Int2ObjectOpenHashMap<>();

    public PipeBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<BakedModel[]> map) {
        super(particle, map);
        onlyGeneralQuads();
    }

    @Override
    public @NotNull IModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, @NotNull IModelData data) {
        data = super.getModelData(world, pos, state, data);
        data.setData(AntimatterProperties.TILE_PROPERTY, ((TileEntityPipe) world.getBlockEntity(pos)));
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
                if (pipe.canConnect(dir.get3DDataValue())) { //&& pipe.getPipeSize().compareTo(PipeSize.TINY) > 0) {
                    int index = RenderHelper.findPipeFront(pipe.getPipeSize(), quads, dir);
                    if (index != -1) {
                        quads.remove(index);
                    }
                }
                coverQuads = covers.getTexturer(dir).getQuads("pipe", coverQuads, state, c,
                        new BaseCover.DynamicKey(dir, null, tex, c.getId()), dir.get3DDataValue(), CoverBakedModel.addCoverModelData(dir, covers, data));
            }
        }
        quads.addAll(coverQuads);
        return quads;
    }

}
