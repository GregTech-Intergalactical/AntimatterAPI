package muramasa.gtu.api.ore;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.properties.GTPropertyInteger;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;

public class BlockOre extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private static Object2ObjectOpenHashMap<String, StoneType[]> STONE_SET_MAP = new Object2ObjectOpenHashMap<>();
    private static Object2ObjectOpenHashMap<String, Tuple<BlockOre, Integer>> ID_LOOKUP = new Object2ObjectOpenHashMap<>();

    private Material material;
    private OreType type;
    private String setId;
    private StoneType[] stoneSet;
    private GTPropertyInteger STONE_TYPE;

    public BlockOre(OreType type, Material material, String setId) {
        super(net.minecraft.block.material.Material.ROCK);
        this.material = material;
        this.type = type;
        this.setId = setId;
        this.stoneSet = STONE_SET_MAP.get(setId);
        for (int i = 0; i < stoneSet.length; i++) {
            ID_LOOKUP.put(type.getType().getId() + "_" + material.getId() + "_" + stoneSet[i].getId(), new Tuple<>(this, i));
        }

        if (stoneSet.length == 1) STONE_TYPE = new GTPropertyInteger("stone_type", 0);
        else STONE_TYPE = new GTPropertyInteger("stone_type", 0, Math.max(stoneSet.length - 1, 1));

        BlockStateContainer blockStateContainer = createBlockState();
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, blockStateContainer, 21);
        setDefaultState(blockStateContainer.getBaseState());

        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechAPI.register(BlockOre.class, this);
    }

    @Override
    public String getId() {
        return type.getType().getId() + "_" + material.getId() + "_" + setId;
    }

    public Material getMaterial() {
        return material;
    }

    public OreType getType() {
        return type;
    }
    
    public String getSetId() {
        return setId;
    }

    public StoneType[] getStoneTypesFromSet(String setId) {
        return STONE_SET_MAP.get(setId);
    }
    
    public GTPropertyInteger getStoneTypeProp() {
        return STONE_TYPE;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return STONE_TYPE != null ? new BlockStateContainer.Builder(this).add(STONE_TYPE).build() : new BlockStateContainer(this);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STONE_TYPE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STONE_TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(STONE_TYPE, placer.getHeldItem(hand).getMetadata());
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return stoneSet[state.getValue(STONE_TYPE)].getSoundType();
    }

    //TODO
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 1.0f + (getHarvestLevel(blockState) * 1.0f);
    }

    //TODO
    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG || super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Configs.WORLD.ORE_VEIN_SPECTATOR_DEBUG ? 15 : 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i < stoneSet.length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
        drops.add(new ItemStack(this, 1, state.getValue(STONE_TYPE)));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, state.getValue(STONE_TYPE));
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        //TODO
//        if (!stack.hasTagCompound()) return stack.getUnlocalizedName();
//        if (stack.getTagCompound().hasKey(Ref.KEY_ORE_STACK_STONE)) {
//            Material material = Materials.get(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_MATERIAL));
//            MaterialType materialType = OreType.VALUES.get(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_TYPE)).getType();
//            return stoneType.getId() + "." + material.getId() + "." + materialType.getId() + ".name";
//        }
        return stack.getUnlocalizedName();
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockOre) && world == null || pos == null || i != 1) return -1;
        return ((BlockOre) world.getBlockState(pos).getBlock()).getMaterial().getRGB();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (!(block instanceof BlockOre)) return -1;
        return i == 1 ? ((BlockOre) block).getMaterial().getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTextures(Set<ResourceLocation> textures) {
        Arrays.stream(stoneSet).forEach(s -> textures.add(s.getTexture()));
        textures.add(material.getSet().getTexture(type.getType(), 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        for (int i = 0; i < stoneSet.length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(Ref.MODID + ":" + getId(), "stone_type=" + i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {
        for (int i = 0; i < stoneSet.length; i++) {
            ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId(), "stone_type=" + i);
            registry.putObject(loc, ModelUtils.getBakedTextureData(new TextureData().base(stoneSet[i].getTexture()).overlay(material.getSet().getTexture(type.getType(), 0))));
        }
    }

    public static ItemStack get(OreType oreType, Material material, StoneType stoneType, int count) {
        Tuple<BlockOre, Integer> tuple = ID_LOOKUP.get(oreType.getType().getId() + "_" + material.getId() + "_" + stoneType.getId());
        if (tuple == null) {
            Utils.onInvalidData("BlockOre.get() returned null");
            return ItemStack.EMPTY;
        }
        return new ItemStack(tuple.getFirst(), count, tuple.getSecond());
    }

//    public static BlockOre get(StoneType stoneType) {
//        return GregTechAPI.get(BlockOre.class, "ore_" + stoneType.getId());
//    }

    public static IBlockState get(OreType oreType, Material material, StoneType stoneType) {
        Tuple<BlockOre, Integer> tuple = ID_LOOKUP.get(oreType.getType().getId() + "_" + material.getId() + "_" + stoneType.getId());
        if (tuple == null) {
            Utils.onInvalidData("BlockOre.get() returned null");
            return Blocks.AIR.getDefaultState();
        }
        return tuple.getFirst().getDefaultState().withProperty(tuple.getFirst().getStoneTypeProp(), tuple.getSecond());
    }

    public static void addStoneSet(String id, StoneType[] set) {
        if (set.length <= 16) STONE_SET_MAP.put(id, set);
    }
}
