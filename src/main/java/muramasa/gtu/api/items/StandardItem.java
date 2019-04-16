package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.registration.IHasModelOverride;
import muramasa.gtu.api.tileentities.TileEntityItemFluidMachine;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
import muramasa.gtu.api.util.Utils;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class StandardItem extends Item implements IHasModelOverride {

    private ItemType type;

    public StandardItem(ItemType type) {
        setUnlocalizedName(type.getName());
        setRegistryName(type.getName());
        setCreativeTab(Ref.TAB_ITEMS);
        this.type = type;
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return ((StandardItem) stack.getItem()).getType().getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(((StandardItem) stack.getItem()).getType().getTooltip());
        if (Utils.hasNoConsumeTag(stack)) {
            tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
        }
        if (type == ItemType.DebugScanner) {

        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            if (GregTechAPI.placeCover(tile, stack, side, hitX, hitY, hitZ)) {
                return EnumActionResult.SUCCESS;
            } else if (ItemType.DebugScanner.isEqual(stack)) {
                if (tile instanceof TileEntityMachine) {
                    if (tile instanceof TileEntityMultiMachine) {
                        if (!((TileEntityMultiMachine) tile).validStructure) {
                            ((TileEntityMultiMachine) tile).checkStructure();
                        }
                        ((TileEntityMultiMachine) tile).checkRecipe();
                    } else if (tile instanceof TileEntityHatch) {
                        //NOOP
                    } else if (tile instanceof TileEntityItemFluidMachine) {
                        MachineFluidHandler fluidHandler = ((TileEntityItemFluidMachine) tile).getFluidHandler();
                        for (FluidStack fluid : fluidHandler.getInputs()) {
                            System.out.println(fluid.getLocalizedName() + " - " + fluid.amount);
                        }
                        tile.markDirty();
                    } else {
                        for (Cover c : GregTechAPI.getRegisteredCovers()) {
                            System.out.println(c.getName() + " - " + c.getInternalId());
                        }
                    }
                } else if (tile instanceof TileEntityPipe) {
                    player.sendMessage(new TextComponentString("C: " + ((TileEntityPipe) tile).getConnections() + (((TileEntityPipe) tile).getConnections() > 63 ? " (Culled)" : " (Non Culled)")));
                }
            }
        } else {

        }
        return EnumActionResult.FAIL; //TODO FAIL?
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":standard_item", "id=" + getType().getName()));
    }
}
