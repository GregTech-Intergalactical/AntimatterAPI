package muramasa.antimatter.client.baked;

import com.google.common.collect.Sets;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.mixin.client.ChunkReaderAccessor;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.texture.Texture;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ProxyBakedModel extends AntimatterBakedModel<ProxyBakedModel> {

    public ProxyBakedModel(TextureAtlasSprite particle) {
        super(particle);
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
    public TextureAtlasSprite getParticleIcon(BlockAndTintGetter getter, BlockPos pos) {
        if (!(getter.getBlockEntity(pos) instanceof TileEntityFakeBlock fakeBlock)) return getParticleIcon();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(fakeBlock.getState());
        return model.getParticleIcon();
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull BlockAndTintGetter level, BlockPos pos)
    {
        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof TileEntityFakeBlock fake) || fake.getState() == null) {
            return Collections.emptyList();
        }
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(fake.getState());
        UnbakedModel m = ModelUtils.getModel(BlockModelShaper.stateToModelLocation(fake.getState()));

        Collection<Material> mats = m.getMaterials(ModelUtils.getDefaultModelGetter(), Sets.newLinkedHashSet());
        Material first = mats.iterator().next();
        BlockState cState = Blocks.AIR.defaultBlockState();
        TileEntityBasicMultiMachine<?> machine = StructureCache.getAnyMulti(getWorld(level), fake.getBlockPos(), TileEntityBasicMultiMachine.class);
        if (machine != null) {
            cState = machine.getBlockState();
        }
        if (side == null)
            return ModelUtils.getQuadsFromBaked(model, fake.getState(), side, rand, level, pos);
        ICover cover = fake.covers()[side.get3DDataValue()];
        if (cover.isEmpty())
            return ModelUtils.getQuadsFromBaked(model, fake.getState(), side, rand, level, pos);
        DynamicTexturer<ICover, ICover.DynamicKey> texturer = fake.getTexturer(side);
        return texturer.getQuads("fake", new LinkedList<>(), cState, cover, new ICover.DynamicKey(cover.side(), null, new Texture(first.texture().toString()), cover.getId()), side.get3DDataValue(), level, pos);
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
