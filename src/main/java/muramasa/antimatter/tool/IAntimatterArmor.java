package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IAntimatterArmor extends ISharedAntimatterObject, IColorHandler, ITextureProvider, IModelProvider, IForgeItem {
    AntimatterArmorType getAntimatterArmorType();

    default Material getMaterial(ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL));
    }

    ItemStack asItemStack(Material primary);

    default CompoundTag getDataTag(ItemStack stack) {
        CompoundTag dataTag = stack.getTagElement(Ref.TAG_TOOL_DATA);
        return dataTag != null ? dataTag : validateTag(stack, Data.NULL);
    }

    default Item getItem() {
        return (Item) this;
    }

    default ItemStack resolveStack(Material primary) {
        Item item = (Item) this;
        ItemStack stack = new ItemStack(item);
        validateTag(stack, primary);
        Map<Enchantment, Integer> mainEnchants = primary.getArmorEnchantments();
        if (!mainEnchants.isEmpty()) {
            mainEnchants.entrySet().stream().filter(e -> e.getKey().canEnchant(stack)).forEach(e -> stack.enchant(e.getKey(), e.getValue()));
            return stack;
        }
        return stack;
    }

    default CompoundTag validateTag(ItemStack stack, Material primary) {
        CompoundTag dataTag = stack.getOrCreateTagElement(Ref.TAG_TOOL_DATA);
        dataTag.putString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL, primary.getId());
        return dataTag;
    }

    default void onGenericFillItemGroup(CreativeModeTab group, NonNullList<ItemStack> list) {
        if (group != Ref.TAB_TOOLS) return;
        list.add(asItemStack(Data.NULL));
    }

    default void onGenericAddInformation(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TextComponent("Material: " + getMaterial(stack).getDisplayName().getString()));
        if (getAntimatterArmorType().getTooltip().size() != 0) tooltip.addAll(getAntimatterArmorType().getTooltip());
    }

    default Ingredient getRepairMaterial(ItemStack stack) {
        Material primary = getMaterial(stack);
        if (primary == null) {
            return Ingredient.EMPTY;
        }
        if (primary.has(Data.GEM)) {
            return Ingredient.of(TagUtils.getForgeItemTag("gems/".concat(primary.getId())));
        } else if (primary.has(Data.INGOT)) {
            return Ingredient.of(TagUtils.getForgeItemTag("ingots/".concat(primary.getId())));
        } else if (primary.has(Data.DUST)) {
            return Ingredient.of(TagUtils.getForgeItemTag("dusts/".concat(primary.getId())));
        } else if (ItemTags.getAllTags().getTag(new ResourceLocation("forge", "blocks/".concat(primary.getId()))) != null) {
            return Ingredient.of(TagUtils.getForgeItemTag("blocks/".concat(primary.getId())));
        }
        return Ingredient.EMPTY;
    }

    @Override
    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 && getMaterial(stack) != null ? getMaterial(stack).getRGB() : -1;
    }

    @Override
    default Texture[] getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        int layers = getAntimatterArmorType().getOverlayLayers();
        textures.add(new Texture(getDomain(), "item/tool/".concat(getAntimatterArmorType().getId())));
        if (layers == 1)
            textures.add(new Texture(getDomain(), "item/tool/overlay/".concat(getAntimatterArmorType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/tool/overlay/", getAntimatterArmorType().getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override
    default void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        if (this.getAntimatterArmorType().getSlot() == EquipmentSlot.HEAD) {
            String id = this.getId();
            ItemModelBuilder builder = prov.getBuilder(id + "_probe");
            builder.parent(new ModelFile.UncheckedModelFile(new ResourceLocation("minecraft", "item/handheld")));
            Texture[] textures = getTextures();
            for (int i = 0; i < textures.length + 1; i++) {
                if (i == textures.length) {
                    builder.texture("layer" + i, new Texture(getDomain(), "item/tool/overlay/".concat(getAntimatterArmorType().getId()).concat("_probe")));
                    continue;
                }
                builder.texture("layer" + i, textures[i]);
            }
            prov.tex(item, "minecraft:item/handheld", getTextures()).override().predicate(new ResourceLocation(Ref.ID, "probe"), 1).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id + "_probe")));
            return;
        }
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }

    @Override
    default String getDomain() {
        return Ref.ID;
    }
}
