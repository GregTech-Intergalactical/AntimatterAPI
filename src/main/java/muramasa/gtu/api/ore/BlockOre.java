package muramasa.gtu.api.ore;

import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.tileentities.TileEntityMaterial;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static muramasa.gtu.api.properties.GTProperties.ORE_SET;
import static muramasa.gtu.api.properties.GTProperties.ORE_TYPE;

public class BlockOre extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private StoneType stoneType;

    public BlockOre(StoneType stoneType) {
        super(net.minecraft.block.material.Material.ROCK);
        this.stoneType = stoneType;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechAPI.register(BlockOre.class, this);
    }

    @Override
    public String getId() {
        return "ore_" + stoneType.getId();
    }

    public StoneType getStoneType() {
        return stoneType;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(ORE_TYPE).add(ORE_SET).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMaterial) {
            TileEntityMaterial ore = (TileEntityMaterial) tile;
            exState = exState.withProperty(ORE_SET, ore.getMaterial().getSet().getInternalId());
        }
        return exState;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ORE_TYPE, OreType.VALUES.get(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ORE_TYPE).ordinal();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMaterial();
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return stoneType.getSoundType();
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
        if (stoneType == StoneType.STONE) {
            OreType.VALUES.forEach(o -> o.getType().getMats().forEach(m -> {
                items.add(new OreStack(this, m, stoneType, o).asItemStack());
            }));
        }
    }

    /** TileEntity Drops Start **/
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMaterial) {
            TileEntityMaterial ore = (TileEntityMaterial) tile;
            drops.add(new OreStack(this, ore.getMaterial(), ((BlockOre) state.getBlock()).stoneType, state.getValue(ORE_TYPE)).asItemStack());
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);
        world.setBlockToAir(pos);
    }
    /** TileEntity Drops End **/

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_ORE_STACK_STONE)) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityMaterial) {
                Material material = Materials.get(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_MATERIAL));
                ((TileEntityMaterial) tile).init(material);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMaterial) {
            TileEntityMaterial ore = (TileEntityMaterial) tile;
            return new OreStack(this, ore.getMaterial(), ((BlockOre) state.getBlock()).stoneType, state.getValue(ORE_TYPE)).asItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (!stack.hasTagCompound()) return stack.getUnlocalizedName();
        if (stack.getTagCompound().hasKey(Ref.KEY_ORE_STACK_STONE)) {
            Material material = Materials.get(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_MATERIAL));
            MaterialType materialType = OreType.VALUES.get(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_TYPE)).getType();
            return stoneType.getId() + "." + material.getId() + "." + materialType.getId() + ".name";
        }
        return stack.getUnlocalizedName();
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof TileEntityMaterial && i == 1 ? ((TileEntityMaterial) tile).getMaterial().getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (!stack.hasTagCompound()) return -1;
        return i == 1 ? Materials.get(stack.getTagCompound().getInteger(Ref.KEY_ORE_STACK_MATERIAL)).getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_ore", "inventory"));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_ore")));
    }

    public static ItemStack get(Material material, StoneType stoneType, OreType oreType) {
        return new OreStack(get(stoneType), material, stoneType, oreType).asItemStack();
    }

    public static BlockOre get(StoneType stoneType) {
        return GregTechAPI.get(BlockOre.class, "ore_" + stoneType.getId());
    }

    public static IBlockState get(StoneType stoneType, OreType oreType) {
        return get(stoneType).getDefaultState().withProperty(ORE_TYPE, oreType);
    }
}
