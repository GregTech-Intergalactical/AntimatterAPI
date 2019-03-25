package muramasa.gregtech.api.items;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.interfaces.IHasModelOverride;
import muramasa.gregtech.client.render.GTModelLoader;
import muramasa.gregtech.client.render.models.ModelFluidCell;
import muramasa.gregtech.loaders.GregTechRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFluidCell extends Item implements IHasModelOverride {

    private static int CAPACITY = 1000;

    public ItemFluidCell() {
        setUnlocalizedName("fluid_cell");
        setRegistryName("fluid_cell");
        setMaxStackSize(1);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStackSimple(stack, CAPACITY);
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
        IFluidHandlerItem handler = player.getHeldItem(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler != null) {
            System.out.println("filling");
            handler.fill(Materials.Titaniumtetrachloride.getLiquid(1000), true);
        }
        return EnumActionResult.SUCCESS;
    }

    @Nullable
    public static FluidStack getContents(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        return handler.getTankProperties()[0].getContents();
    }

    public static ItemStack getCellWithFluid(Fluid fluid) {
        ItemStack stack = new ItemStack(GregTechRegistry.getItem("fluid_cell"));
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        handler.fill(new FluidStack(fluid, CAPACITY), true);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        GTModelLoader.register(getRegistryName().getResourcePath(), new ModelFluidCell());
    }
}
