package muramasa.antimatter.material;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

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
        this(domain, type, material, new Properties().tab(Ref.TAB_MATERIALS));
    }

    public MaterialType<?> getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group) && getType().isVisible()) items.add(new ItemStack(this));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        if (!getMaterial().getChemicalFormula().isEmpty()) {
            if (Screen.hasShiftDown()) {
                tooltip.add(new TextComponent(getMaterial().getChemicalFormula()).withStyle(ChatFormatting.DARK_AQUA));
            } else {
                tooltip.add(new TextComponent("Hold Shift to show formula").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
            }
        }
        if (type == Data.ROCK) {
            tooltip.add(new TranslatableComponent("antimatter.tooltip.occurrence").append(new TextComponent(material.getDisplayName().getString()).withStyle(ChatFormatting.YELLOW)));
        }
    }

    public static InteractionResult interactWithCauldron(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (world.isClientSide()) return InteractionResult.PASS;
        MaterialItem item = (MaterialItem) stack.getItem();
        MaterialType<?> type = item.getType();
        if (type == Data.DUST_IMPURE && state.getBlock() instanceof AbstractCauldronBlock) {
            int level = state.getValue(LayeredCauldronBlock.LEVEL);
            if (level > 0) {
                stack.shrink(1);
                if (!player.addItem(DUST.get(item.getMaterial(), 1))) {
                    player.drop(DUST.get(item.getMaterial(), 1), false);
                }
                LayeredCauldronBlock.lowerFillLevel(state, world, pos);
                world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;

    }

    public Tag.Named<Item> getTag() {
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
