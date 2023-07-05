package muramasa.antimatter.tool.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.tool.IAntimatterArmor;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static muramasa.antimatter.material.Material.NULL;

public class MaterialArmor extends ArmorItem implements IAntimatterArmor, DyeableLeatherItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    protected String domain;
    protected AntimatterArmorType type;
    protected Material material;

    public MaterialArmor(String domain, AntimatterArmorType type, Material materialIn, EquipmentSlot slot, Properties builderIn) {
        super(new MatArmorMaterial(type, materialIn), slot, builderIn);
        this.domain = domain;
        this.material = materialIn;
        this.type = type;
        AntimatterAPI.register(IAntimatterArmor.class, this);
        if (type.getSlot() == EquipmentSlot.HEAD && AntimatterAPI.getSIDE().isClient()) {
            RenderHelper.registerProbePropertyOverrides(this);
        }
    }

    @Override
    public String getId() {
        return material.getId() + "_" + type.getId();
    }

    @Override
    public AntimatterArmorType getAntimatterArmorType() {
        return type;
    }

    @Override
    public Material getMat() {
        return material;
    }

    @Override
    public ItemStack asItemStack() {
        return resolveStack();
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return amount;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getMaxDamage();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category.canEnchant(stack.getItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        onGenericAddInformation(stack, tooltip, flag);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (category != Ref.TAB_TOOLS) return;
        items.add(asItemStack());
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String extra = "";
        if (slot == EquipmentSlot.HEAD && type != null) {
            CompoundTag nbt = stack.getTag();
            if (nbt != null && nbt.contains("theoneprobe") && nbt.getBoolean("theoneprobe")) extra = "_probe";
        }
        return Ref.ID + ":textures/models/armor_layer_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + (type == null ? "" : "_" + type + extra) + ".png";
    }

    @Override
    public int getColor(ItemStack stack) {
        return getItemColor(stack, null, 0);
    }

    @Override
    public boolean hasCustomColor(ItemStack stack) {
        return material != NULL;
    }

    @Override
    public void setColor(ItemStack stack, int color) {

    }

    @Override
    public void clearColor(ItemStack stack) {

    }
}
