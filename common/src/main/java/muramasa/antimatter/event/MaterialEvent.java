package muramasa.antimatter.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.data.ArmorData;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialStack;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.material.data.HandleData;
import muramasa.antimatter.material.data.ToolData;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static muramasa.antimatter.material.MaterialTags.*;

public class MaterialEvent<T extends MaterialEvent<T>> {
    protected Material material = Material.NULL;

    public T setMaterial(Material material){
        this.material = material;
        return (T) this;
    }

    public T setMaterial(String material){
        return setMaterial(Material.get(material));
    }

    public T asDust(IMaterialTag... tags) {
        return asDust(295, tags);
    }

    public T asDust(int meltingPoint, IMaterialTag... tags) {
        flags(AntimatterMaterialTypes.DUST, AntimatterMaterialTypes.DUST_SMALL, AntimatterMaterialTypes.DUST_TINY);
        flags(tags);
        MaterialTags.MELTING_POINT.add(material, meltingPoint);
        if (meltingPoint > 295) {
//            asFluid();//TODO disabled due to Sodium having a fluid
        }
        return (T) this;
    }

    public T asSolid(IMaterialTag... tags) {
        return asSolid(295, tags);
    }

    public T asSolid(int meltingPoint, IMaterialTag... tags){
        asDust(meltingPoint, tags);
        flags(AntimatterMaterialTypes.INGOT, AntimatterMaterialTypes.NUGGET, AntimatterMaterialTypes.BLOCK).asFluid(); //TODO: Shall we generate blocks for every solid?
        return (T) this;
    }

    public T asMetal(IMaterialTag... tags) {
        return asMetal(295, tags);
    }

    public T asMetal(int meltingPoint, IMaterialTag... tags) {
        flags(METAL);
        return asSolid(meltingPoint, tags);
    }

    public T asOre(int minXp, int maxXp, boolean small, IMaterialTag... tags) {
        EXP_RANGE.add(material, UniformInt.of(minXp, maxXp));
        return asOre(small, tags);
    }

    public T asOre(IMaterialTag... tags) {
        return asOre(true, tags);
    }

    public T asOre(boolean small, IMaterialTag... tags) {
        asDust(AntimatterMaterialTypes.ORE, AntimatterMaterialTypes.ROCK, AntimatterMaterialTypes.CRUSHED, AntimatterMaterialTypes.CRUSHED_PURIFIED, AntimatterMaterialTypes.CRUSHED_REFINED, AntimatterMaterialTypes.DUST_IMPURE, AntimatterMaterialTypes.DUST_PURE, AntimatterMaterialTypes.RAW_ORE, AntimatterMaterialTypes.RAW_ORE_BLOCK);
        if (small) flags(AntimatterMaterialTypes.ORE_SMALL);
        if (!has(EXP_RANGE)) EXP_RANGE.add(material, UniformInt.of(1, 5));
        flags(tags);
        return (T) this;
    }

    public T asOreStone(int minXp, int maxXp, IMaterialTag... tags) {
        asOre(minXp, maxXp, false, tags);
        flags(AntimatterMaterialTypes.ORE_STONE);
        return (T) this;
    }

    public T asOreStone(IMaterialTag... tags) {
        asOre(tags);
        asDust(AntimatterMaterialTypes.ORE_STONE, AntimatterMaterialTypes.ORE, AntimatterMaterialTypes.ROCK, AntimatterMaterialTypes.CRUSHED, AntimatterMaterialTypes.CRUSHED_PURIFIED, AntimatterMaterialTypes.CRUSHED_REFINED, AntimatterMaterialTypes.DUST_IMPURE, AntimatterMaterialTypes.DUST_PURE);
        flags(tags);
        return (T) this;
    }

    public T asGemBasic(boolean transparent, IMaterialTag... tags) {
        asDust(tags);
        flags(AntimatterMaterialTypes.GEM, AntimatterMaterialTypes.BLOCK);
        if (transparent) {
            flags(MaterialTags.TRANSPARENT, AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.LENS);
        }
        return (T) this;
    }

    public T asGem(boolean transparent, IMaterialTag... tags) {
        asGemBasic(transparent, tags);
        flags(AntimatterMaterialTypes.GEM_EXQUISITE);
        return (T) this;
    }

    public T asFluid() {
        return asFluid(0);
    }

    public T asFluid(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asFluid(fuelPower, Math.max(meltingPoint, 295));
    }

