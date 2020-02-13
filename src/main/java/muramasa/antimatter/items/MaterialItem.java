package muramasa.antimatter.items;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.SoundType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MaterialItem extends Item implements IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider {

    protected String domain, id;
    protected Material material;
    protected MaterialType type;

    public MaterialItem(String domain, MaterialType type, Material material, Properties properties) {
        super(properties);
        this.material = material;
        this.type = type;
        this.domain = domain;
        this.id = getType().getId() + "_" + getMaterial().getId();
        setRegistryName(domain, getId());
        AntimatterAPI.register(MaterialItem.class, this);
    }

    public MaterialItem(String domain, MaterialType type, Material material) {
        this(domain, type, material, new Properties().group(Ref.TAB_MATERIALS));
    }

    public MaterialType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group) && getType().isVisible()) items.add(new ItemStack(this));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (getMaterial().getChemicalFormula() != null) tooltip.add(new StringTextComponent(getMaterial().getChemicalFormula()).applyTextStyle(TextFormatting.DARK_AQUA));
        if (type == MaterialType.ROCK) {
            tooltip.add(new TranslationTextComponent("gtu.tooltip.occurrence").applyTextStyle(TextFormatting.YELLOW).appendSibling(material.getDisplayName()));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getPlayer() == null) return ActionResultType.PASS;
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        BlockState state = context.getWorld().getBlockState(context.getPos());
        if (type == MaterialType.DUST_IMPURE && state.getBlock() instanceof CauldronBlock) {
            int level = state.get(CauldronBlock.LEVEL);
            if (level > 0) {
                MaterialItem item = (MaterialItem) stack.getItem();
                context.getPlayer().setHeldItem(context.getHand(), get(MaterialType.DUST_IMPURE, item.getMaterial(), stack.getCount()));
                context.getWorld().setBlockState(context.getPos(), state.with(CauldronBlock.LEVEL, --level));
                SoundType.BUCKET_EMPTY.play(context.getWorld(), context.getPos());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
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
        ItemStack replacement = AntimatterAPI.getReplacement(type, material);
        if (!replacement.isEmpty()) return Utils.ca(count, replacement);
        if (!type.allowGeneration(material)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: T(" + type.getId() + ") M(" + material.getId() + ")");
        MaterialItem item = AntimatterAPI.get(MaterialItem.class, type.getId() + "_" + material.getId());
        if (item == null) Utils.onInvalidData("GET ERROR - MAT ITEM NULL: T(" + type.getId() + ") M(" + material.getId() + ")");
        if (count == 0) Utils.onInvalidData("GET ERROR - COUNT 0: T(" + type.getId() + ") M(" + material.getId() + ")");
        ItemStack stack = new ItemStack(item, count);
        if (stack.isEmpty()) Utils.onInvalidData("GET ERROR - MAT STACK EMPTY: T(" + type.getId() + ") M(" + material.getId() + ")");
        return stack;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    public Texture[] getTextures() {
        return getMaterial().getSet().getTextures(getDomain(), getType());
    }
}
