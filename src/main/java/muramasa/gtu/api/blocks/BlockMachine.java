package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.IConfigHandler;
import muramasa.gtu.api.capability.ICoverHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineStack;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tools.ToolType;
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

public class BlockMachine extends Block implements IItemBlock, IModelOverride, IColorHandler {

    private String type;

    public BlockMachine(String type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(type);
        setRegistryName(type);
        setHardness(1.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(Ref.TAB_MACHINES);
        setDefaultState(getDefaultState().withProperty(TIER, 0));
        this.type = type;
    }

    public Machine getType() {
        return Machines.get(type);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TIER).add(TYPE, FACING, TEXTURE, COVER).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            exState = exState
                .withProperty(TYPE, machine.getTypeId())
                .withProperty(FACING, machine.getFacing().getIndex())
                .withProperty(TEXTURE, machine.getTextureData());
            if (getType().hasFlag(MachineFlag.COVERABLE)) {
                if (machine.getCoverHandler() == null) return exState;
                exState = exState.withProperty(COVER, machine.getCoverHandler().getAll());
            }
        }
        return exState;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TIER, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIER);
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
                //TODO if cover returns false, open normal gui if present
                if (machine.getType().hasFlag(MachineFlag.COVERABLE) && !machine.getCoverHandler().get(side).isEmpty())
                    return false;
                GuiData gui = machine.getType().getGui();
                player.openGui(gui.getInstance(), gui.getId(), world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            } else if (machine.getType().hasFlag(MachineFlag.COVERABLE)) {
                ICoverHandler coverHandler = machine.getCoverHandler();
                if (coverHandler == null) return false;
                return coverHandler.onInteract(player, hand, side, ToolType.get(player.getHeldItem(hand)));
            } else if (machine.getType().hasFlag(MachineFlag.CONFIGURABLE)) {
                IConfigHandler configHandler = machine.getConfigHandler();
                if (configHandler == null) return false;
                return configHandler.onInteract(player, hand, side, ToolType.get(player.getHeldItem(hand)));
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        ItemStack stack = placer.getHeldItem(hand);
        if (!stack.isEmpty() && stack.hasTagCompound()) {
            int tier = Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER)).getInternalId();
            return getDefaultState().withProperty(TIER, tier);
        }
        return getDefaultState();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityMachine) {
                EnumFacing facing = EnumFacing.getFacingFromVector((float)placer.getLookVec().x, (float)placer.getLookVec().y, (float)placer.getLookVec().z).getOpposite();
                ((TileEntityMachine) tile).setFacing(facing);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            return Machines.get(machine.getType(), Tier.get(state.getValue(TIER))).asItemStack();
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
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);
        world.setBlockToAir(pos);
    }
    /** TileEntity Drops End **/

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            if (machine.getItemHandler() != null) {
                machine.getItemHandler().getInputList().forEach(i -> Utils.spawnItems(world, pos, null, i));
                machine.getItemHandler().getOutputList().forEach(i -> Utils.spawnItems(world, pos, null, i));
            }
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_MACHINE_STACK_TIER)) {
            Tier tier = Tier.get(stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER));
            return tier.getRarityColor() + Utils.trans("machine." + getType().getName() + "." + tier.getName() + ".name");
        }
        return getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
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
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof TileEntityMachine && i == 0 ? ((TileEntityMachine) tile).getTextureData().getTint() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_machine", "inventory"));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_machine")));
    }
}
