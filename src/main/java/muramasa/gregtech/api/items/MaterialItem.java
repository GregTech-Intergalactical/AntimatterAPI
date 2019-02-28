package muramasa.gregtech.api.items;

import muramasa.gregtech.api.cover.CoverHelper;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.Element;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.util.Sounds;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.creativetab.GregTechTab;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.block.BlockCauldron;
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class MaterialItem extends Item {

    private static LinkedHashMap<String, MaterialItem> TYPE_LOOKUP = new LinkedHashMap<>();

    private Material material;
    private Prefix prefix;

    public MaterialItem(Prefix prefix, Material material) {
        setUnlocalizedName(prefix.getName() + "_" + material.getName());
        setRegistryName(prefix.getName() + "_" + material.getName());
        setCreativeTab(Ref.TAB_MATERIALS);
        TYPE_LOOKUP.put(prefix.getName() + material.getName(), this);
        this.material = material;
        this.prefix = prefix;
    }

    public static void init() {
        for (Prefix prefix : Prefix.getAll()) {
            for (Material material : Materials.getAll()) {
                if (!prefix.allowGeneration(material)) continue;
                new MaterialItem(prefix, material);
            }
        }
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
        if (element != null) {
            tooltip.add(element.getDisplayName());
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            return CoverHelper.placeCover(tile, stack, side, hitX, hitY, hitZ) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        if (prefix == Prefix.DustImpure && world.getBlockState(pos).getBlock() instanceof BlockCauldron) {
            int level = world.getBlockState(pos).getValue(BlockCauldron.LEVEL);
            if (level > 0) {
                System.out.println(level);
                MaterialItem item = (MaterialItem) player.getHeldItem(hand).getItem();
                player.setHeldItem(hand, get(Prefix.DustPure, item.getMaterial(), stack.getCount()));
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCauldron.LEVEL, --level));
                Sounds.BUCKET_EMPTY.play(world, pos);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        String set = getMaterial().getSet().getName();
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":material_set_item/" + set, set + "=" + prefix));
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public Material getMaterial() {
        return material;
    }

    public static boolean hasPrefix(ItemStack stack, Prefix prefix) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getPrefix() == prefix;
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getMaterial() == material;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return hasPrefix(stack, Prefix.Plate);
    }

    public static ItemStack get(Prefix prefix, Material material, int count) {
        ItemStack replacement = prefix.getItemReplacement(material);
        if (replacement == null) {
            return new ItemStack(TYPE_LOOKUP.get(prefix.getName() + material.getName()), count);
        } else {
            replacement.setCount(count);
            return replacement;
        }
    }

    public static Collection<MaterialItem> getAll() {
        return TYPE_LOOKUP.values();
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) {
                if (stack.getItem() instanceof MaterialItem) {
                    Material material = ((MaterialItem) stack.getItem()).getMaterial();
                    if (material != null) {
                        return material.getRGB();
                    }
                }
            }
            return -1;
        }
    }
}
