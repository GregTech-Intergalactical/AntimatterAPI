package muramasa.antimatter.client.scene;

import com.mojang.math.Vector3f;
import muramasa.antimatter.structure.BlockInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: KilaBash
 * @Date: 2021/08/25
 * @Description: TrackedDummyWorld. Used to build a Fake World.
 */
public class TrackedDummyWorld extends Level {
    private static final DimensionType DIMENSION_TYPE;
    static {
        DIMENSION_TYPE = DimensionTypeAccessor.getDEFAULT_OVERWORLD();
    }

    private Predicate<BlockPos> renderFilter;
    private final Level proxyWorld;
    private final Map<BlockPos, BlockInfo> renderedBlocks = new HashMap<>();

    private final Vector3f minPos = new Vector3f(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private final Vector3f maxPos = new Vector3f(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public void setRenderFilter(Predicate<BlockPos> renderFilter) {
        this.renderFilter = renderFilter;
    }

    public TrackedDummyWorld(){
        this(null);
    }

    public TrackedDummyWorld(Level world){
        super(null, null, Holder.direct(DIMENSION_TYPE), null,true, false, 0);
        proxyWorld = world;
    }

    public Map<BlockPos, BlockInfo> getRenderedBlocks() {
        return renderedBlocks;
    }

    public void addBlocks(Map<BlockPos, BlockInfo> renderedBlocks) {
        renderedBlocks.forEach(this::addBlock);
    }

    public void addBlock(BlockPos pos, BlockInfo blockInfo) {
        if (blockInfo.getBlockState().getBlock() == Blocks.AIR)
            return;
        if (blockInfo.getTileEntity() != null) {
            blockInfo.getTileEntity().setLevel(this);
        }
        this.renderedBlocks.put(pos, blockInfo);
        minPos.setX(Math.min(minPos.x(), pos.getX()));
        minPos.setY(Math.min(minPos.y(), pos.getY()));
        minPos.setZ(Math.min(minPos.z(), pos.getZ()));
        maxPos.setX(Math.max(maxPos.x(), pos.getX()));
        maxPos.setY(Math.max(maxPos.y(), pos.getY()));
        maxPos.setZ(Math.max(maxPos.z(), pos.getZ()));
    }

    @Override
    public void setBlockEntity(BlockEntity entity) {
        renderedBlocks.put(entity.getBlockPos(), new BlockInfo(renderedBlocks.getOrDefault(entity.getBlockPos(), BlockInfo.EMPTY).getBlockState(), entity));

    }
    @Override
    public boolean setBlock(@Nonnull BlockPos pos, BlockState state, int a, int b) {
        renderedBlocks.put(pos, new BlockInfo(state, renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity()));
        return true;
    }

    @Override
    public BlockEntity getBlockEntity(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return null;
        return proxyWorld != null ? proxyWorld.getBlockEntity(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity();
    }

    @Nonnull
    @Override
    public BlockState getBlockState(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return Blocks.AIR.defaultBlockState(); //return air if not rendering this block
        return proxyWorld != null ? proxyWorld.getBlockState(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }

    public Vector3f getSize() {
        Vector3f result = new Vector3f();
        result.setX(maxPos.x() - minPos.x() + 1);
        result.setY(maxPos.y() - minPos.y() + 1);
        result.setZ(maxPos.z() - minPos.z() + 1);
        return result;
    }

    public Vector3f getMinPos() {
        return minPos;
    }

    public Vector3f getMaxPos() {
        return maxPos;
    }

    @Override
    public float getShade(Direction direction, boolean b) {
        switch (direction) {
            case DOWN:
            case UP:
                return 0.9F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case WEST:
            case EAST:
                return 0.6F;
            default:
                return 1.0F;
        }
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockPos, @Nonnull ColorResolver colorResolver) {
        return colorResolver.getColor(BuiltinRegistries.BIOME.get(net.minecraft.world.level.biome.Biomes.PLAINS), blockPos.getX(), blockPos.getY());
    }

    @Override
    public int getBrightness(@Nonnull LightLayer lightType, @Nonnull BlockPos pos) {
        return lightType == LightLayer.SKY ? 15 : 0;
    }

    @Override
    public int getRawBrightness(@Nonnull BlockPos pos, int p_226659_2_) {
        return 15;
    }

    @Override
    public boolean canSeeSky(@Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return null;
    }


    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public MapItemSavedData getMapData(String mapName) {
        return null;
    }

    @Override
    public void setMapData(String p_151533_, MapItemSavedData p_151534_) {

    }

    @Override
    public int getFreeMapId() {
        return 0;
    }

    @Override
    public void destroyBlockProgress(int breakerId,
                                     BlockPos pos, int progress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return null;
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return null;
    }

    @Override
    public void playSound(Player player, double x, double y, double z, SoundEvent soundIn, SoundSource category, float volume, float pitch) {

    }

    @Override
    public void playSound(Player playerIn, Entity entityIn, SoundEvent eventIn, SoundSource categoryIn, float volume, float pitch) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return null;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return null;
    }

    @Override
    public ChunkSource getChunkSource() {
        return null;
    }

    @Override
    public void levelEvent(Player playerEntity, int i, BlockPos blockPos, int i1) {

    }

    @Override
    public void gameEvent(@Nullable Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {

    }

    @Override
    public RegistryAccess registryAccess() {
        return null;
    }

    @Override
    public List<? extends Player> players() {
        return null;
    }
}
