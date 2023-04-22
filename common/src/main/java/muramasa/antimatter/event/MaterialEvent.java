package muramasa.antimatter.event;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.data.ArmorData;
import muramasa.antimatter.material.data.FluidProduct;
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static muramasa.antimatter.material.MaterialTags.*;

public class MaterialEvent {
    private Material material = Material.NULL;

    public MaterialEvent setMaterial(Material material){
        this.material = material;
        return this;
    }

    public MaterialEvent setMaterial(String material){
        return setMaterial(Material.get(material));
    }

    public MaterialEvent asDust(IMaterialTag... tags) {
        return asDust(295, tags);
    }

    public MaterialEvent asDust(int meltingPoint, IMaterialTag... tags) {
        flags(AntimatterMaterialTypes.DUST, AntimatterMaterialTypes.DUST_SMALL, AntimatterMaterialTypes.DUST_TINY);
        flags(tags);
        MaterialTags.MELTING_POINT.add(material, meltingPoint);
        if (meltingPoint > 295) {
//            asFluid();//TODO disabled due to Sodium having a fluid
        }
        return this;
    }

    public MaterialEvent asSolid(IMaterialTag... tags) {
        return asSolid(295, 0, tags);
    }

    public MaterialEvent asSolid(int meltingPoint, int blastFurnaceTemp, IMaterialTag... tags) {
        asDust(meltingPoint, tags);
        flags(AntimatterMaterialTypes.INGOT, AntimatterMaterialTypes.NUGGET, AntimatterMaterialTypes.BLOCK).asFluid(); //TODO: Shall we generate blocks for every solid?
        MaterialTags.BLAST_FURNACE_TEMP.add(material, blastFurnaceTemp);
        if (blastFurnaceTemp >= 1000){
            flags(MaterialTags.NEEDS_BLAST_FURNACE);
        }
        if (blastFurnaceTemp > 1750) {
            flags(AntimatterMaterialTypes.INGOT_HOT);
        }
        return this;
    }

    public MaterialEvent asMetal(IMaterialTag... tags) {
        return asMetal(295, 0, tags);
    }

    public MaterialEvent asMetal(int meltingPoint, int blastFurnaceTemp, IMaterialTag... tags) {
        flags(METAL);
        asSolid(meltingPoint, blastFurnaceTemp, tags);
        return this;
    }

    public MaterialEvent asOre(int minXp, int maxXp, boolean small, IMaterialTag... tags) {
        EXP_RANGE.add(material, UniformInt.of(minXp, maxXp));
        return asOre(small, tags);
    }

    public MaterialEvent asOre(IMaterialTag... tags) {
        return asOre(true, tags);
    }

    public MaterialEvent asOre(boolean small, IMaterialTag... tags) {
        asDust(AntimatterMaterialTypes.ORE, AntimatterMaterialTypes.ROCK, AntimatterMaterialTypes.CRUSHED, AntimatterMaterialTypes.CRUSHED_PURIFIED, AntimatterMaterialTypes.CRUSHED_REFINED, AntimatterMaterialTypes.DUST_IMPURE, AntimatterMaterialTypes.DUST_PURE, AntimatterMaterialTypes.RAW_ORE, AntimatterMaterialTypes.RAW_ORE_BLOCK);
        if (small) flags(AntimatterMaterialTypes.ORE_SMALL);
        flags(tags);
        return this;
    }

    public MaterialEvent asOreStone(int minXp, int maxXp, IMaterialTag... tags) {
        asOre(minXp, maxXp, false, tags);
        flags(AntimatterMaterialTypes.ORE_STONE);
        return this;
    }

    public MaterialEvent asOreStone(IMaterialTag... tags) {
        asOre(tags);
        asDust(AntimatterMaterialTypes.ORE_STONE, AntimatterMaterialTypes.ORE, AntimatterMaterialTypes.ROCK, AntimatterMaterialTypes.CRUSHED, AntimatterMaterialTypes.CRUSHED_PURIFIED, AntimatterMaterialTypes.CRUSHED_REFINED, AntimatterMaterialTypes.DUST_IMPURE, AntimatterMaterialTypes.DUST_PURE);
        flags(tags);
        return this;
    }

    public MaterialEvent asGemBasic(boolean transparent, IMaterialTag... tags) {
        asDust(tags);
        flags(AntimatterMaterialTypes.GEM, AntimatterMaterialTypes.BLOCK);
        if (transparent) {
            flags(MaterialTags.TRANSPARENT, AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.LENS);
        }
        return this;
    }

