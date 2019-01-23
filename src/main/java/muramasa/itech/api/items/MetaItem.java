package muramasa.itech.api.items;

import muramasa.itech.ITech;
import muramasa.itech.api.capability.IComponent;
import muramasa.itech.api.capability.ICoverable;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.ItemList;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.materials.Prefix;
import muramasa.itech.api.materials.Material;
import muramasa.itech.api.util.Utils;
import muramasa.itech.client.creativetab.ITechTab;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
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
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class MetaItem extends Item {

    private static HashMap<String, ItemStack> stringToStack = new HashMap<>();
    private static Prefix[] generatedPrefixes;

    public static final int standardItemStartIndex = 32000;

    public MetaItem() {
        setMaxDamage(0);
        setHasSubtypes(true);
        setRegistryName("metaitem");
        setUnlocalizedName(ITech.MODID + ".metaitem");
        setCreativeTab(ITech.TAB_MATERIALS);
        generatedPrefixes = Prefix.values();
        for (int p = 0; p < generatedPrefixes.length; p++) {
            for (int m = 0; m < Material.generated.length; m++) {
                if (Material.generated[m] == null || !generatedPrefixes[p].allowGeneration(Material.generated[m])) continue;
                ItemStack stack = new ItemStack(this, 1, (p * 1000) + Material.generated[m].getId());
                stringToStack.put(generatedPrefixes[p].getName() + Material.generated[m].getDisplayName(), stack);
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (tab instanceof ITechTab) {
            if (((ITechTab) tab).getTabName().equals("materials")) {
                for (int p = 0; p < generatedPrefixes.length; p++) {
                    if (!generatedPrefixes[p].showInCreative()) continue;
                    for (int m = 0; m < Material.generated.length; m++) {
                        if (Material.generated[m] != null) {
                            if (!generatedPrefixes[p].allowGeneration(Material.generated[m])) continue;
                            subItems.add(new ItemStack(this, 1, (p * 1000) + Material.generated[m].getId()));
                        }
                    }
                }
                for (ItemList item : ItemList.values()) {
                    subItems.add(new ItemStack(this, 1, standardItemStartIndex + item.ordinal()));
                }
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getMetadata() < standardItemStartIndex) {
            Prefix prefix = getPrefix(stack);
            Material material = getMaterial(stack);
            if (prefix != null && material != null) {
                return prefix.getDisplayName(material);
            }
        } else {
            ItemList item = ItemList.get(stack);
            if (item != null) {
                return item.getDisplayName();
            }
        }
        return "DISPLAY NAME ERROR";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.getMetadata() < standardItemStartIndex) {
            Material material = getMaterial(stack);
            if (material != null && material.getElement() != null) {
                tooltip.add(material.getElement().name());
            }
        } else {
            ItemList item = ItemList.get(stack);
            if (item != null && !item.getTooltip().isEmpty()) {
                tooltip.add(item.getTooltip());
            }
            if (ItemList.DebugScanner.isItemEqual(stack)) {
                tooltip.add("MULTI FLAG: " + MachineFlag.MULTI.getTypes().size());
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getMetadata() >= standardItemStartIndex) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile != null) {
                if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
                    ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, facing);
                    EnumFacing targetSide = Utils.determineInteractionSide(facing, hitX, hitY, hitZ);
                    boolean consume = false;
                    if (ItemList.CoverItemPort.isItemEqual(stack)) {
                        consume = coverHandler.setCover(targetSide, CoverType.ITEMPORT);
                    } else if (ItemList.CoverFluidPort.isItemEqual(stack)) {
                        consume = coverHandler.setCover(targetSide, CoverType.FLUIDPORT);
                    } else if (ItemList.CoverEnergyPort.isItemEqual(stack)) {
                        consume = coverHandler.setCover(targetSide, CoverType.ENERGYPORT);
                    }
                    if (consume) {
                        stack.shrink(1);
                    }
                }
                if (ItemList.DebugScanner.isItemEqual(stack)) {
                    if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
                        ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, facing);
                        if (coverHandler != null) {
                            player.sendMessage(new TextComponentString(coverHandler.getCover(facing).name()));
                        }
                    }
                    if (tile.hasCapability(ITechCapabilities.ENERGY, facing)) {
                        System.out.println("HAS ENERGY CAP");
                    }
                    /*else if (tile.hasCapability(ITechCapabilities.COMPONENT, null)) {
                        IComponent component = tile.getCapability(ITechCapabilities.COMPONENT, null);
                        player.sendMessage(new TextComponentString(component.getLinkedControllers().toString()));
                    }*/
                    else if (tile instanceof IComponent) {
                        IComponent component = (IComponent) tile;
                        player.sendMessage(new TextComponentString(component.getLinkedControllers().toString()));
                    }
                    else if (tile instanceof TileEntityMultiMachine) {
                        if (((TileEntityMultiMachine) tile).isServerSide() && hand == EnumHand.MAIN_HAND) {
                            ((TileEntityMultiMachine) tile).shouldCheckStructure = true;
                            ((TileEntityMultiMachine) tile).shouldCheckRecipe = true;
                        }
                    }
                    if (!world.isRemote) {
                        player.sendMessage(new TextComponentString("Server: " + tile.toString()));
                    } else {
                        player.sendMessage(new TextComponentString("Client: " + tile.toString()));
                    }
                }
            }
        } else {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile != null) {
                if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
                    if (hasPrefix(stack, Prefix.PLATE)) {
                        ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, facing);
                        coverHandler.setCover(facing, CoverType.BLANK);
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public static ItemStack get(Prefix prefix, Material material, int amount) {
        ItemStack stack = stringToStack.get(prefix.getName() + material.getDisplayName());
        if (stack != null) {
            stack.setCount(amount);
        } else {
            System.err.println("get() NULL: " + prefix.getName() + material.getName());
        }
        return stack;
    }

    public static Material getMaterial(ItemStack stack) {
        return stack.getMetadata() < standardItemStartIndex ? Material.generated[stack.getMetadata() % 1000] : null;
    }

    public static Prefix getPrefix(ItemStack stack) {
        return generatedPrefixes[stack.getMetadata() / 1000];
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return getMaterial(stack) == material;
    }

    public static boolean hasPrefix(ItemStack stack, Prefix prefix) {
        return getPrefix(stack) == prefix;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int p = 0; p < generatedPrefixes.length; p++) {
            for (int m = 0; m < Material.generated.length; m++) {
                if (generatedPrefixes[p] == null || Material.generated[m] == null) continue;
                ModelLoader.setCustomModelResourceLocation(this, (p * 1000) + m, new ModelResourceLocation(ITech.MODID + ":materialsets/" + Material.generated[m].getSet(), Material.generated[m].getSet() + "=" + generatedPrefixes[p].getName()));
            }
        }
        for (ItemList item : ItemList.values()) {
            ModelLoader.setCustomModelResourceLocation(this, standardItemStartIndex + item.ordinal(), new ModelResourceLocation(ITech.MODID + ":metaitem", "standard=" + item.ordinal()));
        }
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (stack.getMetadata() < standardItemStartIndex) {
                if (tintIndex == 0) { //layer0
                    Material material = Material.generated[stack.getMetadata() % 1000];
                    return material != null ? material.getRGB() : 0xffffff;
                }
            }
            return -1;
        }
    }
}
