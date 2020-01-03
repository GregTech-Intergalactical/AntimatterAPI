//package muramasa.antimatter.items;
//
//import muramasa.gtu.Configs;
//import muramasa.gtu.Ref;
//import muramasa.antimatter.capability.impl.FluidHandlerItemCell;
//import muramasa.antimatter.materials.Material;
//import muramasa.antimatter.materials.MaterialType;
//import muramasa.antimatter.util.Utils;
//import muramasa.antimatter.client.itemgroup.GregTechItemGroup;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.util.Hand;
//import net.minecraft.util.NonNullList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.client.model.ModelLoader;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import net.minecraftforge.fluids.capability.IFluidHandlerItem;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class ItemFluidCell extends GregTechItem {
//
//    protected Material material;
//    private int capacity, maxTemp;
//
//    public ItemFluidCell(Material material, int capacity) {
//        super("cell_".concat(material.getId()), new Item.Properties().maxStackSize(1));
//        this.material = material;
//        this.capacity = capacity;
//        this.maxTemp = material.getMeltingPoint();
//    }
//
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public int getMaxTemp() {
//        return maxTemp;
//    }
//
//    @Override
//    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
//        if (tab instanceof GregTechItemGroup && ((GregTechItemGroup) tab).getName().equals("items")) {
//            if (Configs.JEI.SHOW_ALL_FLUID_CELLS) {
//                MaterialType.LIQUID.all().forEach(m -> items.add(fill(m.getLiquid())));
//                MaterialType.GAS.all().forEach(m -> items.add(fill(m.getGas())));
//                MaterialType.PLASMA.all().forEach(m -> items.add(fill(m.getPlasma())));
//            } else {
//                items.add(new ItemStack(this));
//            }
//        }
//    }
//
//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
//        return new FluidHandlerItemCell(stack, capacity, maxTemp);
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        FluidStack fluid = getContents(stack);
//        if (fluid != null) tooltip.add(fluid.getLocalizedName() + " - " + fluid.amount);
//        tooltip.add("Max Temp: " + ((ItemFluidCell) stack.getItem()).getMaxTemp() + "K");
//    }
//
//    @Override
//    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float hitX, float hitY, float hitZ) {
//        //TODO reenable
////        if (world.isRemote) return EnumActionResult.PASS;
//
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
//            System.out.println("has cap");
//
//            IFluidHandler fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
//            IFluidHandlerItem cellHandler = player.getHeldItem(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//
//            if (cellHandler.getTankProperties()[0].getContents() != null) {
//                int countFilled = fluidHandler.fill(getContents(player.getHeldItem(hand)), true);
//                System.out.println("fill tile: " + countFilled);
//                if (countFilled == 1000) cellHandler.drain(1000, true);
//            } else {
//                System.out.println("drain tile");
//                cellHandler.fill(fluidHandler.drain(capacity, true), true);
//            }
//        }
//        return EnumActionResult.SUCCESS;
//    }
//
//    @Nullable
//    public static FluidStack getContents(ItemStack stack) {
//        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//        return handler.getTankProperties()[0].getContents();
//    }
//
//    public ItemStack fill(Fluid fluid) {
//        ItemStack stack = new ItemStack(this);
//        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
//        handler.fill(new FluidStack(fluid, handler.getTankProperties()[0].getCapacity()), true);
//        return stack;
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void onModelRegistration() {
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":fluid_cell", "inventory"));
//    }
//}
