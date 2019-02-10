//package muramasa.gregtech.loaders;
//
//import muramasa.gregtech.api.data.Materials;
//import muramasa.gregtech.api.enums.ItemList;
//import muramasa.gregtech.api.interfaces.IMaterialFlag;
//import muramasa.gregtech.api.materials.Material;
//import muramasa.gregtech.api.materials.MaterialStack;
//import muramasa.gregtech.api.materials.Prefix;
//import muramasa.gregtech.api.recipe.RecipeAdder;
//import muramasa.gregtech.api.recipe.RecipeHelper;
//import muramasa.gregtech.api.util.Utils;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.util.ArrayList;
//
//import static muramasa.gregtech.api.enums.GenerationFlag.*;
//import static muramasa.gregtech.api.enums.RecipeFlag.*;
//
////TODO EXCLUDED FROM COMPILE
//
//public class MaterialRecipeLoader implements Runnable {
//
//    //TODO register purified dust processing to centrifuged processing to regain lost benefits
//
//    private static FluidStack aWaterStack = Materials.Water.getFluid(200);
//    private static FluidStack aDistilledStack = GT_ModHandler.getDistilledWater(200);
//    private static int aSodiumFluidAmount = GT_Mod.gregtechproxy.mDisableOldChemicalRecipes ? 100 : 1000;
//    private static int aMixedOreYieldCount = GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? 2 : 3;
//
//    @Override
//    public void run() {
//
//        ItemStack aTemp1, aTemp2, aTemp3, aTemp4; //TODO use temps?
////        Materials[] materialArray;
//        Material mat;
//
////        materialArray = ELEMENTAL.getMats();
////        for (Materials mat : materialArray) {
////            ItemStack aDataOrb = ItemList.Tool_DataOrb.get(1);
////            Behaviour_DataOrb.setDataTitle(aDataOrb, "Elemental-Scan");
////            Behaviour_DataOrb.setDataName(aDataOrb, mat.mElement.name());
////            ItemStack aRepOutput = ((mat.hasFlag(LIQUID) || mat.hasFlag(GAS)) && !mat.hasFlag(DUST)) ? mat.getCell(1) : mat.getDust(1);
////            Fluid aFluid = mat.mFluid != null ? mat.mFluid : mat.mGas;
////            int aMass = mat.getMass();
////            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{aRepOutput}, new ItemStack[]{aDataOrb}, ItemList.Tool_DataOrb.get(1), null, null, aMass * 8192, 32, 0);
////            GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, aFluid == null ? new ItemStack[]{aRepOutput} : null, new ItemStack[]{aDataOrb}, new FluidStack[]{Materials.UUMatter.getFluid(aMass)}, aFluid == null ? null : new FluidStack[]{new FluidStack(aFluid, 144)}, aMass * 512, 32, 0);
////        }
//
//        for (Integer i : LIQUID.getIds()) {
//            mat = Materials.get(i);
//            //TODO
//        }
//
//        for (Integer i : GAS.getIds()) {
//            mat = Materials.get(i);
//            //TODO
//        }
//
//        for (Integer i : PLASMA.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aPlasmaStack = mat.getCellP(1);
//            RecipeAdder.addFuel(aPlasmaStack, ItemList.Cell_Empty.get(1), Math.max(1024, 1024 * mat.getMass()), 4);
//            RecipeAdder.addVacuumFreezerRecipe(aPlasmaStack, mat.getCell(1), Math.max(mat.getMass() * 2, 1));
//        }
//
//        for (Integer i : HINGOT.getIds()) {
//            mat = Materials.get(i);
//            RecipeAdder.addVacuumFreezerRecipe(mat.getIngotH(1), mat.getIngot(1), Math.max(mat.getMass() * 3, 1));
//        }
//
//        for (Integer i : SPRING.getIds()) {
//            mat = Materials.get(i);
//            RecipeAdder.addBenderRecipe(mat.getRod(1), mat.getSpring(1), 200, 16);
//        }
//
//        for (Integer i : DPLATE.getIds()) {
//            mat = Materials.get(i);
//            //GT_ModHandler.removeRecipeByOutput(aDenseStack);
//            if (mat.hasFlag(NOSMASH)) {
//                RecipeAdder.addBenderRecipe(mat.getPlate(9), mat.getPlateD(1), Math.max(mat.getMass() * 9, 1), 96);
//            }
//            if (!mat.hasFlag(NOSMASH)) {
//                RecipeAdder.addBenderRecipe(mat.getPlate(9), mat.getPlateD(1), Math.max(mat.getMass() * 9, 1), 96);
//            }
//            //GregTech_API.registerCover(aDenseStack, new GT_RenderedTexture(material.mIconSet.mTextures[76], material.mRGBa, false), null);
//        }
//
//        for (Integer i : ROTOR.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aRotor = mat.getRotor(1), aRing = mat.getRing(1), aPlate = mat.getPlate(1);
//            RecipeHelper.addShapedToolRecipe(aRotor, "PhP", "SRf", "PdP", 'P', aPlate, 'R', aRing, 'S', mat.getScrew(1));
//            RecipeAdder.addAssemblerRecipe(Utils.ca(4, aPlate), aRing, Materials.Tin.getFluid(32), aRotor, 240, 24);
//            RecipeAdder.addAssemblerRecipe(Utils.ca(4, aPlate), aRing, Materials.Lead.getFluid(48), aRotor, 240, 24);
//            RecipeAdder.addAssemblerRecipe(Utils.ca(4, aPlate), aRing, Materials.SolderingAlloy.getFluid(16), aRotor, 240, 24);
//        }
//
//        for (Integer i : WIREF.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aWireF = mat.getWireF(1);
//            if (!mat.hasFlag(NOSMASH)) {
//                RecipeAdder.addWiremillRecipe(mat.getIngot(1), Utils.copy(mat.getWire01(2), aWireF), 100, 4);
//                RecipeAdder.addWiremillRecipe(mat.getRod(1), Utils.copy(mat.getWire01(1), aWireF), 50, 4);
//            }
//            RecipeHelper.addShapedToolRecipe(aWireF, "Xx ", "   ", "   ", 'X', mat.getFoil(1));
//        }
//
//        for (Integer i : SGEAR.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aGearS = mat.getGearS(1);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.Mold_Gear_Small.get(0), mat.getFluid(144), aGearS, 16, 8);
//            RecipeHelper.addShapedToolRecipe(aGearS, "P  ", (mat == Materials.Wood || mat == Materials.WoodSealed) ? " s " : " h ", "   ", 'P', mat.getPlate(1));
//        }
//
//        for (Integer i : GEAR.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aGear = mat.getGear(1);
//            GT_ModHandler.removeRecipeByOutput(aGear);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.Mold_Gear.get(0), mat.getFluid(576), aGear, 128, 8);
//            RecipeHelper.addShapedToolRecipe(aGear, "SPS", "PwP", "SPS", 'P', mat.getPlate(1), 'S', mat.getRod(1));
//            if (!mat.hasFlag(NOSMELT)) {
//                int aVoltageMulti = mat.hasFlag(NOSMASH) ? mat.getBlastFurnaceTemp() >= 2800 ? 16 : 4 : mat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                ItemStack aGearSmeltInto = mat.mSmeltInto.getGear(1), aIngot = mat.getIngot(4);
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.Shape_Gear.get(0), aGearSmeltInto, mat.getMass() * 5, 8 * aVoltageMulti);
//                RecipeAdder.addAlloySmelterRecipe(Utils.ca(8, aIngot), ItemList.Mold_Gear.get(0), aGearSmeltInto, mat.getMass() * 10, 2 * aVoltageMulti);
//            }
//            //Unifier.registerOre(Prefix.gear, aGearStack);
//        }
//
//        for (Integer i : SCREW.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aBolt = mat.getBolt(1), aScrew = mat.getScrew(1);
//            RecipeAdder.addLatheRecipe(aBolt, aScrew, null, Math.max(mat.getMass() / 8, 1), 4);
//            RecipeHelper.addShapedToolRecipe(aScrew, "fX ", "X  ", "   ", 'X', aBolt);
//        }
//
//        for (Integer i : FOIL.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aPlate = mat.getPlate(1), aFoil = mat.getFoil(2);
//            RecipeAdder.addBenderRecipe(aPlate, aFoil, Math.max(mat.getMass(), 1), 24);
//            RecipeHelper.addShapedToolRecipe(Utils.ca(4, aFoil), "hX ", "   ", "   ", 'X', aPlate);
//            //GregTech_API.registerCover(aFoilStack, new GT_RenderedTexture(material.mIconSet.mTextures[70], material.mRGBa, false), null);
//        }
//
//        for (Integer i : BOLT.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aBolt = mat.getBolt(1), aRod = mat.getRod(1);
//            RecipeAdder.addCutterRecipe(aRod, mat.getBolt(4), null, Math.max(mat.getMass() * 2, 1), 4);
//            RecipeHelper.addShapedToolRecipe(Utils.ca(2, aBolt), "s  ", " X ", "   ", 'X', aRod);
//            if (!mat.hasFlag(NOSMELT)) {
//                RecipeAdder.addExtruderRecipe(mat.getIngot(1), ItemList.Shape_Bolt.get(0), mat.mSmeltInto.getBolt(8), mat.getMass() * 2, mat.getBlastFurnaceTemp() >= 2800 ? 512 : 128);
//            }
//        }
//
//        for (Integer i : RING.getIds()) {
//            mat = Materials.get(i);
//            if (!mat.hasFlag(NOSMASH)) {
//                RecipeHelper.addShapedToolRecipe(mat.getRing(1), "h  ", " X ", "   ", 'X', mat.getRod(1));
//            }
//            if (!mat.hasFlag(NOSMELT)) {
//                RecipeAdder.addExtruderRecipe(mat.getIngot(1), ItemList.Shape_Ring.get(0), mat.mSmeltInto.getRing(4), mat.getMass() * 2, mat.getBlastFurnaceTemp() >= 2800 ? 384 : 96);
//            }
//        }
//
//        for (Integer i : ROD.getIds()) {
//            mat = Materials.get(i);
//            if (mat.hasFlag(BGEM) && mat.hasFlag(BRITTLEG)) {
//                RecipeAdder.addLatheRecipe(mat.getGem(1), mat.getRod(1), mat.mMacerateInto.getDustS(2), Math.max(mat.getMass() * 5, 1), 16);
//            } else if (mat.hasFlag(INGOT)) {
//                RecipeAdder.addLatheRecipe(mat.getIngot(1), mat.getRod(1), mat.mMacerateInto.getDustS(2), Math.max(mat.getMass() * 5, 1), 16);
//                RecipeHelper.addShapedToolRecipe(mat.getRod(1), "f  ", " X ", "   ", 'X', mat.getIngot(1));
//            }
//            if (!mat.hasFlag(NOSMELT) && !mat.hasFlag(BGEM)) {
//                ItemStack aIngot = mat.getIngot(1);
//                int aEU = mat.getBlastFurnaceTemp() >= 2800 ? 384 : 96;
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.Shape_Rod.get(0), mat.mSmeltInto.getRod(2), mat.getMass() * 2, aEU);
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.Shape_Wire.get(0), mat.mSmeltInto.getWire01(2), mat.getMass() * 2, aEU);
//            }
//        }
//
//        for (Integer i : PLATE.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aPlate = mat.getPlate(1), aIngot = mat.getIngot(1);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.Mold_Plate.get(0), mat.getFluid(144), aPlate, 32, 8);
//            if (mat.getFuelPower() > 0) {
//                RecipeAdder.addFuel(aPlate, null, mat.getFuelPower(), mat.mFuelType);
//            }
//            //TODO MOVRE RecipeAdder.addImplosionRecipe(GT_Utility.ca(mat == Materials.MeteoricIron ? 1 : 2, aPlateStack), 2, Unifier.get(Prefix.compressed, mat), Unifier.get(Prefix.dustTiny, Materials.DarkAsh, 1));
//
//            if (!mat.hasFlag(NOSMASH)) {
//                RecipeHelper.addShapedToolRecipe(aPlate, "h  ", "X  ", "X  ", 'X', mat.hasFlag(BGEM) ? mat.getGem(1) : aIngot);
//                if (mat.hasFlag(GRINDABLE)) {
//                    RecipeHelper.addShapedToolRecipe(mat.getDust(1), "X  ", "m  ", "   ", 'X', aPlate);
//                }
//                RecipeAdder.addHammerRecipe(Utils.ca(3, aIngot), Utils.ca(2, aPlate), Math.max(mat.getMass(), 1), 16);
//                RecipeAdder.addBenderRecipe(aIngot, aPlate, Math.max(mat.getMass(), 1), 24);
//            }
//            if (!mat.hasFlag(NOSMELT)) {
//                int aEU = mat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.Shape_Plate.get(0), mat.mSmeltInto.getPlate(1), mat.getMass(), 8 * aEU);
//                RecipeAdder.addAlloySmelterRecipe(Utils.ca(2, aIngot), ItemList.Mold_Plate.get(0), mat.mSmeltInto.getPlate(1), mat.getMass() * 2, 2 * aEU);
//
//
//                //TODO WUT?
////            if (Prefix.block.isIgnored(mat) && mat != Materials.GraniteRed && mat != Materials.GraniteBlack && mat != Materials.Glass && mat != Materials.Obsidian && mat != Materials.Glowstone && mat != Materials.Paper) {
////                GT_ModHandler.addCompressorRecipe(aDustStack, aPlateStack);
////            }
//            }
//            RecipeAdder.addCutterRecipe(mat.getBlock(1), Utils.ca(9, aPlate), null, Math.max(mat.getMass() * 10, 1), 30);
//            GregTech_API.registerCover(aPlate, new GT_RenderedTexture(mat.mIconSet.mTextures[71], mat.getRGB(), false), null);
//        }
//
//        for (Integer i : BLOCK.getIds()) {
//            mat = Materials.get(i);
//            if (!mat.hasFlag(NOSMELT)) {
//                int aVoltageMulti = mat.hasFlag(NOSMASH) ? mat.getBlastFurnaceTemp() >= 2800 ? 16 : 4 : mat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                ItemStack aIngot9x = mat.getIngot(9);
//                ItemStack aBlock = mat.mSmeltInto.getBlock(1);
//                RecipeAdder.addExtruderRecipe(aIngot9x, ItemList.Shape_Block.get(0), aBlock, 10, 8 * aVoltageMulti);
//                RecipeAdder.addAlloySmelterRecipe(aIngot9x, ItemList.Mold_Block.get(0), aBlock, 5, 4 * aVoltageMulti);
//            }
//        }
//
//        for (Integer i : DUST.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aDust = mat.getDust(1), aDustS = mat.getDustS(1), aDustT = mat.getDustT(1);
//            RecipeHelper.addBasicShapedRecipe(Utils.ca(4, aDustS), " X", "  ", 'X', aDust);
//            RecipeHelper.addBasicShapedRecipe(Utils.ca(9, aDustT), "X ", "  ", 'X', aDust);
//            RecipeHelper.addBasicShapelessRecipe(aDust, aDustS, aDustS, aDustS, aDustS);
//            RecipeHelper.addBasicShapelessRecipe(aDust, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT);
//            if (mat.getFuelPower() > 0) {
//                RecipeAdder.addFuel(aDust, null, mat.mFuelPower, mat.mFuelType);
//            }
//            if (!mat.needsBlastFurnace()) {
//                if (mat.mSmeltInto != null) {
//                    GT_RecipeRegistrator.registerReverseFluidSmelting(aDust, mat, Prefix.dust.mMaterialAmount, null);
//                    GT_RecipeRegistrator.registerReverseFluidSmelting(aDustS, mat, Prefix.dustSmall.mMaterialAmount, null);
//                    GT_RecipeRegistrator.registerReverseFluidSmelting(aDustT, mat, Prefix.dustTiny.mMaterialAmount, null);
//                    if (mat.mSmeltInto.mArcSmeltInto != mat) {
//                        GT_RecipeRegistrator.registerReverseArcSmelting(aDust, mat, Prefix.dust.mMaterialAmount, null, null, null);
//                        GT_RecipeRegistrator.registerReverseArcSmelting(aDustS, mat, Prefix.dustSmall.mMaterialAmount, null, null, null);
//                        GT_RecipeRegistrator.registerReverseArcSmelting(aDustT, mat, Prefix.dustTiny.mMaterialAmount, null, null, null);
//                    }
//                }
//            }
//            if (mat.mDirectSmelting != mat && !mat.hasFlag(NOSMELT) && !(mat.needsBlastFurnace() || mat.mDirectSmelting.isBlastFurnaceRequired()) && !mat.hasFlag(NOBBF)) {
//                RecipeAdder.addPrimitiveBlastRecipe(Utils.ca(2, aDust), null, 2, mat.mDirectSmelting.getIngot(aMixedOreYieldCount), null, 2400);
//            }
////            if (mat.hasFlag(ELECSEPI)) {
////                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Iron.getDustS(1), Materials.Iron.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
////            } else if (mat.hasFlag(ELECSEPG)) {
////                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Gold.getDustS(1), Materials.Gold.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
////            } else if (mat.hasFlag(ELECSEPN)) {
////                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Neodymium.getDustS(1), Materials.Neodymium.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
////            }
//        }
//
//        for (Integer i : IMaterialFlag.getIdsFor(ELEC, CENT)) {
//            mat = Materials.get(i);
//            int aInputCount = 0;
//            int aInputCellCount = 0;
//
//            ArrayList<ItemStack> aOutputs = new ArrayList<>();
//            FluidStack aFirstFluid = null; //The first LIQUID MatStack (LIQUID & !DUST tag combo) uses the Electrolyzers fluid input tack. The preceding are cells.
//
//            Material matProcess;
//            for (MaterialStack matStack : mat.getProcessInto()) {
//                 matProcess = matStack.get();
//                if ((matProcess.hasFlag(LIQUID) || matProcess.hasFlag(GAS)) && !matProcess.hasFlag(DUST)) {
//                    if (aFirstFluid == null) {
//                        if (matProcess.mFluid != null) { //If a Material has mFluid & mGas, Prioritise mFluid.
//                            aFirstFluid = matProcess.getFluid(matStack.size * 1000);
//                        } else {
//                            aFirstFluid = matProcess.getGas(matStack.size * 1000);
//                        }
//                    } else {
//                        aOutputs.add(matProcess.getCell(matStack.size));
//                        aInputCellCount += matStack.size;
//                    }
//                } else {
//                    aOutputs.add(matProcess.getDust(matStack.size));
//                }
//                aInputCount += matStack.size;
//            }
//            aInputCount = Math.min(aInputCount, 64); //This should not happen. This means mProcess total is over 64 and the recipe should be adjusted
//            if (aOutputs.size() > 0) {
//                ItemStack aInput = (mat.hasFlag(LIQUID) || mat.hasFlag(GAS)) && !mat.hasFlag(DUST) ? mat.getCell(aInputCount) : mat.getDust(aInputCount), aInputCell = aInputCellCount > 0 ? ItemList.Cell_Empty.get(aInputCellCount) : null;
//                if (mat.hasFlag(ELEC)) {
//                    RecipeAdder.addElectrolyzerRecipe(aInput, aInputCell, null, aFirstFluid, aOutputs.size() < 1 ? null : aOutputs.get(0), aOutputs.size() < 2 ? null : aOutputs.get(1), aOutputs.size() < 3 ? null : aOutputs.get(2), aOutputs.size() < 4 ? null : aOutputs.get(3), aOutputs.size() < 5 ? null : aOutputs.get(4), aOutputs.size() < 6 ? null : aOutputs.get(5), null, Math.max(1, Math.abs(mat.getProtons() * 2 * aInputCellCount)), Math.min(4, aOutputs.size()) * 30);
//                } else if (mat.hasFlag(CENT)) {
//                    RecipeAdder.addCentrifugeRecipe(aInput, aInputCell, null, aFirstFluid, aOutputs.size() < 1 ? null : aOutputs.get(0), aOutputs.size() < 2 ? null : aOutputs.get(1), aOutputs.size() < 3 ? null : aOutputs.get(2), aOutputs.size() < 4 ? null : aOutputs.get(3), aOutputs.size() < 5 ? null : aOutputs.get(4), aOutputs.size() < 6 ? null : aOutputs.get(5), null, Math.max(1, Math.abs(mat.getMass() * 4 * aInputCellCount)), Math.min(4, aOutputs.size()) * 5);
//                }
//            }
//        }
//
//        for (Integer i : INGOT.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aIngot = mat.getIngot(1), aNugget = mat.getNugget(1), aDust = mat.getDust(1);
//            if (mat.getFuelPower() > 0) {
//                RecipeAdder.addFuel(aIngot, null, mat.getFuelPower(), mat.mFuelType);
//            }
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.Mold_Nugget.get(0), mat.getFluid(16), aNugget, 16, 4);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.Mold_Ingot.get(0), mat.getFluid(144), aIngot, 32, 8);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.Mold_Block.get(0), mat.getFluid(1296), mat.getBlock(1), 288, 8);
//            GT_RecipeRegistrator.registerReverseFluidSmelting(aIngot, mat, Prefix.INGOT.mMaterialAmount, null);
//            GT_RecipeRegistrator.registerReverseMacerating(aIngot, mat, Prefix.INGOT.mMaterialAmount, null, null, null, false);
//            GT_RecipeRegistrator.registerReverseFluidSmelting(aNugget, mat, Prefix.INGOT.mMaterialAmount, null);
//            GT_RecipeRegistrator.registerReverseMacerating(aNugget, mat, Prefix.INGOT.mMaterialAmount, null, null, null, false);
//            if (mat.mSmeltInto.mArcSmeltInto != mat) {
//                GT_RecipeRegistrator.registerReverseArcSmelting(aIngot, mat, Prefix.INGOT.mMaterialAmount, null, null, null);
//            }
//            ItemStack aMacInto = mat.mMacerateInto.getDust(1);
//            if (aMacInto != null && (mat.needsBlastFurnace() || mat.hasFlag(NOSMELT))) {
//                RecipeHelper.removeFurnaceSmelting(aMacInto);
//            }
//            //if (!mat.hasFlag(SMELTG)) {
//            //GT_ModHandler.addBasicShapedRecipe(aIngot, "XXX", "XXX", "XXX", 'X', aNugget);
//            //}
//            if (mat.hasFlag(GRINDABLE)) {
//                RecipeHelper.addShapedToolRecipe(aDust, "X  ", "m  ", "   ", 'X', aIngot);
//            }
//            ItemStack aIngotSmeltInto = mat.getSmeltInto().getIngot(1);
//            RecipeAdder.addAlloySmelterRecipe(Utils.ca(4, aDust), ItemList.Mold_Ingot.get(0), aIngotSmeltInto, 130, 3, true);
//            //TODO GT_RecipeRegistrator.registerUsagesForMaterials(aIngotStack, Prefix.plate.get(mat).toString(), !aNoSmashing);
//            RecipeAdder.addAlloySmelterRecipe(Utils.ca(9, aNugget), /*mat.hasFlag(SMELTG) ? ItemList.Shape_Mold_Ball.get(0) : */ItemList.Shape_Mold_Ingot.get(0), aIngotSmeltInto, 200, 2);
//            if (!mat.hasFlag(NOSMELT)) {
//                RecipeAdder.addAlloySmelterRecipe(aIngot, ItemList.Mold_Nugget.get(0), Utils.ca(9, aNugget), 100, 1);
//                RecipeHelper.addSmeltingRecipe(mat.getDustT(1), mat.getSmeltInto().getNugget(1));
//                RecipeAdder.addAlloySmelterRecipe(mat.getDustT(9), ItemList.Mold_Ingot.get(0), aIngotSmeltInto, 130, 3, true);
////            if (mat.mStandardMoltenFluid == null && mat.hasFlag(SMELTF)) {
////                GT_Mod.gregtechproxy.addAutogeneratedMoltenFluid(mat);
////                if (mat.mSmeltInto != mat && mat.mSmeltInto.mStandardMoltenFluid == null) {
////                    GT_Mod.gregtechproxy.addAutogeneratedMoltenFluid(mat.mSmeltInto);
////                }
////            }
//                ItemStack aDustSmeltInto = mat.getSmeltInto().getDust(1);
//                if (aDustSmeltInto != null) {
//                    RecipeHelper.addSmeltingRecipe(aDust, aDustSmeltInto);
//                }
//                int aVoltageMulti = mat.hasFlag(NOSMASH) ? mat.getBlastFurnaceTemp() >= 2800 ? 16 : 4 : mat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                if (mat.hasSmeltInto()) {
//                    RecipeAdder.addExtruderRecipe(aDust, ItemList.Shape_Ingot.get(0), aIngotSmeltInto, 10, 4 * aVoltageMulti);
//                }
//            }
//            RecipeAdder.addCompressorRecipe(Utils.ca(9, aIngot), mat.getBlock(1));
//            if (mat.needsBlastFurnace()) {
//                int aBlastDuration = Math.max(mat.getMass() / 4, 1) * mat.getBlastFurnaceTemp();
//                ItemStack aBlastStack = mat.getBlastFurnaceTemp() > 1750 && mat.mSmeltInto.hasFlag(HINGOT) ? mat.mSmeltInto.getIngotH(1) : aIngotSmeltInto;
//                RecipeAdder.addBlastFurnaceRecipe(aDust, null, null, null, aBlastStack, null, aBlastDuration, 120, mat.getBlastFurnaceTemp());
//                RecipeAdder.addBlastFurnaceRecipe(mat.getDustS(4), null, null, null, aBlastStack, null, aBlastDuration, 120, mat.getBlastFurnaceTemp());
//                if (!mat.hasFlag(NOSMELT)) { //TODO WUT?
//                    RecipeAdder.addBlastFurnaceRecipe(Utils.ca(9, mat.getDustT(1)), null, null, null, aBlastStack, null, aBlastDuration, 120, mat.getBlastFurnaceTemp());
//                    RecipeHelper.removeFurnaceSmelting(mat.getDustT(1));
//                }
//            }
//        }
//
//        for (Integer i : BGEM.getIds()) {
//            mat = Materials.get(i);
//            if (mat.isTransparent()) { //TODO PLATE > LENS BROKEN
//                RecipeAdder.addLatheRecipe(mat.getPlate(1), mat.getLens(1), mat.getDustS(1), Math.max(mat.getMass() / 2, 1), 480);
//                //GregTech_API.registerCover(aLensStack, new GT_MultiTexture(Textures.BlockIcons.MACHINE_CASINGS[2][0], new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_LENS, material.mRGBa, false)), new GT_Cover_Lens(material.mColor.mIndex));
//            }
//            ItemStack aGem = mat.getGem(1), aBlock = mat.getBlock(1);
//            RecipeAdder.addCompressorRecipe(Utils.ca(9, aGem), aBlock);
//            RecipeAdder.addHammerRecipe(aBlock, Utils.ca(9, aGem), 100, 24);
//            if (mat.hasFlag(CRYSTALLIZE)) {
//                ItemStack aDust = mat.getDust(1);
//                RecipeAdder.addAutoclaveRecipe(aDust, aWaterStack, aGem, 7000, 2000, 24);
//                RecipeAdder.addAutoclaveRecipe(aDust, aDistilledStack, aGem, 9000, 1500, 24);
//            }
//        }
//
//        for (Integer i : GEM.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aGem = mat.getGem(1), aChipped = mat.getGemChipped(1), aFlawed = mat.getGemFlawed(1), aFlawless = mat.getGemFlawless(1), aExquisite = mat.getGemExquisite(1);
//            if (mat.getFuelPower() > 0) {
//                RecipeAdder.addFuel(aGem, null, mat.getFuelPower() * 2, mat.mFuelType);
//                RecipeAdder.addFuel(aChipped, null, mat.getFuelPower() / 2, mat.mFuelType);
//                RecipeAdder.addFuel(aFlawed, null, mat.getFuelPower(), mat.mFuelType);
//                RecipeAdder.addFuel(aFlawless, null, mat.getFuelPower() * 4, mat.mFuelType);
//                RecipeAdder.addFuel(aExquisite, null, mat.getFuelPower() * 8, mat.mFuelType);
//            }
//            if (mat.hasFlag(NOSMASH)) {
//                RecipeAdder.addHammerRecipe(aGem, Utils.ca(2, aFlawed), 64, 16);
//            }
//            RecipeAdder.addLatheRecipe(aFlawless, mat.getRod(1), mat.getDust(1), mat.getMass(), 16);
//            RecipeAdder.addLatheRecipe(mat.getGemExquisite(1), mat.getLens(1), mat.getDust(2), mat.getMass(), 24);
//            //TODO FIX
//            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aGemStack), "h  ", "X  ", "   ", 'X', aFlawlessStack);
//            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aChippedStack), "h  ", "X  ", "   ", 'X', aFlawedStack);
//            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aFlawedStack), "h  ", "X  ", "   ", 'X', aGemStack);
//            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aFlawlessStack), "h  ", "X  ", "   ", 'X', aExquisiteStack);
//            if (mat.hasFlag(GRINDABLE)) {
//                //GT_ModHandler.addShapedToolRecipe(aDustStack, "X  ", "m  ", "   ", 'X', aGemStack);
//                //GT_ModHandler.addShapedToolRecipe(aSDustStack, "X  ", "m   ", "   ", 'X', aChippedStack);
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aSDustStack), "X  ", "m  ", "   ", 'X', aFlawedStack);
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aDustStack), "X  ", "m  ", "   ", 'X', aFlawlessStack);
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(4, aDustStack), "X  ", "m  ", "   ", 'X', aExquisiteStack);
//            }
//            RecipeAdder.addHammerRecipe(aFlawed, Utils.ca(2, aChipped), 64, 16);
//            RecipeAdder.addHammerRecipe(aFlawless, Utils.ca(2, aGem), 64, 16);
//            //TODO NPE GT_RecipeRegistrator.registerUsagesForMaterials(aGemStack, aPlateStack.toString(), !aNoSmashing);
//        }
//
//        for (Integer i : CRUSHED.getIds()) {
//            mat = Materials.get(i);
//
//            if (mat.hasByProducts()) {
//                ArrayList<Material> byProducts = mat.getByProducts();
//                ItemStack[] dustProducts = new ItemStack[byProducts.size()];
//                for (int b = 0; b < byProducts.size(); b++) {
//                    dustProducts[b] = byProducts.get(b).getDust(1);
//                }
//                GT_Recipe.GT_Recipe_Map.sByProductList.addFakeRecipe(false, new ItemStack[]{aChunk}, aByProducts, null, null, null, null, 0, 0, 0);
//            }
//
//            boolean aNeedsBlastFurnace = mat.needsBlastFurnace() || mat.mDirectSmelting.isBlastFurnaceRequired();
//            int aMultiplier = /*aIsRich ? 2 : */1; //TODO implement in some way, but for now support is codded in
//            ItemStack aChunk = mat.getChunk(1), aCrushed = mat.getCrushed(1), aDust = mat.getDust(1), aStoneDust = Materials.Stone.getDust(1);
//
//            RecipeHelper.addShapedToolRecipe(aDust, "h  ", "X  ", "   ", 'X', aCrushed); //TODO BROKEN
//            Materials aOreByProduct1 = mat.mByProducts.size() >= 1 ? mat.mByProducts.get(0) : mat.mMacerateInto;
//            Materials aOreByProduct2 = mat.mByProducts.size() >= 2 ? mat.mByProducts.get(1) : aOreByProduct1;
//            RecipeAdder.addPulverizerRecipe(aChunk, Utils.ca((aCrushed.stackSize * mat.mOreMultiplier * aMultiplier) * 2, aCrushed), mat.mByProducts.size() > 0 ? mat.mByProducts.get(0).getDust(1) : aDust, 10 * aMultiplier * mat.mByProductMultiplier, aStoneDust, 50, true);
//            RecipeAdder.addPulverizerRecipe(aCrushed, mat.mMacerateInto.getDust(1), aOreByProduct1.getDust(1), 10, false);
//            RecipeAdder.addHammerRecipe(aChunk, mat.hasFlag(BRITTLEG) ? mat.getGem(1) : aCrushed, 16, 10);
//            RecipeAdder.addHammerRecipe(aCrushed, aDust, 10, 16);
//            if (mat.hasFlag(INGOT) || mat.hasFlag(BGEM)) {
//                ItemStack aIngotOrGemStack = mat == mat.mDirectSmelting ? mat.hasFlag(BGEM) ? mat.getGem(1) : mat.getIngot(1) : mat.hasFlag(BRITTLEG) ? mat.mDirectSmelting.getGem(1) : mat.mDirectSmelting.getIngot(1);
//                if (!aNeedsBlastFurnace) {
//                    ItemStack aNonDirectSmeltingOutput = GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? mat.getNugget(6) : Unifier.getComponent(mat.hasFlag(BGEM) ? Prefix.gem : Prefix.ingot, mat.mDirectSmelting, 1);
//                    if (!mat.hasDirectSmeltInto()) {
//                        ItemStack aCrushedSmeltingOutput = mat.getNugget(10);
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aCrushedSmeltingOutput);
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aCrushedSmeltingOutput);
//                    } else if (aNonDirectSmeltingOutput != null) {
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aNonDirectSmeltingOutput);
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aNonDirectSmeltingOutput);
//                    }
//                    RecipeHelper.addSmeltingRecipe(aDust, aIngotOrGemStack); //TODO move to dust recipes
//                    RecipeHelper.addSmeltingRecipe(aChunk, Utils.ca(aMultiplier * mat.mSmeltingMultiplier, aIngotOrGemStack));
//                }
//                if (mat.hasFlag(BGEM)) { //Gem Specific Recipes
//                    RecipeHelper.addSmeltingRecipe(aChunk, Utils.ca(aMultiplier * mat.mSmeltingMultiplier, aIngotOrGemStack));
//                    if (mat.hasFlag(GEM)) {
//                        RecipeAdder.addSifterRecipe(aCrushed, new ItemStack[]{mat.getGemExquisite(1), mat.getGemFlawless(1), aIngotOrGemStack, mat.getGemFlawed(1), mat.getGemChipped(1), aDust}, new int[]{300, 1200, 4500, 1400, 2800, 3500}, 800, 16);
//                    } else {
//                        RecipeAdder.addSifterRecipe(aCrushed, new ItemStack[]{aIngotOrGemStack, aIngotOrGemStack, aIngotOrGemStack, aIngotOrGemStack, aIngotOrGemStack, aDust}, new int[]{100, 400, 1500, 2000, 4000, 5000}, 800, 16);
//                    }
//                } else if (mat.hasFlag(INGOT)) { //Solid Specific Recipes
//                    if (aNeedsBlastFurnace) {
//                        ItemStack aIngotSmeltInto = mat == mat.mSmeltInto ? aIngotOrGemStack : mat.mSmeltInto.getIngot(1);
//                        int aBlastDuration = Math.max(mat.getMass() / 4, 1) * mat.getBlastFurnaceTemp();
//                        ItemStack aBlastStack = mat.getBlastFurnaceTemp() > 1750 && mat.mSmeltInto.hasFlag(HINGOT) ? mat.mSmeltInto.getIngotH(1) : aIngotSmeltInto;
//                        RecipeAdder.addBlastFurnaceRecipe(aCrushed, null, null, null, aBlastStack, null, aBlastDuration, 120, mat.getBlastFurnaceTemp());
//                        RecipeAdder.addBlastFurnaceRecipe(aCrushed, null, null, null, aBlastStack, null, aBlastDuration, 120, mat.getBlastFurnaceTemp());
//                        RecipeAdder.addBlastFurnaceRecipe(aDust, null, null, null, aBlastStack, null, aBlastDuration, 120, mat.getBlastFurnaceTemp()); //TODO move to dust recipes
//                    }
//                    if (mat.hasFlag(CALCITE3X)) {
//                        ItemStack aMultiStack = Utils.mul(aMultiplier * 3 * mat.mSmeltingMultiplier, aIngotOrGemStack);
//                        ItemStack aDarkAsh = Materials.DarkAsh.getDustS(1);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Calcite.getDust(aMultiplier), null, null, aMultiStack, aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Quicklime.getDust(aMultiplier), null, null, aMultiStack, aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                    } else if (mat.hasFlag(CALCITE2X)) {
//                        ItemStack aDarkAsh = Materials.DarkAsh.getDustS(1);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Calcite.getDust(aMultiplier), null, null, Utils.mul(aMultiplier * aMixedOreYieldCount * mat.mSmeltingMultiplier, aIngotOrGemStack), aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Quicklime.getDustT(aMultiplier * 3), null, null, Utils.mul(aMultiplier * 3 * mat.mSmeltingMultiplier, aIngotOrGemStack), aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                    }
//                }
//            }
//            if (mat.hasFlag(CRUSHEDC)) RecipeAdder.addThermalCentrifugeRecipe(aCrushed, mat.getCrushedC(1), aOreByProduct2.getDustT(1), aStoneDust);
//            if (mat.hasFlag(CRUSHEDP)) RecipeAdder.addOreWasherRecipe(aCrushed, mat.getCrushedP(1), aOreByProduct1.getDustT(1), aStoneDust);
//            if (mat.hasFlag(WASHM)) RecipeAdder.addChemicalBathRecipe(aCrushed, Materials.Mercury.getFluid(1000), mat.getCrushedP(1), mat.mMacerateInto.getDust(1), Materials.Stone.getDust(1), new int[]{10000, 7000, 4000}, 800, 8);
//            if (mat.hasFlag(WASHS)) RecipeAdder.addChemicalBathRecipe(aCrushed, Materials.SodiumPersulfate.getFluid(aSodiumFluidAmount), mat.getCrushedP(1), mat.mMacerateInto.getDust(1), Materials.Stone.getDust(1), new int[]{10000, 7000, 4000}, 800, 8);
//        }
//
//        for (Integer i : CRUSHEDC.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aCrushedC = mat.getCrushedC(1);
//            RecipeHelper.addShapedToolRecipe(mat.getDust(1), "h  ", "X  ", "   ", 'X', aCrushedC);
//            RecipeAdder.addHammerRecipe(aCrushedC, mat.mMacerateInto.getDust(1), 10, 16);
//            Materials aOreByProduct1 = mat.mByProducts.size() >= 1 ? mat.mByProducts.get(0) : mat.mMacerateInto;
//            Materials aOreByProduct2 = mat.mByProducts.size() >= 2 ? mat.mByProducts.get(1) : aOreByProduct1;
//            Materials aOreByProduct3 = mat.mByProducts.size() >= 3 ? mat.mByProducts.get(2) : aOreByProduct2;
//            RecipeAdder.addPulverizerRecipe(aCrushedC, mat.mMacerateInto.getDust(1), aOreByProduct3.getDust(1), 10, false);
//            if (mat.hasFlag(INGOT) || mat.hasFlag(BGEM)) {
//                if (!(mat.needsBlastFurnace() || mat.mDirectSmelting.isBlastFurnaceRequired())) {
//                    ItemStack aNonDirectSmeltingOutput = GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? mat.getNugget(6) : Unifier.getComponent(mat.hasFlag(BGEM) ? Prefix.gem : Prefix.ingot, mat.mDirectSmelting, 1);
//                    if (!mat.hasDirectSmeltInto()) {
//                        ItemStack aCrushedSmeltingOutput = mat.getNugget(10);
//                        RecipeHelper.addSmeltingRecipe(aCrushedC, aCrushedSmeltingOutput);
//                    } else if (aNonDirectSmeltingOutput != null) {
//                        RecipeHelper.addSmeltingRecipe(aCrushedC, aNonDirectSmeltingOutput);
//                    }
//                } else {
//                    ItemStack aIngotOrGemStack = (mat == mat.mDirectSmelting ? mat.hasFlag(BGEM) ? mat.getGem(1) : mat.getIngot(1) : mat.hasFlag(BRITTLEG) ? mat.mDirectSmelting.getGem(1) : mat.mDirectSmelting.getIngot(1));
//                    ItemStack aBlastStack = mat.getBlastFurnaceTemp() > 1750 && mat.mSmeltInto.hasFlag(HINGOT) ? mat.mSmeltInto.getIngotH(1) : (mat == mat.mSmeltInto ? aIngotOrGemStack : mat.mSmeltInto.getIngot(1));
//                    RecipeAdder.addBlastFurnaceRecipe(aCrushedC, null, null, null, aBlastStack, null, Math.max(mat.getMass() / 4, 1) * mat.getBlastFurnaceTemp(), 120, mat.getBlastFurnaceTemp());
//                }
//            }
//        }
//
//        for (Integer i : CRUSHEDP.getIds()) {
//            mat = Materials.get(i);
//            ItemStack aCrushedP = mat.getCrushedP(1), aDust = mat.getDust(1);
//            RecipeHelper.addShapedToolRecipe(mat.getDust(1), "h  ", "X  ", "   ", 'X', aCrushedP); //TODO BROKEN?
//            Materials aOreByProduct1 = mat.mByProducts.size() >= 1 ? mat.mByProducts.get(0) : mat.mMacerateInto; //TODO simplify?
//            Materials aOreByProduct2 = mat.mByProducts.size() >= 2 ? mat.mByProducts.get(1) : aOreByProduct1;
//            RecipeAdder.addHammerRecipe(aCrushedP, mat.mMacerateInto.getDust(1), 10, 16);
//            RecipeAdder.addPulverizerRecipe(aCrushedP, aDust, aOreByProduct2.getDust(1), 10, false);
//            RecipeAdder.addThermalCentrifugeRecipe(aCrushedP, mat.mMacerateInto.getCrushedC(1), aOreByProduct2.getDustT(1));
//            if ((mat.hasFlag(INGOT) || mat.hasFlag(BGEM)) && (mat.needsBlastFurnace() || mat.mDirectSmelting.isBlastFurnaceRequired())) {
//                ItemStack aIngotOrGemStack = (mat == mat.mDirectSmelting ? mat.hasFlag(BGEM) ? mat.getGem(1) : mat.getIngot(1) : mat.hasFlag(BRITTLEG) ? mat.mDirectSmelting.getGem(1) : mat.mDirectSmelting.getIngot(1));
//                ItemStack aBlastStack = mat.getBlastFurnaceTemp() > 1750 && mat.mSmeltInto.hasFlag(HINGOT) ? mat.mSmeltInto.getIngotH(1) : (mat == mat.mSmeltInto ? aIngotOrGemStack : mat.mSmeltInto.getIngot(1));
//                RecipeAdder.addBlastFurnaceRecipe(aCrushedP, null, null, null, aBlastStack, null, Math.max(mat.getMass() / 4, 1) * mat.getBlastFurnaceTemp(), 120, mat.getBlastFurnaceTemp());
//            }
//        }
//
//        for (Integer i : RUBBERTOOLS.getIds()) {
//            mat = Materials.get(i);
//            RecipeHelper.addBasicShapedRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, mat, Materials.Wood), "XX ", "XXS", "XX ", 'X', mat.getIngot(1), 'S', Materials.Wood.getRod(1));
//            RecipeHelper.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, mat, Materials.Wood), mat.getHeadHammer(1), Materials.Wood.getRod(1));
//        }
//
//        for (Integer i : TOOLS.getIds()) {
//            mat = Materials.get(i);
//            if (mat.hasFlag(INGOT) && mat.hasFlag(PLATE) && !mat.hasFlag(RUBBERTOOLS) && mat == mat.mMacerateInto) {
//                ItemStack aStainlessScrew = Materials.StainlessSteel.getScrew(1), aTitaniumScrew = Materials.Titanium.getScrew(1), aTungstensteelScrew = Materials.TungstenSteel.getScrew(1), aStainlessPlate = Materials.StainlessSteel.getPlate(1), aTitaniumPlate = Materials.Titanium.getPlate(1), aTungstensteelPlate = Materials.TungstenSteel.getPlate(1), aStainlessSmallGear = Materials.StainlessSteel.getGearS(1), aTitaniumSmallGear = Materials.Titanium.getGearS(1), aTungstensteelSmallGear = Materials.TungstenSteel.getGearS(1), aTitaniumSpring = Materials.Titanium.getSpring(1);
//                ItemStack aTempStack, aSteelPlate = Materials.Steel.getPlate(1), aSteelRing = Materials.Steel.getRing(1);
//                ItemStack aDust = mat.getDust(1), aIngot = mat.getIngot(1), aPlate = mat.getPlate(1), aRod = mat.getRod(1), aBolt = mat.getBolt(1), aHandle = mat.mHandleMaterial.getRod(1);
//
//                int tVoltageMultiplier = 8 * (mat.getBlastFurnaceTemp() >= 2800 ? 64 : 16);
//                ItemStack aDustx2 = Utils.ca(2, aDust), aDustx3 = Utils.ca(3, aDust), aIngotx2 = Utils.ca(2, aIngot), aIngotx3 = Utils.ca(3, aIngot);
//
//                aTempStack = mat.getHeadHammer(1);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(HARDHAMMER, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                RecipeAdder.addExtruderRecipe(Utils.ca(6, aDust), ItemList.Shape_Extruder_Hammer.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadHammer(1), mat.getMass() * 6, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(Utils.ca(6, aIngot), ItemList.Shape_Extruder_Hammer.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadHammer(1), mat.getMass() * 6, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadFile(1);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(FILE, 1, mat, mat.mHandleMaterial), "P  ", "P  ", "S  ", 'P', aPlate, 'S', aHandle);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(FILE, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_File.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadFile(1), mat.getMass() * 2, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_File.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadFile(1), mat.getMass() * 2, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadAxe(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "PIh", "P  ", "f  ", 'P', aPlate, 'I', aIngot);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(AXE, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                RecipeAdder.addExtruderRecipe(aDustx3, ItemList.Shape_Extruder_Axe.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadAxe(1), mat.getMass() * 3, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngotx3, ItemList.Shape_Extruder_Axe.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadAxe(1), mat.getMass() * 3, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadHoe(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "PIh", "f  ", 'P', aPlate, 'I', aIngot);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(HOE, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_Hoe.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadHoe(1), mat.getMass() * 2, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_Hoe.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadHoe(1), mat.getMass() * 2, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadPickaxe(1);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(PICKAXE, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "PII", "f h", 'P', aPlate, 'I', aIngot);
//                RecipeAdder.addExtruderRecipe(aDustx3, ItemList.Shape_Extruder_Pickaxe.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadPickaxe(1), mat.getMass() * 3, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngotx3, ItemList.Shape_Extruder_Pickaxe.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadPickaxe(1), mat.getMass() * 3, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadSaw(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "PP ", "fh ", 'P', aPlate, 'I', aIngot);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SAW, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_Saw.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadSaw(1), mat.getMass() * 2, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_Saw.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadSaw(1), mat.getMass() * 2, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadSword(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, " P ", "fPh", 'P', aPlate, 'I', aIngot);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SWORD, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_Sword.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadSword(1), mat.getMass() * 2, tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_Sword.get(0), mat == mat.mSmeltInto ? aTempStack : mat.mSmeltInto.getHeadSword(1), mat.getMass() * 2, tVoltageMultiplier);
//
//                aTempStack = mat.getHeadPlow(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "PP", "PP", "hf", 'P', aPlate);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(PLOW, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//
//                aTempStack = mat.getHeadScythe(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "PPI", "hf ", 'P', aPlate, 'I', aIngot);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SCYTHE, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//
//                aTempStack = mat.getHeadShovel(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "fPh", 'P', aPlate, 'I', aIngot);
//                //GT_ModHandler.addShapedToolRecipe(mat.getHeadUniSpade(1), "fX", 'X', aTempStack); //TODO?
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SHOVEL, 1, mat, mat.mHandleMaterial), aTempStack, aHandle);
//                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(UNIVERSALSPADE, 1, mat, mat), aTempStack, aBolt, aRod);
//                RecipeAdder.addExtruderRecipe(aDust, ItemList.Shape_Extruder_Shovel.get(0), mat.mSmeltInto.getHeadShovel(1), mat.getMass(), tVoltageMultiplier);
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.Shape_Extruder_Shovel.get(0), mat.mSmeltInto.getHeadShovel(1), mat.getMass(), tVoltageMultiplier);
//
//                aTempStack = mat.getHeadTurbine(1);
//                RecipeAdder.addAssemblerRecipe(Utils.ca(8, aTempStack), Materials.Magnalium.getRod(1), INSTANCE.getToolWithStats(TURBINE_SMALL, 1, mat, mat), 160, 100);
//                RecipeAdder.addAssemblerRecipe(Utils.ca(16, aTempStack), Materials.Titanium.getRod(1), INSTANCE.getToolWithStats(TURBINE, 1, mat, mat), 320, 400);
//                RecipeAdder.addAssemblerRecipe(Utils.ca(24, aTempStack), Materials.TungstenSteel.getRod(1), INSTANCE.getToolWithStats(TURBINE_LARGE, 1, mat, mat), 640, 1600);
//                RecipeAdder.addAssemblerRecipe(Utils.ca(32, aTempStack), Materials.Americium.getRod(1), INSTANCE.getToolWithStats(TURBINE_HUGE, 1, mat, mat), 1280, 6400);
//
//                aTempStack = mat.getHeadBuzzSaw(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "wXh", "X X", "fXx", 'X', aPlate);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, mat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, mat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, mat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
//
//                aTempStack = mat.getHeadChainsaw(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "SRS", "XhX", "SRS", 'X', aPlate, 'S', aSteelPlate, 'R', aSteelRing);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, mat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, mat, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, mat, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, mat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, mat, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, mat, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, mat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, mat, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Sodium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, mat, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
//
//                aTempStack = mat.getHeadDrill(1);
//                GT_ModHandler.addShapedToolRecipe(aTempStack, "XSX", "XSX", "ShS", 'X', aPlate, 'S', aSteelPlate);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, mat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, mat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, mat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, mat, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, mat, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, mat, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Sodium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, mat, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, mat, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, mat, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
//
//                aTempStack = mat.getHeadWrench(1);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, mat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, mat, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, mat, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, mat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, mat, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, mat, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, mat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, mat, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Sodium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, mat, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
//
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, mat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, mat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, mat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
//
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, mat, Materials.Titanium, 1600000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemList.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, mat, Materials.Titanium, 1200000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemList.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, mat, Materials.Titanium, 800000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemList.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
//
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadTurbine(1), "fPd", "SPS", " P ", 'P', aPlate, 'S', aBolt);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadWrench(1), "hXW", "XRX", "WXd", 'X', aPlate, 'S', aSteelPlate, 'R', aSteelRing, 'W', Materials.Steel.getScrew(1));
//
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH, 1, mat, mat), "IhI", "III", " I ", 'I', aIngot);
//                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(CROWBAR, 1, mat, mat), "hDS", "DSD", "SDf", 'S', aRod, 'D', Dyes.blue);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER, 1, mat, mat.mHandleMaterial), " fS", " Sh", "W  ", 'S', aRod, 'W', aHandle);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCOOP, 1, mat, mat), "SWS", "SSS", "xSh", 'S', aRod, 'W', new ItemStack(Blocks.wool, 1, 32767));
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WIRECUTTER, 1, mat, mat), "PfP", "hPd", "STS", 'S', aRod, 'P', aPlate, 'T', aBolt);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BRANCHCUTTER, 1, mat, mat), "PfP", "PdP", "STS", 'S', aRod, 'P', aPlate, 'T', aBolt);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(KNIFE, 1, mat, mat), "fPh", " S ", 'S', aRod, 'P', aPlate);
//                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUTCHERYKNIFE, 1, mat, mat), "PPf", "PP ", "Sh ", 'S', aRod, 'P', aPlate);
//
//                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(PLUNGER, 1, mat, mat), "xRR", " SR", "S f", 'S', aRod, 'R', OreDicts.rubber.get(Prefix.plate));
//                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(SOLDERING_IRON_LV, 1, mat, Materials.Rubber, 100000L, 32L, 1L, -1L), "LBf", "Sd ", "P  ", 'B', aBolt, 'P', OreDicts.rubber.get(Prefix.plate), 'S', Materials.Iron.getRod(1), 'L', ItemList.Battery_RE_LV_Lithium.get(1));
//            } else if (mat.hasFlag(BGEM)) {
//                ItemStack aGem = mat.getGem(1);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadAxe(1), "GG ", "G  ", "f  ", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadHoe(1), "GG ", "f  ", "   ", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadPickaxe(1), "GGG", "f  ", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadPlow(1), "GG", "GG", " f", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadSaw(1), "GGf", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadScythe(1), "GGG", " f ", "   ", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadShovel(1), "fG", 'G', aGem);
//                GT_ModHandler.addShapedToolRecipe(mat.getHeadSword(1), " G", "fG", 'G', aGem);
//            }
//        }
//    }
//}
