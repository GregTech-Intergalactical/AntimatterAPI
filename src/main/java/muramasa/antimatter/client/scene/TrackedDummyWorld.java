package muramasa.antimatter.client.scene;

import muramasa.antimatter.mixin.DimensionTypeAccessor;
import muramasa.antimatter.structure.BlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.MapData;

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
public class TrackedDummyWorld extends World {
    private static final DimensionType DIMENSION_TYPE;
    static {
        DIMENSION_TYPE = DimensionTypeAccessor.getDEFAULT_OVERWORLD();
    }

    private Predicate<BlockPos> renderFilter;
    private final World proxyWorld;
    private final Map<BlockPos, BlockInfo> renderedBlocks = new HashMap<>();

    private final Vector3f minPos = new Vector3f(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private final Vector3f maxPos = new Vector3f(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public void setRenderFilter(Predicate<BlockPos> renderFilter) {
        this.renderFilter = renderFilter;
    }

    public TrackedDummyWorld(){
        this(null);
    }

    public TrackedDummyWorld(World world){
        super(null, null, DIMENSION_TYPE, null,true, false, 0);
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
            blockInfo.getTileEntity().setLevelAndPosition(this, pos);
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
    public void setBlockEntity(@Nonnull BlockPos pos, TileEntity tileEntity) {
        renderedBlocks.put(pos, new BlockInfo(renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState(), tileEntity));
    }

    @Override
    public boolean setBlock(@Nonnull BlockPos pos, BlockState state, int a, int b) {
        renderedBlocks.put(pos, new BlockInfo(state, renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity()));
        return true;
    }

    @Override
    public TileEntity getBlockEntity(@Nonnull BlockPos pos) {
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
    public WorldLightManager getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockPos, @Nonnull ColorResolver colorResolver) {
        return colorResolver.getColor(BiomeRegistry.PLAINS, blockPos.getX(), blockPos.getY());
    }

    @Override
    public int getBrightness(@Nonnull LightType lightType, @Nonnull BlockPos pos) {
        return lightType == LightType.SKY ? 15 : 0;
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
    public Biome getUncachedNoiseBiome(int x, int y, int z) {
        return null;
    }


    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public MapData getMapData(String mapName) {
        return null;
    }

    @Override
    public void setMapData(MapData mapDataIn) {

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
    public ITagCollectionSupplier getTagManager() {
        return null;
    }

    @Override
    public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {

    }

    @Override
    public void playSound(PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch) {

    }

    @Override
    public ITickList<Block> getBlockTicks() {
        return null;
    }

    @Override
    public ITickList<Fluid> getLiquidTicks() {
        return null;
    }

    @Override
    public AbstractChunkProvider getChunkSource() {
        return null;
    }

    @Override
    public void levelEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int i1) {

    }

    @Override
    public DynamicRegistries registryAccess() {
        return null;
    }

    @Override
    public List<? extends PlayerEntity> players() {
        return null;
    }
}
