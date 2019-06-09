package muramasa.gtu.api.items;

import com.google.common.base.CaseFormat;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.tileentities.TileEntityItemFluidMachine;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public class StandardItem extends Item implements IGregTechObject, IModelOverride {

    protected String id, tooltip = "";
    protected boolean enabled = true;

    public StandardItem(String id) {
        this.id = id;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_ITEMS);
        GregTechAPI.register(this);
    }

    public StandardItem(String id, String tooltip) {
        this(id);
        this.tooltip = tooltip;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEnabled() {
        return enabled || Ref.enableAllModItem;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return Utils.trans("item.standard." + getId() + ".name");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.tooltip);
        if (Utils.hasNoConsumeTag(stack)) {
            tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
        }
        if (Data.DebugScanner.equals(this)) {
            tooltip.add(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, Prefix.IngotHot.getId() + "_" + Materials.Uranium235.getId()));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            if (GregTechAPI.placeCover(tile, stack, side, hitX, hitY, hitZ)) {
                return EnumActionResult.SUCCESS;
            } else if (Data.DebugScanner.isEqual(stack)) {
                if (tile instanceof TileEntityMachine) {
                    if (tile instanceof TileEntityMultiMachine) {
                        if (!((TileEntityMultiMachine) tile).validStructure) {
                            ((TileEntityMultiMachine) tile).checkStructure();
                        }
                        ((TileEntityMultiMachine) tile).checkRecipe();
                    } else if (tile instanceof TileEntityHatch) {
                        MachineFluidHandler handler = ((TileEntityHatch) tile).getFluidHandler();
                        if (handler != null) {
                            System.out.println(handler.toString());
                        }
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
            if (Data.DebugScanner.isEqual(stack)) {

            }
        }
        return EnumActionResult.FAIL; //TODO FAIL?
    }

//    public ItemType required(String... mods) {
//        for (int i = 0; i < mods.length; i++) {
//            if (!Utils.isModLoaded(mods[i])) {
//                enabled = false;
//                break;
//            }
//        }
//        return this;
//    }
//
//    public ItemType optional(String... mods) {
//        enabled = false;
//        for (int i = 0; i < mods.length; i++) {
//            if (Utils.isModLoaded(mods[i])) {
//                enabled = true;
//                break;
//            }
//        }
//        return this;
//    }

    public boolean isEqual(ItemStack stack) {
        return stack.getItem() == this;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return GregTechAPI.getCoverFromCatalyst(stack) != null;
    }

    public ItemStack get(int count) {
        if (count == 0) return Utils.addNoConsumeTag(new ItemStack(this, 1));
        return new ItemStack(this, count);
    }

    @Override
    public ItemStack asItemStack() {
        return get(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":standard_item", "id=" + id));
    }
}
