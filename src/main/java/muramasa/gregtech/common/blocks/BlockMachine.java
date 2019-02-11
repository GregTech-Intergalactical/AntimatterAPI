package muramasa.gregtech.common.blocks;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.capability.ICoverable;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.properties.ITechProperties;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.client.render.bakedmodels.BakedModelBase;
import muramasa.gregtech.common.items.ItemBlockMachines;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.utils.Ref;
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
import net.minecraft.nbt.NBTTagCompound;
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

import static muramasa.gregtech.api.properties.ITechProperties.*;

public class BlockMachine extends Block {

    private static StateMapperRedirect stateMapRedirect = new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_machine"));

    private String type;

    public BlockMachine(String type) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(Ref.MODID + "_machine_" + type);
        setRegistryName("machine_" + type);
        setSoundType(SoundType.METAL);
        setCreativeTab(Ref.TAB_MACHINES);
        this.type = type;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TYPE, TIER, FACING, OVERLAY, TINT, TEXTURE, COVERS).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            exState = exState
                .withProperty(TYPE, machine.getTypeId())
                .withProperty(TIER, machine.getTierId())
                .withProperty(FACING, machine.getFacing())
                .withProperty(OVERLAY, machine.getMachineState().getOverlayId())
                .withProperty(TINT, machine.getTint())
                .withProperty(TEXTURE, machine.getTexture());
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, null);
            if (coverHandler != null) {
                exState = exState
                    .withProperty(COVERS, new CoverType[] {
                        coverHandler.getCover(EnumFacing.UP),
                        coverHandler.getCover(EnumFacing.DOWN),
                        coverHandler.getCover(EnumFacing.SOUTH),
                        CoverType.NONE,
                        coverHandler.getCover(EnumFacing.EAST),
                        coverHandler.getCover(EnumFacing.WEST)
                    });
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
        Machine machine = Machines.get(type);
        for (Tier tier : machine.getTiers()) {
            items.add(Machines.get(type, tier.getName()).asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        try {
            return (TileEntityMachine) Machines.get(type).getTileClass().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            int guiId = ((TileEntityMachine) tile).getMachineType().getGuiId();
            player.openGui(GregTech.INSTANCE, guiId, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.getItem() instanceof ItemBlockMachines) {
            if (stack.hasTagCompound()) {
                NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
                String machineType = data.getString(Ref.KEY_MACHINE_STACK_TYPE);
                String machineTier = data.getString(Ref.KEY_MACHINE_STACK_TIER);

                TileEntity tile = Utils.getTile(world, pos);
                if (tile instanceof TileEntityMachine) {
                    ((TileEntityMachine) tile).init(machineType, machineTier, placer.getHorizontalFacing().getOpposite().getIndex() - 2);
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
        return new MachineStack(Machines.ALLOY_SMELTER, Tier.LV).asItemStack();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_machine", "inventory"));
        ModelLoader.setCustomStateMapper(this, stateMapRedirect);
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
            if (tintIndex == 0 && state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) state;
                if (BakedModelBase.hasUnlistedProperty(exState, ITechProperties.TINT)) {
                    return exState.getValue(ITechProperties.TINT) != null ? exState.getValue(ITechProperties.TINT) : -1;
                }
            }
            return -1;
        }
    }
}
