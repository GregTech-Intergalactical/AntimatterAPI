package muramasa.gtu.integration.ctx;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.IMaterialFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.TextureSet;
import stanhebben.zenscript.annotations.ZenMethod;

public class CTMaterial {

    private Material material;

    public CTMaterial(Material material) {
        this.material = material;
    }

    public CTMaterial(String name, int rgb, String setId) {
        TextureSet set = GregTechAPI.get(TextureSet.class, setId);
        material = new Material(name, rgb, set);
    }

    @ZenMethod
    public CTMaterial asDust(int meltingPoint) {
        material.asDust(meltingPoint);
        return this;
    }

    @ZenMethod
    public CTMaterial asSolid() {
        material.asSolid();
        return this;
    }

    @ZenMethod
    public CTMaterial asSolid(int meltingPoint, int blastFurnaceTemp) {
        material.asSolid(meltingPoint, blastFurnaceTemp);
        return this;
    }

    @ZenMethod
    public CTMaterial asMetal() {
        material.asMetal();
        return this;
    }

    @ZenMethod
    public CTMaterial asMetal(int meltingPoint, int blastFurnaceTemp) {
        material.asMetal(meltingPoint, blastFurnaceTemp);
        return this;
    }

    @ZenMethod
    public CTMaterial asGemBasic(boolean transparent) {
        material.asGemBasic(transparent);
        return this;
    }

    @ZenMethod
    public CTMaterial asGem(boolean transparent) {
        material.asGem(transparent);
        return this;
    }

    @ZenMethod
    public CTMaterial asFluid() {
        material.asFluid();
        return this;
    }

    @ZenMethod
    public CTMaterial asFluid(int fuelPower) {
        material.asFluid(fuelPower);
        return this;
    }

    @ZenMethod
    public CTMaterial asGas() {
        material.asGas();
        return this;
    }

    @ZenMethod
    public CTMaterial asGas(int fuelPower) {
        material.asGas(fuelPower);
        return this;
    }

    @ZenMethod
    public CTMaterial asPlasma() {
        material.asPlasma();
        return this;
    }

    @ZenMethod
    public CTMaterial asPlasma(int fuelPower) {
        material.asPlasma(fuelPower);
        return this;
    }

    @ZenMethod
    public CTMaterial addTools(float toolSpeed, int toolDurability, int toolQuality) {
        material.addTools(toolSpeed, toolDurability, toolQuality);
        return this;
    }

    @ZenMethod
    public CTMaterial addFlags(String... flagNames) {
        material.add(IMaterialFlag.getFlagsFor(flagNames).toArray(new IMaterialFlag[0]));
        return this;
    }

    @ZenMethod
    public CTMaterial removeFlags(String... flagNames) {
        material.remove(IMaterialFlag.getFlagsFor(flagNames).toArray(new IMaterialFlag[0]));
        return this;
    }
}
