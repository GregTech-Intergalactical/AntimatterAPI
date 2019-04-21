package muramasa.gtu.integration.ctx;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.TextureSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;

@ZenClass("mods.gtu")
public class GregTechTweaker {

    public static void init() {
        CraftTweakerAPI.registerClass(GregTechTweaker.class);
        CraftTweakerAPI.registerClass(CTMaterial.class);
        CraftTweakerAPI.registerClass(CTRecipeBuilder.class);
        CraftTweakerAPI.registerClass(CTStructureBuilder.class);
    }

    @ZenMethod
    public static CTMaterial getMaterial(String name) {
        Material material = Materials.get(name);
        if (material == null) throw new NullPointerException("material for name " + name + " does not exist");
        return new CTMaterial(material);
    }

    @ZenMethod
    public static CTMaterial addMaterial(String name, int rgb, String textureSet) {
        if (Materials.get(name) != null) throw new IllegalArgumentException("material for name " + name + " already exists");
        return new CTMaterial(name, rgb, textureSet);
    }

    @ZenMethod
    public static void addTextureSet(String name) {
        if (TextureSet.get(name) != null) throw new IllegalArgumentException("TextureSet for name" + name + "already exists");
        new TextureSet(name);
    }

    @ZenMethod
    public static CTRecipeBuilder getRecipeMap(String name) {
        return new CTRecipeBuilder(Machines.get(name).getRecipeMap());
    }

    @ZenMethod
    public static CTStructureBuilder addStructure(String name) {
        Machine machine = Machines.get(name);
        if (machine == null) throw new NullPointerException("machine for name " + name + " does not exist");
        return new CTStructureBuilder(machine);
    }

    public static ItemStack[] getItems(IIngredient... items) {
        return Arrays.stream(items).map(CraftTweakerMC::getItemStack).toArray(ItemStack[]::new);
    }

    public static FluidStack[] getFluids(ILiquidStack... fluids) {
        return Arrays.stream(fluids).map(ILiquidStack::getInternal).toArray(FluidStack[]::new);
    }
}
