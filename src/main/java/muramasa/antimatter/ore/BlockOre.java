package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Configs;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockOre extends BlockMaterialStone implements ITextureProvider, IModelProvider {

    private MaterialType<?> oreType;

    public BlockOre(String domain, Material material, StoneType stoneType, MaterialType<?> oreType, Block.Properties properties) {
        super(domain, oreType.getId() + "_" + material.getId() + "_" + stoneType.getId(), material, stoneType, getOreProperties(properties));
        this.oreType = oreType;
        AntimatterAPI.register(BlockOre.class, this);
    }

    public BlockOre(String domain, Material material, StoneType stoneType, MaterialType<?> oreType) {
        this(domain, material, stoneType, oreType, Block.Properties.create(net.minecraft.block.material.Material.ROCK).sound(stoneType.getSoundType()));
    }

    @Override
    public String getTranslationKey() {
        return getId();
    }

    public MaterialType<?> getOreType() {
        return oreType;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (stoneType.getId().equals("stone")) items.add(new ItemStack(this));
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
//    @Override
//    public String getHarvestTool(BlockState state) {
//        return stoneSet[state.getValue(STONE_TYPE)].getSoundType() == SoundType.STONE ? "pickaxe" : "shovel";
//    }
//
//    //TODO
//    @Override
//    public int getHarvestLevel(BlockState state) {
//        int stoneLvl = stoneSet[state.getValue(STONE_TYPE)].getHarvestLevel();
//        return Math.max(stoneLvl, material.getToolQuality() > 0 ? material.getToolQuality() - 1 : 1);
//    }
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

    @Override
    public Texture[] getTextures() {
        return new Texture[]{getStoneType().getTexture(), getMaterial().getSet().getTexture(getOreType(), 0)};
    }

    public static Block.Properties getOreProperties(Block.Properties properties) {
        if (Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG) properties.notSolid().lightValue(15);
        return properties;
    }
}
