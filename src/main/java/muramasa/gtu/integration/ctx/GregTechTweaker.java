package muramasa.gtu.integration.ctx;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;

@ZenClass("mods.gtu")
public class GregTechTweaker {

    public static void init() {
        CraftTweakerAPI.registerClass(GregTechTweaker.class);
        CraftTweakerAPI.registerClass(CTMaterial.class);
        CraftTweakerAPI.registerClass(CTRecipeBuilder.class);
    }

    @ZenMethod
    public static CTMaterial getMaterial(String name) {
        Material material = Materials.get(name);
        if (material == null) throw new NullPointerException("material for " + name + " does not exist");
        return new CTMaterial(material);
    }

    @ZenMethod
    public static CTMaterial addMaterial(String name, int rgb, String textureSet) {
        if (Materials.get(name) != null) throw new IllegalArgumentException("material for name " + name + " already exists");
        return new CTMaterial(name, rgb, textureSet);
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
