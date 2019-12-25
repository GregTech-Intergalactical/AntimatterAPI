package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelProvider;
import muramasa.gtu.api.util.SoundType;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.proxy.providers.GregTechItemModelProvider;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

import javax.annotation.Nullable;
import java.util.List;

public class MaterialItem extends Item implements IGregTechObject, IModelProvider, IColorHandler {

    private Material material;
    private MaterialType type;

    public MaterialItem(MaterialType type, Material material) {
        super(new Properties().group(Ref.TAB_MATERIALS));
        this.material = material;
        this.type = type;
        setRegistryName(getId());
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
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group) && getType().isVisible()) items.add(new ItemStack(this));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return ((MaterialItem) stack.getItem()).getType().getDisplayName(((MaterialItem) stack.getItem()).getMaterial());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (getMaterial().getChemicalFormula() != null) tooltip.add(new StringTextComponent(TextFormatting.DARK_AQUA + getMaterial().getChemicalFormula()));
        if (type == MaterialType.ROCK) tooltip.add(new StringTextComponent("Indicates occurrence of " + TextFormatting.YELLOW + material.getDisplayName()));
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
        ItemStack replacement = GregTechAPI.getReplacement(type, material);
        if (!replacement.isEmpty()) return Utils.ca(count, replacement);
        if (!type.allowGeneration(material)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: T(" + type.getId() + ") M(" + material.getId() + ")");
        MaterialItem item = GregTechAPI.get(MaterialItem.class, type.getId() + "_" + material.getId());
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
    @OnlyIn(Dist.CLIENT)
    public void onItemModelBuild(GregTechItemModelProvider provider, ItemModelBuilder builder) {
        provider.layered(builder, getMaterial().getSet().getTextures(getType()));
    }
}