    public T asFluid(int fuelPower, int temp) {
        flags(AntimatterMaterialTypes.LIQUID);
        MaterialTags.FUEL_POWER.add(this.material, fuelPower);
        MaterialTags.LIQUID_TEMPERATURE.add(this.material, temp);
        if (temp >= 400 && material.has(METAL)){
            flags(MOLTEN);
        }
        return (T) this;
    }

    public T asGas() {
        return asGas(0);
    }

    public T asGas(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asGas(fuelPower, Math.max(meltingPoint, 295));
    }

    public T asGas(int fuelPower,int temp) {
        flags(AntimatterMaterialTypes.GAS);
        MaterialTags.FUEL_POWER.add(this.material, fuelPower);
        MaterialTags.GAS_TEMPERATURE.add(this.material, temp);
        return (T) this;
    }



    public T asPlasma() {
        return asPlasma(0);
    }

    public T asPlasma(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asPlasma(fuelPower, meltingPoint);
    }

    public T asPlasma(int fuelPower,int temp) {
        flags(AntimatterMaterialTypes.PLASMA);
        return asGas(fuelPower,temp);
    }

    public T harvestLevel(int harvestLevel) {
        MaterialTags.MINING_LEVEL.add(this.material, harvestLevel);
        return (T) this;
    }

