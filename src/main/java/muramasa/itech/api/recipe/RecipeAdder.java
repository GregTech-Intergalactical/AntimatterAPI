package muramasa.itech.api.recipe;

import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.itech.api.machines.MachineList.*;
import static muramasa.itech.api.util.Utils.arr;

public class RecipeAdder {

    private static void addBasicRecipe(Machine type, ItemStack[] inputs, ItemStack[] outputs, int d, int p) {
        if (Utils.areStacksValid(inputs, outputs)) {
            type.getRecipeMap().add(new Recipe(inputs, outputs, d, p));
        }
    }

    public static void addBasicRecipe(Machine type, FluidStack[] inputs, FluidStack[] outputs, int d, int p) {
        if (Utils.areFluidsValid(inputs, outputs)) {
            type.getRecipeMap().add(new Recipe(null, null, inputs, outputs, d, p));
        }
    }

    private static void addBasicRecipe(Machine type, ItemStack[] inputs, FluidStack[] fluidInputs, ItemStack[] outputs, int d, int p) {
        if (Utils.areStacksValid(inputs, outputs) && Utils.areFluidsValid(fluidInputs)) {
            type.getRecipeMap().add(new Recipe(inputs, outputs, fluidInputs, null, d, p));
        }
    }

    private static void addBasicRecipe(Machine type, ItemStack[] inputs, ItemStack[] outputs, FluidStack[] fluidOutputs, int d, int p) {
        if (Utils.areStacksValid(inputs, outputs) && Utils.areFluidsValid(fluidOutputs)) {
            type.getRecipeMap().add(new Recipe(inputs, outputs, null, fluidOutputs, d, p));
        }
    }

    private static void addBasicRecipe(Machine type, ItemStack[] inputs, FluidStack[] fluidInputs, FluidStack[] fluidOutputs, int d, int p) {
        if (Utils.areStacksValid(inputs) && Utils.areFluidsValid(fluidInputs, fluidOutputs)) {
            type.getRecipeMap().add(new Recipe(inputs, null, fluidInputs, fluidOutputs, d, p));
        }
    }

    private static void addBasicRecipe(Machine type, ItemStack[] inputs, FluidStack[] fluidInputs, ItemStack[] outputs, FluidStack[] fluidOutputs, int d, int p) {
        if (Utils.areStacksValid(inputs, outputs) && Utils.areFluidsValid(fluidInputs, fluidOutputs)) {
            type.getRecipeMap().add(new Recipe(inputs, outputs, fluidInputs, fluidOutputs, d, p));
        }
    }

    public static void addAlloySmelterRecipe(ItemStack input1, ItemStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(ALLOY_SMELTER, arr(input1, input2), arr(output), d, p);
    }

    public static void addAssemblerRecipe(ItemStack[] inputs, ItemStack output, int d, int p) {
        addBasicRecipe(ASSEMBLER, inputs, arr(output), d, p);
    }

