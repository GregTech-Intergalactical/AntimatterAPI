package muramasa.antimatter.item;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.fluid.FluidHandlerItemCell;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class ItemFluidCell extends ItemBasic<ItemFluidCell> {

    public final Material material;
    private int capacity, maxTemp;

    private final Fluid stack;

    public ItemFluidCell(String domain, Material material, int capacity) {
        super(domain, "cell_".concat(material.getId()));
        Data.EMPTY_CELLS.add(this);
        AntimatterTextureStitcher.addStitcher(t -> {
            t.accept(new ResourceLocation(domain, "item/other/"+getId()+"_cover"));
            t.accept(new ResourceLocation(domain, "item/other/"+getId()+"_fluid"));
        });
        this.material = material;
        this.capacity = capacity;
        this.maxTemp = material.getMeltingPoint();
        this.stack = Fluids.EMPTY;
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
        if (worldIn == null) return;
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(x -> {
            FluidStack fluid = x.getFluidInTank(0);
            if (!fluid.isEmpty()) tooltip.add(new StringTextComponent(fluid.getDisplayName() + " - " + fluid.getAmount()));
            tooltip.add(new StringTextComponent("Max Temp: " + ((ItemFluidCell) stack.getItem()).getMaxTemp() + "K"));
        });
    }

    public static ITag.INamedTag<Item> getTag() {
        return Utils.getItemTag(new ResourceLocation(Ref.ID, "cell"));
    }

    public ItemStack fill(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(h -> {
            h.fill(new FluidStack(fluid, h.getTankCapacity(0)), EXECUTE);
        });
        return stack;
    }

    public Fluid getFluid() {
        return this.stack;
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
                        if (!cellHandler.drain(1000, EXECUTE).isEmpty()) {
                            ctxt.getPlayer().setItemStackToSlot(ctxt.getHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, cellHandler.getContainer());
                        }
                    }
                });
            });
        }

        return ActionResultType.PASS;
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ((AntimatterItemModelBuilder)prov.getAntimatterBuilder(item).bucketProperties(stack,true,false).parent(new ModelFile.UncheckedModelFile("forge:item/bucket"))).tex((map) -> {
            map.put("base", getDomain() + ":item/basic/" + getId());
            map.put("cover",getDomain() + ":item/other/"+getId() + "_cover");
            map.put("fluid", getDomain() + ":item/other/"+getId() + "_fluid");
        });
    }


}
