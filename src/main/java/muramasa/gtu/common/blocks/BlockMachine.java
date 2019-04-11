package muramasa.gtu.common.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineStack;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.registration.IHasItemBlock;
import muramasa.gtu.api.registration.IHasModelOverride;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.GTLoc;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.gtu.api.properties.GTProperties.*;

public class BlockMachine extends Block implements IHasItemBlock, IHasModelOverride {

    private static StateMapperRedirect stateMapRedirect = new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_machine"));

    private String type;

    public BlockMachine(String type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(type);
        setRegistryName(type);
        setHardness(1.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(Ref.TAB_MACHINES);
        this.type = type;
    }

    public Machine getType() {
        return Machines.get(type);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TYPE, FACING, TEXTURE, COVER).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            exState = exState
                .withProperty(TYPE, machine.getTypeId())
                .withProperty(FACING, machine.getFacing())
                .withProperty(TEXTURE, machine.getTextureData());
            if (getType().hasFlag(MachineFlag.COVERABLE)) {
                if (machine.getCoverHandler() == null) return exState;
                exState = exState.withProperty(COVER, machine.getCoverHandler().getAll());
            }
        }
        return exState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        Machine type = getType();
        if (type == Machines.INVALID) return;
        for (Tier tier : type.getTiers()) {
            items.add(Machines.get(type, tier).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        try {
            return (TileEntityMachine) getType().getTileClass().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Was not able to instantiate a TileEntity class for: " + type);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            if (machine.getType().hasFlag(MachineFlag.GUI)) {
                if (machine.getType().hasFlag(MachineFlag.COVERABLE) && !machine.getCoverHandler().get(side).isEmpty()) return false;
                GuiData gui = machine.getType().getGui();
                player.openGui(gui.getInstance(), gui.getId(), world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityMachine) {
                String machineTier = stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER);
                ((TileEntityMachine) tile).init(Tier.get(machineTier), placer.getHorizontalFacing().getOpposite().getIndex());
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            return Machines.get(machine.getType(), machine.getTier()).asItemStack();
        }
        return Machines.get(Machines.INVALID, Tier.LV).asItemStack();
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wrench";
    }

    /** TileEntity Drops Start **/
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            drops.add(new MachineStack(((TileEntityMachine) tile).getType(), ((TileEntityMachine) tile).getTier()).asItemStack());
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }
    /** TileEntity Drops End **/

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public String getItemStackDisplayName(Block block, ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_MACHINE_STACK_TIER)) {
            Tier tier = Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER));
            return tier.getRarityColor() + GTLoc.get("machine." + getType().getName() + "." + tier.getName() + ".name");
        }
        return getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_MACHINE_STACK_TIER)) {
            if (getType().hasFlag(MachineFlag.BASIC)) {
                Tier tier = Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER));
                tooltip.add("Voltage IN: " + TextFormatting.GREEN + tier.getVoltage() + " (" + tier.getName().toUpperCase() + ")");
                tooltip.add("Capacity: " + TextFormatting.BLUE + (tier.getVoltage() * 64));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_machine", "inventory"));
        ModelLoader.setCustomStateMapper(this, stateMapRedirect);
    }
}
