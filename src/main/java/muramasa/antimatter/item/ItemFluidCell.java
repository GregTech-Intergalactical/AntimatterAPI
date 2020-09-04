package muramasa.antimatter.item;

import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.capability.fluid.FluidHandlerItemCell;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class ItemFluidCell extends ItemBasic<ItemFluidCell> {

    protected Material material;
    private int capacity, maxTemp;

    public ItemFluidCell(Material material, int capacity) {
        super(Ref.ID, "cell_".concat(material.getId()));
        this.material = material;
        this.capacity = capacity;
        this.maxTemp = material.getMeltingPoint();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

   /* @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechItemGroup && ((GregTechItemGroup) tab).getName().equals("items")) {
            if (Configs.JEI.SHOW_ALL_FLUID_CELLS) {
                MaterialType.LIQUID.all().forEach(m -> items.add(fill(m.getLiquid())));
                MaterialType.GAS.all().forEach(m -> items.add(fill(m.getGas())));
                MaterialType.PLASMA.all().forEach(m -> items.add(fill(m.getPlasma())));
            } else {
                items.add(new ItemStack(this));
            }
        }
    }*/

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FluidHandlerItemCell(stack, capacity, maxTemp);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        /*stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(x -> {
            FluidStack fluid = x.getFluidInTank(0);
            if (fluid != null) tooltip.add(new StringTextComponent(fluid.getDisplayName() + " - " + fluid.getAmount()));
            tooltip.add(new StringTextComponent("Max Temp: " + ((ItemFluidCell) stack.getItem()).getMaxTemp() + "K"));
        });*/
    }

    public ItemStack fill(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(h -> {
            h.fill(new FluidStack(fluid, h.getTankCapacity(0)), EXECUTE);
        });
        return stack;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctxt) {
        //TODO reenable
//      if (world.isRemote) return EnumActionResult.PASS;

        TileEntity tile = Utils.getTile(ctxt.getWorld(), ctxt.getPos());
        if (tile != null) {
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                ctxt.getPlayer().getHeldItem(ctxt.getHand()).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cellHandler -> {
                    int countFilled = fluidHandler.fill(cellHandler.getFluidInTank(0), SIMULATE);
                    if (countFilled == 1000) {
                        fluidHandler.fill(cellHandler.getFluidInTank(0), EXECUTE);
                        cellHandler.drain(1000, EXECUTE);
                    }
                });
            });
        }

        return ActionResultType.PASS;
    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":fluid_cell", "inventory"));
    }*/
}
