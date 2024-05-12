package muramasa.antimatter.dynamic;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.baked.AntimatterBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DynamicBakedModel extends AntimatterBakedModel<DynamicBakedModel> {

    //protected IBakedModel bakedDefault;
    protected Int2ObjectOpenHashMap<BakedModel[]> bakedConfigs;
    protected boolean hasConfig;
    protected BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    public DynamicBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<BakedModel[]> map) {
        super(particle);
        this.bakedConfigs = map;
        this.hasConfig = bakedConfigs.size() > 0;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos) {
        if (!hasConfig || !(state.getBlock() instanceof BlockDynamic dynamic)) return Collections.emptyList();//bakedDefault.getQuads(state, side, rand, data);
        List<BakedQuad> quads = new LinkedList<>();
        ModelConfig config = dynamic.getConfig(state, level, mutablePos, pos);
        if (config == null) return Collections.emptyList();
        List<BakedQuad> configQuads = config.getQuads(new LinkedList<>(), bakedConfigs, state, side, rand, level, pos);
        //if (Arrays.stream(config.config).anyMatch(t -> t == -1) || configQuads.size() == 0) {
        //    quads.addAll(bakedDefault.getQuads(state, side, rand, data));
        //}
        if (configQuads.size() > 0) quads.addAll(configQuads);
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
