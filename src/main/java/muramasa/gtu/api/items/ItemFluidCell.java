package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.impl.FluidHandlerItemCell;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.util.Utils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFluidCell extends StandardItem {

    protected Material material;
    private int capacity, maxTemp;
    
    public ItemFluidCell(Material material, int capacity) {
        super("cell_".concat(material.getId()));
        this.material = material;
        this.capacity = capacity;
        this.maxTemp = material.getMeltingPoint();
        setMaxStackSize(1);
    }

    public ItemFluidCell(Material material, int capacity, int stackSize) {
        this(material, capacity);
        setMaxStackSize(stackSize);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        GenerationFlag.LIQUID.getMats().forEach(m -> items.add(fill(m.getLiquid())));
        GenerationFlag.GAS.getMats().forEach(m -> items.add(fill(m.getGas())));
        GenerationFlag.PLASMA.getMats().forEach(m -> items.add(fill(m.getPlasma())));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemCell(stack, capacity, maxTemp);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        FluidStack fluid = getContents(stack);
        if (fluid != null) tooltip.add(fluid.getLocalizedName() + " - " + fluid.amount);
        tooltip.add("Max Temp: " + ((ItemFluidCell) stack.getItem()).getMaxTemp() + "K");
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
        }
        return EnumActionResult.SUCCESS;
    }

    @Nullable
    public static FluidStack getContents(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        return handler.getTankProperties()[0].getContents();
    }

    public ItemStack fill(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        handler.fill(new FluidStack(fluid, handler.getTankProperties()[0].getCapacity()), true);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":fluid_cell", "inventory"));
    }
}
