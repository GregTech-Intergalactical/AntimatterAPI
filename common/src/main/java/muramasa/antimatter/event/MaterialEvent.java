package muramasa.antimatter.event;

import muramasa.antimatter.Data;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTags.METAL;

public class MaterialEvent {
    private Material material = Data.NULL;

    public MaterialEvent setMaterial(Material material){
        this.material = material;
        return this;
    }

    public MaterialEvent asDust(IMaterialTag... tags) {
        return asDust(295, tags);
    }

    public MaterialEvent asDust(int meltingPoint, IMaterialTag... tags) {
        flags(DUST, DUST_SMALL, DUST_TINY);
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
        flags(INGOT, NUGGET, BLOCK).asFluid(); //TODO: Shall we generate blocks for every solid?
        MaterialTags.BLAST_FURNACE_TEMP.add(material, blastFurnaceTemp);
        if (blastFurnaceTemp >= 1000){
            flags(MaterialTags.NEEDS_BLAST_FURNACE);
        }
        if (blastFurnaceTemp > 1750) {
            flags(INGOT_HOT);
        }
        return this;
    }

    public MaterialEvent asMetal(IMaterialTag... tags) {
        return asMetal(295, 0, tags);
    }

    public MaterialEvent asMetal(int meltingPoint, int blastFurnaceTemp, IMaterialTag... tags) {
        asSolid(meltingPoint, blastFurnaceTemp, tags);
        flags(METAL);
        return this;
    }

    public MaterialEvent asOre(int minXp, int maxXp, boolean small, IMaterialTag... tags) {
        //this.expRange = IntRange.range(minXp, maxXp);
        return asOre(small, tags);
    }

    public MaterialEvent asOre(IMaterialTag... tags) {
        return asOre(true, tags);
    }

    public MaterialEvent asOre(boolean small, IMaterialTag... tags) {
        asDust(ORE, ROCK, CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE, DUST_PURE, RAW_ORE);
        if (small) flags(ORE_SMALL);
        flags(tags);
        return this;
    }

    public MaterialEvent asOreStone(int minXp, int maxXp, IMaterialTag... tags) {
        asOre(minXp, maxXp, false, tags);
        flags(ORE_STONE);
        return this;
    }

    public MaterialEvent asOreStone(IMaterialTag... tags) {
        asOre(tags);
        asDust(ORE_STONE, ORE, ROCK, CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE, DUST_PURE);
        flags(tags);
        return this;
    }

    public MaterialEvent asGemBasic(boolean transparent, IMaterialTag... tags) {
        asDust(tags);
        flags(GEM, BLOCK);
        if (transparent) {
            flags(MaterialTags.TRANSPARENT, PLATE, LENS, GEM_BRITTLE, GEM_POLISHED);
        }
        return this;
    }

    public MaterialEvent asGem(boolean transparent, IMaterialTag... tags) {
        asGemBasic(transparent, tags);
        if (!transparent) flags(GEM_BRITTLE, GEM_POLISHED);
        return this;
    }

    public MaterialEvent asFluid() {
        return asFluid(0);
    }

    public MaterialEvent asFluid(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asFluid(fuelPower, Math.max(meltingPoint, 295));
    }

    public MaterialEvent asFluid(int fuelPower, int temp) {
        flags(LIQUID);
        MaterialTags.FUEL_POWER.add(this.material, fuelPower);
        MaterialTags.LIQUID_TEMPERATURE.add(this.material, temp);
        return this;
    }

    public MaterialEvent asGas() {
        return asGas(0);
    }

    public MaterialEvent asGas(int fuelPower) {
        int meltingPoint = this.has(MaterialTags.MELTING_POINT) ? MaterialTags.MELTING_POINT.getInt(this.material) : 295;
        return asGas(fuelPower, Math.max(meltingPoint, 295));
    }

    public MaterialEvent asGas(int fuelPower, int temp) {
        flags(GAS);
        MaterialTags.FUEL_POWER.add(this.material, fuelPower);
        MaterialTags.GAS_TEMPERATURE.add(this.material, temp);
        return this;
    }

    public MaterialEvent asPlasma() {
        return asPlasma(0);
    }

    public MaterialEvent asPlasma(int fuelPower) {
        asGas(fuelPower);
        flags(PLASMA);
        return this;
    }

    public MaterialEvent harvestLevel(int harvestLevel) {
        MaterialTags.MINING_LEVEL.add(this.material, harvestLevel);
        return this;
    }

