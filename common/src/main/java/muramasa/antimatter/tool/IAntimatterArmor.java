package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IAntimatterArmor extends ISharedAntimatterObject, IColorHandler, ITextureProvider, IModelProvider, IAbstractToolMethods {
    AntimatterArmorType getAntimatterArmorType();

    Material getMat();

    ItemStack asItemStack();

    default Item getItem() {
        return (Item) this;
    }

    default ItemStack resolveStack() {
        Item item = (Item) this;
        ItemStack stack = new ItemStack(item);
        Map<Enchantment, Integer> mainEnchants = MaterialTags.ARMOR.get(getMat()).toolEnchantment();
        if (!mainEnchants.isEmpty()) {
            mainEnchants.entrySet().stream().filter(e -> e.getKey().canEnchant(stack)).forEach(e -> stack.enchant(e.getKey(), e.getValue()));
            return stack;
        }
        return stack;
    }

    default void onGenericAddInformation(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        if (getAntimatterArmorType().getTooltip().size() != 0) tooltip.addAll(getAntimatterArmorType().getTooltip());
    }

    @Override
    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 && getMat() != null ? getMat().getRGB() : -1;
    }

    @Override
    default Texture[] getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        int layers = getAntimatterArmorType().getOverlayLayers();
        textures.add(new Texture(Ref.ID, "item/tool/".concat(getAntimatterArmorType().getId())));
        if (layers == 1)
            textures.add(new Texture(Ref.ID, "item/tool/overlay/".concat(getAntimatterArmorType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(Ref.ID, String.join("", "item/tool/overlay/", getAntimatterArmorType().getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override
    default void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        if (this.getAntimatterArmorType().getSlot() == EquipmentSlot.HEAD) {
            String id = this.getId();
            AntimatterItemModelBuilder builder = prov.getBuilder(id + "_probe");
            builder.parent(new ResourceLocation("minecraft", "item/handheld"));
            Texture[] textures = getTextures();
            for (int i = 0; i < textures.length + 1; i++) {
                if (i == textures.length) {
                    builder.texture("layer" + i, new Texture(Ref.ID, "item/tool/overlay/".concat(getAntimatterArmorType().getId()).concat("_probe")));
                    continue;
                }
                builder.texture("layer" + i, textures[i]);
            }
            prov.tex(item, "minecraft:item/handheld", getTextures()).override().predicate(new ResourceLocation(Ref.ID, "probe"), 1).model(new ResourceLocation(Ref.SHARED_ID, "item/" + id + "_probe")).end();
            return;
        }
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }
}
