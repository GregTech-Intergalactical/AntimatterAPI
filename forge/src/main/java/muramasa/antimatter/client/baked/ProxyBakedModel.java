package muramasa.antimatter.client.baked;

import com.google.common.collect.Sets;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.AntimatterProperties.ProxyProperties;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.mixin.client.ChunkReaderAccessor;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ProxyBakedModel extends AntimatterBakedModel<ProxyBakedModel> {

    public ProxyBakedModel(TextureAtlasSprite particle) {
        super(particle);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        if (tileData instanceof EmptyModelData) {
            tileData = new ModelDataMap.Builder().build();
        }
        TileEntityFakeBlock fake = (TileEntityFakeBlock) world.getBlockEntity(pos);
        if (fake == null || fake.getState() == null) {
            return tileData;
        }
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(fake.getState());
        UnbakedModel m = ForgeModelBakery.instance().getModel(BlockModelShaper.stateToModelLocation(fake.getState()));

        Collection<Material> mats = m.getMaterials(ForgeModelBakery.defaultModelGetter(), Sets.newLinkedHashSet());
        Material first = mats.iterator().next();
        tileData = model.getModelData(world, pos, state, tileData);
        BlockState cState = Blocks.AIR.defaultBlockState();
        TileEntityBasicMultiMachine<?> machine = StructureCache.getAnyMulti(getWorld(world), fake.getBlockPos(), TileEntityBasicMultiMachine.class);
        if (machine != null) {
            cState = machine.getBlockState();
        }
        ProxyProperties prop = new ProxyProperties(fake.getState(), cState, first.texture(), fake::getTexturer, fake.covers(), fake.facing);
        tileData.setData(AntimatterProperties.FAKE_MODEL_PROPERTY, prop);
        return tileData;
    }

    protected Level getWorld(BlockAndTintGetter reader) {
        if (reader instanceof Level l) {
            return l;
        }
        if (reader instanceof RenderChunkRegion region) {
            return ((ChunkReaderAccessor)region).getLevel();
        };
        return null;
    }


    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(data.getData(AntimatterProperties.STATE_MODEL_PROPERTY));
        return model != null ? model.getParticleIcon(data) : getParticleIcon();
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        ProxyProperties props = data.getData(AntimatterProperties.FAKE_MODEL_PROPERTY);
        if (props == null || props.state == null) return Collections.emptyList();
        if (side == null)
            return Minecraft.getInstance().getBlockRenderer().getBlockModel(props.state).getQuads(props.state, side, rand, data);
        ICover cover = props.covers[side.get3DDataValue()];
        if (cover.isEmpty())
            return Minecraft.getInstance().getBlockRenderer().getBlockModel(props.state).getQuads(props.state, side, rand, data);
        DynamicTexturer<ICover, ICover.DynamicKey> texturer = props.texturer.apply(side);
        return texturer.getQuads("fake", new LinkedList<>(), props.controllerState, cover, new ICover.DynamicKey(cover.side(), null, props.texture, cover.getId()), side.get3DDataValue(), data);
    }

    @Override
    public List<BakedQuad> getItemQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
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
        return true;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
