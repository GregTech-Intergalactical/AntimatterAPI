package muramasa.gregtech.api.items;

import muramasa.gregtech.api.cover.CoverHelper;
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

    private ItemType type;

    public StandardItem(ItemType type) {
        setUnlocalizedName(type.getName());
        setRegistryName(type.getName());
        setCreativeTab(Ref.TAB_ITEMS);
        this.type = type;
        TYPE_LOOKUP.put(type.getName(), this);
    }

    public static void init() {
        for (ItemType type : ItemType.getAll()) {
            new StandardItem(type);
        }
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
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            if (CoverHelper.placeCover(tile, stack, side, hitX, hitY, hitZ)) {
                return EnumActionResult.SUCCESS;
            } else if (ItemType.DebugScanner.isEqual(stack)) {
                if (tile instanceof TileEntityMachine) {
                    if (tile instanceof TileEntityMultiMachine) {
                        ((TileEntityMultiMachine) tile).shouldCheckStructure = true;
                        System.out.println("Forced Structure Check");
//                        ((TileEntityMultiMachine) tile).shouldCheckRecipe = true;
                    } else if (tile instanceof TileEntityHatch) {
//                            System.out.println(((TileEntityHatch) tile).getTexture());
//                            ((TileEntityHatch) tile).setTexture(((TileEntityHatch) tile).getTextureId() == Machines.BLAST_FURNACE.getInternalId() ? ((TileEntityHatch) tile).getTierId() : Machines.BLAST_FURNACE.getInternalId());
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
        return EnumActionResult.FAIL; //TODO FAIL?
    }

    public ItemType getType() {
        return type;
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
