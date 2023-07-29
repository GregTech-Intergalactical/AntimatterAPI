package muramasa.antimatter.worldgen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.feature.FeatureOre;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class WorldGenHelper {

  public static Object2ObjectOpenHashMap<BlockState, StoneType> STONE_MAP = new Object2ObjectOpenHashMap<>();
  public static ObjectOpenHashSet<BlockState> ROCK_SET = new ObjectOpenHashSet<>();
  public static ObjectOpenHashSet<BlockState> STONE_SET = new ObjectOpenHashSet<>();
  public static ObjectOpenHashSet<BlockState> TREE_SET = new ObjectOpenHashSet<>();
  public static ObjectOpenHashSet<String> TREE_BIOME_SET = new ObjectOpenHashSet<>();

  public static BlockState STONE_STATE = Blocks.STONE.defaultBlockState();
  public static BlockState WATER_STATE = Blocks.WATER.defaultBlockState();

  public static Predicate<BlockState> ORE_PREDICATE = state -> STONE_MAP.containsKey(state);
  public static Predicate<BlockState> ROCK_PREDICATE = state -> ROCK_SET.contains(state);
  public static Predicate<BlockState> STONE_PREDICATE = state -> STONE_SET.contains(state);

  public static void init() {
    AntimatterAPI.all(StoneType.class).forEach(t -> STONE_MAP.put(t.getState(), t));

    ROCK_SET.add(Blocks.AIR.defaultBlockState());
    ROCK_SET.add(Blocks.WATER.defaultBlockState());

    STONE_SET.add(Blocks.STONE.defaultBlockState());
    STONE_SET.add(Blocks.GRANITE.defaultBlockState());
    STONE_SET.add(Blocks.ANDESITE.defaultBlockState());
    STONE_SET.add(Blocks.DIORITE.defaultBlockState());
    STONE_SET.add(Blocks.END_STONE.defaultBlockState());
    STONE_SET.add(Blocks.NETHERRACK.defaultBlockState());
    STONE_SET.add(Blocks.DEEPSLATE.defaultBlockState());
    STONE_SET.add(Blocks.TUFF.defaultBlockState());

    STONE_SET.add(Blocks.COAL_ORE.defaultBlockState());
    STONE_SET.add(Blocks.IRON_ORE.defaultBlockState());
    STONE_SET.add(Blocks.GOLD_ORE.defaultBlockState());
    STONE_SET.add(Blocks.DIAMOND_ORE.defaultBlockState());
    STONE_SET.add(Blocks.EMERALD_ORE.defaultBlockState());
    STONE_SET.add(Blocks.LAPIS_ORE.defaultBlockState());
    STONE_SET.add(Blocks.REDSTONE_ORE.defaultBlockState());

    TREE_SET.add(Blocks.GRASS.defaultBlockState());
    TREE_BIOME_SET.add("Forest");
    TREE_BIOME_SET.add("ForestHills");
  }

  /**
   * Efficiently sets a BlockState, without causing block updates or notifying the
   * client
   **/
  public static boolean setState(LevelAccessor world, BlockPos pos, BlockState state) {
    if (state == null) {
      Antimatter.LOGGER.error("WorldGenHelper: tried to place null state at " + pos.toString());
      return false;
    }
    return world.setBlock(pos, state, 0);
  }

  /**
   * Replaces the block at the given position with an ore block for the specified
   * material.
   * Will only replace the block, if the existing state is a registered stone & the stone generates ores for that type.
   */
  public static boolean setOre(LevelAccessor world, BlockPos pos, BlockState existing, Material material,
      MaterialType<?> type) {
    StoneType stone = STONE_MAP.get(existing);
    if (stone == null || !stone.doesGenerateOre() || stone == AntimatterStoneTypes.BEDROCK)
      return false;
    BlockState oreState = type == AntimatterMaterialTypes.ORE ? AntimatterMaterialTypes.ORE.get().get(material, stone).asState()
        : AntimatterMaterialTypes.ORE_SMALL.get().get(material, stone).asState();
    if (!ORE_PREDICATE.test(existing))
      return false;
    return setState(world, pos, oreState);
  }

  /**
   * Replaces the block at the given position with an ore block for the specified
   * material.
   * Will only replace the block, if the existing state is a registered stone & the stone generates ores for that type.
   */
  public static boolean setOre(LevelAccessor world, BlockPos pos, Material material, MaterialType<?> type) {
    final BlockState existing = world.getBlockState(pos);
    return setOre(world, pos, existing, material, type);
  }

    public static boolean addOre(LevelAccessor world, BlockPos pos, Material material, boolean normalOre) {
        FeatureOre.ORES.computeIfAbsent(world.getChunk(pos).getPos(), k -> new ObjectArrayList<>()).add(new ImmutableTriple<>(pos, material, normalOre));
        return true;
    }

    public static boolean addRock(LevelAccessor world, BlockPos pos, Material material, int chance) {
        int y = Math.min(world.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX(), pos.getZ()), world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()));
        BlockState state = world.getBlockState(new BlockPos(pos.getX(), y - 1, pos.getZ()));
        return setRock(world, new BlockPos(pos.getX(), y, pos.getZ()), material, state, chance);
    }

  /**
   * Replaces the block at the given position with a surface rock block for the
   * specified material.
   * Will only replace the block, if the existing state can be replaced by a
   * surface rock.
   */
  public static boolean setRock(LevelAccessor world, BlockPos pos, Material material, @Nullable() BlockState fill, int chance) {
      if (world.getRandom().nextInt(chance) != 0) return false;
      StoneType stone = fill != null ? STONE_MAP.get(fill) : null;
    BlockState rockState = AntimatterMaterialTypes.ROCK.get().get(material, stone != null && stone != AntimatterStoneTypes.BEDROCK && stone.doesGenerateOre() ? stone : AntimatterStoneTypes.STONE).asState();

    final BlockState existingBelow = world.getBlockState(pos.below());
    if (existingBelow.isAir() || !existingBelow.getMaterial().isSolid())
      return false;

    final BlockState existing = world.getBlockState(pos);

    if (!ROCK_PREDICATE.test(existing))
      return false;
    if (existing == WorldGenHelper.WATER_STATE)
      rockState = WorldGenHelper.waterLogState(rockState);
    return setState(world, pos, rockState);
  }

  /**
   *
   **/
  public static boolean setStone(LevelAccessor world, BlockPos pos, BlockState existing, BlockState replacement) {
    if (!STONE_PREDICATE.test(existing))
      return false;
    return setState(world, pos, replacement);
  }

  public static BlockState waterLogState(BlockState state) {
    return state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, true)
        : state;
  }
}