    public T addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality) {
        return addTools(toolDamage, toolSpeed, toolDurability, toolQuality, ImmutableMap.of());
    }

    public T addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment, AntimatterToolType... toolTypes) {
        if (has(AntimatterMaterialTypes.INGOT))
            flags(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.ROD, AntimatterMaterialTypes.SCREW, AntimatterMaterialTypes.BOLT); //TODO: We need to add bolt for now since screws depends on bolt, need to find time to change it
        else flags(AntimatterMaterialTypes.ROD);
        List<AntimatterToolType> toolTypesList = toolTypes.length > 0 ? Arrays.asList(toolTypes) : AntimatterAPI.all(AntimatterToolType.class);
        MaterialTags.TOOLS.add(this.material, new ToolData(toolDamage, toolSpeed, toolDurability, toolQuality, toolEnchantment, toolTypesList));
        MaterialTags.MINING_LEVEL.add(this.material, toolQuality - 1);
        if (toolTypesList.contains(AntimatterDefaultTools.ELECTRIC_WRENCH)) flags(AntimatterMaterialTypes.WRENCHBIT);
        if (toolTypesList.contains(AntimatterDefaultTools.BUZZSAW)) flags(AntimatterMaterialTypes.BUZZSAW_BLADE);
        if (toolTypesList.contains(AntimatterDefaultTools.DRILL)) flags(AntimatterMaterialTypes.DRILLBIT);
        if (toolTypesList.contains(AntimatterDefaultTools.CHAINSAW)) flags(AntimatterMaterialTypes.CHAINSAWBIT);
        return (T) this;
    }

    public T addTools(Material derivedMaterial, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        ToolData data = MaterialTags.TOOLS.get(derivedMaterial);
        return addTools(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality(), toolEnchantment);
    }

    public T addTools(Material derivedMaterial) {
        ToolData data = MaterialTags.TOOLS.get(derivedMaterial);
        return addTools(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality());
    }

    public T setAllowedTypes(AntimatterToolType... toolTypes) {
        if (!has(MaterialTags.TOOLS)) return (T) this;
        ToolData data = MaterialTags.TOOLS.get(this.material);
        List<AntimatterToolType> toolTypesList = toolTypes.length > 0 ? Arrays.asList(toolTypes) : AntimatterAPI.all(AntimatterToolType.class);
        MaterialTags.TOOLS.add(this.material, new ToolData(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality(), data.toolEnchantment(), toolTypesList));
        return (T) this;
    }

    public T addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor) {
        return addArmor(armor, toughness, knockbackResistance, armorDurabilityFactor, ImmutableMap.of());
    }

    public T addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (armor.length != 4) {
            Antimatter.LOGGER.info("Material " + this.material.getId() + " unable to add armor, protection array must have exactly 4 values");
            return (T) this;
        }
        if (has(AntimatterMaterialTypes.INGOT)) flags(AntimatterMaterialTypes.PLATE);
        MaterialTags.ARMOR.add(this.material, new ArmorData(armor, toughness, knockbackResistance, armorDurabilityFactor, toolEnchantment));
        return (T) this;
    }

    public T addArmor(Material material, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!material.has(ARMOR)) return (T) this;
        ArmorData data = ARMOR.get(material);
        return addArmor(data.armor(), data.toughness(), data.knockbackResistance(), data.armorDurabilityFactor(), toolEnchantment);
    }

    public T addArmor(Material material) {
        if (!material.has(ARMOR)) return (T) this;
        ArmorData data = ARMOR.get(material);
        return addArmor(data.armor(), data.toughness(), data.knockbackResistance(), data.armorDurabilityFactor());
    }

    public T addHandleStat(int durability, float speed) {
        return addHandleStat(durability, speed, ImmutableMap.of());
    }

    public T addHandleStat(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!has(AntimatterMaterialTypes.ROD)) flags(AntimatterMaterialTypes.ROD);
        HANDLE.add(this.material, new HandleData(durability, speed, toolEnchantment));
        return (T) this;
    }

    public boolean has(IMaterialTag... tags) {
        for (IMaterialTag t : tags) {
            if (!t.all().contains(this.material)) return false;
        }
        return true;
    }

    public T flags(IMaterialTag... tags) {
        if (!this.material.enabled) return (T) this;
        for (IMaterialTag t : tags) {
            if (!this.has(t)) {
                t.add(this.material);
            }
            flags(t.dependents().stream().filter(d -> !this.has(d)).toArray(IMaterialTag[]::new));
        }
        return (T) this;
    }

    public T setExpRange(UniformInt expRange) {
        MaterialTags.EXP_RANGE.add(this.material, expRange);
        return (T) this;
    }

    public T setExpRange(int min, int max) {
        return this.setExpRange(UniformInt.of(min, max));
    }

    public void remove(IMaterialTag... tags) {
        if (!this.material.enabled) return;
        for (IMaterialTag t : tags) {
            t.remove(this.material);
        }
    }

    public T mats(Function<ImmutableMap.Builder<Material, Integer>, ImmutableMap.Builder<Material, Integer>> func) {
        if (!this.material.enabled) return (T) this;
        return mats(func.apply(new ImmutableMap.Builder<>()).build());
    }

    public T mats(ImmutableMap<Material, Integer> stacks) {
        return mats(stacks, -1);
    }

    public T mats(ImmutableMap<Material, Integer> stacks, int inputAmount) {
        if (!this.material.enabled) return (T) this;
        ImmutableList.Builder<MaterialStack> builder = new ImmutableList.Builder<>();
        stacks.forEach((k, v) -> builder.add(new MaterialStack(k, v)));
        PROCESS_INTO.add(material, Pair.of(builder.build(), inputAmount));
        return (T) this;
    }

    /**
     * Processing Getters/Setters
     **/

    public T setOreMulti(int multi) {
        MaterialTags.ORE_MULTI.add(this.material, multi);
        return (T) this;
    }

    public T setSmeltingMulti(int multi) {
        MaterialTags.SMELTING_MULTI.add(this.material, multi);
        return (T) this;
    }

    public T setByProductMulti(int multi) {
        MaterialTags.BY_PRODUCT_MULTI.add(this.material, multi);
        return (T) this;
    }

    public T setSmeltInto(Material m) {
        MaterialTags.SMELT_INTO.add(this.material, m);
        return (T) this;
    }

    public T setDirectSmeltInto(Material m) {
        MaterialTags.DIRECT_SMELT_INTO.add(this.material, m);
        return (T) this;
    }

    public T setArcSmeltInto(Material m) {
        MaterialTags.ARC_SMELT_INTO.add(this.material, m);
        return (T) this;
    }

    public T setMacerateInto(Material m) {
        MaterialTags.MACERATE_INTO.add(this.material, m);
        return (T) this;
    }

    public T addByProduct(Material... mats) {
        MaterialTags.BYPRODUCTS.add(this.material, new ObjectArrayList<>());
        MaterialTags.BYPRODUCTS.getList(this.material).addAll(Arrays.asList(mats));
        return (T) this;
    }

    public T replaceItem(MaterialTypeItem<?> type, Item toReplace){
        type.replacement(this.material, () -> toReplace);
        return (T) this;
    }

    public T replaceBlock(MaterialTypeBlock<?> type, Item toReplace){
        type.replacement(this.material, () -> toReplace);
        return (T) this;
    }

    public T replaceItem(MaterialTypeItem<?> type, Supplier<Item> toReplace){
        type.replacement(this.material, toReplace);
        return (T) this;
    }

    public T replaceBlock(MaterialTypeBlock<?> type, Supplier<Item> toReplace){
        type.replacement(this.material, toReplace);
        return (T) this;
    }
}
