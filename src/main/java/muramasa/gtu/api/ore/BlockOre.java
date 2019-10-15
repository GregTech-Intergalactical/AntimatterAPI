package muramasa.gtu.api.ore;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class BlockOre extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private Material material;
    private OreType oreType;
    private StoneType stoneType;

    public BlockOre(Material material, OreType oreType, StoneType stoneType) {
        super(Block.Properties.create(net.minecraft.block.material.Material.ROCK).sound(stoneType.getSoundType()));
        this.material = material;
        this.oreType = oreType;
        this.stoneType = stoneType;

        setRegistryName(getId());
        GregTechAPI.register(BlockOre.class, this);
    }

    @Override
    public String getId() {
        return material.getId() + "_" + oreType.getMaterialType().getId() + "_" + stoneType.getId();
    }

    @Override
    public String getTranslationKey() {
        return getId();
    }

    public Material getMaterial() {
        return material;
    }

    public OreType getType() {
        return oreType;
    }

    public StoneType getStoneType() {
        return stoneType;
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

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    //    @Override
//    public boolean shouldSideBeRendered(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
//        return Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG || super.shouldSideBeRendered(state, world, pos, side);
//    }

//    @Override
//    public int getLightValue(BlockState state, IBlockAccess world, BlockPos pos) {
//        return Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG ? 15 : 0;
//    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (world == null || pos == null || i != 1 || state.isAir(world, pos)) return -1;
        return ((BlockOre) world.getBlockState(pos).getBlock()).getMaterial().getRGB();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 1 && block != null ? ((BlockOre) block).getMaterial().getRGB() : -1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getTextures(Set<ResourceLocation> textures) {
        textures.add(material.getSet().getTexture(oreType.getMaterialType(), 0));
        textures.add(stoneType.getTexture());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onModelRegistration() {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onModelBake(ModelBakeEvent e, Map<ResourceLocation, IBakedModel> registry) {
        ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId());
        registry.put(loc, new TextureData().base(stoneType.getTexture()).overlay(material.getSet().getTexture(oreType.getMaterialType(), 0)).bake());
    }

    public static ItemStack get(Material material, OreType oreType, StoneType stoneType, int count) {
        BlockOre block = GregTechAPI.get(BlockOre.class, material.getId() + "_" + oreType.getMaterialType().getId() + "_" + stoneType.getId());
        return block != null ? new ItemStack(block.asItem(), count) : ItemStack.EMPTY;
    }

    public static BlockState get(Material material, OreType oreType, StoneType stoneType) {
        BlockOre block = GregTechAPI.get(BlockOre.class, material.getId() + "_" + oreType.getMaterialType().getId() + "_" + stoneType.getId());
        return block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState();
    }
}
