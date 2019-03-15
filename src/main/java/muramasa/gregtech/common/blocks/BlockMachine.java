package muramasa.gregtech.common.blocks;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.gui.GuiData;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.common.items.ItemBlockMachine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static muramasa.gregtech.api.properties.GTProperties.*;

public class BlockMachine extends Block {

    private static StateMapperRedirect stateMapRedirect = new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_machine"));

    private String type;

    public BlockMachine(String type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(type);
        setRegistryName(type);
        setSoundType(SoundType.METAL);
        setCreativeTab(Ref.TAB_MACHINES);
        this.type = type;
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
        if (stack.getItem() instanceof ItemBlockMachine) {
            if (stack.hasTagCompound()) {
                String machineTier = stack.getTagCompound().getString(Ref.KEY_MACHINE_STACK_TIER);
                TileEntity tile = Utils.getTile(world, pos);
                if (tile instanceof TileEntityMachine) {
                    ((TileEntityMachine) tile).init(Tier.get(machineTier), placer.getHorizontalFacing().getOpposite().getIndex());
                }
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

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public Machine getType() {
        return Machines.get(type);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_machine", "inventory"));
        ModelLoader.setCustomStateMapper(this, stateMapRedirect);
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
            if (index == 0) {
                TileEntityMachine tile = (TileEntityMachine) Utils.getTile(world, pos);
                if (tile != null && tile.getTextureData().getTint() > -1) return tile.getTextureData().getTint();
            }
            return -1;
        }
    }
}