    public static void addBenderRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(BENDER, arr(input), arr(output), d, p);
    }

    public static void addCannerRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(CANNER, arr(input), arr(output), d, p);
    }

    public static void addCompressorRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, ItemStack input5, ItemStack input6, ItemStack output, int d, int p) {
        addBasicRecipe(COMPRESSOR, arr(input1, input2, input3, input4, input5, input6), arr(output), d, p);
    }

    public static void addCutterRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(CUTTER, arr(input), arr(output), d, p);
    }

    public static void addExtractorRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(EXTRACTOR, arr(input), arr(output), d, p);
    }

    public static void addExtruderRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(EXTRUDER, arr(input), arr(output), d, p);
    }

    public static void addLatheRecipe(ItemStack input, ItemStack output1, ItemStack output2, int d, int p) {
        addBasicRecipe(LATHE, arr(input), arr(output1, output2), d, p);
    }

    public static void addPulverizerRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(PULVERIZER, arr(input), arr(output), d, p);
    }

    public static void addRecyclerRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(RECYCLER, arr(input), arr(output), d, p);
    }

    public static void addScannerRecipe(ItemStack input, FluidStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(SCANNER, arr(input), arr(input2), arr(output), d, p);
    }

    public static void addWiremillRecipe(ItemStack input, ItemStack output, int d, int p) {
        addBasicRecipe(WIRE_MILL, arr(input), arr(output), d, p);
    }

    public static void addCentrifugeRecipe(ItemStack input1, ItemStack input2, FluidStack input3, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, ItemStack output5, ItemStack output6, FluidStack output7, int d, int p) {
        addBasicRecipe(CENTRIFUGE, arr(input1, input2), arr(input3), arr(output1, output2, output3, output4, output5, output6), arr(output7), d, p);
    }

    public static void addElectrolyzerRecipe(ItemStack input1, ItemStack input2, FluidStack input3, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, ItemStack output5, ItemStack output6, FluidStack output7, int d, int p) {
        addBasicRecipe(ELECTROLYZER, arr(input1, input2), arr(input3), arr(output1, output2, output3, output4, output5, output6), arr(output7), d, p);
    }

    public static void addThermalCentrifugeRecipe(ItemStack input, ItemStack output1, ItemStack output2, ItemStack output3, int d, int p) {
        addBasicRecipe(THERMAL_CENTRIFUGE, arr(input), arr(output1, output2, output3), d, p);
    }

    public static void addOreWasherRecipe(ItemStack input1, FluidStack input2, ItemStack output1, ItemStack output2, ItemStack output3, int d, int p) {
        addBasicRecipe(ORE_WASHER, arr(input1), arr(input2), arr(output1, output2, output3), d, p);
    }

    public static void addChemicalReactorRecipe(ItemStack input1, ItemStack input2, FluidStack input3, ItemStack output1, ItemStack output2, FluidStack output3, int d, int p) {
        addBasicRecipe(CHEMICAL_REACTOR, arr(input1, input2), arr(input3), arr(output1, output2), arr(output3), d, p);
    }

    public static void addFluidCannerRecipe(ItemStack input1, FluidStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(FLUID_CANNER, arr(input1), arr(input2), arr(output), d, p);
    }

    public static void addDisassemblerRecipe(ItemStack input, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, ItemStack output5, ItemStack output6, ItemStack output7, ItemStack output8, ItemStack output9, int d, int p) {
        addBasicRecipe(DISASSEMBLER, arr(input), arr(output1, output2, output3, output4, output5, output6, output7, output8, output9), d, p);
    }

    public static void addMassFabricatorRecipe(ItemStack input1, FluidStack input2, FluidStack output, int d, int p) {
        addBasicRecipe(MASS_FABRICATOR, arr(input1), arr(input2), arr(output), d, p);
    }

    public static void addAmplifabricatorRecipe(ItemStack input, ItemStack output1, FluidStack output2, int d, int p) {
        addBasicRecipe(AMPLI_FABRICATOR, arr(input), arr(output1), arr(output2), d, p);
    }

    public static void addReplicatorRecipe(ItemStack input1, FluidStack input2, FluidStack output, int d, int p) {
        addBasicRecipe(REPLICATOR, arr(input1), arr(input2), arr(output), d, p);
    }

    public static void addFermenterRecipe(FluidStack input, FluidStack output, int d, int p) {
        addBasicRecipe(FERMENTER, arr(input), arr(output), d, p);
    }

    public static void addFluidExtractorRecipe(ItemStack input, ItemStack output1, FluidStack output2, int d, int p) {
        addBasicRecipe(FLUID_EXTRACTOR, arr(input), arr(output1), arr(output2), d, p);
    }

    public static void addFluidSolidifierRecipe(ItemStack input1, FluidStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(FLUID_SOLIDIFIER, arr(input1), arr(input2), arr(output), d, p);
    }

    public static void addDistilleryRecipe(ItemStack input1, FluidStack input2, ItemStack output1, FluidStack output2, int d, int p) {
        addBasicRecipe(DISTILLERY, arr(input1), arr(input2), arr(output1), arr(output2), d, p);
    }

    public static void addChemicalBathRecipe(ItemStack input1, FluidStack input2, ItemStack output1, ItemStack output2, ItemStack output3, int d, int p) {
        addBasicRecipe(CHEMICAL_BATH, arr(input1), arr(input2), arr(output1, output2, output3), d, p);
    }

    public static void addAutoclaveRecipe(ItemStack input1, FluidStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(AUTOCLAVE, arr(input1), arr(input2), arr(output), d, p);
    }

    public static void addMixerRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack input4, FluidStack input5, ItemStack output1, FluidStack output2, int d, int p) {
        addBasicRecipe(MIXER, arr(input1, input2, input3, input4), arr(input5), arr(output1), arr(output2), d, p);
    }

    public static void addLaserEngraverRecipe(ItemStack input1, ItemStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(LASER_ENGRAVER, arr(input1, input2), arr(output), d, p);
    }

    public static void addFormingPressRecipe(ItemStack input1, ItemStack input2, ItemStack output, int d, int p) {
        addBasicRecipe(FORMING_PRESS, arr(input1, input2), arr(output), d, p);
    }

    public static void addSifterRecipe(ItemStack input, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, ItemStack output5, ItemStack output6, ItemStack output7, ItemStack output8, ItemStack output9, int d, int p) {
        addBasicRecipe(SIFTER, arr(input), arr(output1, output2, output3, output4, output5, output6, output7, output8, output9), d, p);
    }

    public static void addArcFurnaceRecipe(ItemStack input1, FluidStack input2, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, int d, int p) {
        addBasicRecipe(ARC_FURNACE, arr(input1), arr(input2), arr(output1, output2, output3, output4), d, p);
    }

    public static void addPlasmaArcFurnaceRecipe(ItemStack input1, FluidStack input2, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, FluidStack output5, int d, int p) {
        addBasicRecipe(PLASMA_ARC_FURNACE, arr(input1), arr(input2), arr(output1, output2, output3, output4), arr(output5), d, p);
    }

    /** MultiMachine Recipes **/
    public static void addBlastFurnaceRecipe(ItemStack input1, ItemStack output1, int d, int p) {
        addBasicRecipe(BLAST_FURNACE, arr(input1), arr(output1), d, p);
    }
}
