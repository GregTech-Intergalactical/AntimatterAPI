package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockOre extends BlockMaterialStone implements ITextureProvider, IModelProvider, ISharedAntimatterObject {

    private final MaterialType<?> oreType;

    public BlockOre(String domain, Material material, StoneType stoneType, MaterialType<?> oreType, Block.Properties properties) {
        super(domain, oreType.getId() + "_" + material.getId() + "_" + stoneType.getId(), material, stoneType, getOreProperties(properties, stoneType));
        this.oreType = oreType;
    }

    public BlockOre(String domain, Material material, StoneType stoneType, MaterialType<?> oreType) {
        this(domain, material, stoneType, oreType, getOreProperties(Block.Properties.create(stoneType.getBlockMaterial()), stoneType));
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return getId();
    }

    public MaterialType<?> getOreType() {
        return oreType;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (stoneType.getId().equals("stone")) items.add(new ItemStack(this)); //todo move stonetype to antimatter
    }

    //    @Override
//    public net.minecraft.block.material.Material getMaterial(BlockState state) {
//        ToolType tool = getHarvestTool(state);
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
    @Override
    public int getHarvestLevel(BlockState state) {
        int stoneLvl = stoneType.getHarvestLevel();
        return Math.max(stoneLvl, material.getMiningLevel() > -1 ? material.getMiningLevel() : 0);
    }
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
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.stoneType.getGravity()) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.getFallDelay());
        }
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (this.stoneType.getGravity()) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.getFallDelay());
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (this.stoneType.getGravity()) {
            if (worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down())) && pos.getY() >= 0) {
                FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, worldIn.getBlockState(pos));
                this.onStartFalling(fallingblockentity);
                worldIn.addEntity(fallingblockentity);
            }
        }
    }

    protected void onStartFalling(FallingBlockEntity fallingEntity) {
    }

    protected int getFallDelay() {
        return 2;
    }

    public static boolean canFallThrough(BlockState state) {
        net.minecraft.block.material.Material material = state.getMaterial();
        return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (this.stoneType.getGravity()) {
            if (rand.nextInt(16) == 0) {
                BlockPos blockpos = pos.down();
                if (worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) {
                    double d0 = (double) pos.getX() + rand.nextDouble();
                    double d1 = (double) pos.getY() - 0.05D;
                    double d2 = (double) pos.getZ() + rand.nextDouble();
                    worldIn.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, stateIn), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
        return this.stoneType.getFallingDustColor();
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{getStoneType().getTexture(), getMaterial().getSet().getTexture(getOreType(), 0)};
    }

    public static Block.Properties getOreProperties(Block.Properties properties, StoneType type) {
        if (AntimatterConfig.WORLD.ORE_VEIN_SPECTATOR_DEBUG) properties.notSolid().setLightLevel(b -> 15);
        properties.hardnessAndResistance(type.getHardness() * 2, type.getResistence() / 2).harvestTool(type.getToolType()).sound(type.getSoundType());
        if (type.doesRequireTool()) properties.setRequiresTool();
        return properties;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        if (silktouch == 0 && material.getExpRange() != null) {
            List<ItemStack> self = getDrops(state, ((ServerWorld) world), pos, world.getTileEntity(pos));
            if (self.stream().anyMatch(i -> i.getItem() == this.asItem())) {
                return 0;
            }
            return material.getExpRange().getRandomWithinRange(RANDOM);
        }
        return 0;
    }
}
