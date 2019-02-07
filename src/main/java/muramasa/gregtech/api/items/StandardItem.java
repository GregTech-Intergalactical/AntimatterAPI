package muramasa.gregtech.api.items;

import muramasa.gregtech.api.capability.ICoverable;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.enums.ItemList;
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
import java.util.List;

public class StandardItem extends Item {

    public StandardItem() {
        setRegistryName("standard_item");
        setUnlocalizedName(Ref.MODID + ".standard_item");
        setCreativeTab(Ref.TAB_ITEMS);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("items")) {
                for (ItemList item : ItemList.values()) {
                    items.add(new ItemStack(this, 1, item.ordinal()));
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        ItemList item = ItemList.get(stack);
        if (item != null) {
            return item.getDisplayName();
        }
        return "DISPLAY NAME ERROR";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ItemList item = ItemList.get(stack);
        if (item != null) {
            if (!item.getTooltip().isEmpty()) {
                tooltip.add(item.getTooltip());
            }
            if (ItemList.Debug_Scanner.isItemEqual(stack)) {
                tooltip.add("Size: " + Machines.PULVERIZER.getRecipeMap().getRecipes().size());
            }
        }
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
                if (ItemList.Cover_Item_Port.isItemEqual(stack)) {
                    consume = coverHandler.setCover(targetSide, CoverType.ITEM_PORT);
                } else if (ItemList.Cover_Fluid_Port.isItemEqual(stack)) {
                    consume = coverHandler.setCover(targetSide, CoverType.FLUID_PORT);
                } else if (ItemList.Cover_Energy_Port.isItemEqual(stack)) {
                    consume = coverHandler.setCover(targetSide, CoverType.ENERGY_PORT);
                }
                if (consume) {
                    stack.shrink(1);
                }
            }
            if (ItemList.Debug_Scanner.isItemEqual(stack)) {
                if (tile instanceof TileEntityMachine) {
                    if (tile instanceof TileEntityMultiMachine) {
                        ((TileEntityMultiMachine) tile).shouldCheckStructure = true;
                        ((TileEntityMultiMachine) tile).shouldCheckRecipe = true;
                    } else if (tile instanceof TileEntityHatch) {
//                            System.out.println(((TileEntityHatch) tile).getTexture());
//                            ((TileEntityHatch) tile).setTexture(((TileEntityHatch) tile).getTextureId() == Machines.BLAST_FURNACE.getId() ? ((TileEntityHatch) tile).getTierId() : Machines.BLAST_FURNACE.getId());
//                            ((TileEntityHatch) tile).markForRenderUpdate();
                    } else {
                        System.out.println("Setting Tint");
                        ((TileEntityMachine) tile).setTint(((TileEntityMachine) tile).getTint() != -1 ? -1 : Materials.Plutonium241.getRGB());
                        ((TileEntityMachine) tile).markForRenderUpdate();
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS; //TODO FAIL?
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (ItemList item : ItemList.values()) {
            ModelLoader.setCustomModelResourceLocation(this, item.ordinal(), new ModelResourceLocation(Ref.MODID + ":standard_item", "id=" + item.ordinal()));
        }
    }
}
