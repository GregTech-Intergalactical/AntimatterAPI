package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.blocks.BlockStorage;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFluidCell extends StandardItem {

    protected int capacity;

    public ItemFluidCell(ItemType type, int capacity) {
        super(type);
        setMaxStackSize(1);
        this.capacity = capacity;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStackSimple(stack, capacity);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        FluidStack fluid = getContents(stack);
        if (fluid != null) {
            tooltip.add(fluid.getLocalizedName() + " - " + fluid.amount);
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        //TODO reenable
//        if (world.isRemote) return EnumActionResult.PASS;

        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            System.out.println("has cap");

            IFluidHandler fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            IFluidHandlerItem cellHandler = player.getHeldItem(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

            if (cellHandler.getTankProperties()[0].getContents() != null) {
                int countFilled = fluidHandler.fill(getContents(player.getHeldItem(hand)), true);
                System.out.println("fill tile: " + countFilled);
                if (countFilled == 1000) cellHandler.drain(1000, true);
            } else {
                System.out.println("drain tile");
                cellHandler.fill(fluidHandler.drain(capacity, true), true);
            }
        } else if (world.getBlockState(pos).getBlock() instanceof BlockStorage) {
            Material mat = ((BlockStorage) world.getBlockState(pos).getBlock()).getMaterial();
            if (mat != null && mat.has(ItemFlag.LIQUID)) {
                player.setHeldItem(hand, getCellWithFluid(getType(), mat.getLiquid()));
            }
        } else{
//            Material mat = Materials.getAll().toArray(new Material[0])[Ref.RNG.nextInt(Materials.getCount())];
//            if (mat != null && mat.has(ItemFlag.LIQUID)) {
//                player.setHeldItem(hand, getCellWithFluid(mat.getLiquid()));
//            }
            player.setHeldItem(hand, getCellWithFluid(getType(), Materials.Diesel.getLiquid()));
        }
        return EnumActionResult.SUCCESS;
    }

    @Nullable
    public static FluidStack getContents(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        return handler.getTankProperties()[0].getContents();
    }

    public static ItemStack getCellWithFluid(ItemType cellType, Fluid fluid) {
        ItemStack stack = new ItemStack(GregTechRegistry.getItem(cellType.getName()));
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        handler.fill(new FluidStack(fluid, handler.getTankProperties()[0].getCapacity()), true);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":fluid_cell", "inventory"));
    }
}
