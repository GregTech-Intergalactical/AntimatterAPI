package muramasa.gregtech.api.items;

import muramasa.gregtech.api.capability.ICoverable;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.creativetab.GregTechTab;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class StandardItem extends Item {

    private static LinkedHashMap<String, StandardItem> TYPE_LOOKUP = new LinkedHashMap<>();

    private String type;

    public StandardItem(ItemType type) {
        setUnlocalizedName(Ref.MODID + "_item_" + type.getName());
        setRegistryName("item_" + type.getName());
        setCreativeTab(Ref.TAB_ITEMS);
        this.type = type.getName();
        TYPE_LOOKUP.put(type.getName(), this);
    }

    public ItemType getType() {
        return ItemType.get(type);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("items")) {
                items.add(new ItemStack(this));
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return ((StandardItem) stack.getItem()).getType().getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(((StandardItem) stack.getItem()).getType().getTooltip());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
                ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, facing);
                EnumFacing targetSide = Utils.determineInteractionSide(facing, hitX, hitY, hitZ);
                boolean consume = false;
                if (ItemType.ItemPort.isItemEqual(stack)) {
                    consume = coverHandler.setCover(targetSide, CoverType.ITEM_PORT);
                } else if (ItemType.FluidPort.isItemEqual(stack)) {
                    consume = coverHandler.setCover(targetSide, CoverType.FLUID_PORT);
                } else if (ItemType.EnergyPort.isItemEqual(stack)) {
                    consume = coverHandler.setCover(targetSide, CoverType.ENERGY_PORT);
                }
                if (consume) {
                    stack.shrink(1);
                }
            }
            if (ItemType.DebugScanner.isItemEqual(stack)) {
                if (tile instanceof TileEntityMachine) {
                    if (tile instanceof TileEntityMultiMachine) {
                        ((TileEntityMultiMachine) tile).shouldCheckStructure = true;
                        ((TileEntityMultiMachine) tile).shouldCheckRecipe = true;
                    } else if (tile instanceof TileEntityHatch) {
//                            System.out.println(((TileEntityHatch) tile).getTexture());
//                            ((TileEntityHatch) tile).setTexture(((TileEntityHatch) tile).getTextureId() == Machines.BLAST_FURNACE.getId() ? ((TileEntityHatch) tile).getTierId() : Machines.BLAST_FURNACE.getId());
//                            ((TileEntityHatch) tile).markForRenderUpdate();
                    } else {
//                        System.out.println("Setting Tint");
//                        ((TileEntityMachine) tile).setTint(((TileEntityMachine) tile).getTint() != -1 ? -1 : Materials.Plutonium241.getRGB());
//                        ((TileEntityMachine) tile).markForRenderUpdate();
                        System.out.println(tile);
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS; //TODO FAIL?
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":standard_item", "id=" + getType().getName()));
    }

    public static ItemStack get(String name, int count) {
        return new ItemStack(TYPE_LOOKUP.get(name), count);
    }

    public static Collection<StandardItem> getAll() {
        return TYPE_LOOKUP.values();
    }
}
