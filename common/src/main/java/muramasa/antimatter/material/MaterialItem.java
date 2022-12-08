package muramasa.antimatter.material;

import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.data.AntimatterMaterialTypes.CRUSHED_PURIFIED;
import static muramasa.antimatter.data.AntimatterMaterialTypes.DUST;
import static muramasa.antimatter.data.AntimatterMaterialTypes.DUST_TINY;

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

    @SuppressWarnings("NoTranslation")
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        //Here only add specific types, events are handled below.
        if (type == AntimatterMaterialTypes.ROCK) {
            tooltip.add(new TranslatableComponent("antimatter.tooltip.occurrence").append(new TextComponent(material.getDisplayName().getString()).withStyle(ChatFormatting.YELLOW)));
        }
    }

    @SuppressWarnings("NoTranslation")
    public static void addTooltipsForMaterialItems(ItemStack stack, Material mat, MaterialType<?> type, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        if (!mat.getChemicalFormula().isEmpty()) {
            if (Screen.hasShiftDown()) {
                tooltip.add(new TranslatableComponent("antimatter.tooltip.chemical_formula").append(": ").append(new TextComponent(mat.getChemicalFormula()).withStyle(ChatFormatting.DARK_AQUA)));
                tooltip.add(new TranslatableComponent("antimatter.tooltip.mass").append(": ").append(new TextComponent(mat.getMass() + "").withStyle(ChatFormatting.DARK_AQUA)));
                tooltip.add(new TranslatableComponent("antimatter.tooltip.material_modid", AntimatterPlatformUtils.getModName(mat.materialDomain())));
            } else {
                tooltip.add(new TranslatableComponent("antimatter.tooltip.formula").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
            }
        }
    }

    public static InteractionResult interactWithCauldron(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (world.isClientSide()) return InteractionResult.PASS;
        MaterialItem item = (MaterialItem) stack.getItem();
        MaterialType<?> type = item.getType();
        if (state.getBlock() instanceof AbstractCauldronBlock){
            int level = state.getValue(LayeredCauldronBlock.LEVEL);
            if (level > 0){
                Material material = ((MaterialItem) stack.getItem()).getMaterial();
                if (type == AntimatterMaterialTypes.DUST_IMPURE) {
                    if (material.has(DUST)) {
                        stack.shrink(1);
                        if (!player.addItem(DUST.get(material, 1))) {
                            player.drop(DUST.get(material, 1), false);
                        }
                        LayeredCauldronBlock.lowerFillLevel(state, world, pos);
                        world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                } else if (type == AntimatterMaterialTypes.CRUSHED) {
                    if (material.has(CRUSHED_PURIFIED)) {
                        stack.shrink(1);
                        if (!player.addItem(CRUSHED_PURIFIED.get(material, 1))) {
                            player.drop(CRUSHED_PURIFIED.get(material, 1), false);
                        }
                        Material oreByProduct = material.getByProducts().size() >= 1 ? material.getByProducts().get(0) : material;
                        if (oreByProduct.has(DUST) && world.random.nextInt(100) < 50){
                            if (!player.addItem(DUST_TINY.get(oreByProduct, 1))) {
                                player.drop(DUST_TINY.get(oreByProduct, 1), false);
                            }
                        }
                        LayeredCauldronBlock.lowerFillLevel(state, world, pos);
                        world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

        }
        return InteractionResult.PASS;

    }

    public TagKey<Item> getTag() {
        return TagUtils.getForgelikeItemTag(String.join("", Utils.getConventionalMaterialType(type), "/", material.getId()));
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
        return hasType(stack, AntimatterMaterialTypes.PLATE);
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
