package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Element;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.util.SoundType;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.creativetab.GregTechTab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class MaterialItem extends Item implements IGregTechObject, IModelOverride, IColorHandler {

    private Material material;
    private Prefix prefix;

    public MaterialItem(Prefix prefix, Material material) {
        this.material = material;
        this.prefix = prefix;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_MATERIALS);
        GregTechRegistry.register(MaterialItem.class, this);
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String getId() {
        return prefix.getId() + "_" + material.getId();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("materials")) {
                if (getPrefix().isVisible()) {
                    items.add(new ItemStack(this));
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        MaterialItem item = (MaterialItem) stack.getItem();
        return item.getPrefix().getDisplayName(item.getMaterial());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Element element = ((MaterialItem) stack.getItem()).getMaterial().getElement();
        if (element != null) tooltip.add(element.getDisplayName());
        if (Utils.hasChanceTag(stack)) tooltip.add(TextFormatting.WHITE + "Chance: " + Utils.getChanceTag(stack) + "%");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            return GregTechAPI.placeCover(tile, stack, side, hitX, hitY, hitZ) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        if (prefix == Prefix.DustImpure && world.getBlockState(pos).getBlock() instanceof BlockCauldron) {
            int level = world.getBlockState(pos).getValue(BlockCauldron.LEVEL);
            if (level > 0) {
                MaterialItem item = (MaterialItem) player.getHeldItem(hand).getItem();
                player.setHeldItem(hand, get(Prefix.DustPure, item.getMaterial(), stack.getCount()));
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCauldron.LEVEL, --level));
                SoundType.BUCKET_EMPTY.play(world, pos);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    public static boolean hasPrefix(ItemStack stack, Prefix prefix) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getPrefix() == prefix;
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getMaterial() == material;
    }

    public static Prefix getPrefix(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem) stack.getItem()).getPrefix();
    }

    public static Material getMaterial(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem) stack.getItem()).getMaterial();
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return hasPrefix(stack, Prefix.Plate);
    }

    public static ItemStack get(Prefix prefix, Material material, int count) {
        ItemStack replacement = prefix.getReplacement(material);
        if (replacement == null) {
            if (!prefix.allowGeneration(material)) {
                if (Ref.RECIPE_EXCEPTIONS) {
                    throw new IllegalStateException("GET ERROR - DOES NOT GENERATE: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                } else {
                    System.err.println("GET ERROR - DOES NOT GENERATE: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                }
            }
            MaterialItem item = GregTechRegistry.get(MaterialItem.class, prefix.getId() + "_" + material.getId());
            if (item == null) {
                if (Ref.RECIPE_EXCEPTIONS) {
                    throw new IllegalStateException("GET ERROR - MAT ITEM NULL: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                } else {
                    System.err.println("GET ERROR - MAT ITEM NULL: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                }
            }
            if (count == 0) {
                if (Ref.RECIPE_EXCEPTIONS) {
                    System.out.println("ITEM: " + item.getPrefix().getId() + " - " + item.getMaterial().getId());
                    throw new IllegalStateException("GET ERROR - COUNT 0: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                } else {
                    System.err.println("GET ERROR - COUNT 0: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                }
            }
            ItemStack mat = new ItemStack(item, count);
            if (mat.isEmpty()) {
                if (Ref.RECIPE_EXCEPTIONS) {
                    System.out.println("ITEM: " + item.getPrefix().getId() + " - " + item.getMaterial().getId());
                    throw new IllegalStateException("GET ERROR - MAT STACK EMPTY: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                } else {
                    System.err.println("GET ERROR - MAT STACK EMPTY: P(" + prefix.getId() + ") M(" + material.getId() + ")");
                }
            }
            return mat;
        } else {
            return Utils.ca(count, replacement);
        }
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        String set = getMaterial().getSet().getId();
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":material_set_item/" + set, set + "=" + prefix));
    }
}
