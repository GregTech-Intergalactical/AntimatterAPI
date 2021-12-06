package muramasa.antimatter.tool.armor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class AntimatterArmorType implements ISharedAntimatterObject {
    private final String domain, id;
    private final List<Component> tooltip = new ObjectArrayList<>();
    private final boolean repairable;
    private final int durabilityFactor, baseArmor;
    private final float baseToughness, baseKnockback;
    private final CreativeModeTab itemGroup;
    private EquipmentSlot slot;
    private SoundEvent event;
    int overlayLayers;
    @Nullable
    private IMaterialTag materialRequirement;

    /**
     * Instantiates a AntimatterArmorType with its basic values
     *
     * @param domain           unique identifier provided by the mod
     * @param id               unique identifier
     * @param durabilityFactor durability multiplier used to determine the durability of an armor piece, it is multiplied by these to determine the durability: 13(head), 15(chest), 16(legs), 11(feet)
     * @param baseArmor        base armor protection that would be applied to item's attribute
     * @param baseToughness    base toughness that would be applied to item's attribute
     * @param baseKnockback    base knockback resistance that would be applied to the item's attributes
     * @param slot             armor slot the item goes in
     * @return a brand new AntimatterArmorType for enjoyment
     */
    public AntimatterArmorType(String domain, String id, int durabilityFactor, int baseArmor, float baseToughness, float baseKnockback, EquipmentSlot slot) {
        this.domain = domain;
        this.id = id;
        this.repairable = true;
        this.durabilityFactor = durabilityFactor;
        this.baseArmor = baseArmor;
        this.baseToughness = baseToughness;
        this.baseKnockback = baseKnockback;
        this.itemGroup = Ref.TAB_TOOLS;
        this.slot = slot;
        this.event = SoundEvents.ARMOR_EQUIP_IRON;
        this.overlayLayers = 0;
        AntimatterAPI.register(AntimatterArmorType.class, this);
    }

    public IAntimatterArmor instantiateTools(String domain) {
        return new MaterialArmor(domain, this, new MatArmorMaterial(this), slot, prepareInstantiation(domain));
    }

    public IAntimatterArmor instantiateTools(String domain, Supplier<Item.Properties> properties) {
        return new MaterialArmor(domain, this, new MatArmorMaterial(this), slot, properties.get());
    }

    private Item.Properties prepareInstantiation(String domain) {
        if (domain.isEmpty()) Utils.onInvalidData("An AntimatterArmorType was instantiated with an empty domain name!");
        Item.Properties properties = new Item.Properties().tab(itemGroup);
        if (!repairable) properties.setNoRepair();
        // if (!TOOL_TYPES.isEmpty()) TOOL_TYPES.forEach(t -> properties.addTag<Block>(t, tier.getHarvestLevel()));
        return properties;
    }

    public AntimatterArmorType setOverlayLayers(int overlayLayers) {
        this.overlayLayers = overlayLayers;
        return this;
    }

    public AntimatterArmorType setEvent(SoundEvent event) {
        this.event = event;
        return this;
    }

    public AntimatterArmorType setArmorSlot(EquipmentSlot slot) {
        this.slot = slot;
        return this;
    }

    public AntimatterArmorType setToolTip(Component... tooltip) {
        this.tooltip.addAll(Arrays.asList(tooltip));
        return this;
    }

    public AntimatterArmorType setPrimaryRequirement(IMaterialTag tag) {
        if (tag == null)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterArmorType was set to have no primary material requirement even when it was explicitly called!");
        this.materialRequirement = tag;
        return this;
    }

    public ItemStack getToolStack(Material primary) {
        return Objects.requireNonNull(AntimatterAPI.get(IAntimatterArmor.class, id)).asItemStack(primary);
    }

    public List<Component> getTooltip() {
        return tooltip;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isRepairable() {
        return repairable;
    }

    public int getOverlayLayers() {
        return overlayLayers;
    }

    public int getDurabilityFactor() {
        return durabilityFactor;
    }

    public int getBaseArmor() {
        return baseArmor;
    }

    public float getBaseToughness() {
        return baseToughness;
    }

    public float getBaseKnockback() {
        return baseKnockback;
    }

    public CreativeModeTab getItemGroup() {
        return itemGroup;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public SoundEvent getEvent() {
        return event;
    }
}
