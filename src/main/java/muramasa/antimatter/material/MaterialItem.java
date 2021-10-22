package muramasa.antimatter.material;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.Data.DUST;

public class MaterialItem extends ItemBasic<MaterialItem> implements ISharedAntimatterObject, IColorHandler, ITextureProvider, IModelProvider {

    protected Material material;
    protected MaterialType<?> type;

    public MaterialItem(String domain, MaterialType<?> type, Material material, Properties properties) {
        super(domain, type.getId() + "_" + material.getId(), MaterialItem.class, properties);
        this.material = material;
        this.type = type;
    }

    public MaterialItem(String domain, MaterialType<?> type, Material material) {
        this(domain, type, material, new Properties().group(Ref.TAB_MATERIALS));
    }

    public MaterialType<?> getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group) && getType().isVisible()) items.add(new ItemStack(this));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (!getMaterial().getChemicalFormula().isEmpty()) {
            if (Screen.hasShiftDown()) {
                tooltip.add(new StringTextComponent(getMaterial().getChemicalFormula()).mergeStyle(TextFormatting.DARK_AQUA));
            } else {
                tooltip.add(new StringTextComponent("Hold Shift to show formula").mergeStyle(TextFormatting.AQUA).mergeStyle(TextFormatting.ITALIC));
            }
        }
        if (type == Data.ROCK) {
            tooltip.add(new TranslationTextComponent("antimatter.tooltip.occurrence").appendSibling(new StringTextComponent(material.getDisplayName().getString()).mergeStyle(TextFormatting.YELLOW)));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getPlayer() == null) return ActionResultType.PASS;
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        ItemStack stack = player.getHeldItem(context.getHand());
        BlockState state = world.getBlockState(pos);
        if (type == Data.DUST_IMPURE && state.getBlock() instanceof CauldronBlock) {
            int level = state.get(CauldronBlock.LEVEL);
            if (level > 0) {
                MaterialItem item = (MaterialItem) stack.getItem();
                if (item.getMaterial().has(DUST)){
                    stack.shrink(1);
                    if (!player.addItemStackToInventory(DUST.get(item.getMaterial(), 1))){
                        player.dropItem(DUST.get(item.getMaterial(), 1), false);
                    }
                    world.setBlockState(context.getPos(), state.with(CauldronBlock.LEVEL, --level));
                    world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.FAIL;
    }

    public ITag.INamedTag<Item> getTag() {
        return TagUtils.getForgeItemTag(String.join("", Utils.getConventionalMaterialType(type), "/", material.getId()));
    }

    public static boolean hasType(ItemStack stack, MaterialType<?> type) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getType() == type;
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getMaterial() == material;
    }

    public static MaterialType<?> getType(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem) stack.getItem()).getType();
    }

    public static Material getMaterial(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem) stack.getItem()).getMaterial();
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return hasType(stack, Data.PLATE);
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? material.getRGB() : -1;
    }

    @Override
    public Texture[] getTextures() {
        return getMaterial().getSet().getTextures(getType());
    }
}