    public MaterialEvent asGem(boolean transparent, IMaterialTag... tags) {
        asGemBasic(transparent, tags);
        if (!transparent) flags(AntimatterMaterialTypes.GEM_EXQUISITE);
        return this;
    }

    public MaterialEvent asFluid() {
        return asFluid(0);
    }

    public MaterialEvent asFluid(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asFluid(fuelPower, Math.max(meltingPoint, 295));
    }

    public MaterialEvent asFluid(int fuelPower, int temp) {return asFluid(fuelPower, temp, false,null,0);}

    public MaterialEvent asFluid(int fuelPower, int temp, boolean canDistill, FluidProduct[] distillationProducts, int distillationAmount) {
        flags(AntimatterMaterialTypes.LIQUID);
        MaterialTags.FUEL_POWER.add(this.material, fuelPower);
        MaterialTags.LIQUID_TEMPERATURE.add(this.material, temp);
        if (temp >= 400 && material.has(METAL)){
            flags(MOLTEN);
        }
        if (canDistill){
            DISTILLATION_FLUID_INPUT_AMOUNT.add(this.material, distillationAmount);
            DISTILL_INTO.add(this.material, Arrays.stream(distillationProducts).toList());
        }
        return this;
    }

    public MaterialEvent asGas() {
        return asGas(0);
    }

    public MaterialEvent asGas(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asGas(fuelPower, Math.max(meltingPoint, 295));
    }

    public MaterialEvent asGas(int fuelPower,int temp) {
        return asGas(fuelPower, temp,false,null,0);
    }

    public MaterialEvent asGas(int fuelPower, int temp, boolean canDistill, FluidProduct[] distillationProducts, int distillationAmount) {
        flags(AntimatterMaterialTypes.GAS);
        MaterialTags.FUEL_POWER.add(this.material, fuelPower);
        MaterialTags.GAS_TEMPERATURE.add(this.material, temp);
        if (canDistill){
            DISTILLATION_FLUID_INPUT_AMOUNT.add(this.material, distillationAmount);
            DISTILL_INTO.add(this.material, Arrays.stream(distillationProducts).toList());
        }
        return this;
    }

    public MaterialEvent asPlasma() {
        return asPlasma(0);
    }

    public MaterialEvent asPlasma(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asPlasma(fuelPower, meltingPoint);
    }

    public MaterialEvent asPlasma(int fuelPower,int temp) {
        return asPlasma(fuelPower,temp,false,null,0);
    }

    public MaterialEvent asPlasma(int fuelPower, int temp, boolean canDistill, FluidProduct[] distillationProducts, int distillationAmount) {
        flags(AntimatterMaterialTypes.PLASMA);
        return asGas(fuelPower,temp,canDistill,distillationProducts,distillationAmount);
    }

    public MaterialEvent harvestLevel(int harvestLevel) {
        MaterialTags.MINING_LEVEL.add(this.material, harvestLevel);
        return this;
    }

