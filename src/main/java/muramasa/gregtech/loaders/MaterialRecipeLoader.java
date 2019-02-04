//package muramasa.itech.loaders;
//
//import muramasa.itech.api.enums.ItemList;
//import muramasa.itech.api.materials.MaterialStack;
//import muramasa.itech.api.materials.Materials;
//import muramasa.itech.api.recipe.RecipeAdder;
//import muramasa.itech.api.util.RecipeHelper;
//import muramasa.itech.api.util.Utils;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.util.ArrayList;
//
//import static muramasa.itech.api.enums.ItemFlag.*;
//import static muramasa.itech.api.enums.RecipeFlag.*;
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
//        Materials[] aMaterialArray;
//
////        aMaterialArray = ELEMENTAL.getMats();
////        for (Materials aMat : aMaterialArray) {
////            ItemStack aDataOrb = ItemList.Tool_DataOrb.get(1);
////            Behaviour_DataOrb.setDataTitle(aDataOrb, "Elemental-Scan");
////            Behaviour_DataOrb.setDataName(aDataOrb, aMat.mElement.name());
////            ItemStack aRepOutput = ((aMat.hasFlag(FLUID) || aMat.hasFlag(GAS)) && !aMat.hasFlag(DUST)) ? aMat.getCell(1) : aMat.getDust(1);
////            Fluid aFluid = aMat.mFluid != null ? aMat.mFluid : aMat.mGas;
////            int aMass = aMat.getMass();
////            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{aRepOutput}, new ItemStack[]{aDataOrb}, ItemList.Tool_DataOrb.get(1), null, null, aMass * 8192, 32, 0);
////            GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, aFluid == null ? new ItemStack[]{aRepOutput} : null, new ItemStack[]{aDataOrb}, new FluidStack[]{Materials.UUMatter.getFluid(aMass)}, aFluid == null ? null : new FluidStack[]{new FluidStack(aFluid, 144)}, aMass * 512, 32, 0);
////        }
//
//        aMaterialArray = FLUID.getMats();
//        for (Materials aMat : aMaterialArray) {
//            //TODO ?
//        }
//
//        aMaterialArray = GAS.getMats();
//        for (Materials aMat : aMaterialArray) {
//            //TODO ?
//        }
//
////        aMaterialArray = PLASMA.getMats();
////        for (Materials aMat : aMaterialArray) {
////            ItemStack aPlasmaStack = aMat.getCellP(1);
////            RecipeAdder.addFuel(aPlasmaStack, ItemList.Cell_Empty.get(1), Math.max(1024, 1024 * aMat.getMass()), 4);
////            RecipeAdder.addVacuumFreezerRecipe(aPlasmaStack, aMat.getCell(1), Math.max(aMat.getMass() * 2, 1));
////        }
//
////        aMaterialArray = HINGOT.getMats();
////        for (Materials aMat : aMaterialArray) {
////            RecipeAdder.addVacuumFreezerRecipe(aMat.getIngotH(1), aMat.getIngot(1), Math.max(aMat.getMass() * 3, 1));
////        }
//
//        aMaterialArray = SPRING.getMats();
//        for (Materials aMat : aMaterialArray) {
//            RecipeAdder.addBenderRecipe(aMat.getRod(1), aMat.getSpring(1), 200, 16);
//        }
//
//        aMaterialArray = DPLATE.getMats();
//        for (Materials aMat : aMaterialArray) {
//            //GT_ModHandler.removeRecipeByOutput(aDenseStack);
//            if (aMat.mMaterialInto == aMat && aMat.hasFlag(NOSMASH)) {
//                RecipeAdder.addBenderRecipe(aMat.getPlate(9), aMat.getPlateD(1), Math.max(aMat.getMass() * 9, 1), 96);
//            }
//            if (!aMat.hasFlag(NOSMASH)) {
//                RecipeAdder.addBenderRecipe(aMat.getPlate(9), aMat.getPlateD(1), Math.max(aMat.getMass() * 9, 1), 96);
//            }
//            //GregTech_API.registerCover(aDenseStack, new GT_RenderedTexture(aMaterial.mIconSet.mTextures[76], aMaterial.mRGBa, false), null);
//        }
//
//        aMaterialArray = ROTOR.getMats();
//        for (Materials aMat : aMaterialArray) {
//            if (aMat.mMaterialInto == aMat) {
//                ItemStack aRotor = aMat.getRotor(1), aRing = aMat.getRing(1), aPlate = aMat.getPlate(1);
//                RecipeHelper.addShapedToolRecipe(aRotor, "PhP", "SRf", "PdP", 'P', aPlate, 'R', aRing, 'S', aMat.getScrew(1));
//                RecipeAdder.addAssemblerRecipe(Utils.ca(4, aPlate), aRing, Materials.Tin.getFluid(32), aRotor, 240, 24);
//                RecipeAdder.addAssemblerRecipe(Utils.ca(4, aPlate), aRing, Materials.Lead.getFluid(48), aRotor, 240, 24);
//                RecipeAdder.addAssemblerRecipe(Utils.ca(4, aPlate), aRing, Materials.SolderingAlloy.getFluid(16), aRotor, 240, 24);
//            }
//        }
//
//        aMaterialArray = WIREF.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aWireF = aMat.getWireF(1);
//            if (!aMat.hasFlag(NOSMASH)) {
//                RecipeAdder.addWiremillRecipe(aMat.getIngot(1), Utils.copy(aMat.getWire01(2), aWireF), 100, 4);
//                RecipeAdder.addWiremillRecipe(aMat.getRod(1), Utils.copy(aMat.getWire01(1), aWireF), 50, 4);
//            }
//            if (aMat.mMaterialInto == aMat) {
//                RecipeHelper.addShapedToolRecipe(aWireF, "Xx ", "   ", "   ", 'X', aMat.getFoil(1));
//            }
//        }
//
//        aMaterialArray = SGEAR.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aGearS = aMat.getGearS(1);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.MoldGearSmall.get(0), aMat.getFluid(144), aGearS, 16, 8);
//            if (aMat.mMaterialInto == aMat) {
//                RecipeHelper.addShapedToolRecipe(aGearS, "P  ", (aMat == Materials.Wood || aMat == Materials.WoodSealed) ? " s " : " h ", "   ", 'P', aMat.getPlate(1));
//            }
//        }
//
//        aMaterialArray = GEAR.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aGear = aMat.getGear(1);
//            GT_ModHandler.removeRecipeByOutput(aGear);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.MoldGear.get(0), aMat.getFluid(576), aGear, 128, 8);
//            if (aMat.mMaterialInto == aMat) {
//                RecipeHelper.addShapedToolRecipe(aGear, "SPS", "PwP", "SPS", 'P', aMat.getPlate(1), 'S', aMat.getRod(1));
//            }
//            if (!aMat.hasFlag(NOSMELT)) {
//                int aVoltageMulti = aMat.hasFlag(NOSMASH) ? aMat.getBlastFurnaceTemp() >= 2800 ? 16 : 4 : aMat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                ItemStack aGearSmeltInto = aMat.mSmeltInto.getGear(1), aIngot = aMat.getIngot(4);
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.ShapeGear.get(0), aGearSmeltInto, aMat.getMass() * 5, 8 * aVoltageMulti);
//                RecipeAdder.addAlloySmelterRecipe(Utils.ca(8, aIngot), ItemList.MoldGear.get(0), aGearSmeltInto, aMat.getMass() * 10, 2 * aVoltageMulti);
//            }
//            //Unifier.registerOre(OrePrefixes.gear, aGearStack);
//        }
//
//        aMaterialArray = SCREW.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aBolt = aMat.getBolt(1), aScrew = aMat.getScrew(1);
//            RecipeAdder.addLatheRecipe(aBolt, aScrew, null, Math.max(aMat.getMass() / 8, 1), 4);
//            if (aMat.mMaterialInto == aMat) {
//                RecipeHelper.addShapedToolRecipe(aScrew, "fX ", "X  ", "   ", 'X', aBolt);
//            }
//        }
//
//        aMaterialArray = FOIL.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aPlate = aMat.getPlate(1), aFoil = aMat.getFoil(2);
//            RecipeAdder.addBenderRecipe(aPlate, aFoil, Math.max(aMat.getMass(), 1), 24);
//            RecipeHelper.addShapedToolRecipe(Utils.ca(4, aFoil), "hX ", "   ", "   ", 'X', aPlate);
//            //GregTech_API.registerCover(aFoilStack, new GT_RenderedTexture(aMaterial.mIconSet.mTextures[70], aMaterial.mRGBa, false), null);
//        }
//
//        aMaterialArray = BOLT.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aBolt = aMat.getBolt(1), aRod = aMat.getRod(1);
//            RecipeAdder.addCutterRecipe(aRod, aMat.getBolt(4), null, Math.max(aMat.getMass() * 2, 1), 4);
//            if (aMat.mMaterialInto == aMat) {
//                RecipeHelper.addShapedToolRecipe(Utils.ca(2, aBolt), "s  ", " X ", "   ", 'X', aRod);
//            }
//            if (!aMat.hasFlag(NOSMELT)) {
//                RecipeAdder.addExtruderRecipe(aMat.getIngot(1), ItemList.ShapeBolt.get(0), aMat.mSmeltInto.getBolt(8), aMat.getMass() * 2, aMat.getBlastFurnaceTemp() >= 2800 ? 512 : 128);
//            }
//        }
//
//        aMaterialArray = RING.getMats();
//        for (Materials aMat : aMaterialArray) {
//            if (aMat.mMaterialInto == aMat && !aMat.hasFlag(NOSMASH)) {
//                RecipeHelper.addShapedToolRecipe(aMat.getRing(1), "h  ", " X ", "   ", 'X', aMat.getRod(1));
//            }
//            if (!aMat.hasFlag(NOSMELT)) {
//                RecipeAdder.addExtruderRecipe(aMat.getIngot(1), ItemList.ShapeRing.get(0), aMat.mSmeltInto.getRing(4), aMat.getMass() * 2, aMat.getBlastFurnaceTemp() >= 2800 ? 384 : 96);
//            }
//        }
//
//        aMaterialArray = ROD.getMats();
//        for (Materials aMat : aMaterialArray) {
//            if (aMat.hasFlag(BGEM) && aMat.hasFlag(BRITTLEG)) {
//                RecipeAdder.addLatheRecipe(aMat.getGem(1), aMat.getRod(1), aMat.mMacerateInto.getDustS(2), Math.max(aMat.getMass() * 5, 1), 16);
//            } else if (aMat.hasFlag(INGOT)) {
//                RecipeAdder.addLatheRecipe(aMat.getIngot(1), aMat.getRod(1), aMat.mMacerateInto.getDustS(2), Math.max(aMat.getMass() * 5, 1), 16);
//                if (aMat.mMaterialInto == aMat) {
//                    RecipeHelper.addShapedToolRecipe(aMat.getRod(1), "f  ", " X ", "   ", 'X', aMat.getIngot(1));
//                }
//            }
//            if (!aMat.hasFlag(NOSMELT) && !aMat.hasFlag(BGEM)) {
//                ItemStack aIngot = aMat.getIngot(1);
//                int aEU = aMat.getBlastFurnaceTemp() >= 2800 ? 384 : 96;
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.ShapeRod.get(0), aMat.mSmeltInto.getRod(2), aMat.getMass() * 2, aEU);
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.ShapeWire.get(0), aMat.mSmeltInto.getWire01(2), aMat.getMass() * 2, aEU);
//            }
//        }
//
//        aMaterialArray = PLATE.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aPlate = aMat.getPlate(1), aIngot = aMat.getIngot(1);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.MoldPlate.get(0), aMat.getFluid(144), aPlate, 32, 8);
//            if (aMat.mFuelPower > 0) {
//                RecipeAdder.addFuel(aPlate, null, aMat.mFuelPower, aMat.mFuelType);
//            }
//            //TODO MOVRE RecipeAdder.addImplosionRecipe(GT_Utility.ca(aMat == Materials.MeteoricIron ? 1 : 2, aPlateStack), 2, Unifier.get(OrePrefixes.compressed, aMat), Unifier.get(OrePrefixes.dustTiny, Materials.DarkAsh, 1));
//
//            if (!aMat.hasFlag(NOSMASH)) {
//                if (aMat.mMaterialInto == aMat) {
//                    RecipeHelper.addShapedToolRecipe(aPlate, "h  ", "X  ", "X  ", 'X', aMat.hasFlag(BGEM) ? aMat.getGem(1) : aIngot);
//                    if (aMat.hasFlag(GRINDABLE)) {
//                        RecipeHelper.addShapedToolRecipe(aMat.getDust(1), "X  ", "m  ", "   ", 'X', aPlate);
//                    }
//                }
//                RecipeAdder.addHammerRecipe(Utils.ca(3, aIngot), Utils.ca(2, aPlate), Math.max(aMat.getMass(), 1), 16);
//                RecipeAdder.addBenderRecipe(aIngot, aPlate, Math.max(aMat.getMass(), 1), 24);
//            }
//            if (!aMat.hasFlag(NOSMELT)) {
//                int aEU = aMat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                RecipeAdder.addExtruderRecipe(aIngot, ItemList.ShapePlate.get(0), aMat.mSmeltInto.getPlate(1), aMat.getMass(), 8 * aEU);
//                RecipeAdder.addAlloySmelterRecipe(Utils.ca(2, aIngot), ItemList.MoldPlate.get(0), aMat.mSmeltInto.getPlate(1), aMat.getMass() * 2, 2 * aEU);
//
//
//                //TODO WUT?
////            if (OrePrefixes.block.isIgnored(aMat) && aMat != Materials.GraniteRed && aMat != Materials.GraniteBlack && aMat != Materials.Glass && aMat != Materials.Obsidian && aMat != Materials.Glowstone && aMat != Materials.Paper) {
////                GT_ModHandler.addCompressorRecipe(aDustStack, aPlateStack);
////            }
//            }
//            RecipeAdder.addCutterRecipe(aMat.getBlock(1), Utils.ca(9, aPlate), null, Math.max(aMat.getMass() * 10, 1), 30);
//            GregTech_API.registerCover(aPlate, new GT_RenderedTexture(aMat.mIconSet.mTextures[71], aMat.mRGBa, false), null);
//        }
//
//        aMaterialArray = BLOCK.getMats();
//        for (Materials aMat : aMaterialArray) {
//            if (!aMat.hasFlag(NOSMELT)) {
//                int aVoltageMulti = aMat.hasFlag(NOSMASH) ? aMat.getBlastFurnaceTemp() >= 2800 ? 16 : 4 : aMat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                ItemStack aIngot9x = aMat.getIngot(9);
//                ItemStack aBlock = aMat.mSmeltInto.getBlock(1);
//                RecipeAdder.addExtruderRecipe(aIngot9x, ItemList.ShapeBlock.get(0), aBlock, 10, 8 * aVoltageMulti);
//                RecipeAdder.addAlloySmelterRecipe(aIngot9x, ItemList.MoldBlock.get(0), aBlock, 5, 4 * aVoltageMulti);
//            }
//        }
//
//        aMaterialArray = DUST.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aDust = aMat.getDust(1), aDustS = aMat.getDustS(1), aDustT = aMat.getDustT(1);
//            RecipeHelper.addBasicShapedRecipe(Utils.ca(4, aDustS), " X", "  ", 'X', aDust);
//            RecipeHelper.addBasicShapedRecipe(Utils.ca(9, aDustT), "X ", "  ", 'X', aDust);
//            RecipeHelper.addBasicShapelessRecipe(aDust, aDustS, aDustS, aDustS, aDustS);
//            RecipeHelper.addBasicShapelessRecipe(aDust, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT, aDustT);
//            if (aMat.mFuelPower > 0) {
//                RecipeAdder.addFuel(aDust, null, aMat.mFuelPower, aMat.mFuelType);
//            }
//            if (!aMat.needsBlastFurnace()) {
//                if (aMat.mSmeltInto != null) {
//                    GT_RecipeRegistrator.registerReverseFluidSmelting(aDust, aMat, OrePrefixes.dust.mMaterialAmount, null);
//                    GT_RecipeRegistrator.registerReverseFluidSmelting(aDustS, aMat, OrePrefixes.dustSmall.mMaterialAmount, null);
//                    GT_RecipeRegistrator.registerReverseFluidSmelting(aDustT, aMat, OrePrefixes.dustTiny.mMaterialAmount, null);
//                    if (aMat.mSmeltInto.mArcSmeltInto != aMat) {
//                        GT_RecipeRegistrator.registerReverseArcSmelting(aDust, aMat, OrePrefixes.dust.mMaterialAmount, null, null, null);
//                        GT_RecipeRegistrator.registerReverseArcSmelting(aDustS, aMat, OrePrefixes.dustSmall.mMaterialAmount, null, null, null);
//                        GT_RecipeRegistrator.registerReverseArcSmelting(aDustT, aMat, OrePrefixes.dustTiny.mMaterialAmount, null, null, null);
//                    }
//                }
//            }
//            if (aMat.mDirectSmelting != aMat && !aMat.hasFlag(NOSMELT) && !(aMat.needsBlastFurnace() || aMat.mDirectSmelting.isBlastFurnaceRequired()) && !aMat.hasFlag(NOBBF)) {
//                RecipeAdder.addPrimitiveBlastRecipe(Utils.ca(2, aDust), null, 2, aMat.mDirectSmelting.getIngot(aMixedOreYieldCount), null, 2400);
//            }
////            if (aMat.hasFlag(ELECSEPI)) {
////                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Iron.getDustS(1), Materials.Iron.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
////            } else if (aMat.hasFlag(ELECSEPG)) {
////                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Gold.getDustS(1), Materials.Gold.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
////            } else if (aMat.hasFlag(ELECSEPN)) {
////                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Neodymium.getDustS(1), Materials.Neodymium.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
////            }
//        }
//
//        aMaterialArray = Materials.getMatsFor(ELEC, CENT);
//        for (Materials aMat : aMaterialArray) {
//            int aInputCount = 0;
//            int aInputCellCount = 0;
//
//            ArrayList<ItemStack> aOutputs = new ArrayList<>();
//            FluidStack aFirstFluid = null; //The first FLUID MatStack (FLUID & !DUST tag combo) uses the Electrolyzers fluid input tack. The preceding are cells.
//
//            MaterialStack[] aMatStacks = aMat.getProcessInto().toArray(new MaterialStack[0]);
//            for (MaterialStack aMatStack : aMatStacks) {
//                if ((aMatStack.material.hasFlag(FLUID) || aMatStack.material.hasFlag(GAS)) && !aMatStack.material.hasFlag(DUST)) {
//                    if (aFirstFluid == null) {
//                        if (aMatStack.material.mFluid != null) { //If a Material has mFluid & mGas, Prioritise mFluid.
//                            aFirstFluid = aMatStack.material.getFluid(aMatStack.amount * 1000);
//                        } else {
//                            aFirstFluid = aMatStack.material.getGas(aMatStack.amount * 1000);
//                        }
//                    } else {
//                        aOutputs.register(aMatStack.material.getCell(aMatStack.amount));
//                        aInputCellCount += aMatStack.amount;
//                    }
//                } else {
//                    aOutputs.register(aMatStack.material.getDust(aMatStack.amount));
//                }
//                aInputCount += aMatStack.amount;
//            }
//            aInputCount = Math.min(aInputCount, 64); //This should not happen. This means mProcess total is over 64 and the recipe should be adjusted
//            if (aOutputs.size() > 0) {
//                ItemStack aInput = (aMat.hasFlag(FLUID) || aMat.hasFlag(GAS)) && !aMat.hasFlag(DUST) ? aMat.getCell(aInputCount) : aMat.getDust(aInputCount), aInputCell = aInputCellCount > 0 ? ItemList.Cell_Empty.get(aInputCellCount) : null;
//                if (aMat.hasFlag(ELEC)) {
//                    RecipeAdder.addElectrolyzerRecipe(aInput, aInputCell, null, aFirstFluid, aOutputs.size() < 1 ? null : aOutputs.get(0), aOutputs.size() < 2 ? null : aOutputs.get(1), aOutputs.size() < 3 ? null : aOutputs.get(2), aOutputs.size() < 4 ? null : aOutputs.get(3), aOutputs.size() < 5 ? null : aOutputs.get(4), aOutputs.size() < 6 ? null : aOutputs.get(5), null, Math.max(1, Math.abs(aMat.getProtons() * 2 * aInputCellCount)), Math.min(4, aOutputs.size()) * 30);
//                } else if (aMat.hasFlag(CENT)) {
//                    RecipeAdder.addCentrifugeRecipe(aInput, aInputCell, null, aFirstFluid, aOutputs.size() < 1 ? null : aOutputs.get(0), aOutputs.size() < 2 ? null : aOutputs.get(1), aOutputs.size() < 3 ? null : aOutputs.get(2), aOutputs.size() < 4 ? null : aOutputs.get(3), aOutputs.size() < 5 ? null : aOutputs.get(4), aOutputs.size() < 6 ? null : aOutputs.get(5), null, Math.max(1, Math.abs(aMat.getMass() * 4 * aInputCellCount)), Math.min(4, aOutputs.size()) * 5);
//                }
//            }
//        }
//
//        aMaterialArray = INGOT.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aIngot = aMat.getIngot(1), aNugget = aMat.getNugget(1), aDust = aMat.getDust(1);
//            if (aMat.mFuelPower > 0) {
//                RecipeAdder.addFuel(aIngot, null, aMat.mFuelPower, aMat.mFuelType);
//            }
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.MoldNugget.get(0), aMat.getFluid(16), aNugget, 16, 4);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.MoldIngot.get(0), aMat.getFluid(144), aIngot, 32, 8);
//            RecipeAdder.addFluidSolidifierRecipe(ItemList.MoldBlock.get(0), aMat.getFluid(1296), aMat.getBlock(1), 288, 8);
//            GT_RecipeRegistrator.registerReverseFluidSmelting(aIngot, aMat, OrePrefixes.ingot.mMaterialAmount, null);
//            GT_RecipeRegistrator.registerReverseMacerating(aIngot, aMat, OrePrefixes.ingot.mMaterialAmount, null, null, null, false);
//            GT_RecipeRegistrator.registerReverseFluidSmelting(aNugget, aMat, OrePrefixes.nugget.mMaterialAmount, null);
//            GT_RecipeRegistrator.registerReverseMacerating(aNugget, aMat, OrePrefixes.nugget.mMaterialAmount, null, null, null, false);
//            if (aMat.mSmeltInto.mArcSmeltInto != aMat) {
//                GT_RecipeRegistrator.registerReverseArcSmelting(aIngot, aMat, OrePrefixes.ingot.mMaterialAmount, null, null, null);
//            }
//            ItemStack aMacInto = aMat.mMacerateInto.getDust(1);
//            if (aMacInto != null && (aMat.needsBlastFurnace() || aMat.hasFlag(NOSMELT))) {
//                RecipeHelper.removeFurnaceSmelting(aMacInto);
//            }
//            if (aMat.mMaterialInto == aMat) {
//                //if (!aMat.hasFlag(SMELTG)) {
//                //GT_ModHandler.addBasicShapedRecipe(aIngot, "XXX", "XXX", "XXX", 'X', aNugget);
//                //}
//                if (aMat.hasFlag(GRINDABLE)) {
//                    RecipeHelper.addShapedToolRecipe(aDust, "X  ", "m  ", "   ", 'X', aIngot);
//                }
//            }
//            ItemStack aIngotSmeltInto = aMat.mSmeltInto.getIngot(1);
//            RecipeAdder.addAlloySmelterRecipe(Utils.ca(4, aDust), ItemList.MoldIngot.get(0), aIngotSmeltInto, 130, 3, true);
//            //TODO GT_RecipeRegistrator.registerUsagesForMaterials(aIngotStack, OrePrefixes.plate.get(aMat).toString(), !aNoSmashing);
//            RecipeAdder.addAlloySmelterRecipe(Utils.ca(9, aNugget), /*aMat.hasFlag(SMELTG) ? ItemList.Shape_Mold_Ball.get(0) : */ItemList.Shape_Mold_Ingot.get(0), aIngotSmeltInto, 200, 2);
//            if (!aMat.hasFlag(NOSMELT)) {
//                RecipeAdder.addAlloySmelterRecipe(aIngot, ItemList.MoldNugget.get(0), Utils.ca(9, aNugget), 100, 1);
//                RecipeHelper.addSmeltingRecipe(aMat.getDustT(1), aMat.mSmeltInto.getNugget(1));
//                RecipeAdder.addAlloySmelterRecipe(aMat.getDustT(9), ItemList.MoldIngot.get(0), aIngotSmeltInto, 130, 3, true);
////            if (aMat.mStandardMoltenFluid == null && aMat.hasFlag(SMELTF)) {
////                GT_Mod.gregtechproxy.addAutogeneratedMoltenFluid(aMat);
////                if (aMat.mSmeltInto != aMat && aMat.mSmeltInto.mStandardMoltenFluid == null) {
////                    GT_Mod.gregtechproxy.addAutogeneratedMoltenFluid(aMat.mSmeltInto);
////                }
////            }
//                ItemStack aDustSmeltInto = aMat.mSmeltInto.getIngot(1);
//                if (aDustSmeltInto != null) {
//                    RecipeHelper.addSmeltingRecipe(aDust, aDustSmeltInto);
//                }
//                int aVoltageMulti = aMat.hasFlag(NOSMASH) ? aMat.getBlastFurnaceTemp() >= 2800 ? 16 : 4 : aMat.getBlastFurnaceTemp() >= 2800 ? 64 : 16;
//                if (aMat != aMat.mSmeltInto) {
//                    RecipeAdder.addExtruderRecipe(aDust, ItemList.ShapeIngot.get(0), aIngotSmeltInto, 10, 4 * aVoltageMulti);
//                }
//            }
//            RecipeAdder.addCompressorRecipe(Utils.ca(9, aIngot), aMat.getBlock(1));
//            if (aMat.needsBlastFurnace()) {
//                int aBlastDuration = Math.max(aMat.getMass() / 4, 1) * aMat.getBlastFurnaceTemp();
//                ItemStack aBlastStack = aMat.getBlastFurnaceTemp() > 1750 && aMat.mSmeltInto.hasFlag(HINGOT) ? aMat.mSmeltInto.getIngotH(1) : aIngotSmeltInto;
//                RecipeAdder.addBlastFurnaceRecipe(aDust, null, null, null, aBlastStack, null, aBlastDuration, 120, aMat.getBlastFurnaceTemp());
//                RecipeAdder.addBlastFurnaceRecipe(aMat.getDustS(4), null, null, null, aBlastStack, null, aBlastDuration, 120, aMat.getBlastFurnaceTemp());
//                if (!aMat.hasFlag(NOSMELT)) { //TODO WUT?
//                    RecipeAdder.addBlastFurnaceRecipe(Utils.ca(9, aMat.getDustT(1)), null, null, null, aBlastStack, null, aBlastDuration, 120, aMat.getBlastFurnaceTemp());
//                    RecipeHelper.removeFurnaceSmelting(aMat.getDustT(1));
//                }
//            }
//        }
//
//        aMaterialArray = BGEM.getMats();
//        for (Materials aMat : aMaterialArray) {
//            if (aMat.transparent()) { //TODO PLATE > LENS BROKEN
//                RecipeAdder.addLatheRecipe(aMat.getPlate(1), aMat.getLens(1), aMat.getDustS(1), Math.max(aMat.getMass() / 2, 1), 480);
//                //GregTech_API.registerCover(aLensStack, new GT_MultiTexture(Textures.BlockIcons.MACHINE_CASINGS[2][0], new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_LENS, aMaterial.mRGBa, false)), new GT_Cover_Lens(aMaterial.mColor.mIndex));
//            }
//            ItemStack aGem = aMat.getGem(1), aBlock = aMat.getBlock(1);
//            RecipeAdder.addCompressorRecipe(Utils.ca(9, aGem), aBlock);
//            RecipeAdder.addHammerRecipe(aBlock, Utils.ca(9, aGem), 100, 24);
//            if (aMat.hasFlag(CRYSTALLIZE)) {
//                ItemStack aDust = aMat.getDust(1);
//                RecipeAdder.addAutoclaveRecipe(aDust, aWaterStack, aGem, 7000, 2000, 24);
//                RecipeAdder.addAutoclaveRecipe(aDust, aDistilledStack, aGem, 9000, 1500, 24);
//            }
//        }
//
//        aMaterialArray = GEM.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aGem = aMat.getGem(1), aChipped = aMat.getGemChipped(1), aFlawed = aMat.getGemFlawed(1), aFlawless = aMat.getGemFlawless(1), aExquisite = aMat.getGemExquisite(1);
//            if (aMat.mFuelPower > 0) {
//                RecipeAdder.addFuel(aGem, null, aMat.mFuelPower * 2, aMat.mFuelType);
//                RecipeAdder.addFuel(aChipped, null, aMat.mFuelPower / 2, aMat.mFuelType);
//                RecipeAdder.addFuel(aFlawed, null, aMat.mFuelPower, aMat.mFuelType);
//                RecipeAdder.addFuel(aFlawless, null, aMat.mFuelPower * 4, aMat.mFuelType);
//                RecipeAdder.addFuel(aExquisite, null, aMat.mFuelPower * 8, aMat.mFuelType);
//            }
//            if (aMat.hasFlag(NOSMASH)) {
//                RecipeAdder.addHammerRecipe(aGem, Utils.ca(2, aFlawed), 64, 16);
//            }
//            RecipeAdder.addLatheRecipe(aFlawless, aMat.getRod(1), aMat.getDust(1), aMat.getMass(), 16);
//            RecipeAdder.addLatheRecipe(aMat.getGemExquisite(1), aMat.getLens(1), aMat.getDust(2), aMat.getMass(), 24);
//            if (aMat.mMaterialInto == aMat) {
//                //TODO FIX
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aGemStack), "h  ", "X  ", "   ", 'X', aFlawlessStack);
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aChippedStack), "h  ", "X  ", "   ", 'X', aFlawedStack);
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aFlawedStack), "h  ", "X  ", "   ", 'X', aGemStack);
//                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aFlawlessStack), "h  ", "X  ", "   ", 'X', aExquisiteStack);
//                if (aMat.hasFlag(GRINDABLE)) {
//                    //GT_ModHandler.addShapedToolRecipe(aDustStack, "X  ", "m  ", "   ", 'X', aGemStack);
//                    //GT_ModHandler.addShapedToolRecipe(aSDustStack, "X  ", "m   ", "   ", 'X', aChippedStack);
//                    //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aSDustStack), "X  ", "m  ", "   ", 'X', aFlawedStack);
//                    //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aDustStack), "X  ", "m  ", "   ", 'X', aFlawlessStack);
//                    //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(4, aDustStack), "X  ", "m  ", "   ", 'X', aExquisiteStack);
//                }
//            }
//            RecipeAdder.addHammerRecipe(aFlawed, Utils.ca(2, aChipped), 64, 16);
//            RecipeAdder.addHammerRecipe(aFlawless, Utils.ca(2, aGem), 64, 16);
//            //TODO NPE GT_RecipeRegistrator.registerUsagesForMaterials(aGemStack, aPlateStack.toString(), !aNoSmashing);
//        }
//
//        aMaterialArray = CRUSHED.getMats();
//        for (Materials aMat : aMaterialArray) {
//            boolean aNeedsBlastFurnace = aMat.needsBlastFurnace() || aMat.mDirectSmelting.isBlastFurnaceRequired();
//            int aMultiplier = /*aIsRich ? 2 : */1; //TODO implement in some way, but for now support is codded in
//            ItemStack aChunk = aMat.getChunk(1), aCrushed = aMat.getCrushed(1), aDust = aMat.getDust(1), aStoneDust = Materials.Stone.getDust(1);
//            if (aMat.mByProducts.size() > 0) {
//                ItemStack[] aByProducts = new ItemStack[aMat.mByProducts.size()];
//                for (int i = 0; i < aMat.mByProducts.size(); i++) {
//                    aByProducts[i] = aMat.mByProducts.get(i).getDust(1);
//                }
//                GT_Recipe.GT_Recipe_Map.sByProductList.addFakeRecipe(false, new ItemStack[]{aChunk}, aByProducts, null, null, null, null, 0, 0, 0);
//            }
//            RecipeHelper.addShapedToolRecipe(aDust, "h  ", "X  ", "   ", 'X', aCrushed); //TODO BROKEN
//            Materials aOreByProduct1 = aMat.mByProducts.size() >= 1 ? aMat.mByProducts.get(0) : aMat.mMacerateInto;
//            Materials aOreByProduct2 = aMat.mByProducts.size() >= 2 ? aMat.mByProducts.get(1) : aOreByProduct1;
//            RecipeAdder.addPulverizerRecipe(aChunk, Utils.ca((aCrushed.stackSize * aMat.mOreMultiplier * aMultiplier) * 2, aCrushed), aMat.mByProducts.size() > 0 ? aMat.mByProducts.get(0).getDust(1) : aDust, 10 * aMultiplier * aMat.mByProductMultiplier, aStoneDust, 50, true);
//            RecipeAdder.addPulverizerRecipe(aCrushed, aMat.mMacerateInto.getDust(1), aOreByProduct1.getDust(1), 10, false);
//            RecipeAdder.addHammerRecipe(aChunk, aMat.hasFlag(BRITTLEG) ? aMat.getGem(1) : aCrushed, 16, 10);
//            RecipeAdder.addHammerRecipe(aCrushed, aDust, 10, 16);
//            if (aMat.hasFlag(INGOT) || aMat.hasFlag(BGEM)) {
//                ItemStack aIngotOrGemStack = aMat == aMat.mDirectSmelting ? aMat.hasFlag(BGEM) ? aMat.getGem(1) : aMat.getIngot(1) : aMat.hasFlag(BRITTLEG) ? aMat.mDirectSmelting.getGem(1) : aMat.mDirectSmelting.getIngot(1);
//                if (!aNeedsBlastFurnace) {
//                    ItemStack aNonDirectSmeltingOutput = GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? aMat.getNugget(6) : Unifier.getComponent(aMat.hasFlag(BGEM) ? OrePrefixes.gem : OrePrefixes.ingot, aMat.mDirectSmelting, 1);
//                    if (aMat == aMat.mDirectSmelting) {
//                        ItemStack aCrushedSmeltingOutput = aMat.getNugget(10);
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aCrushedSmeltingOutput);
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aCrushedSmeltingOutput);
//                    } else if (aNonDirectSmeltingOutput != null) {
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aNonDirectSmeltingOutput);
//                        RecipeHelper.addSmeltingRecipe(aCrushed, aNonDirectSmeltingOutput);
//                    }
//                    RecipeHelper.addSmeltingRecipe(aDust, aIngotOrGemStack); //TODO move to dust recipes
//                    RecipeHelper.addSmeltingRecipe(aChunk, Utils.ca(aMultiplier * aMat.mSmeltingMultiplier, aIngotOrGemStack));
//                }
//                if (aMat.hasFlag(BGEM)) { //Gem Specific Recipes
//                    RecipeHelper.addSmeltingRecipe(aChunk, Utils.ca(aMultiplier * aMat.mSmeltingMultiplier, aIngotOrGemStack));
//                    if (aMat.hasFlag(GEM)) {
//                        RecipeAdder.addSifterRecipe(aCrushed, new ItemStack[]{aMat.getGemExquisite(1), aMat.getGemFlawless(1), aIngotOrGemStack, aMat.getGemFlawed(1), aMat.getGemChipped(1), aDust}, new int[]{300, 1200, 4500, 1400, 2800, 3500}, 800, 16);
//                    } else {
//                        RecipeAdder.addSifterRecipe(aCrushed, new ItemStack[]{aIngotOrGemStack, aIngotOrGemStack, aIngotOrGemStack, aIngotOrGemStack, aIngotOrGemStack, aDust}, new int[]{100, 400, 1500, 2000, 4000, 5000}, 800, 16);
//                    }
//                } else if (aMat.hasFlag(INGOT)) { //Solid Specific Recipes
//                    if (aNeedsBlastFurnace) {
//                        ItemStack aIngotSmeltInto = aMat == aMat.mSmeltInto ? aIngotOrGemStack : aMat.mSmeltInto.getIngot(1);
//                        int aBlastDuration = Math.max(aMat.getMass() / 4, 1) * aMat.getBlastFurnaceTemp();
//                        ItemStack aBlastStack = aMat.getBlastFurnaceTemp() > 1750 && aMat.mSmeltInto.hasFlag(HINGOT) ? aMat.mSmeltInto.getIngotH(1) : aIngotSmeltInto;
//                        RecipeAdder.addBlastFurnaceRecipe(aCrushed, null, null, null, aBlastStack, null, aBlastDuration, 120, aMat.getBlastFurnaceTemp());
//                        RecipeAdder.addBlastFurnaceRecipe(aCrushed, null, null, null, aBlastStack, null, aBlastDuration, 120, aMat.getBlastFurnaceTemp());
//                        RecipeAdder.addBlastFurnaceRecipe(aDust, null, null, null, aBlastStack, null, aBlastDuration, 120, aMat.getBlastFurnaceTemp()); //TODO move to dust recipes
//                    }
//                    if (aMat.hasFlag(CALCITE3X)) {
//                        ItemStack aMultiStack = Utils.mul(aMultiplier * 3 * aMat.mSmeltingMultiplier, aIngotOrGemStack);
//                        ItemStack aDarkAsh = Materials.DarkAsh.getDustS(1);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Calcite.getDust(aMultiplier), null, null, aMultiStack, aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Quicklime.getDust(aMultiplier), null, null, aMultiStack, aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                    } else if (aMat.hasFlag(CALCITE2X)) {
//                        ItemStack aDarkAsh = Materials.DarkAsh.getDustS(1);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Calcite.getDust(aMultiplier), null, null, Utils.mul(aMultiplier * aMixedOreYieldCount * aMat.mSmeltingMultiplier, aIngotOrGemStack), aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                        RecipeAdder.addBlastFurnaceRecipe(aChunk, Materials.Quicklime.getDustT(aMultiplier * 3), null, null, Utils.mul(aMultiplier * 3 * aMat.mSmeltingMultiplier, aIngotOrGemStack), aDarkAsh, aIngotOrGemStack.stackSize * 500, 120, 1500);
//                    }
//                }
//            }
//            if (aMat.hasFlag(CRUSHEDC)) RecipeAdder.addThermalCentrifugeRecipe(aCrushed, aMat.getCrushedC(1), aOreByProduct2.getDustT(1), aStoneDust);
//            if (aMat.hasFlag(CRUSHEDP)) RecipeAdder.addOreWasherRecipe(aCrushed, aMat.getCrushedP(1), aOreByProduct1.getDustT(1), aStoneDust);
//            if (aMat.hasFlag(WASHM)) RecipeAdder.addChemicalBathRecipe(aCrushed, Materials.Mercury.getFluid(1000), aMat.getCrushedP(1), aMat.mMacerateInto.getDust(1), Materials.Stone.getDust(1), new int[]{10000, 7000, 4000}, 800, 8);
//            if (aMat.hasFlag(WASHS)) RecipeAdder.addChemicalBathRecipe(aCrushed, Materials.SodiumPersulfate.getFluid(aSodiumFluidAmount), aMat.getCrushedP(1), aMat.mMacerateInto.getDust(1), Materials.Stone.getDust(1), new int[]{10000, 7000, 4000}, 800, 8);
//        }
//
//        aMaterialArray = CRUSHEDC.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aCrushedC = aMat.getCrushedC(1);
//            RecipeHelper.addShapedToolRecipe(aMat.getDust(1), "h  ", "X  ", "   ", 'X', aCrushedC);
//            RecipeAdder.addHammerRecipe(aCrushedC, aMat.mMacerateInto.getDust(1), 10, 16);
//            Materials aOreByProduct1 = aMat.mByProducts.size() >= 1 ? aMat.mByProducts.get(0) : aMat.mMacerateInto;
//            Materials aOreByProduct2 = aMat.mByProducts.size() >= 2 ? aMat.mByProducts.get(1) : aOreByProduct1;
//            Materials aOreByProduct3 = aMat.mByProducts.size() >= 3 ? aMat.mByProducts.get(2) : aOreByProduct2;
//            RecipeAdder.addPulverizerRecipe(aCrushedC, aMat.mMacerateInto.getDust(1), aOreByProduct3.getDust(1), 10, false);
//            if (aMat.hasFlag(INGOT) || aMat.hasFlag(BGEM)) {
//                if (!(aMat.needsBlastFurnace() || aMat.mDirectSmelting.isBlastFurnaceRequired())) {
//                    ItemStack aNonDirectSmeltingOutput = GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? aMat.getNugget(6) : Unifier.getComponent(aMat.hasFlag(BGEM) ? OrePrefixes.gem : OrePrefixes.ingot, aMat.mDirectSmelting, 1);
//                    if (aMat == aMat.mDirectSmelting) {
//                        ItemStack aCrushedSmeltingOutput = aMat.getNugget(10);
//                        RecipeHelper.addSmeltingRecipe(aCrushedC, aCrushedSmeltingOutput);
//                    } else if (aNonDirectSmeltingOutput != null) {
//                        RecipeHelper.addSmeltingRecipe(aCrushedC, aNonDirectSmeltingOutput);
//                    }
//                } else {
//                    ItemStack aIngotOrGemStack = (aMat == aMat.mDirectSmelting ? aMat.hasFlag(BGEM) ? aMat.getGem(1) : aMat.getIngot(1) : aMat.hasFlag(BRITTLEG) ? aMat.mDirectSmelting.getGem(1) : aMat.mDirectSmelting.getIngot(1));
//                    ItemStack aBlastStack = aMat.getBlastFurnaceTemp() > 1750 && aMat.mSmeltInto.hasFlag(HINGOT) ? aMat.mSmeltInto.getIngotH(1) : (aMat == aMat.mSmeltInto ? aIngotOrGemStack : aMat.mSmeltInto.getIngot(1));
//                    RecipeAdder.addBlastFurnaceRecipe(aCrushedC, null, null, null, aBlastStack, null, Math.max(aMat.getMass() / 4, 1) * aMat.getBlastFurnaceTemp(), 120, aMat.getBlastFurnaceTemp());
//                }
//            }
//        }
//
//        aMaterialArray = CRUSHEDP.getMats();
//        for (Materials aMat : aMaterialArray) {
//            ItemStack aCrushedP = aMat.getCrushedP(1), aDust = aMat.getDust(1);
//            RecipeHelper.addShapedToolRecipe(aMat.getDust(1), "h  ", "X  ", "   ", 'X', aCrushedP); //TODO BROKEN?
//            Materials aOreByProduct1 = aMat.mByProducts.size() >= 1 ? aMat.mByProducts.get(0) : aMat.mMacerateInto; //TODO simplify?
//            Materials aOreByProduct2 = aMat.mByProducts.size() >= 2 ? aMat.mByProducts.get(1) : aOreByProduct1;
//            RecipeAdder.addHammerRecipe(aCrushedP, aMat.mMacerateInto.getDust(1), 10, 16);
//            RecipeAdder.addPulverizerRecipe(aCrushedP, aDust, aOreByProduct2.getDust(1), 10, false);
//            RecipeAdder.addThermalCentrifugeRecipe(aCrushedP, aMat.mMacerateInto.getCrushedC(1), aOreByProduct2.getDustT(1));
//            if ((aMat.hasFlag(INGOT) || aMat.hasFlag(BGEM)) && (aMat.needsBlastFurnace() || aMat.mDirectSmelting.isBlastFurnaceRequired())) {
//                ItemStack aIngotOrGemStack = (aMat == aMat.mDirectSmelting ? aMat.hasFlag(BGEM) ? aMat.getGem(1) : aMat.getIngot(1) : aMat.hasFlag(BRITTLEG) ? aMat.mDirectSmelting.getGem(1) : aMat.mDirectSmelting.getIngot(1));
//                ItemStack aBlastStack = aMat.getBlastFurnaceTemp() > 1750 && aMat.mSmeltInto.hasFlag(HINGOT) ? aMat.mSmeltInto.getIngotH(1) : (aMat == aMat.mSmeltInto ? aIngotOrGemStack : aMat.mSmeltInto.getIngot(1));
//                RecipeAdder.addBlastFurnaceRecipe(aCrushedP, null, null, null, aBlastStack, null, Math.max(aMat.getMass() / 4, 1) * aMat.getBlastFurnaceTemp(), 120, aMat.getBlastFurnaceTemp());
//            }
//        }
//
//        aMaterialArray = RUBBERTOOLS.getMats();
//        for (Materials aMat : aMaterialArray) {
//            RecipeHelper.addBasicShapedRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, aMat, Materials.Wood), "XX ", "XXS", "XX ", 'X', aMat.getIngot(1), 'S', Materials.Wood.getRod(1));
//            RecipeHelper.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, aMat, Materials.Wood), aMat.getHeadHammer(1), Materials.Wood.getRod(1));
//        }
//
////        aMaterialArray = TOOL.getMats();
////        for (Materials aMat : aMaterialArray) {
////            if (aMat.hasFlag(INGOT) && aMat.hasFlag(PLATE) && !aMat.hasFlag(RUBBERTOOLS) && aMat == aMat.mMacerateInto) {
////                ItemStack aStainlessScrew = Materials.StainlessSteel.getScrew(1), aTitaniumScrew = Materials.Titanium.getScrew(1), aTungstensteelScrew = Materials.TungstenSteel.getScrew(1), aStainlessPlate = Materials.StainlessSteel.getPlate(1), aTitaniumPlate = Materials.Titanium.getPlate(1), aTungstensteelPlate = Materials.TungstenSteel.getPlate(1), aStainlessSmallGear = Materials.StainlessSteel.getGearS(1), aTitaniumSmallGear = Materials.Titanium.getGearS(1), aTungstensteelSmallGear = Materials.TungstenSteel.getGearS(1), aTitaniumSpring = Materials.Titanium.getSpring(1);
////                ItemStack aTempStack, aSteelPlate = Materials.Steel.getPlate(1), aSteelRing = Materials.Steel.getRing(1);
////                ItemStack aDust = aMat.getDust(1), aIngot = aMat.getIngot(1), aPlate = aMat.getPlate(1), aRod = aMat.getRod(1), aBolt = aMat.getBolt(1), aHandle = aMat.mHandleMaterial.getRod(1);
////
////                int tVoltageMultiplier = 8 * (aMat.getBlastFurnaceTemp() >= 2800 ? 64 : 16);
////                ItemStack aDustx2 = Utils.ca(2, aDust), aDustx3 = Utils.ca(3, aDust), aIngotx2 = Utils.ca(2, aIngot), aIngotx3 = Utils.ca(3, aIngot);
////
////                aTempStack = aMat.getHeadHammer(1);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(HARDHAMMER, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                RecipeAdder.addExtruderRecipe(Utils.ca(6, aDust), ItemList.Shape_Extruder_Hammer.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadHammer(1), aMat.getMass() * 6, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(Utils.ca(6, aIngot), ItemList.Shape_Extruder_Hammer.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadHammer(1), aMat.getMass() * 6, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadFile(1);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(FILE, 1, aMat, aMat.mHandleMaterial), "P  ", "P  ", "S  ", 'P', aPlate, 'S', aHandle);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(FILE, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_File.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadFile(1), aMat.getMass() * 2, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_File.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadFile(1), aMat.getMass() * 2, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadAxe(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "PIh", "P  ", "f  ", 'P', aPlate, 'I', aIngot);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(AXE, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                RecipeAdder.addExtruderRecipe(aDustx3, ItemList.Shape_Extruder_Axe.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadAxe(1), aMat.getMass() * 3, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngotx3, ItemList.Shape_Extruder_Axe.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadAxe(1), aMat.getMass() * 3, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadHoe(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "PIh", "f  ", 'P', aPlate, 'I', aIngot);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(HOE, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_Hoe.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadHoe(1), aMat.getMass() * 2, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_Hoe.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadHoe(1), aMat.getMass() * 2, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadPickaxe(1);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(PICKAXE, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "PII", "f h", 'P', aPlate, 'I', aIngot);
////                RecipeAdder.addExtruderRecipe(aDustx3, ItemList.Shape_Extruder_Pickaxe.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadPickaxe(1), aMat.getMass() * 3, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngotx3, ItemList.Shape_Extruder_Pickaxe.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadPickaxe(1), aMat.getMass() * 3, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadSaw(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "PP ", "fh ", 'P', aPlate, 'I', aIngot);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SAW, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_Saw.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadSaw(1), aMat.getMass() * 2, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_Saw.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadSaw(1), aMat.getMass() * 2, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadSword(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, " P ", "fPh", 'P', aPlate, 'I', aIngot);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SWORD, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                RecipeAdder.addExtruderRecipe(aDustx2, ItemList.Shape_Extruder_Sword.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadSword(1), aMat.getMass() * 2, tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngotx2, ItemList.Shape_Extruder_Sword.get(0), aMat == aMat.mSmeltInto ? aTempStack : aMat.mSmeltInto.getHeadSword(1), aMat.getMass() * 2, tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadPlow(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "PP", "PP", "hf", 'P', aPlate);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(PLOW, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////
////                aTempStack = aMat.getHeadScythe(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "PPI", "hf ", 'P', aPlate, 'I', aIngot);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SCYTHE, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////
////                aTempStack = aMat.getHeadShovel(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "fPh", 'P', aPlate, 'I', aIngot);
////                //GT_ModHandler.addShapedToolRecipe(aMat.getHeadUniSpade(1), "fX", 'X', aTempStack); //TODO?
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SHOVEL, 1, aMat, aMat.mHandleMaterial), aTempStack, aHandle);
////                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(UNIVERSALSPADE, 1, aMat, aMat), aTempStack, aBolt, aRod);
////                RecipeAdder.addExtruderRecipe(aDust, ItemList.Shape_Extruder_Shovel.get(0), aMat.mSmeltInto.getHeadShovel(1), aMat.getMass(), tVoltageMultiplier);
////                RecipeAdder.addExtruderRecipe(aIngot, ItemList.Shape_Extruder_Shovel.get(0), aMat.mSmeltInto.getHeadShovel(1), aMat.getMass(), tVoltageMultiplier);
////
////                aTempStack = aMat.getHeadTurbine(1);
////                RecipeAdder.addAssemblerRecipe(Utils.ca(8, aTempStack), Materials.Magnalium.getRod(1), INSTANCE.getToolWithStats(TURBINE_SMALL, 1, aMat, aMat), 160, 100);
////                RecipeAdder.addAssemblerRecipe(Utils.ca(16, aTempStack), Materials.Titanium.getRod(1), INSTANCE.getToolWithStats(TURBINE, 1, aMat, aMat), 320, 400);
////                RecipeAdder.addAssemblerRecipe(Utils.ca(24, aTempStack), Materials.TungstenSteel.getRod(1), INSTANCE.getToolWithStats(TURBINE_LARGE, 1, aMat, aMat), 640, 1600);
////                RecipeAdder.addAssemblerRecipe(Utils.ca(32, aTempStack), Materials.Americium.getRod(1), INSTANCE.getToolWithStats(TURBINE_HUGE, 1, aMat, aMat), 1280, 6400);
////
////                aTempStack = aMat.getHeadBuzzSaw(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "wXh", "X X", "fXx", 'X', aPlate);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, aMat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, aMat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, aMat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
////
////                aTempStack = aMat.getHeadChainsaw(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "SRS", "XhX", "SRS", 'X', aPlate, 'S', aSteelPlate, 'R', aSteelRing);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, aMat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, aMat, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, aMat, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, aMat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, aMat, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, aMat, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, aMat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, aMat, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Sodium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, aMat, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
////
////                aTempStack = aMat.getHeadDrill(1);
////                GT_ModHandler.addShapedToolRecipe(aTempStack, "XSX", "XSX", "ShS", 'X', aPlate, 'S', aSteelPlate);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, aMat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, aMat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, aMat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, aMat, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, aMat, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, aMat, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Sodium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, aMat, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, aMat, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, aMat, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
////
////                aTempStack = aMat.getHeadWrench(1);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, aMat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, aMat, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, aMat, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, aMat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, aMat, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, aMat, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, aMat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, aMat, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemList.Battery_RE_MV_Sodium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, aMat, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemList.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
////
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, aMat, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, aMat, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, aMat, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemList.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemList.Battery_RE_LV_Sodium.get(1));
////
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, aMat, Materials.Titanium, 1600000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemList.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemList.Battery_RE_HV_Lithium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, aMat, Materials.Titanium, 1200000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemList.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemList.Battery_RE_HV_Cadmium.get(1));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, aMat, Materials.Titanium, 800000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemList.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemList.Battery_RE_HV_Sodium.get(1));
////
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadTurbine(1), "fPd", "SPS", " P ", 'P', aPlate, 'S', aBolt);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadWrench(1), "hXW", "XRX", "WXd", 'X', aPlate, 'S', aSteelPlate, 'R', aSteelRing, 'W', Materials.Steel.getScrew(1));
////
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH, 1, aMat, aMat), "IhI", "III", " I ", 'I', aIngot);
////                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(CROWBAR, 1, aMat, aMat), "hDS", "DSD", "SDf", 'S', aRod, 'D', Dyes.blue);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER, 1, aMat, aMat.mHandleMaterial), " fS", " Sh", "W  ", 'S', aRod, 'W', aHandle);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCOOP, 1, aMat, aMat), "SWS", "SSS", "xSh", 'S', aRod, 'W', new ItemStack(Blocks.wool, 1, 32767));
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WIRECUTTER, 1, aMat, aMat), "PfP", "hPd", "STS", 'S', aRod, 'P', aPlate, 'T', aBolt);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BRANCHCUTTER, 1, aMat, aMat), "PfP", "PdP", "STS", 'S', aRod, 'P', aPlate, 'T', aBolt);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(KNIFE, 1, aMat, aMat), "fPh", " S ", 'S', aRod, 'P', aPlate);
////                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUTCHERYKNIFE, 1, aMat, aMat), "PPf", "PP ", "Sh ", 'S', aRod, 'P', aPlate);
////
////                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(PLUNGER, 1, aMat, aMat), "xRR", " SR", "S f", 'S', aRod, 'R', OreDicts.rubber.get(OrePrefixes.plate));
////                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(SOLDERING_IRON_LV, 1, aMat, Materials.Rubber, 100000L, 32L, 1L, -1L), "LBf", "Sd ", "P  ", 'B', aBolt, 'P', OreDicts.rubber.get(OrePrefixes.plate), 'S', Materials.Iron.getRod(1), 'L', ItemList.Battery_RE_LV_Lithium.get(1));
////            } else if (aMat.hasFlag(BGEM)) {
////                ItemStack aGem = aMat.getGem(1);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadAxe(1), "GG ", "G  ", "f  ", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadHoe(1), "GG ", "f  ", "   ", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadPickaxe(1), "GGG", "f  ", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadPlow(1), "GG", "GG", " f", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadSaw(1), "GGf", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadScythe(1), "GGG", " f ", "   ", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadShovel(1), "fG", 'G', aGem);
////                GT_ModHandler.addShapedToolRecipe(aMat.getHeadSword(1), " G", "fG", 'G', aGem);
////            }
////        }
//    }
//}
