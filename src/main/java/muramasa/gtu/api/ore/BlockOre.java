package muramasa.gtu.api.ore;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.properties.GTPropertyInteger;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
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
import java.util.*;
import java.util.Map.Entry;

public class BlockOre extends BlockFalling implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private static Object2ObjectLinkedOpenHashMap<String, StoneType[]> STONE_SET_MAP = new Object2ObjectLinkedOpenHashMap<>();
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
        return type.getType().getId() + "_" + material.getId() + (setId.isEmpty() ? "" : "_" + setId);
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
    public net.minecraft.block.material.Material getMaterial(IBlockState state) {
        String tool = getHarvestTool(state);
        if (tool != null && tool.equals("shovel")) return net.minecraft.block.material.Material.SAND;
        return net.minecraft.block.material.Material.ROCK;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return stoneSet[state.getValue(STONE_TYPE)].getSoundType();
    }

    //TODO
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return stoneSet[blockState.getValue(STONE_TYPE)].getBaseState().getBlockHardness(worldIn, pos) + getHarvestLevel(blockState) - (type == OreType.SMALL ? 0.2F : 0);
    }
    
    @Override
    public String getHarvestTool(IBlockState state) {
        return stoneSet[state.getValue(STONE_TYPE)].getSoundType() == SoundType.STONE ? "pickaxe" : "shovel";
    }

    //TODO
    @Override
    public int getHarvestLevel(IBlockState state) {
        int stoneLvl = stoneSet[state.getValue(STONE_TYPE)].getHarvestLevel();
        return Math.max(stoneLvl, material.getToolQuality() > 0 ? material.getToolQuality() - 1 : 1);
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (stoneSet[state.getValue(STONE_TYPE)].getGravity()) worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (stoneSet[state.getValue(STONE_TYPE)].getGravity()) world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (stoneSet[state.getValue(STONE_TYPE)].getGravity()) super.updateTick(world, pos, state, rand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (stoneSet[state.getValue(STONE_TYPE)].getGravity()) super.randomDisplayTick(state, world, pos, rand);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
        if (type == OreType.SMALL) {
            //BlockOre block = (BlockOre) state.getBlock();
            //StoneType stoneType = getStoneTypesFromSet(block.setId)[state.getValue(block.getStoneTypeProp())];
            XSTR rand = new XSTR();
            StoneType stoneType = STONE_SET_MAP.get(setId)[state.getValue(STONE_TYPE)];
            Material stoneMaterial = stoneType.getMaterial();
            int bonus = rand.nextInt(fortune + 2) - 1 > 0 ? rand.nextInt(fortune + 2) - 1 : 0;
            if (material.has(MaterialType.GEM)) {
                int roll = rand.nextInt(25);
                boolean hasBrittle = material.has(MaterialType.GEM_BRITTLE);
                if (hasBrittle) drops.add(material.getGemBrittle(roll > 22 ? 2 + bonus : 1 + bonus));
                if (!hasBrittle) drops.add(material.getGem(1 + bonus));
                if (roll == 0 || !hasBrittle) drops.add(material.getGem(1 + bonus));
            }
            else {
                int roll = rand.nextInt(2);
                if (roll == 0) drops.add(material.getDustImpure(1 + bonus));
                else drops.add(material.getCrushed(1 + bonus));
            }
            if (stoneType == StoneType.SAND) drops.add(new ItemStack(Blocks.SAND));
            else if (stoneType == StoneType.SANDSTONE) drops.add(new ItemStack(Blocks.SANDSTONE));
            else if (stoneType == StoneType.SAND_RED) drops.add(new ItemStack(Blocks.SAND, 1, 1));
            if (stoneMaterial.has(MaterialType.DUST_TINY)) {
                drops.add(stoneMaterial.getDustTiny(1 + bonus));
            }
            else drops.add(new ItemStack(Blocks.DIRT)); //This shouldn't happen(?)
        }
        else drops.add(new ItemStack(this, 1, state.getValue(STONE_TYPE)));
    }
    
    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
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
    @SideOnly(Side.CLIENT)
    public int getDustColor(IBlockState state) {
        return material.getRGB();
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        if (world == null || pos == null || i != 1 || world.isAirBlock(pos)) return -1;
        return ((BlockOre) world.getBlockState(pos).getBlock()).getMaterial().getRGB();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 1 && block != null ? ((BlockOre) block).getMaterial().getRGB() : -1;
    }

    @Override
    public void getTextures(Set<ResourceLocation> textures) {
        for (int i = 0; i < stoneSet.length; i++) {
            textures.add(stoneSet[i].getTexture());
        }
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
            registry.putObject(loc, new TextureData().base(stoneSet[i].getTexture()).overlay(material.getSet().getTexture(type.getType(), 0)).bake());
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
    
    public static StoneType[] getStoneTypesFromSet(String setId) {
        return STONE_SET_MAP.get(setId);
    }
    
    public static String[] getAvailableSets() {
        return STONE_SET_MAP.keySet().toArray(new String[STONE_SET_MAP.keySet().size()]);
    }
    
    public static void addStoneSet(String id, StoneType[] set) {
        set = organiseStoneSets(set);
        int length = set.length;
        //The first set will be added, or a set with full meta slots
        if (STONE_SET_MAP.isEmpty() || length == 16) STONE_SET_MAP.put(id, set);
        //A set that has amount divisible by 16 (max amount that can be in a set) is handled here
        else if (length % 16 == 0) {
            int sets = length / 16;
            for (int i = 0; i <= sets; i++) {
                STONE_SET_MAP.put(id + "_" + Integer.toString(i), Arrays.copyOfRange(set, i * sets, (i + 1) * sets));
            }
        }
        //A set that has less than 16 will be handled here
        else if (length < 16) sortStoneSet(id, set);
        //Everything here *should* be more than 16 but can't be divided by 16
        else {
            int sets = length / 16;
            int remainder = length % 16;
            for (int i = 0; i <= sets; i++) {
                if (i == sets) sortStoneSet(id, Arrays.copyOfRange(set, i * 16, (i * 16) + remainder));
                else STONE_SET_MAP.put(id + "_" + Integer.toString(i), Arrays.copyOfRange(set, i * 16, (i + 1) * 16));
            }
        }
    }
    
    private static void sortStoneSet(String id, StoneType[] set) {
        int length = set.length;
        Optional<Entry<String, StoneType[]>> findFirstType = STONE_SET_MAP.entrySet().stream().filter(e -> (!e.getKey().isEmpty() && e.getValue().length < 16)).findFirst();
        findFirstType.ifPresent(entry -> {
            String key = entry.getKey();
            StoneType[] tempTypes = entry.getValue();
            List<StoneType> types = new ArrayList<StoneType>();
            Collections.addAll(types, tempTypes);
            int tempLength = types.size();
            int accepted = Math.min(length, 16 - tempLength);
            for (int i = 0; i < accepted; i++) {
                types.add(set[i]);
            }
            //STONE_SET_MAP.remove(key);
            //STONE_SET_MAP.put(key + "_and_" + id, types.toArray(new StoneType[tempLength]));
            STONE_SET_MAP.replace(entry.getKey(), tempTypes, types.toArray(new StoneType[tempLength]));
            if (length > 16 - tempLength) STONE_SET_MAP.put(id, Arrays.copyOfRange(set, accepted, length));
        });
        if (!findFirstType.isPresent()) STONE_SET_MAP.put(id, set);
    }
    
    private static StoneType[] organiseStoneSets(StoneType[] set) {
        List<StoneType> types = new ArrayList<StoneType>();
        for (StoneType type : set) {
            if (type.getGenerating()) types.add(type);
        }
        return types.toArray(new StoneType[types.size()]);
    }

}
