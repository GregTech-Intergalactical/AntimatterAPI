package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Element;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
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
    private MaterialType type;

    public MaterialItem(MaterialType type, Material material) {
        this.material = material;
        this.type = type;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_MATERIALS);
        GregTechAPI.register(MaterialItem.class, this);
    }

    public MaterialType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String getId() {
        return type.getId() + "_" + material.getId();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getName().equals("materials")) {
                if (getType().isVisible()) {
                    items.add(new ItemStack(this));
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        MaterialItem item = (MaterialItem) stack.getItem();
        return item.getType().getDisplayName(item.getMaterial());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (getMaterial().getChemicalFormula() != null) tooltip.add(getMaterial().getChemicalFormula());
        if (Utils.hasChanceTag(stack)) tooltip.add(TextFormatting.WHITE + "Chance: " + Utils.getChanceTag(stack) + "%");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            return GregTechAPI.placeCover(tile, stack, side, hitX, hitY, hitZ) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        if (type == MaterialType.DUST_IMPURE && world.getBlockState(pos).getBlock() instanceof BlockCauldron) {
            int level = world.getBlockState(pos).getValue(BlockCauldron.LEVEL);
            if (level > 0) {
                MaterialItem item = (MaterialItem) player.getHeldItem(hand).getItem();
                player.setHeldItem(hand, get(MaterialType.DUST_IMPURE, item.getMaterial(), stack.getCount()));
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCauldron.LEVEL, --level));
                SoundType.BUCKET_EMPTY.play(world, pos);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    public static boolean hasType(ItemStack stack, MaterialType type) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getType() == type;
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getMaterial() == material;
    }

    public static MaterialType getType(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem) stack.getItem()).getType();
    }

    public static Material getMaterial(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem) stack.getItem()).getMaterial();
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return hasType(stack, MaterialType.PLATE);
    }

    public static ItemStack get(MaterialType type, Material material, int count) {
        ItemStack replacement = GregTechAPI.getReplacement(type, material);
        if (replacement != null) return Utils.ca(count, replacement);

        if (!type.allowGeneration(material)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: P(" + type.getId() + ") M(" + material.getId() + ")");
        MaterialItem item = GregTechAPI.get(MaterialItem.class, type.getId() + "_" + material.getId());
        if (item == null) Utils.onInvalidData("GET ERROR - MAT ITEM NULL: P(" + type.getId() + ") M(" + material.getId() + ")");
        if (count == 0) Utils.onInvalidData("GET ERROR - COUNT 0: P(" + type.getId() + ") M(" + material.getId() + ")");
        ItemStack mat = new ItemStack(item, count);
        if (mat.isEmpty()) Utils.onInvalidData("GET ERROR - MAT STACK EMPTY: P(" + type.getId() + ") M(" + material.getId() + ")");
        return mat;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":" + type.getId() + "_" + material.getId(), "inventory"));
    }
}
