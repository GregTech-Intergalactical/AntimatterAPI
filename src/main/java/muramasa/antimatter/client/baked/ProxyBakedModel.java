package muramasa.antimatter.client.baked;

import com.google.common.collect.Sets;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Data;
import muramasa.antimatter.AntimatterProperties.ProxyProperties;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.mixin.ChunkReaderAccessor;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
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
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        if (tileData instanceof EmptyModelData) {
            tileData = new ModelDataMap.Builder().build();
        }
        TileEntityFakeBlock fake = (TileEntityFakeBlock) world.getBlockEntity(pos);
        if (fake.getState() == null) {
            return tileData;
        }
        IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(fake.getState());
        IUnbakedModel m = ModelLoader.instance().getModel(BlockModelShapes.stateToModelLocation(fake.getState()));

        Collection<RenderMaterial> mats = m.getMaterials(ModelLoader.defaultModelGetter(), Sets.newLinkedHashSet());
        RenderMaterial first = mats.iterator().next();
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

    protected World getWorld(IBlockDisplayReader reader) {
        if (reader instanceof World) {
            return (World) reader;
        }
        if (reader instanceof ChunkRenderCache) {
            return ((ChunkReaderAccessor)reader).getLevel();
        }
        return null;
    }


    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(data.getData(AntimatterProperties.STATE_MODEL_PROPERTY));
        return model != null ? model.getParticleTexture(data) : getParticleIcon();
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
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
