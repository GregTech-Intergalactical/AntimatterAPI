package muramasa.antimatter.dynamic;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.client.baked.AntimatterBakedModel;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        if (!hasConfig || !(state.getBlock() instanceof BlockDynamic)) return data;
        if (data instanceof EmptyModelData) {
            data = new ModelDataMap.Builder().build();
        }
        mutablePos.set(pos);
        data.setData(AntimatterProperties.DYNAMIC_CONFIG, ((BlockDynamic) state.getBlock()).getConfig(state, world, mutablePos, pos));
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityBase)
            data.setData(AntimatterProperties.TILE_PROPERTY, (TileEntityBase) tile);
        return data;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (!hasConfig || data == null) return Collections.emptyList();//bakedDefault.getQuads(state, side, rand, data);
        List<BakedQuad> quads = new LinkedList<>();
        ModelConfig config = data.getData(AntimatterProperties.DYNAMIC_CONFIG);
        if (config == null) return Collections.emptyList();
        List<BakedQuad> configQuads = config.getQuads(new LinkedList<>(), bakedConfigs, state, side, rand, data);
        //if (Arrays.stream(config.config).anyMatch(t -> t == -1) || configQuads.size() == 0) {
        //    quads.addAll(bakedDefault.getQuads(state, side, rand, data));
        //}
        if (configQuads.size() > 0) quads.addAll(configQuads);
        return quads;
    }

    @Override
    public List<BakedQuad> getItemQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return Collections.emptyList();
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