    public MaterialEvent addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality) {
        return addTools(toolDamage, toolSpeed, toolDurability, toolQuality, ImmutableMap.of());
    }

    public MaterialEvent addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment, AntimatterToolType... toolTypes) {
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
        return this;
    }

    public MaterialEvent addTools(Material derivedMaterial, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        ToolData data = MaterialTags.TOOLS.get(derivedMaterial);
        return addTools(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality(), toolEnchantment);
    }

    public MaterialEvent addTools(Material derivedMaterial) {
        ToolData data = MaterialTags.TOOLS.get(derivedMaterial);
        return addTools(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality());
    }

    public MaterialEvent setAllowedTypes(AntimatterToolType... toolTypes) {
        if (!has(MaterialTags.TOOLS)) return this;
        ToolData data = MaterialTags.TOOLS.get(this.material);
        List<AntimatterToolType> toolTypesList = toolTypes.length > 0 ? Arrays.asList(toolTypes) : AntimatterAPI.all(AntimatterToolType.class);
        MaterialTags.TOOLS.add(this.material, new ToolData(data.toolDamage(), data.toolSpeed(), data.toolDurability(), data.toolQuality(), data.toolEnchantment(), toolTypesList));
        return this;
    }

    public MaterialEvent addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor) {
        return addArmor(armor, toughness, knockbackResistance, armorDurabilityFactor, ImmutableMap.of());
    }

    public MaterialEvent addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (armor.length != 4) {
            Antimatter.LOGGER.info("Material " + this.material.getId() + " unable to add armor, protection array must have exactly 4 values");
            return this;
        }
        if (has(AntimatterMaterialTypes.INGOT)) flags(AntimatterMaterialTypes.PLATE);
        MaterialTags.ARMOR.add(this.material, new ArmorData(armor, toughness, knockbackResistance, armorDurabilityFactor, toolEnchantment));
        return this;
    }

    public MaterialEvent addArmor(Material material, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!material.has(ARMOR)) return this;
        ArmorData data = ARMOR.get(material);
        return addArmor(data.armor(), data.toughness(), data.knockbackResistance(), data.armorDurabilityFactor(), toolEnchantment);
    }

    public MaterialEvent addArmor(Material material) {
        if (!material.has(ARMOR)) return this;
        ArmorData data = ARMOR.get(material);
        return addArmor(data.armor(), data.toughness(), data.knockbackResistance(), data.armorDurabilityFactor());
    }

    public MaterialEvent addHandleStat(int durability, float speed) {
        return addHandleStat(durability, speed, ImmutableMap.of());
    }

    public MaterialEvent addHandleStat(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!has(AntimatterMaterialTypes.ROD)) flags(AntimatterMaterialTypes.ROD);
        HANDLE.add(this.material, new HandleData(durability, speed, toolEnchantment));
        return this;
    }

    public boolean has(IMaterialTag... tags) {
        for (IMaterialTag t : tags) {
            if (!t.all().contains(this.material)) return false;
        }
        return true;
    }

    public MaterialEvent flags(IMaterialTag... tags) {
        if (!this.material.enabled) return this;
        for (IMaterialTag t : tags) {
            if (!this.has(t)) {
                t.add(this.material);
            }
            flags(t.dependents().stream().filter(d -> !this.has(d)).toArray(IMaterialTag[]::new));
        }
        return this;
    }

    public MaterialEvent setExpRange(UniformInt expRange) {
        MaterialTags.EXP_RANGE.add(this.material, expRange);
        return this;
    }

    public MaterialEvent setExpRange(int min, int max) {
        return this.setExpRange(UniformInt.of(min, max));
    }

    public void remove(IMaterialTag... tags) {
        if (!this.material.enabled) return;
        for (IMaterialTag t : tags) {
            t.remove(this.material);
        }
    }

    public MaterialEvent mats(Function<ImmutableMap.Builder<Material, Integer>, ImmutableMap.Builder<Material, Integer>> func) {
        if (!this.material.enabled) return this;
        return mats(func.apply(new ImmutableMap.Builder<>()).build());
    }

    public MaterialEvent mats(ImmutableMap<Material, Integer> stacks) {
        if (!this.material.enabled) return this;
        stacks.forEach((k, v) -> MaterialTags.PROCESS_INTO.add(this.material, new MaterialStack(k, v)));
        return this;
    }

    /**
     * Processing Getters/Setters
     **/

    public MaterialEvent setOreMulti(int multi) {
        MaterialTags.ORE_MULTI.add(this.material, multi);
        return this;
    }

    public MaterialEvent setSmeltingMulti(int multi) {
        MaterialTags.SMELTING_MULTI.add(this.material, multi);
        return this;
    }

    public MaterialEvent setByProductMulti(int multi) {
        MaterialTags.BY_PRODUCT_MULTI.add(this.material, multi);
        return this;
    }

    public MaterialEvent setSmeltInto(Material m) {
        MaterialTags.SMELT_INTO.add(this.material, m);
        return this;
    }

    public MaterialEvent setDirectSmeltInto(Material m) {
        MaterialTags.DIRECT_SMELT_INTO.add(this.material, m);
        return this;
    }

    public MaterialEvent setArcSmeltInto(Material m) {
        MaterialTags.ARC_SMELT_INTO.add(this.material, m);
        return this;
    }

    public MaterialEvent setMacerateInto(Material m) {
        MaterialTags.MACERATE_INTO.add(this.material, m);
        return this;
    }

    public MaterialEvent addByProduct(Material... mats) {
        MaterialTags.BYPRODUCTS.add(this.material, new ObjectArrayList<>());
        MaterialTags.BYPRODUCTS.getList(this.material).addAll(Arrays.asList(mats));
        return this;
    }

    public MaterialEvent replaceItem(MaterialTypeItem<?> type, Item toReplace){
        type.replacement(this.material, () -> toReplace);
        return this;
    }

    public MaterialEvent replaceBlock(MaterialTypeBlock<?> type, Item toReplace){
        type.replacement(this.material, () -> toReplace);
        return this;
    }

    public MaterialEvent replaceItem(MaterialTypeItem<?> type, Supplier<Item> toReplace){
        type.replacement(this.material, toReplace);
        return this;
    }

    public MaterialEvent replaceBlock(MaterialTypeBlock<?> type, Supplier<Item> toReplace){
        type.replacement(this.material, toReplace);
        return this;
    }
}
