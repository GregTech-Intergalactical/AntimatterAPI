package muramasa.gtu.integration.ctx;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.materials.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

import java.util.ArrayList;
import java.util.Arrays;

@ZenClass("mods.gtu")
public class GregTechTweaker {

    @ZenProperty
    public static int spring = ItemFlag.SPRING.ordinal();

    public static void init() {
        CraftTweakerAPI.registerClass(GregTechTweaker.class);
        CraftTweakerAPI.registerClass(CTMaterialBuilder.class);
        CraftTweakerAPI.registerClass(CTRecipeBuilder.class);
    }

    @ZenMethod
    public static CTMaterialBuilder addMaterial(String displayName, int rgb, String textureSet) {
        return new CTMaterialBuilder(displayName, rgb, textureSet);
    }

    @ZenMethod
    public static void addFlag(String materialName, String flagName) {
        Material material = Materials.get(materialName);
        ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
        if (material != null) material.add(flag);
    }

    @ZenMethod
    public static CTRecipeBuilder builder(String name) {
        return new CTRecipeBuilder(Machines.get(name).getRecipeMap());
    }

    public static ItemStack[] getItems(IIngredient... ingredients) {
        ArrayList<ItemStack> items = new ArrayList<>();
        Arrays.stream(ingredients).forEach(i -> items.add(CraftTweakerMC.getItemStack(i)));
        return items.toArray(new ItemStack[0]);
    }

    public static FluidStack[] getFluids(ILiquidStack... liquids) {
        ArrayList<FluidStack> fluids = new ArrayList<>();
        Arrays.stream(liquids).filter(l -> l.getInternal() != null).forEach(l -> fluids.add((FluidStack) l.getInternal()));
        return fluids.toArray(new FluidStack[0]);
    }
}
