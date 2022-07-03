package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ScheduledTick;
;
;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockOre extends BlockMaterialStone implements ITextureProvider, IModelProvider, ISharedAntimatterObject {

    private final MaterialType<?> oreType;

    public BlockOre(String domain, Material material, StoneType stoneType, MaterialType<?> oreType, Properties properties) {
        super(domain, oreType.getId() + "_" + material.getId() + "_" + stoneType.getId(), material, stoneType, getOreProperties(properties, stoneType));
        this.oreType = oreType;
    }

    public BlockOre(String domain, Material material, StoneType stoneType, MaterialType<?> oreType) {
        this(domain, material, stoneType, oreType, getOreProperties(Properties.of(stoneType.getBlockMaterial()), stoneType));
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return getId();
    }

    public MaterialType<?> getOreType() {
        return oreType;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (stoneType.getId().equals("stone")) items.add(new ItemStack(this)); //todo move stonetype to antimatter
    }

    //    @Override
//    public net.minecraft.block.material.Material getMaterial(BlockState state) {
//        Tag<Block> tool = getHarvestTool(state);
//        if (tool != null && tool.equals("shovel")) return net.minecraft.block.material.Material.SAND;
//        return net.minecraft.block.material.Material.ROCK;
//    }

    //    //TODO
//    @Override
//    public float getBlockHardness(BlockState blockState, World worldIn, BlockPos pos) {
//        return stoneSet[blockState.getValue(STONE_TYPE)].getBaseState().getBlockHardness(worldIn, pos) + getHarvestLevel(blockState) - (type == OreType.SMALL ? 0.2F : 0);
//    }
//
//
//    //TODO
   /* @Override
    public int getHarvestLevel(BlockState state) {
        int stoneLvl = stoneType.getHarvestLevel();
        return Math.max(stoneLvl, material.getMiningLevel() > -1 ? material.getMiningLevel() : 0);
    }*/
//
//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
//        drops.clear();
//        if (type == OreType.SMALL) {
//            //BlockOre block = (BlockOre) state.getBlock();
//            //StoneType stoneType = getStoneTypesFromSet(block.setId)[state.getValue(block.getStoneTypeProp())];
//            XSTR rand = new XSTR();
//            StoneType stoneType = STONE_SET_MAP.get(setId)[state.getValue(STONE_TYPE)];
//            Material stoneMaterial = stoneType.getMaterial();
//            int bonus = rand.nextInt(fortune + 2) - 1 > 0 ? rand.nextInt(fortune + 2) - 1 : 0;
//            if (material.has(MaterialType.GEM)) {
//                int roll = rand.nextInt(25);
//                boolean hasBrittle = material.has(MaterialType.GEM_BRITTLE);
//                if (hasBrittle) drops.add(material.getGemBrittle(roll > 22 ? 2 + bonus : 1 + bonus));
//                if (!hasBrittle) drops.add(material.getGem(1 + bonus));
//                if (roll == 0 || !hasBrittle) drops.add(material.getGem(1 + bonus));
//            }
//            else {
//                int roll = rand.nextInt(2);
//                if (roll == 0) drops.add(material.getDustImpure(1 + bonus));
//                else drops.add(material.getCrushed(1 + bonus));
//            }
//            if (stoneType == StoneType.SAND) drops.add(new ItemStack(Blocks.SAND));
//            else if (stoneType == StoneType.SANDSTONE) drops.add(new ItemStack(Blocks.SANDSTONE));
//            else if (stoneType == StoneType.SAND_RED) drops.add(new ItemStack(Blocks.SAND, 1, 1));
//            if (stoneMaterial.has(MaterialType.DUST_TINY)) {
//                drops.add(stoneMaterial.getDustTiny(1 + bonus));
//            }
//            else drops.add(new ItemStack(Blocks.DIRT)); //This shouldn't happen(?)
//        }
//        else drops.add(new ItemStack(this, 1, state.getValue(STONE_TYPE)));
//    }
//
//    @Override
//    public boolean canSilkHarvest(World world, BlockPos pos, BlockState state, EntityPlayer player) {
//        return false;
//    }

    //    @Override
//    public boolean shouldSideBeRendered(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
//        return Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG || super.shouldSideBeRendered(state, world, pos, side);
//    }

//    @Override
//    public int getLightValue(BlockState state, IBlockAccess world, BlockPos pos) {
//        return Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG ? 15 : 0;
//    }

    /**
     * Falling block stuff
     **/
    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.stoneType.getGravity()) {
            worldIn.getBlockTicks().schedule(new ScheduledTick<>(this, pos, this.getFallDelay(), 0L));
        }
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (this.stoneType.getGravity()) {
            worldIn.getBlockTicks().schedule(new ScheduledTick<>(this, currentPos, this.getFallDelay(), 0L));
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (this.stoneType.getGravity()) {
            if (worldIn.isEmptyBlock(pos.below()) || canFallThrough(worldIn.getBlockState(pos.below())) && pos.getY() >= 0) {
                //FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, worldIn.getBlockState(pos));
               // this.onStartFalling(fallingblockentity);
              //  worldIn.addFreshEntity(fallingblockentity);
            }
        }
    }

    protected void onStartFalling(FallingBlockEntity fallingEntity) {
    }

    protected int getFallDelay() {
        return 2;
    }

    public static boolean canFallThrough(BlockState state) {
        net.minecraft.world.level.material.Material material = state.getMaterial();
        return state.isAir() || state.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        if (this.stoneType.getGravity()) {
            if (rand.nextInt(16) == 0) {
                BlockPos blockpos = pos.below();
                if (worldIn.isEmptyBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) {
                    double d0 = (double) pos.getX() + rand.nextDouble();
                    double d1 = (double) pos.getY() - 0.05D;
                    double d2 = (double) pos.getZ() + rand.nextDouble();
                    worldIn.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, stateIn), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
        return this.stoneType.getFallingDustColor();
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{getStoneType().getTexture(), getMaterial().getSet().getTexture(getOreType(), 0)};
    }

    public static Properties getOreProperties(Properties properties, StoneType type) {
        if (AntimatterConfig.WORLD.ORE_VEIN_SPECTATOR_DEBUG) properties.noOcclusion().lightLevel(b -> 15);
        properties.strength(type.getHardness() * 2, type.getResistence() / 2).sound(type.getSoundType());
        if (type.doesRequireTool()) properties.requiresCorrectToolForDrops();
        return properties;
    }

    //TODO figure out fabric alternative
    //@Override
    public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch) {
        if (silktouch == 0 && material.has(MaterialTags.EXP_RANGE)) {
            List<ItemStack> self = getDrops(state, ((ServerLevel) world), pos, world.getBlockEntity(pos));
            if (self.stream().anyMatch(i -> i.getItem() == this.asItem())) {
                return 0;
            }
            return MaterialTags.EXP_RANGE.getIntRange(material).sample(((ServerLevel) world).getRandom());
        }
        return 0;
    }
}
