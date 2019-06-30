package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.IMaterialTag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.recipe.RecipeHelper;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import static muramasa.gtu.api.data.RecipeMaps.*;
import static muramasa.gtu.api.materials.MaterialTag.*;
import static muramasa.gtu.api.materials.MaterialType.*;
import static muramasa.gtu.common.Data.*;

//TODO EXCLUDED FROM COMPILE

public class MaterialRecipeLoader {

    //TODO register purified dust processing to centrifuged processing to regain lost benefits

    public static int mixedOreYield = Ref.mixedOreYieldsTwoThirdsPureOre ? 2 : 3;

    public static void init() {       
        //ELEMENTAL.getMats().forEach();
//            ItemStack aDataOrb = ItemType.Tool_DataO.RB(1);
//            Behaviour_DataOrb.setDataTitle(aDataOrb, "Elemental-Scan");
//            Behaviour_DataOrb.setDataName(aDataOrb, m.mElement.id());
//            ItemStack aRepOutput = ((m.has(LIQUID) || m.has(GAS)) && !m.has(Dust)) ? m.getCell(1) : m.getDust(1);
//            Fluid aFluid = m.mFluid != null ? m.mFluid : m.mGas;
//            int aMass = m.getMass();
//            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{aRepOutput}, new ItemStack[]{aDataOrb}, ItemType.Tool_DataO.RB(1), null, null, aMass * 8192, 32, 0);
//            GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, aFluid == null ? new ItemStack[]{aRepOutput} : null, new ItemStack[]{aDataOrb}, new FluidStack[]{Materials.UUMatter.getFluid(aMass)}, aFluid == null ? null : new FluidStack[]{new FluidStack(aFluid, 144)}, aMass * 512, 32, 0);

        //LIQUID.getMats().forEach();

        //GAS.getMats().forEach();

        PLASMA.getMats().forEach(m -> {
            ItemStack cell = m.has(LIQUID) ? m.getCell(1) : m.getCellGas(1);
            VACUUM_FREEZING.RB().ii(m.getCellPlasma(1)).io(cell).add(Math.max(m.getMass() * 2, 1), 120);
            PLASMA_FUELS.RB().fi(m.getPlasma(1296)).add(0, 0, Math.max(1024, 1024 * m.getMass()) * 1000); //TODO: 1296 or 1000? To per cell amount or to '9' units?
        });
        
        INGOT_HOT.getMats().forEach(m -> VACUUM_FREEZING.RB().ii(m.getIngotHot(1)).io(m.getIngot(1)).add(Math.max(m.getMass() * 3, 1), 120)); //TODO: Coolant idea?

        SPRING.getMats().forEach(m -> BENDING.RB().ii(m.getRod(1)).io(m.getSpring(1)).add(200, 16));

        PLATE_DENSE.getMats().forEach(m -> BENDING.RB().ii(m.getPlate(9)).io(m.getPlateDense(1)).add(Math.max(m.getMass() * 9, 1), 96));
        //GregTech_API.registerCover(aDenseStack, new GT_RenderedTexture(merial.mIconSet.mTextures[76], merial.mRGBa, false), null);
        
        ROTOR.getMats().forEach(m -> {
            ItemStack plate = m.getPlate(1), ring = m.getRing(1), rotor = m.getRotor(1);
            RecipeHelper.addShaped("rotor_" + m.getId(), rotor, "PhP", "SRf", "PdP", 'P', plate, 'R', ring, 'S', m.getScrew(1));
            //ItemStack plates = plate.copy();
            plate = Utils.ca(4, plate);
            ASSEMBLING.RB().ii(plate, ring).fi(Materials.Lead.getLiquid(48)).io(rotor).add(240, 24);
            ASSEMBLING.RB().ii(plate, ring).fi(Materials.Tin.getLiquid(32)).io(rotor).add(240, 24);
            ASSEMBLING.RB().ii(plate, ring).fi(Materials.SolderingAlloy.getLiquid(16)).io(rotor).add(240, 24);          
        });
        
        /*WIRE_FINE.getMats().forEach(m -> {
            if (!m.has(NOSMASH)) {
                //RecipeAdder.addWiremillRecipe(m.getIngot(1), Utils.copy(m.getWire01(2), aWireF), 100, 4);
                //RecipeAdder.addWiremillRecipe(m.getRod(1), Utils.copy(m.getWire01(1), aWireF), 50, 4);
            }
            else {
              //RecipeHelper.addShapedToolRecipe(aWireF, "Xx ", "   ", "   ", 'X', m.getFoil(1));
            }
        });*/

        GEAR_SMALL.getMats().forEach(m -> {
            ItemStack gearSmall = m.getGearSmall(1);
            RecipeHelper.addShaped("plate_to_small_gear_", gearSmall, "P  ", (m == Materials.Wood/* || m == Materials.WoodSealed*/) ? " s " : " h ", "   ", 'P', m.getPlate(1));
            FLUID_SOLIDIFYING.RB().ii(MoldGearSmall.get(0)).fi(m.getLiquid(144)).io(gearSmall).add(16, 8);        
        });
        
        GEAR.getMats().forEach(m -> {
            ItemStack gear = m.getGear(1);
            if (m.has(PLATE)) RecipeHelper.addShaped("gear_" + m.getId(), gear, "SPS", "PwP", "SPS", 'P', m.getPlate(1), 'S', m.getRod(1));
            if (m.has(LIQUID)) FLUID_SOLIDIFYING.RB().ii(MoldGear.get(0)).fi(m.getLiquid(576)).io(gear).add(128, 8);
            if (m.has(INGOT) && !m.has(NOSMELT)) {
                int voltage = m.getBlastTemp() >= 2800 ? 64 : 16;
                if (m.has(NOSMASH)) {
                    voltage /= 4;
                }
                //int aVoltageMulti = m.has(NOSMASH) ? m.getBlastTemp() >= 2800 ? 16 : 4 : m.getBlastTemp() >= 2800 ? 64 : 16;
                ItemStack gearSmeltInto = m.getSmeltInto().getGear(1);
                EXTRUDING.RB().ii(m.getIngot(4), ShapeGear.get(0)).io(gearSmeltInto).add(Math.max(m.getMass() * 5, 1), 8 * voltage);
                ALLOY_SMELTING.RB().ii(m.getIngot(8), MoldGear.get(0)).io(gearSmeltInto).add(m.getMass() * 10, 2 * voltage);
            }
        });
        
        //Screws depends on bolt, so lets do this for now
        IMaterialTag.getMats(BOLT, SCREW).forEach(m -> {
            ItemStack bolt = m.getBolt(1), screw = m.getScrew(1);
            RecipeHelper.addShaped("bolt_to_screw_" + m.getId(), screw, "fX ", "X  ", "   ", 'X', bolt);
            LATHING.RB().ii(bolt).io(screw).add(Math.max(m.getMass() / 8, 1), 4);  
            bolt = Utils.ca(2, bolt);
            RecipeHelper.addShaped("rod_to_bolt_" + m.getId(), bolt, "s  ", " X ", "   ", 'X', m.getRod(1));
            bolt = Utils.ca(4, bolt);
            CUTTING.RB().ii(m.getRod(1)).io(bolt).add(Math.max(m.getMass() * 2, 1), 4);
            if (!m.has(NOSMELT)) EXTRUDING.RB().ii(m.getIngot(1), ShapeBolt.get(0)).io(m.getSmeltInto().getBolt(8)).add(m.getMass() * 2, m.getBlastTemp() >= 2800 ? 512 : 128);
        });
        
        FOIL.getMats().forEach(m -> {
            ItemStack plate = m.getPlate(1), foil = m.getFoil(1);
            RecipeHelper.addShaped("plate_to_foil_" + m.getId(), foil, "hX ", "   ", "   ", 'X', plate);
            foil.setCount(4);
            BENDING.RB().ii(plate).io(foil).add(Math.max(m.getMass(), 1), 24);
          //GregTech_API.registerCover(aFoilStack, new GT_RenderedTexture(merial.mIconSet.mTextures[70], merial.mRGBa, false), null);
        });
        
        /*BOLT.getMats().forEach(m -> {
            
        });*/
        
        /*SCREW.getMats().forEach(m -> {
        
        });*/
        
        RING.getMats().forEach(m -> {
            ItemStack ring = m.getRing(1);
            if (!m.has(NOSMASH)) {
                RecipeHelper.addShaped("rod_to_ring_" + m.getId(), ring, "h  ", " X ", "   ", 'X', m.getRod(1));
            }
            if (!m.has(NOSMELT)) {
                ring = Utils.ca(4, ring);
                EXTRUDING.RB().ii(m.getIngot(1), ShapeRing.get(0)).io(m.getSmeltInto().getRing(4)).add(m.getMass() * 2, m.getBlastTemp() >= 2800 ? 384 : 96);
            }
        });

        ROD.getMats().forEach(m -> {  
            if (m == Materials.Wood) return; //Rod <-> stick interchangeability?
            ItemStack ingotOrGem = m.has(GEM) ? m.getGem(1) : m.getIngot(1), rod = m.getRod(1);
            LATHING.RB().ii(ingotOrGem).io(rod, m.getMacerateInto().getDustSmall(2)).add(Math.max(m.getMass() * 5, 1), 16);
            if (m.has(INGOT)) {
                RecipeHelper.addShaped("ingot_to_rod_", rod, "f  ", " X ", "   ", 'X', ingotOrGem);
                if (!m.has(NOSMELT)) {
                    int aEU = m.getBlastTemp() >= 2800 ? 384 : 96;
                    EXTRUDING.RB().ii(ingotOrGem, ShapeRod.get(0)).io(m.getSmeltInto().getRod(2)).add(m.getMass() * 2, aEU);
                    //TODO .RB(EXTRUDER).ii(m.getIngot(1), ShapeWire.get(0)).io(m.getSmeltInto().getWire01(2)).add(m.getMass() * 2, aEU);
                }
            }
        });

        //ROD_LONG.getMats().forEach(m -> ());

        BLOCK.getMats().forEach(m -> { 
            ItemStack block = m.getBlock(1), ingotOrGem = m.has(GEM) ? m.getGem(1) : m.getIngot(1);
            //TODO: Leave in compressor? Hardcore mode?
            RecipeHelper.addShapeless("block_compress_" + m.getId(), block, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem);
            ingotOrGem = Utils.ca(9, ingotOrGem);
            RecipeHelper.addShapeless("block_decompress_" + m.getId(), ingotOrGem, block);
            if (m.has(PLATE)) {
                CUTTING.RB().ii(block).io(m.getPlate(9)).add(Math.max(m.getMass() * 10, 1), 30);
            }
            if (m.has(LIQUID)) {
                FLUID_SOLIDIFYING.RB().ii(MoldBlock.get(0)).fi(m.getLiquid(1296)).io(block).add(288, 8);
            }
            COMPRESSING.RB().ii(ingotOrGem).io(block).add(300, 2);
            if (m.has(INGOT) && !m.has(NOSMELT)) {
                int voltage = m.has(NOSMASH) ? m.getBlastTemp() >= 2800 ? 16 : 4 : m.getBlastTemp() >= 2800 ? 64 : 16;
                EXTRUDING.RB().ii(ingotOrGem, ShapeBlock.get(0)).io(block).add(10, 8 * voltage);
                ALLOY_SMELTING.RB().ii(ingotOrGem, MoldBlock.get(0)).io(block).add(5, 4 * voltage);
            }
        });

        PLATE.getMats().forEach(m -> { 
            if (m == Materials.Wood || m == Materials.Glass) return;
            ItemStack plate = m.getPlate(1), ingotOrGem = m.has(GEM) ? m.getGem(1) : m.getIngot(1);
            if (m.has(LIQUID)) {
                FLUID_SOLIDIFYING.RB().ii(MoldPlate.get(0)).fi(m.getLiquid(144)).io(plate).add(32, 8);
            }
            /*if (m.getFuelPower() > 0) {
                RecipeAdder.addFuel(aPlate, null, m.getFuelPower(), m.mFuelType);
            }*/
            if (!m.has(NOSMASH)) {
                RecipeHelper.addShaped("plate_" + m.getId(), plate, "h", "X", "X", 'X', ingotOrGem);
                if (m.has(GRINDABLE)) {
                    RecipeHelper.addShapeless("plate_grind_to_dust_" + m.getId(), m.getDust(1), "m", plate);
                }
                if (m.has(INGOT)) {
                    long duration = Math.max(m.getMass(), 1);
                    BENDING.RB().ii(ingotOrGem).io(plate).add(duration, 24);     
                    ingotOrGem = Utils.ca(3, ingotOrGem);
                    plate = Utils.ca(2, plate);
                    HAMMERING.RB().ii(ingotOrGem).io(plate).add(duration, 16);  
                }
            }
            //TODO: Needs reworking
            if (!m.has(NOSMELT)) {
                if (m.has(INGOT)) {
                    ingotOrGem = Utils.ca(1, ingotOrGem);
                    int eu = m.getBlastTemp() >= 2800 ? 64 : 16;
                    EXTRUDING.RB().ii(ingotOrGem, ShapePlate.get(0)).io(m.getSmeltInto().getPlate(1)).add(m.getMass(), 8 * eu);
                    ingotOrGem = Utils.ca(2, ingotOrGem);
                    ALLOY_SMELTING.RB().ii(ingotOrGem, MoldPlate.get(0)).io(m.getSmeltInto().getPlate(1)).add(m.getMass() * 2, 2 * eu);
                }
                //TODO WUT?
//              if (Prefix.block.isIgnored(m) && m != Materials.GraniteRed && m != Materials.GraniteBlack && m != Materials.Glass && m != Materials.Obsidian && m != Materials.Glowstone && m != Materials.Paper) {
//                GT_ModHandler.addCompressorRecipe(aDustStack, aPlateStack);
//              }
            }
            //GregTech_API.registerCover(aPlate, new GT_RenderedTexture(m.mIconSet.mTextures[71], m.getRGB(), false), null);
        });

        //Handle tiny, small, normal variants of dusts here! NOT IMPURE/PURE!
        DUST.getMats().forEach(m -> {
            ItemStack dust = m.getDust(1), dustSmall = m.getDustSmall(1), dustTiny = m.getDustTiny(1);           
            RecipeHelper.addShapeless("dust_tiny_to_dust_" + m.getId(), dust, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny);
            RecipeHelper.addShapeless("dust_small_to_dust_" + m.getId(), dust, dustSmall, dustSmall, dustSmall, dustSmall);
            //RecipeHelper.addShaped("dust_small_to_dust_" + m.getId(), dust, "XX ", "XX ", "   ", 'X', dustSmall);
            dustTiny = Utils.ca(9, dustTiny);
            RecipeHelper.addShapeless("dust_to_tiny_dust" + m.getId(), dustTiny, dust);
            if (m.getFuelPower() > 0) {
                //RecipeAdder.addFuel(aDust, null, m.mFuelPower, m.mFuelType);
            }
            if (!m.has(INGOT)) return;
            ItemStack ingotSmeltInto = m.getSmeltInto().getIngot(1);
            if (!m.has(NOSMELT)) {               
                ALLOY_SMELTING.RB().ii(dustTiny, MoldIngot.get(0)).io(ingotSmeltInto).add(130, 3);
                dustTiny = Utils.ca(1, dustTiny);
                RecipeHelper.addSmelting(dustTiny, m.getSmeltInto().getNugget(1));             
//              if (m.mStandardMoltenFluid == null && m.has(SMELTF)) {
//                   GT_Mod.gregtechproxy.addAutogeneratedMoltenFluid(m);
//                if (m.mSmeltInto != m && m.mSmeltInto.mStandardMoltenFluid == null) {
//                    GT_Mod.gregtechproxy.addAutogeneratedMoltenFluid(m.mSmeltInto);
//                }
//              }
                ItemStack dustSmeltInto = m.getSmeltInto().getDust(1);
                if (dustSmeltInto != null) {
                    RecipeHelper.addSmelting(dust, dustSmeltInto);
                }
                int voltage = m.has(NOSMASH) ? m.getBlastTemp() >= 2800 ? 16 : 4 : m.getBlastTemp() >= 2800 ? 64 : 16;
                if (m.hasSmeltInto()) {
                    EXTRUDING.RB().ii(dust, ShapeIngot.get(0)).io(ingotSmeltInto).add(10, 4 * voltage);
                }
            }
            ALLOY_SMELTING.RB().ii(dust, MoldIngot.get(0)).io(ingotSmeltInto).add(130, 3);
            //TODO GT_RecipeRegistrator.registerUsagesForMaterials(aIngotStack, Prefix.plate.get(m).toString(), !aNoSmashing);
            if (m.needsBlastFurnace()) {
                long duration = Math.max(m.getMass() / 40, 1) * m.getBlastTemp();
                ItemStack blastStack = m.getBlastTemp() > 1750 && m.getSmeltInto().has(INGOT_HOT) ? m.getSmeltInto().getIngotHot(1) : ingotSmeltInto;
                BLASTING.RB().ii(dust).io(blastStack).add(duration, 120, m.getBlastTemp());
                dustSmall = Utils.ca(4, dustSmall);
                BLASTING.RB().ii(dustSmall).io(blastStack).add(duration, 120, m.getBlastTemp());
                if (!m.has(NOSMELT)) { //TODO WUT?
                    RecipeHelper.removeSmelting(dustTiny);
                    dustTiny.setCount(9);
                    BLASTING.RB().ii(dustTiny).io(blastStack).add(duration, 120, m.getBlastTemp());              
                }
//              (int) Math.max(aMaterial.getMass() / 40L, 1L) * aMaterial.mBlastFurnaceTemp, 120, aMaterial.mBlastFurnaceTemp
            }
            if (!m.needsBlastFurnace()) {
                if (m.getSmeltInto() != null) {
                    //GT_RecipeRegistrator.registerReverseFluidSmelting(aDust, m, Prefix.dust.mMaterialAmount, null);
                    //GT_RecipeRegistrator.registerReverseFluidSmelting(aDustS, m, Prefix.dustSmall.mMaterialAmount, null);
                    //GT_RecipeRegistrator.registerReverseFluidSmelting(aDustT, m, Prefix.dustTiny.mMaterialAmount, null);
                    if (m.getSmeltInto().getArcSmeltInto() != m) {
                        //GT_RecipeRegistrator.registerReverseArcSmelting(aDust, m, Prefix.dust.mMaterialAmount, null, null, null);
                        //GT_RecipeRegistrator.registerReverseArcSmelting(aDustS, m, Prefix.dustSmall.mMaterialAmount, null, null, null);
                        //GT_RecipeRegistrator.registerReverseArcSmelting(aDustT, m, Prefix.dustTiny.mMaterialAmount, null, null, null);
                    }
                }
            }
            if (m.getDirectSmeltInto() != m && !m.has(NOSMELT) && !(m.needsBlastFurnace() || m.getDirectSmeltInto().needsBlastFurnace()) && !m.has(NOBBF)) {
                //TODO requires special handling
                if (m.getDirectSmeltInto().has(INGOT)) { //TODO INGOT check was added to avoid DOES NOT GENERATE: P(INGOT) M(mercury)
                    BASIC_BLASTING.RB().add(new ItemStack[]{m.getDust(2)}, new ItemStack[]{}, 2, 2400);
                }
                //RecipeAdder.addPrimitiveBlastRecipe(Utils.ca(2, aDust), null, 2, m.mDirectSmelting.getIngot(aMixedOreYieldCount), null, 2400);
            }
//            if (m.has(ELECSEPI)) {
//                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Iron.getDustS(1), Materials.Iron.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
//            } else if (m.has(ELECSEPG)) {
//                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Gold.getDustS(1), Materials.Gold.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
//            } else if (m.has(ELECSEPN)) {
//                RecipeAdder.addElectromagneticSeparatorRecipe(aDustP, aDust, Materials.Neodymium.getDustS(1), Materials.Neodymium.getNugget(1), new int[]{10000, 4000, 2000}, 400, 24);
//            }
        });

//        IMaterialFlag.getMats(ELEC, CENT).forEach(m -> {
//            int inputCount = 0;
//            int inputCellCount = 0;
//
//            ArrayList<ItemStack> outputs = new ArrayList<>();
//            FluidStack firstFluid = null; //The first FLUID MatStack (LIQUID/GAS & !Dust tag combo) uses the Electrolyzers fluid input tack. The preceding are cells.
//
//            Material process;
//            for (MaterialStack stack : m.getProcessInto()) {
//                process = stack.m;
//                if ((process.has(LIQUID) || process.has(GAS)) && !process.has(DUST)) {
//                    if (firstFluid == null) {
//                        if (process.getLiquid() != null) { //If a Material has mFluid & mGas, Prioritise mFluid.
//                            firstFluid = process.getLiquid(stack.s * 1000);
//                        } else {
//                            firstFluid = process.getGas(stack.s * 1000);
//                        }
//                    } else {
//                        outputs.add(process.has(LIQUID) ? process.getCell(stack.s) : process.getCellGas(stack.s));
//                        inputCellCount += stack.s;
//                    }
//                } else {
//                    outputs.add(process.getDust(stack.s));
//                }
//                inputCount += stack.s;
//            }
//            inputCount = Math.min(inputCount, 64); //This should not happen. This means process total is over 64 and the recipe should be adjusted
//            if (outputs.size() > 0) {
//                ItemStack input;
//                if (m.has(LIQUID) && !m.has(DUST)){
//                    input = m.getCell(inputCount);
//                } else if (m.has(GAS) && !m.has(DUST)) {
//                    input = m.getCellGas(inputCount);
//                } else {
//                    input = m.getDust(inputCount);
//                }
//                ItemStack inputCell = inputCellCount > 0 ? CellTin.get(inputCellCount) : ItemStack.EMPTY;
//                if (m.has(ELEC)) {
//                    if (Utils.areItemsValid(input, inputCell)) {
//                        System.out.println("oh no");
//                    }
//                    ELECTROLYZING.RB().ii(input, inputCell).fi(firstFluid).io(outputs.toArray(new ItemStack[0])).add(Math.max(1, Math.abs(m.getProtons() * 2 * inputCellCount)), Math.min(4, outputs.size()) * 30);
//                    //RecipeAdder.addElectrolyzerRecipe(input, inputCell, null, firstFluid, outputs.size() < 1 ? null : outputs.get(0), outputs.size() < 2 ? null : outputs.get(1), outputs.size() < 3 ? null : outputs.get(2), outputs.size() < 4 ? null : outputs.get(3), outputs.size() < 5 ? null : outputs.get(4), outputs.size() < 6 ? null : outputs.get(5), null, Math.max(1, Math.abs(m.getProtons() * 2 * inputCellCount)), Math.min(4, outputs.size()) * 30);
//                } else if (m.has(CENT)) {
//                    CENTRIFUGING.RB().ii(input, inputCell).fi(firstFluid).io(outputs.toArray(new ItemStack[0])).add(Math.max(1, Math.abs(m.getMass() * 4 * inputCellCount)), Math.min(4, outputs.size()) * 5);
//                    //RecipeAdder.addCentrifugeRecipe(input, inputCell, null, firstFluid, outputs.size() < 1 ? null : outputs.get(0), outputs.size() < 2 ? null : outputs.get(1), outputs.size() < 3 ? null : outputs.get(2), outputs.size() < 4 ? null : outputs.get(3), outputs.size() < 5 ? null : outputs.get(4), outputs.size() < 6 ? null : outputs.get(5), null, Math.max(1, Math.abs(m.getMass() * 4 * inputCellCount)), Math.min(4, outputs.size()) * 5);
//                }
//            }
//        });

        NUGGET.getMats().forEach(m -> {
            if (m.has(LIQUID)) {
                FLUID_SOLIDIFYING.RB().ii(MoldNugget.get(0)).fi(m.getLiquid(16)).io(m.getNugget(1)).add(16, 4);
            }
            ALLOY_SMELTING.RB().ii(m.getNugget(9), MoldIngot.get(0)).io(m.getSmeltInto().getIngot(1)).add(200, 2);
            if (!m.has(NOSMELT)) {
                ALLOY_SMELTING.RB().ii(m.getIngot(1), MoldNugget.get(0)).io(m.getNugget(9)).add(100, 1);
            }
        });

        INGOT.getMats().forEach(m -> {
            ItemStack ingot = m.getIngot(1), dust = m.getDust(1);
            RecipeHelper.addShapeless("nugget_to_ingot_" + m.getId(), ingot, dust, dust, dust, dust, dust, dust, dust, dust, dust);
            if (m.has(GRINDABLE)) {
                RecipeHelper.addShapeless("ingot_to_dust" + m.getId(), dust, "m", ingot);
            }
            if (m.getFuelPower() > 0) {
                //RecipeAdder.addFuel(aIngot, null, m.getFuelPower(), m.mFuelType);
            }
            if (m.has(LIQUID)) {
                FLUID_SOLIDIFYING.RB().ii(MoldIngot.get(0)).fi(m.getLiquid(144)).io(ingot).add(32, 8);
            }
            //GT_RecipeRegistrator.registerReverseFluidSmelting(aIngot, m, Prefix.INGOT.mMaterialAmount, null);
            //GT_RecipeRegistrator.registerReverseMacerating(aIngot, m, Prefix.INGOT.mMaterialAmount, null, null, null, false);
            //GT_RecipeRegistrator.registerReverseFluidSmelting(aNugget, m, Prefix.INGOT.mMaterialAmount, null);
            //GT_RecipeRegistrator.registerReverseMacerating(aNugget, m, Prefix.INGOT.mMaterialAmount, null, null, null, false);
            if (m.getSmeltInto().getArcSmeltInto() != m) {
                //GT_RecipeRegistrator.registerReverseArcSmelting(aIngot, m, Prefix.INGOT.mMaterialAmount, null, null, null);
            }
            ItemStack macerateInto = m.getMacerateInto().getDust(1);
            if (macerateInto != null && (m.needsBlastFurnace() || m.has(NOSMELT))) {
                RecipeHelper.removeSmelting(macerateInto);
            }
        });

        GEM.getMats().forEach(m -> {
            ItemStack gem = m.getGem(1), block = m.getBlock(1), dust = m.getDust(1);            
            if (m.has(CRYSTALLIZE)) {
                AUTOCLAVING.RB().ii(dust).fi(Materials.Water.getLiquid(200)).io(gem).chances(70).add(2000, 24);
                AUTOCLAVING.RB().ii(dust).fi(Materials.DistilledWater.getLiquid(200)).io(gem).add(1500, 24);
            }
            if (m.has(BLOCK)) {
                gem = Utils.ca(9, gem);
                COMPRESSING.RB().ii(gem).io(block).add(300, 2);
                HAMMERING.RB().ii(block).io(gem).add(100, 24);
            }
        });

        GEM_VARIANTS.getMats().forEach(m -> {
            if (m.isTransparent()) {
                LATHING.RB().ii(m.getPlate(1)).io(m.getLens(1), m.getDustSmall(1)).add(Math.max(m.getMass() / 2, 1), 480);
                //GregTech_API.registerCover(aLensStack, new GT_MultiTexture(Textures.BlockIcons.MACHINE_CASINGS[2][0], new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_LENS, merial.mRGBa, false)), new GT_Cover_Lens(merial.mColor.mIndex));
            }
            if (m.getFuelPower() > 0) {
                //RecipeAdder.addFuel(aGem, null, m.getFuelPower() * 2, m.mFuelType);
                //RecipeAdder.addFuel(aChipped, null, m.getFuelPower() / 2, m.mFuelType);
                //RecipeAdder.addFuel(aFlawed, null, m.getFuelPower(), m.mFuelType);
                //RecipeAdder.addFuel(aFlawless, null, m.getFuelPower() * 4, m.mFuelType);
                //RecipeAdder.addFuel(aExquisite, null, m.getFuelPower() * 8, m.mFuelType);
            }
            if (m.has(NOSMASH)) {
                HAMMERING.RB().ii(m.getGem(1)).io(m.getGemFlawed(2)).add(64, 16);
            }
            /*if (m.has(RODL)) {
                .RB(LATHE).ii(m.getGemFlawless(1)).io(m.getRod(1), m.getDust(1)).add(m.getMass(), 16);
            }*/
            LATHING.RB().ii(m.getGemExquisite(1)).io(m.getLens(1), m.getDust(2)).add(m.getMass(), 24);
            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aGemStack), "h  ", "X  ", "   ", 'X', aFlawlessStack);
            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aChippedStack), "h  ", "X  ", "   ", 'X', aFlawedStack);
            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aFlawedStack), "h  ", "X  ", "   ", 'X', aGemStack);
            //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aFlawlessStack), "h  ", "X  ", "   ", 'X', aExquisiteStack);
            if (m.has(GRINDABLE)) {
                //GT_ModHandler.addShapedToolRecipe(aDustStack, "X  ", "m  ", "   ", 'X', aGemStack);
                //GT_ModHandler.addShapedToolRecipe(aSDustStack, "X  ", "m   ", "   ", 'X', aChippedStack);
                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aSDustStack), "X  ", "m  ", "   ", 'X', aFlawedStack);
                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(2, aDustStack), "X  ", "m  ", "   ", 'X', aFlawlessStack);
                //GT_ModHandler.addShapedToolRecipe(GT_Utility.ca(4, aDustStack), "X  ", "m  ", "   ", 'X', aExquisiteStack);
            }
            HAMMERING.RB().ii(m.getGemFlawed(1)).io(m.getGemChipped(2)).add(64, 16);
            HAMMERING.RB().ii(m.getGemFlawless(1)).io(m.getGem(2)).add(64, 16);
            //TODO NPE GT_RecipeRegistrator.registerUsagesForMaterials(aGemStack, aPlateStack.toString(), !aNoSmashing);
        });

        for (Material m : CRUSHED.getMats()) {
            if (m.hasByProducts()) {
                ArrayList<ItemStack> dusts = new ArrayList<>(m.getByProducts().size());
                m.getByProducts().forEach(p -> dusts.add(p.getDust(1)));
                ORE_BY_PRODUCTS.RB().ii(m.getOre(1)).io(dusts.toArray(new ItemStack[0])).add();
            }

            boolean aNeedsBlastFurnace = m.needsBlastFurnace() || m.getDirectSmeltInto().needsBlastFurnace();
            int aMultiplier = /*aIsRich ? 2 : */1; //TODO implement in some way, but for now support is coded in
            ItemStack ore = m.getOre(1), crushed = m.getCrushed(1), dust = m.getDust(1), stoneDust = Materials.Stone.getDust(1);

            //RecipeHelper.addShapedToolRecipe(m.getDustIP(1), "h  ", "X  ", "   ", 'X', crushed);

            //TODO better way to do this
            Material aOreByProduct1 = m.getByProducts().size() >= 1 ? m.getByProducts().get(0) : m.getMacerateInto();
            Material aOreByProduct2 = m.getByProducts().size() >= 2 ? m.getByProducts().get(1) : aOreByProduct1;

            PULVERIZING.RB().ii(ore).io(Utils.ca((m.getOreMulti() * aMultiplier) * 2, crushed), m.getByProducts().size() > 0 ? m.getByProducts().get(0).getDust(1) : dust, stoneDust).chances(100, 10 * aMultiplier * m.getByProductMulti(), 50).add(400, 2);
            PULVERIZING.RB().ii(crushed).io(m.getMacerateInto().getDustImpure(1), aOreByProduct1.getDust(1)).chances(100, 10).add(400, 2);
            HAMMERING.RB().ii(ore).io(m.has(BRITTLEG) ? m.getGem(1) : crushed).add(16, 10);
            HAMMERING.RB().ii(crushed).io(m.getDustImpure(1)).add(10, 16);
            if (m.has(GEM)) { //Gem Specific Recipes
                ItemStack gem = m.hasDirectSmeltInto() ? m.getDirectSmeltInto().getGem(1) : m.getGem(1);
                RecipeHelper.addSmelting(ore, Utils.ca(aMultiplier * m.getSmeltingMulti(), gem));
                if (m.has(GEM_VARIANTS)) {
                    SIFTING.RB().ii(crushed).io(m.getGemExquisite(1), m.getGemFlawless(1), gem, m.getGemFlawed(1), m.getGemChipped(1), dust).chances(3, 12, 45, 14, 28, 35).add(800, 16);
                } else {
                    SIFTING.RB().ii(crushed).io(gem, gem, gem, gem, gem, dust).chances(1, 4, 15, 20, 40, 50).add(800, 16);
                }
            } else if (m.has(INGOT)) { //Solid Specific Recipes
                ItemStack INGOT = m.hasDirectSmeltInto() ? m.getDirectSmeltInto().getIngot(1) : m.getIngot(1);
                ItemStack aNonDirectSmeltingOutput = Ref.mixedOreYieldsTwoThirdsPureOre ? m.getNugget(6) : m.getDirectSmeltInto().getIngot(1);
                if (m == m.getDirectSmeltInto()) {
                    ItemStack aCrushedSmeltingOutput = m.getNugget(10);
                    RecipeHelper.addSmelting(crushed, aCrushedSmeltingOutput);
                    RecipeHelper.addSmelting(crushed, aCrushedSmeltingOutput);
                } else if (aNonDirectSmeltingOutput != null) {
                    RecipeHelper.addSmelting(crushed, aNonDirectSmeltingOutput);
                    RecipeHelper.addSmelting(crushed, aNonDirectSmeltingOutput);
                }
                if (aNeedsBlastFurnace) {
                    ItemStack aIngotSmeltInto = m == m.getSmeltInto() ? INGOT : m.getSmeltInto().getIngot(1);
                    ItemStack blastOut = m.getBlastTemp() > 1750 && m.getSmeltInto().has(INGOT_HOT) ? m.getSmeltInto().getIngotHot(1) : aIngotSmeltInto;
                    long aBlastDuration = Math.max(m.getMass() / 4, 1) * m.getBlastTemp();
                    BLASTING.RB().ii(crushed).io(blastOut).add(aBlastDuration, 120, m.getBlastTemp());
                    BLASTING.RB().ii(m.getCrushedPurified(1)).io(blastOut).add(aBlastDuration, 120, m.getBlastTemp());
                    BLASTING.RB().ii(m.getCrushedCentrifuged(1)).io(blastOut).add(aBlastDuration, 120, m.getBlastTemp());
                    BLASTING.RB().ii(m.getDustPure(1)).io(blastOut).add(aBlastDuration, 120, m.getBlastTemp());
                    BLASTING.RB().ii(m.getDustImpure(1)).io(blastOut).add(aBlastDuration, 120, m.getBlastTemp());
                }
                if (m.has(CALCITE3X)) {
                    ItemStack ingotMulti = Utils.mul(aMultiplier * 3 * m.getSmeltingMulti(), INGOT);
                    ItemStack darkAsh = Materials.DarkAsh.getDustSmall(1);
                    BLASTING.RB().ii(ore, Materials.Calcite.getDust(aMultiplier)).io(ingotMulti, darkAsh).add(INGOT.getCount() * 500, 120, 1500);
                    BLASTING.RB().ii(ore, Materials.Quicklime.getDust(aMultiplier)).io(ingotMulti, darkAsh).add(INGOT.getCount() * 500, 120, 1500);
                } else if (m.has(CALCITE2X)) {
                    ItemStack darkAsh = Materials.DarkAsh.getDustSmall(1);
                    BLASTING.RB().ii(ore, Materials.Calcite.getDust(aMultiplier)).io(Utils.mul(aMultiplier * mixedOreYield * m.getSmeltingMulti(), INGOT), darkAsh).add( INGOT.getCount() * 500, 120, 1500);
                    BLASTING.RB().ii(ore, Materials.Quicklime.getDustTiny(aMultiplier * 3)).io(Utils.mul(aMultiplier * 3 * m.getSmeltingMulti(), INGOT), darkAsh).add(INGOT.getCount() * 500, 120, 1500);
                }
                RecipeHelper.addSmelting(dust, INGOT);
                RecipeHelper.addSmelting(ore, Utils.ca(aMultiplier * m.getSmeltingMulti(), INGOT));
            }
            if (m.has(CRUSHED_CENTRIFUGED)) {
                THERMAL_CENTRIFUGING.RB().ii(crushed).io(m.getCrushedCentrifuged(1), aOreByProduct2.getDustTiny(1), stoneDust).add(500, 48);
            }
            if (m.has(CRUSHED_PURIFIED)) {
                ORE_WASHING.RB().ii(crushed).fi(Materials.Water.getLiquid(1000)).io(m.getCrushedPurified(1), aOreByProduct1.getDustTiny(1), stoneDust).add(500, 16);
            }
            if (m.has(WASHM)) {
                CHEMICAL_BATHING.RB().ii(crushed).fi(Materials.Mercury.getLiquid(1000)).io(m.getCrushedPurified(1), m.getMacerateInto().getDust(1), stoneDust).chances(100, 70, 40).add(800, 8);
            }
            if (m.has(WASHS)) {
                CHEMICAL_BATHING.RB().ii(crushed).fi(Materials.SodiumPersulfate.getLiquid(1000)).io(m.getCrushedPurified(1), m.getMacerateInto().getDust(1), stoneDust).chances(100, 70, 40).add(800, 8);
            }
        }

        for (Material m : CRUSHED_CENTRIFUGED.getMats()) {
            ItemStack aCrushedC = m.getCrushedCentrifuged(1);
            //RecipeHelper.addShapedToolRecipe(m.getDust(1), "h  ", "X  ", "   ", 'X', aCrushedC);
            HAMMERING.RB().ii(aCrushedC).io(m.getMacerateInto().getDust(1)).add(10, 16);
            //TODO simplify
            Material aOreByProduct1 = m.getByProducts().size() >= 1 ? m.getByProducts().get(0) : m.getMacerateInto();
            Material aOreByProduct2 = m.getByProducts().size() >= 2 ? m.getByProducts().get(1) : aOreByProduct1;
            Material aOreByProduct3 = m.getByProducts().size() >= 3 ? m.getByProducts().get(2) : aOreByProduct2;
            PULVERIZING.RB().ii(aCrushedC).io(m.getMacerateInto().getDust(1), aOreByProduct3.getDust(1)).chances(100, 10).add(400, 2);
            if (m.has(INGOT)) {
                if (!(m.needsBlastFurnace() || m.getDirectSmeltInto().needsBlastFurnace())) {
                    ItemStack aNonDirectSmeltingOutput = Ref.mixedOreYieldsTwoThirdsPureOre ? m.getNugget(6) : m.getDirectSmeltInto().getIngot(1);
                    if (!m.hasDirectSmeltInto()) {
                        ItemStack aCrushedSmeltingOutput = m.getNugget(10);
                        RecipeHelper.addSmelting(aCrushedC, aCrushedSmeltingOutput);
                    } else if (aNonDirectSmeltingOutput != null) {
                        RecipeHelper.addSmelting(aCrushedC, aNonDirectSmeltingOutput);
                    }
                } else {
                    ItemStack INGOT = m.hasDirectSmeltInto() ? m.getDirectSmeltInto().getIngot(1) : m.getIngot(1);
                    ItemStack blastOut = m.getBlastTemp() > 1750 && m.getSmeltInto().has(INGOT_HOT) ? m.getSmeltInto().getIngotHot(1) : (m == m.getSmeltInto() ? INGOT : m.getSmeltInto().getIngot(1));
                    BLASTING.RB().ii(aCrushedC).io(blastOut).add(Math.max(m.getMass() / 4, 1) * m.getBlastTemp(), 120, m.getBlastTemp());
                }
            }
        }

        for (Material m : CRUSHED_PURIFIED.getMats()) {
            ItemStack crushed = m.getCrushedPurified(1), dust = m.getDust(1);
            //RecipeHelper.addShapedToolRecipe(m.getDustPure(1), "h  ", "X  ", "   ", 'X', crushed); //TODO BROKEN?
            Material aOreByProduct1 = m.getByProducts().size() >= 1 ? m.getByProducts().get(0) : m.getMacerateInto(); //TODO simplify?
            Material aOreByProduct2 = m.getByProducts().size() >= 2 ? m.getByProducts().get(1) : aOreByProduct1;
            HAMMERING.RB().ii(crushed).io(m.getMacerateInto().getDustPure(1)).add(10, 16);
            PULVERIZING.RB().ii(crushed).io(m.getDustPure(1), aOreByProduct2.getDust(1)).chances(100, 10).add(400, 2);
            THERMAL_CENTRIFUGING.RB().ii(crushed).io(m.getMacerateInto().getCrushedCentrifuged(1), aOreByProduct2.getDustTiny(1)).add(500, 48);
            if ((m.has(INGOT)) && (m.needsBlastFurnace() || m.getDirectSmeltInto().needsBlastFurnace())) {
                ItemStack INGOT = m.hasDirectSmeltInto() ? m.getDirectSmeltInto().getIngot(1) : m.getIngot(1);
                ItemStack blastOut = m.getBlastTemp() > 1750 && m.getSmeltInto().has(INGOT_HOT) ? m.getSmeltInto().getIngotHot(1) : (m == m.getSmeltInto() ? INGOT : m.getSmeltInto().getIngot(1));
                BLASTING.RB().ii(crushed).io(blastOut).add(Math.max(m.getMass() / 4, 1) * m.getBlastTemp(), 120, m.getBlastTemp());
            }
        }

        for (Material m : RUBBERTOOLS.getMats()) {
//            RecipeHelper.addBasicShapedRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, m, Materials.Wood), "XX ", "XXS", "XX ", 'X', m.getIngot(1), 'S', Materials.Wood.getRod(1));
//            RecipeHelper.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, m, Materials.Wood), m.getHeadHammer(1), Materials.Wood.getRod(1));
        }

        for (Material m : TOOLS.getMats()) {

            if (!m.has(INGOT)) continue; //TODO temp
            RecipeHelper.addShaped("wrench_" + m.getId(), ToolType.WRENCH.get(m), "IhI", "III", " I ", 'I', m.getIngot(1));
            RecipeHelper.addShaped("hammer_" + m.getId(), ToolType.HAMMER.get(m, Materials.Wood), "II ", "IIS", "II ", 'I', m.getIngot(1), 'S', "stickWood");
            RecipeHelper.addShaped("sword_" + m.getId(), ToolType.SWORD.get(m, Materials.Wood), " P ", "fPh", " S ", 'P', m.getPlate(1), 'S', "stickWood");
            
            /*
            if (m.has(INGOT) && m.has(Plate) && !m.has(RUBBERTOOLS) && m == m.mMacerateInto) {
                ItemStack aStainlessScrew = Materials.StainlessSteel.getScrew(1), aTitaniumScrew = Materials.Titanium.getScrew(1), aTungstensteelScrew = Materials.TungstenSteel.getScrew(1), aStainlessPlate = Materials.StainlessSteel.getPlate(1), aTitaniumPlate = Materials.Titanium.getPlate(1), aTungstensteelPlate = Materials.TungstenSteel.getPlate(1), aStainlessSmallGear = Materials.StainlessSteel.getGearS(1), aTitaniumSmallGear = Materials.Titanium.getGearS(1), aTungstensteelSmallGear = Materials.TungstenSteel.getGearS(1), aTitaniumSpring = Materials.Titanium.getSpring(1);
                ItemStack aTempStack, aSteelPlate = Materials.Steel.getPlate(1), aSteelRing = Materials.Steel.getRing(1);
                ItemStack aDust = m.getDust(1), aIngot = m.getIngot(1), aPlate = m.getPlate(1), aRod = m.getRod(1), aBolt = m.getBolt(1), aHandle = m.mHandleMaterial.getRod(1);

                int tVoltageMultiplier = 8 * (m.getBlastTemp() >= 2800 ? 64 : 16);
                ItemStack aDustx2 = Utils.ca(2, aDust), aDustx3 = Utils.ca(3, aDust), aIngotx2 = Utils.ca(2, aIngot), aIngotx3 = Utils.ca(3, aIngot);

                aTempStack = m.getHeadHammer(1);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(HARDHAMMER, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                RecipeAdder.addExtruderRecipe(Utils.ca(6, aDust), ItemType.Shape_Extruder_Hammer.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadHammer(1), m.getMass() * 6, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(Utils.ca(6, aIngot), ItemType.Shape_Extruder_Hammer.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadHammer(1), m.getMass() * 6, tVoltageMultiplier);

                aTempStack = m.getHeadFile(1);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(FILE, 1, m, m.mHandleMaterial), "P  ", "P  ", "S  ", 'P', aPlate, 'S', aHandle);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(FILE, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                RecipeAdder.addExtruderRecipe(aDustx2, ItemType.Shape_Extruder_File.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadFile(1), m.getMass() * 2, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngotx2, ItemType.Shape_Extruder_File.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadFile(1), m.getMass() * 2, tVoltageMultiplier);

                aTempStack = m.getHeadAxe(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "PIh", "P  ", "f  ", 'P', aPlate, 'I', aIngot);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(AXE, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                RecipeAdder.addExtruderRecipe(aDustx3, ItemType.Shape_Extruder_Axe.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadAxe(1), m.getMass() * 3, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngotx3, ItemType.Shape_Extruder_Axe.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadAxe(1), m.getMass() * 3, tVoltageMultiplier);

                aTempStack = m.getHeadHoe(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "PIh", "f  ", 'P', aPlate, 'I', aIngot);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(HOE, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                RecipeAdder.addExtruderRecipe(aDustx2, ItemType.Shape_Extruder_Hoe.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadHoe(1), m.getMass() * 2, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngotx2, ItemType.Shape_Extruder_Hoe.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadHoe(1), m.getMass() * 2, tVoltageMultiplier);

                aTempStack = m.getHeadPickaxe(1);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(PICKAXE, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "PII", "f h", 'P', aPlate, 'I', aIngot);
                RecipeAdder.addExtruderRecipe(aDustx3, ItemType.Shape_Extruder_Pickaxe.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadPickaxe(1), m.getMass() * 3, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngotx3, ItemType.Shape_Extruder_Pickaxe.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadPickaxe(1), m.getMass() * 3, tVoltageMultiplier);

                aTempStack = m.getHeadSaw(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "PP ", "fh ", 'P', aPlate, 'I', aIngot);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SAW, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                RecipeAdder.addExtruderRecipe(aDustx2, ItemType.Shape_Extruder_Saw.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadSaw(1), m.getMass() * 2, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngotx2, ItemType.Shape_Extruder_Saw.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadSaw(1), m.getMass() * 2, tVoltageMultiplier);

                aTempStack = m.getHeadSword(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, " P ", "fPh", 'P', aPlate, 'I', aIngot);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SWORD, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                RecipeAdder.addExtruderRecipe(aDustx2, ItemType.Shape_Extruder_Sword.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadSword(1), m.getMass() * 2, tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngotx2, ItemType.Shape_Extruder_Sword.get(0), m == m.mSmeltInto ? aTempStack : m.mSmeltInto.getHeadSword(1), m.getMass() * 2, tVoltageMultiplier);

                aTempStack = m.getHeadPlow(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "PP", "PP", "hf", 'P', aPlate);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(PLOW, 1, m, m.mHandleMaterial), aTempStack, aHandle);

                aTempStack = m.getHeadScythe(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "PPI", "hf ", 'P', aPlate, 'I', aIngot);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SCYTHE, 1, m, m.mHandleMaterial), aTempStack, aHandle);

                aTempStack = m.getHeadShovel(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "fPh", 'P', aPlate, 'I', aIngot);
                //GT_ModHandler.addShapedToolRecipe(m.getHeadUniSpade(1), "fX", 'X', aTempStack); //TODO?
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SHOVEL, 1, m, m.mHandleMaterial), aTempStack, aHandle);
                GT_ModHandler.addBasicShapelessRecipe(INSTANCE.getToolWithStats(UNIVERSALSPADE, 1, m, m), aTempStack, aBolt, aRod);
                RecipeAdder.addExtruderRecipe(aDust, ItemType.Shape_Extruder_Shovel.get(0), m.mSmeltInto.getHeadShovel(1), m.getMass(), tVoltageMultiplier);
                RecipeAdder.addExtruderRecipe(aIngot, ItemType.Shape_Extruder_Shovel.get(0), m.mSmeltInto.getHeadShovel(1), m.getMass(), tVoltageMultiplier);

                aTempStack = m.getHeadTurbine(1);
                RecipeAdder.addAssemblerRecipe(Utils.ca(8, aTempStack), Materials.Magnalium.getRod(1), INSTANCE.getToolWithStats(TURBINE_SMALL, 1, m, m), 160, 100);
                RecipeAdder.addAssemblerRecipe(Utils.ca(16, aTempStack), Materials.Titanium.getRod(1), INSTANCE.getToolWithStats(TURBINE, 1, m, m), 320, 400);
                RecipeAdder.addAssemblerRecipe(Utils.ca(24, aTempStack), Materials.TungstenSteel.getRod(1), INSTANCE.getToolWithStats(TURBINE_LARGE, 1, m, m), 640, 1600);
                RecipeAdder.addAssemblerRecipe(Utils.ca(32, aTempStack), Materials.Americium.getRod(1), INSTANCE.getToolWithStats(TURBINE_HUGE, 1, m, m), 1280, 6400);

                aTempStack = m.getHeadBuzzSaw(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "wXh", "X X", "fXx", 'X', aPlate);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, m, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, m, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUZZSAW, 1, m, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "PBM", "dXG", "SGP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Sodium.get(1));

                aTempStack = m.getHeadChainsaw(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "SRS", "XhX", "SRS", 'X', aPlate, 'S', aSteelPlate, 'R', aSteelRing);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, m, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, m, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, m, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, m, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, m, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, m, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_LV, 1, m, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Sodium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_MV, 1, m, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Sodium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(CHAINSAW_HV, 1, m, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Sodium.get(1));

                aTempStack = m.getHeadDrill(1);
                GT_ModHandler.addShapedToolRecipe(aTempStack, "XSX", "XSX", "ShS", 'X', aPlate, 'S', aSteelPlate);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, m, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, m, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_LV, 1, m, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Sodium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, m, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, m, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_MV, 1, m, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Sodium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, m, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, m, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(DRILL_HV, 1, m, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Sodium.get(1));

                aTempStack = m.getHeadWrench(1);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, m, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, m, Materials.Titanium, 400000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, m, Materials.TungstenSteel, 1600000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, m, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, m, Materials.Titanium, 300000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, m, Materials.TungstenSteel, 1200000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_LV, 1, m, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Sodium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_MV, 1, m, Materials.Titanium, 200000L, 128L, 2L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_MV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'G', aTitaniumSmallGear, 'B', ItemType.Battery_RE_MV_Sodium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH_HV, 1, m, Materials.TungstenSteel, 800000L, 512L, 3L, -1L), "SXd", "GMG", "PBP", 'X', aTempStack, 'M', ItemType.Electric_Motor_HV.get(1), 'S', aTungstensteelScrew, 'P', aTungstensteelPlate, 'G', aTungstensteelSmallGear, 'B', ItemType.Battery_RE_HV_Sodium.get(1));

                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, m, Materials.StainlessSteel, 100000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, m, Materials.StainlessSteel, 75000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER_LV, 1, m, Materials.StainlessSteel, 50000L, 32L, 1L, -1L), "PdX", "MGS", "GBP", 'X', aRod, 'M', ItemType.Electric_Motor_LV.get(1), 'S', aStainlessScrew, 'P', aStainlessPlate, 'G', aStainlessSmallGear, 'B', ItemType.Battery_RE_LV_Sodium.get(1));

                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, m, Materials.Titanium, 1600000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemType.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemType.Battery_RE_HV_Lithium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, m, Materials.Titanium, 1200000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemType.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemType.Battery_RE_HV_Cadmium.get(1));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(JACKHAMMER, 1, m, Materials.Titanium, 800000L, 512L, 3L, -1L), "SXd", "PRP", "MPB", 'X', aRod, 'M', ItemType.Electric_Piston_HV.get(1), 'S', aTitaniumScrew, 'P', aTitaniumPlate, 'R', aTitaniumSpring, 'B', ItemType.Battery_RE_HV_Sodium.get(1));

                GT_ModHandler.addShapedToolRecipe(m.getHeadTurbine(1), "fPd", "SPS", " P ", 'P', aPlate, 'S', aBolt);
                GT_ModHandler.addShapedToolRecipe(m.getHeadWrench(1), "hXW", "XRX", "WXd", 'X', aPlate, 'S', aSteelPlate, 'R', aSteelRing, 'W', Materials.Steel.getScrew(1));

                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WRENCH, 1, m, m), "IhI", "III", " I ", 'I', aIngot);
                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(CROWBAR, 1, m, m), "hDS", "DSD", "SDf", 'S', aRod, 'D', Dyes.blue);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCREWDRIVER, 1, m, m.mHandleMaterial), " fS", " Sh", "W  ", 'S', aRod, 'W', aHandle);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(SCOOP, 1, m, m), "SWS", "SSS", "xSh", 'S', aRod, 'W', new ItemStack(Blocks.wool, 1, 32767));
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(WIRECUTTER, 1, m, m), "PfP", "hPd", "STS", 'S', aRod, 'P', aPlate, 'T', aBolt);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BRANCHCUTTER, 1, m, m), "PfP", "PdP", "STS", 'S', aRod, 'P', aPlate, 'T', aBolt);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(KNIFE, 1, m, m), "fPh", " S ", 'S', aRod, 'P', aPlate);
                GT_ModHandler.addBasicShapedRecipe(INSTANCE.getToolWithStats(BUTCHERYKNIFE, 1, m, m), "PPf", "PP ", "Sh ", 'S', aRod, 'P', aPlate);

                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(PLUNGER, 1, m, m), "xRR", " SR", "S f", 'S', aRod, 'R', OreDicts.rubber.get(Prefix.plate));
                GT_ModHandler.addCraftingRecipe(INSTANCE.getToolWithStats(SOLDERING_IRON_LV, 1, m, Materials.Rubber, 100000L, 32L, 1L, -1L), "LBf", "Sd ", "P  ", 'B', aBolt, 'P', OreDicts.rubber.get(Prefix.plate), 'S', Materials.Iron.getRod(1), 'L', ItemType.Battery_RE_LV_Lithium.get(1));
            } else if (m.has(BGEM)) {
                ItemStack aGem = m.getGem(1);
                GT_ModHandler.addShapedToolRecipe(m.getHeadAxe(1), "GG ", "G  ", "f  ", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadHoe(1), "GG ", "f  ", "   ", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadPickaxe(1), "GGG", "f  ", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadPlow(1), "GG", "GG", " f", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadSaw(1), "GGf", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadScythe(1), "GGG", " f ", "   ", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadShovel(1), "fG", 'G', aGem);
                GT_ModHandler.addShapedToolRecipe(m.getHeadSword(1), " G", "fG", 'G', aGem);
            }*/
        }
    }
}
