package muramasa.antimatter.client.scene;

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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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
    private static DimensionType DIMENSION_TYPE;
    static {
        try {
            DIMENSION_TYPE = (DimensionType) ObfuscationReflectionHelper.findField(DimensionType.class, "OVERWORLD_TYPE").get(DimensionType.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
            blockInfo.getTileEntity().setWorldAndPos(this, pos);
        }
        this.renderedBlocks.put(pos, blockInfo);
        minPos.setX(Math.min(minPos.getX(), pos.getX()));
        minPos.setY(Math.min(minPos.getY(), pos.getY()));
        minPos.setZ(Math.min(minPos.getZ(), pos.getZ()));
        maxPos.setX(Math.max(maxPos.getX(), pos.getX()));
        maxPos.setY(Math.max(maxPos.getY(), pos.getY()));
        maxPos.setZ(Math.max(maxPos.getZ(), pos.getZ()));
    }

    @Override
    public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity) {
        renderedBlocks.put(pos, new BlockInfo(renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState(), tileEntity));
    }

    @Override
    public boolean setBlockState(@Nonnull BlockPos pos, BlockState state, int a, int b) {
        renderedBlocks.put(pos, new BlockInfo(state, renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity()));
        return true;
    }

    @Override
    public TileEntity getTileEntity(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return null;
        return proxyWorld != null ? proxyWorld.getTileEntity(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity();
    }

    @Nonnull
    @Override
    public BlockState getBlockState(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return Blocks.AIR.getDefaultState(); //return air if not rendering this block
        return proxyWorld != null ? proxyWorld.getBlockState(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }

    public Vector3f getSize() {
        Vector3f result = new Vector3f();
        result.setX(maxPos.getX() - minPos.getX() + 1);
        result.setY(maxPos.getY() - minPos.getY() + 1);
        result.setZ(maxPos.getZ() - minPos.getZ() + 1);
        return result;
    }

    public Vector3f getMinPos() {
        return minPos;
    }

    public Vector3f getMaxPos() {
        return maxPos;
    }

    @Override
    public float func_230487_a_(Direction direction, boolean b) {
        switch(direction) {
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
    public WorldLightManager getLightManager() {
        return null;
    }

    @Override
    public int getBlockColor(@Nonnull BlockPos blockPos, @Nonnull ColorResolver colorResolver) {
        return colorResolver.getColor(BiomeRegistry.PLAINS, blockPos.getX(), blockPos.getY());
    }

    @Override
    public int getLightFor(@Nonnull LightType lightType, @Nonnull BlockPos pos) {
        return lightType == LightType.SKY ? 15 : 0;
    }

    @Override
    public int getLightSubtracted(@Nonnull BlockPos pos, int p_226659_2_) {
        return 15;
    }

    @Override
    public boolean canSeeSky(@Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public Biome getNoiseBiomeRaw(int x, int y, int z) {
        return null;
    }


    @Override
    public Entity getEntityByID(int id) {
        return null;
    }

    @Override
    public MapData getMapData(String mapName) {
        return null;
    }

    @Override
    public void registerMapData(MapData mapDataIn) {

    }

    @Override
    public int getNextMapId() {
        return 0;
    }

    @Override
    public void sendBlockBreakProgress(int breakerId,
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
    public ITagCollectionSupplier getTags() {
        return null;
    }

    @Override
    public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {

    }

    @Override
    public void playMovingSound(PlayerEntity playerIn, Entity entityIn,SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch) {

    }

    @Override
    public ITickList<Block> getPendingBlockTicks() {
        return null;
    }

    @Override
    public ITickList<Fluid> getPendingFluidTicks() {
        return null;
    }

    @Override
    public AbstractChunkProvider getChunkProvider() {
        return null;
    }

    @Override
    public void playEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int i1) {

    }

    @Override
    public DynamicRegistries func_241828_r() {
        return null;
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return null;
    }
}
