package muramasa.gregtech.api.items;

import muramasa.gregtech.api.capability.ICoverable;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.enums.ItemList;
import muramasa.gregtech.api.machines.MachineList;
import muramasa.gregtech.api.materials.GTItemStack;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.creativetab.GregTechTab;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.gregtech.common.utils.Ref;
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
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class MetaItem extends Item {

    private static HashMap<String, GTItemStack> stringToStack = new HashMap<>();
    private static Prefix[] generatedPrefixes;

    public static final int standardItemStartIndex = 32000;

    public MetaItem() {
        setMaxDamage(0);
        setHasSubtypes(true);
        setRegistryName("meta_item");
        setUnlocalizedName(Ref.MODID + ".meta_item");
        setCreativeTab(Ref.TAB_MATERIALS);
        generatedPrefixes = Prefix.values();
        for (int p = 0; p < generatedPrefixes.length; p++) {
            for (int m = 0; m < Material.generated.length; m++) {
                if (Material.generated[m] == null || !generatedPrefixes[p].allowGeneration(Material.generated[m])) continue;
                ItemStack stack = new ItemStack(this, 1, (p * 1000) + Material.generated[m].getId());
                stringToStack.put(generatedPrefixes[p].getName() + Material.generated[m].getDisplayName(), new GTItemStack(stack, generatedPrefixes[p].showInCreative()));
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("materials")) {
                for (int p = 0; p < generatedPrefixes.length; p++) {
                    if (!generatedPrefixes[p].showInCreative()) continue;
                    for (int m = 0; m < Material.generated.length; m++) {
                        if (Material.generated[m] != null) {
                            if (!generatedPrefixes[p].allowGeneration(Material.generated[m])) continue;
                            subItems.add(new ItemStack(this, 1, (p * 1000) + Material.generated[m].getId()));
                        }
                    }
                }
//                for (GTItemStack stack : stringToStack.values()) {
//                    if (stack.doesShowInCreative()) {
//                        subItems.add(stack.getStack());
//                    }
//                }
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
            if (ItemList.Debug_Scanner.isItemEqual(stack)) {
//                Recipe recipe = MachineList.ALLOY_SMELTER.findRecipe(new ItemStack[]{Material.Copper.getIngot(1), Material.Cobalt.getDust(1)});
//                if (recipe != null) {
//                    tooltip.add(recipe.toString());
//                } else {
                tooltip.add("No Recipe");
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
//                    if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
//                        ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, facing);
//                        if (coverHandler != null) {
//                            player.sendMessage(new TextComponentString(coverHandler.getCover(facing).name()));
//                        }
//                    }
//                    if (tile.hasCapability(ITechCapabilities.ENERGY, facing)) {
//                        System.out.println("HAS ENERGY CAP");
//                    }
                    /*else if (tile.hasCapability(ITechCapabilities.COMPONENT, null)) {
                        IComponent component = tile.getCapability(ITechCapabilities.COMPONENT, null);
                        player.sendMessage(new TextComponentString(component.getLinkedControllers().toString()));
                    }*/
//                    else if (tile.hasCapability(ITechCapabilities.COMPONENT, null)) {
//                        IComponent component = tile.getCapability(ITechCapabilities.COMPONENT, null);
//                        if (component != null) {
//                            player.sendMessage(new TextComponentString(TextFormatting.DARK_AQUA + component.getId()));
//                        }
//                    }
//                    else if (tile instanceof TileEntityMultiMachine) {
//                        if (((TileEntityMultiMachine) tile).isServerSide() && hand == EnumHand.MAIN_HAND) {
//                            ((TileEntityMultiMachine) tile).shouldCheckStructure = true;
//                            ((TileEntityMultiMachine) tile).shouldCheckRecipe = true;
//                        }
//                    }
//                    if (!world.isRemote) {
//                        player.sendMessage(new TextComponentString("Server: " + tile.toString()));
//                    } else {
//                        player.sendMessage(new TextComponentString("Client: " + tile.toString()));
//                    }
                    if (tile instanceof TileEntityMachine) {
                        if (tile instanceof TileEntityMultiMachine) {
                            ((TileEntityMultiMachine) tile).shouldCheckStructure = true;
                            ((TileEntityMultiMachine) tile).shouldCheckRecipe = true;
                        } else if (tile instanceof TileEntityHatch) {
                            System.out.println(((TileEntityHatch) tile).getTextureId());
                            ((TileEntityHatch) tile).setTextureId(((TileEntityHatch) tile).getTextureId() == MachineList.BLAST_FURNACE.getInternalId() ? ((TileEntityHatch) tile).getTierId() : MachineList.BLAST_FURNACE.getInternalId());
                            ((TileEntityHatch) tile).markForRenderUpdate();
                        } else {
//                            if (((TileEntityMachine) tile).isServerSide()) {
//                                System.out.println("SERVER FACING: " + ((TileEntityMachine) tile).getFacing());
//                            } else if (((TileEntityMachine) tile).isClientSide()) {
//                                System.out.println("CLIENT FACING: " + ((TileEntityMachine) tile).getFacing());
//                            }
                            System.out.println("Setting Tint");
                            ((TileEntityMachine) tile).setTint(((TileEntityMachine) tile).getTint() != -1 ? -1 : Material.Plutonium241.getRGB());
//                            ((TileEntityMachine) tile).markDirty();
                            ((TileEntityMachine) tile).markForRenderUpdate();
                        }
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
        GTItemStack stack = stringToStack.get(prefix.getName() + material.getDisplayName());
        if (stack != null) {
            ItemStack copy = stack.getStack().copy();
            copy.setCount(amount);
            return copy;
        }
        System.err.println("get() NULL: " + prefix.getName() + material.getName());
        return null;
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
                ModelLoader.setCustomModelResourceLocation(this, (p * 1000) + m, new ModelResourceLocation(Ref.MODID + ":material_set/" + Material.generated[m].getSet(), Material.generated[m].getSet() + "=" + generatedPrefixes[p].getName()));
            }
        }
        for (ItemList item : ItemList.values()) {
            ModelLoader.setCustomModelResourceLocation(this, standardItemStartIndex + item.ordinal(), new ModelResourceLocation(Ref.MODID + ":meta_item", "standard=" + item.ordinal()));
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