    /*public MaterialEvent addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality) {
        return addTools(toolDamage, toolSpeed, toolDurability, toolQuality, ImmutableMap.of());
    }

    public MaterialEvent addTools(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment, AntimatterToolType... toolTypes) {
        if (has(INGOT))
            flags(TOOLS, PLATE, ROD, SCREW, BOLT); //TODO: We need to add bolt for now since screws depends on bolt, need to find time to change it
        else flags(TOOLS, ROD);
        this.toolDamage = toolDamage;
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        this.miningLevel = toolQuality - 1;
        this.toolEnchantment = toolEnchantment;
        if (toolTypes.length > 0) {
            this.toolTypes= Arrays.asList(toolTypes);
        } else {
            this.toolTypes = AntimatterAPI.all(AntimatterToolType.class);
        }
        if (this.toolTypes.contains(ELECTRIC_WRENCH)) flags(WRENCHBIT);
        if (this.toolTypes.contains(BUZZSAW)) flags(BUZZSAW_BLADE);
        if (this.toolTypes.contains(DRILL)) flags(DRILLBIT);
        if (this.toolTypes.contains(CHAINSAW)) flags(CHAINSAWBIT);
        return this;
    }

    public MaterialEvent addTools(Material derivedMaterial, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        return addTools(derivedMaterial.toolDamage, derivedMaterial.toolSpeed, derivedMaterial.toolDurability, derivedMaterial.toolQuality, toolEnchantment);
    }

    public MaterialEvent addTools(Material derivedMaterial) {
        return addTools(derivedMaterial.toolDamage, derivedMaterial.toolSpeed, derivedMaterial.toolDurability, derivedMaterial.toolQuality);
    }

    public MaterialEvent setAllowedTypes(AntimatterToolType... toolTypes) {
        if (toolTypes.length > 0) {
            this.toolTypes = Arrays.asList(toolTypes);
        } else {
            this.toolTypes = AntimatterAPI.all(AntimatterToolType.class);
        }
        return this;
    }

    public MaterialEvent addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor) {
        return addArmor(armor, toughness, knockbackResistance, armorDurabilityFactor, ImmutableMap.of());
    }

    public MaterialEvent addArmor(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (armor.length < 4) {
            Antimatter.LOGGER.info("Material " + this.getId() + " unable to add armor, protection array must have at least 4 values");
            return this;
        }
        if (has(INGOT)) flags(ARMOR, PLATE);
        else flags(ARMOR);
        this.armor = armor;
        this.toughness = toughness;
        this.armorDurabilityFactor = armorDurabilityFactor;
        this.knockbackResistance = knockbackResistance;
        this.armorEnchantment = toolEnchantment;
        return this;
    }

    public MaterialEvent addArmor(Material material, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        return addArmor(material.armor, material.toughness, material.knockbackResistance, material.armorDurabilityFactor, toolEnchantment);
    }

    public MaterialEvent addArmor(Material material) {
        return addArmor(material.armor, material.toughness, material.knockbackResistance, material.armorDurabilityFactor);
    }

    public MaterialEvent addHandleStat(int durability, float speed) {
        return addHandleStat(durability, speed, ImmutableMap.of());
    }

    public MaterialEvent addHandleStat(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment) {
        if (!has(ROD)) flags(ROD);
        flags(HANDLE);
        this.isHandle = true;
        this.handleDurability = durability;
        this.handleSpeed = speed;
        this.handleEnchantment = toolEnchantment;
        return this;
    }*/

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

    /*public MaterialEvent setExpRange(IntRange expRange) {
        this.expRange = expRange;
        return this;
    }

    public MaterialEvent setExpRange(int min, int max) {
        this.expRange = IntRange.range(min, max);
        return this;
    }*/

    public void remove(IMaterialTag... tags) {
        if (!this.material.enabled) return;
        for (IMaterialTag t : tags) {
            t.remove(this.material);
        }
    }

    /*public MaterialEvent mats(Function<ImmutableMap.Builder<Material, Integer>, ImmutableMap.Builder<Material, Integer>> func) {
        if (!this.material.enabled) return this;
        return mats(func.apply(new ImmutableMap.Builder<>()).build());
    }

    public MaterialEvent mats(ImmutableMap<Material, Integer> stacks) {
        if (!this.material.enabled) return this;
        stacks.forEach((k, v) -> processInto.add(new MaterialStack(k, v)));
        return this;
    }*/
}
