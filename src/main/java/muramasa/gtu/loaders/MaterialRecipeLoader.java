package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.data.Materials;
import muramasa.antimatter.materials.IMaterialTag;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialStack;
import muramasa.antimatter.recipe.RecipeHelper;
import muramasa.antimatter.tools.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static muramasa.gtu.data.RecipeMaps.*;
import static muramasa.antimatter.materials.MaterialTag.*;
import static muramasa.antimatter.materials.MaterialType.*;
import static muramasa.antimatter.recipe.RecipeTag.IGNORE_NBT;
import static muramasa.gtu.common.Data.*;

//TODO EXCLUDED FROM COMPILE

public class MaterialRecipeLoader { 

    //TODO register purified dust processing to centrifuged processing to regain lost benefits

    public static int mixedOreYield = Ref.mixedOreYieldsTwoThirdsPureOre ? 2 : 3;

    //TODO: When we do have MaterialInfo or a MaterialType 'amount' system, some of this would need to adapt to it!
    //TODO: Plasma Arc/Normal Arc smelting will be handled differently, when we have said amount system.    
    
    public static void init() {       
        //ELEMENTAL.all().forEach();
//            ItemStack aDataOrb = ItemType.Tool_DataO.RB(1);
//            Behaviour_DataOrb.setDataTitle(aDataOrb, "Elemental-Scan");
//            Behaviour_DataOrb.setDataName(aDataOrb, m.mElement.id());
//            ItemStack aRepOutput = ((m.has(LIQUID) || m.has(GAS)) && !m.has(Dust)) ? m.getCell(1) : m.getDust(1);
//            Fluid aFluid = m.mFluid != null ? m.mFluid : m.mGas;
//            int aMass = m.getMass();
//            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{aRepOutput}, new ItemStack[]{aDataOrb}, ItemType.Tool_DataO.RB(1), null, null, aMass * 8192, 32, 0);
//            GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, aFluid == null ? new ItemStack[]{aRepOutput} : null, new ItemStack[]{aDataOrb}, new FluidStack[]{Materials.UUMatter.getFluid(aMass)}, aFluid == null ? null : new FluidStack[]{new FluidStack(aFluid, 144)}, aMass * 512, 32, 0);

        //LIQUID.all().forEach();

        //GAS.all().forEach();

        PLASMA.all().forEach(m -> {
            ItemStack cell = m.has(LIQUID) ? m.getCell(1) : m.getCellGas(1);
            VACUUM_FREEZING.RB().ii(m.getCellPlasma(1)).io(cell).add(Math.max(m.getMass() * 2, 1), 120);
            PLASMA_FUELS.RB().fi(m.getPlasma(1296)).add(0, 0, Math.max(1024, 1024 * m.getMass()) * 1000); //TODO: 1296 or 1000? To per cell amount or to '9' units?
        });
        
        INGOT_HOT.all().forEach(m -> VACUUM_FREEZING.RB().ii(m.getIngotHot(1)).io(m.getIngot(1)).add(Math.max(m.getMass() * 3, 1), 120)); //TODO: Coolant idea?

        SPRING.all().forEach(m -> BENDING.RB().ii(m.getRod(1)).io(m.getSpring(1)).add(200, 16));

        PLATE_DENSE.all().forEach(m -> {
            ItemStack plateDense = m.getPlateDense(1);
            long mass = Math.max(m.getMass() * 9, 1);
            BENDING.RB().ii(m.getPlate(9)).io(plateDense).add(mass, 96);  
            BENDING.RB().ii(m.getIngot(9)).io(plateDense).add(mass + 44, 96);
            //GregTech_API.registerCover(aDenseStack, new GT_RenderedTexture(merial.mIconSet.mTextures[76], merial.mRGBa, false), null);
        });
   
        ROTOR.all().forEach(m -> {
            ItemStack plate = m.getPlate(1), ring = m.getRing(1), rotor = m.getRotor(1);
            RecipeHelper.addShaped("rotor_" + m.getId(), rotor, "PhP", "SRf", "PdP", 'P', plate, 'R', ring, 'S', m.getScrew(1));
            //ItemStack plates = plate.copy();
            plate = Utils.ca(4, plate);
            ASSEMBLING.RB().ii(plate, ring).fi(Materials.Lead.getLiquid(48)).io(rotor).add(240, 24);
            ASSEMBLING.RB().ii(plate, ring).fi(Materials.Tin.getLiquid(32)).io(rotor).add(240, 24);
            ASSEMBLING.RB().ii(plate, ring).fi(Materials.SolderingAlloy.getLiquid(16)).io(rotor).add(240, 24);          
        });
        
        /*WIRE_FINE.all().forEach(m -> {
            if (!m.has(NOSMASH)) {
                //RecipeAdder.addWiremillRecipe(m.getIngot(1), Utils.copy(m.getWire01(2), aWireF), 100, 4);
                //RecipeAdder.addWiremillRecipe(m.getRod(1), Utils.copy(m.getWire01(1), aWireF), 50, 4);
            }
            else {
              //RecipeHelper.addShapedToolRecipe(aWireF, "Xx ", "   ", "   ", 'X', m.getFoil(1));
            }
        });*/

        GEAR_SMALL.all().stream().filter(m -> m != Materials.Wood).forEach(m -> {
            ItemStack gearSmall = m.getGearSmall(1);
            RecipeHelper.addShapeless("plate_to_small_gear_", gearSmall, "h", m.getPlate(1));
            FLUID_SOLIDIFYING.RB().ii(MoldGearSmall.get(0)).fi(m.getLiquid(144)).io(gearSmall).add(16, 8);        
        });
        
        GEAR.all().forEach(m -> {
            ItemStack gear = m.getGear(1);
            if (m.has(PLATE)) RecipeHelper.addShaped("gear_" + m.getId(), gear, "SPS", "PwP", "SPS", 'P', m.getPlate(1), 'S', m.getRod(1));
            if (m.has(GEM) && m.has(ROD)) RecipeHelper.addShaped("gear_" + m.getId(), gear, "SPS", "PwP", "SPS", 'P', m.getGemBrittle(1), 'S', m.getRod(1));
            if (m.has(LIQUID)) FLUID_SOLIDIFYING.RB().ii(MoldGear.get(0)).fi(m.getLiquid(576)).io(gear).add(128, 8);
            if (m.has(INGOT) && !m.has(NOSMELT)) {
                int voltage = m.getBlastTemp() >= 2800 ? 64 : 16;
                if (m.has(NOSMASH)) {
                    voltage /= 4;
                }
                long mass = m.getMass();
                //int aVoltageMulti = m.has(NOSMASH) ? m.getBlastTemp() >= 2800 ? 16 : 4 : m.getBlastTemp() >= 2800 ? 64 : 16;
                ItemStack gearSmeltInto = m.getSmeltInto().getGear(1);
                EXTRUDING.RB().ii(m.getIngot(4), ShapeGear.get(0)).io(gearSmeltInto).add(Math.max(mass * 5, 1), 8 * voltage);
                ALLOY_SMELTING.RB().ii(m.getIngot(8), MoldGear.get(0)).io(gearSmeltInto).add(mass * 10, 2 * voltage);
            }
        });
        
        FOIL.all().forEach(m -> {
            ItemStack plate = m.getPlate(1), foil = m.getFoil(2);
            RecipeHelper.addShapeless("plate_to_foil_" + m.getId(), foil, "h", plate);
            BENDING.RB().ii(plate).io(Utils.ca(4, foil)).add(Math.max(m.getMass(), 1), 24);
            //GregTech_API.registerCover(aFoilStack, new GT_RenderedTexture(merial.mIconSet.mTextures[70], merial.mRGBa, false), null);
        });
        
        SCREW.all().forEach(m -> {
            if (!m.has(ROD)) return;
            ItemStack screw = m.getScrew(1), rod = m.getRod(1);
            RecipeHelper.addShaped("rod_to_screw_" + m.getId(), screw, "sRf", "   ", "   ", 'R', rod);
            LATHING.RB().ii(rod).io(screw).add(Math.max(m.getMass(), 1), 4);
        });
        
        RING.all().forEach(m -> {
            ItemStack ring = m.getRing(1);
            Material smeltInto = m.getSmeltInto();
            ItemStack ringQuadruple = smeltInto != m ? smeltInto.getRing(4) : Utils.ca(4, ring);
            long mass = m.getMass() * 2;
            int voltage = m.getBlastTemp() >= 2800 ? 384 : 96;
            if (m.has(RUBBERTOOLS)) {
                if (m == Materials.Rubber) RecipeHelper.addShapeless(m.getId() + "_ring_with_knife", ring, "k", m.getRod(1));
                else EXTRUDING.RB().ii(m.getDust(1), ShapeRing.get(0)).io(ringQuadruple).add(mass, voltage);
            }
            if (!m.has(NOSMASH)) {
                RecipeHelper.addShapeless("rod_to_ring_" + m.getId(), ring, "h", m.getRod(1));
            }
            if (!m.has(NOSMELT)) {
                ring = Utils.ca(4, ring);
                EXTRUDING.RB().ii(m.getIngot(1), ShapeRing.get(0)).io(ringQuadruple).add(mass, voltage);
            }
        });

        ROD.all().forEach(m -> {
            ItemStack ingotOrGem = m.has(GEM) ? m.getGem(1) : m.getIngot(1), rod = m.getRod(1);
            long mass = m.getMass();
            LATHING.RB().ii(ingotOrGem).io(rod, m.getMacerateInto().getDustSmall(2)).add(Math.max(mass * 5, 1), 16);
            if (m.has(INGOT)) {
                RecipeHelper.addShapeless("ingot_to_rod_" + m.getId(), rod, "f", ingotOrGem);
                if (!m.has(NOSMELT)) {
                    int aEU = m.getBlastTemp() >= 2800 ? 384 : 96;
                    EXTRUDING.RB().ii(ingotOrGem, ShapeRod.get(0)).io(m.getSmeltInto().getRod(2)).add(mass * 2, aEU);
                    //TODO .RB(EXTRUDER).ii(m.getIngot(1), ShapeWire.get(0)).io(m.getSmeltInto().getWire01(2)).add(m.getMass() * 2, aEU);
                }
            }
        });

        ROD_LONG.all().forEach(m -> {
            if (!m.has(ROD)) return;
            ItemStack rod = m.getRod(2), rodLong = m.getRodLong(1);
            long mass = m.getMass();
            long duration = Math.max(mass, 1);
            RecipeHelper.addShapeless("long_rod_to_rod_" + m.getId(), rod, "s", rodLong);
            RecipeHelper.addShapeless("rod_to_long_rod_" + m.getId(), rodLong, "h", rod, rod);
            CUTTING.RB().ii(rodLong).fi(Materials.Lubricant.getLiquid(1)).io(rod).add(Math.max(mass / 2, 1), 4);
            CUTTING.RB().ii(rodLong).fi(Materials.DistilledWater.getLiquid(4)).io(rod).add(duration, 4);
            CUTTING.RB().ii(rodLong).fi(Materials.Water.getLiquid(10)).io(rod).add(duration, 4);
            HAMMERING.RB().ii(rod).io(rodLong).add(duration, 16);
        });

        BLOCK.all().forEach(m -> {
            ItemStack block = m.getBlock(1), ingotOrGem = m.has(GEM) ? m.getGem(1) : m.getIngot(1);
            //TODO: Leave in compressor? Hardcore mode?
            RecipeHelper.addShapeless("block_compress_" + m.getId(), block, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem, ingotOrGem);
            ingotOrGem = Utils.ca(9, ingotOrGem);
            COMPRESSING.RB().ii(ingotOrGem).io(block).add(300, 2);
            HAMMERING.RB().ii(block).io(ingotOrGem).add(80, 24);
            RecipeHelper.addShapeless("block_decompress_" + m.getId(), ingotOrGem, block);
            if (m.has(PLATE)) {
                ItemStack plate = m.getPlate(9);
                long mass = m.getMass();
                int liquidAmount = (int) mass * 8;
                long duration = Math.min(liquidAmount * 2, 120);
                int voltage = m.has(INGOT) ? 18 : 30;
                CUTTING.RB().ii(block).io(plate).add(Math.max(m.getMass() * 10, 1), 30);
                CUTTING.RB().ii(block).fi(Materials.Lubricant.getLiquid(liquidAmount)).io(plate).add(duration, voltage);
                CUTTING.RB().ii(block).fi(Materials.DistilledWater.getLiquid(4 * liquidAmount)).io(plate).add(duration, voltage);
                CUTTING.RB().ii(block).fi(Materials.Water.getLiquid(10 * liquidAmount)).io(plate).add(duration, voltage);
            }
            if (m.has(LIQUID)) {
                FLUID_SOLIDIFYING.RB().ii(MoldBlock.get(0)).fi(m.getLiquid(1296)).io(block).add(288, 8);
            }
            if (m.has(INGOT) && !m.has(NOSMELT)) {
                int voltage = m.has(NOSMASH) ? m.getBlastTemp() >= 2800 ? 16 : 4 : m.getBlastTemp() >= 2800 ? 64 : 16;
                EXTRUDING.RB().ii(ingotOrGem, ShapeBlock.get(0)).io(block).add(10, 8 * voltage);
                ALLOY_SMELTING.RB().ii(ingotOrGem, MoldBlock.get(0)).io(block).add(5, 4 * voltage);
            }
        });
        
        PLATE.all().forEach(m -> {
            ItemStack plate = m.getPlate(1), dust = m.getDust(1);
            if (m.has(CRYSTALLIZE)) {
                COMPRESSING.RB().ii(dust).io(plate).add(300, 2); 
            }
            if (!m.has(INGOT)) return;
            ItemStack ingot = m.getIngot(1), block = m.getBlock(1);
            if (m.has(LIQUID)) FLUID_SOLIDIFYING.RB().ii(MoldPlate.get(0)).fi(m.getLiquid(144)).io(plate).add(32, 8);
            long mass = m.getMass();
            if (!m.has(NOSMASH)) {
                //RecipeHelper.addShaped("plate_" + m.getId(), plate, "h", "X", "X", 'X', ingot);
                RecipeHelper.addShapeless("plate_" + m.getId(), plate, "h", ingot, ingot);
                if (m.has(GRINDABLE)) RecipeHelper.addShapeless("plate_grind_to_dust_" + m.getId(), dust, "m", plate);
                long duration = Math.max(mass, 1);
                BENDING.RB().ii(ingot).io(plate).add(duration, 24);     
                HAMMERING.RB().ii(Utils.ca(3, ingot)).io(Utils.ca(2, plate)).add(duration, 16);  
            }
            //TODO: Needs reworking
            if (!m.has(NOSMELT)) {
                ingot = ingot.getCount() != 1 ? Utils.ca(1, ingot) : ingot;
                int eu = m.getBlastTemp() >= 2800 ? 64 : 16;
                ItemStack smeltIntoPlate = m.getSmeltInto().getPlate(1);
                EXTRUDING.RB().ii(ingot, ShapePlate.get(0)).io(smeltIntoPlate).add(mass, 8 * eu);
                ALLOY_SMELTING.RB().ii(Utils.ca(2, ingot), MoldPlate.get(0)).io(smeltIntoPlate).tags(IGNORE_NBT).add(mass * 2, 2 * eu);
                //TODO WUT?
//              if (Prefix.block.isIgnored(m) && m != Materials.GraniteRed && m != Materials.GraniteBlack && m != Materials.Glass && m != Materials.Obsidian && m != Materials.Glowstone && m != Materials.Paper) {
//              GT_ModHandler.addCompressorRecipe(aDustStack, aPlateStack);
//              }
            }
            //GregTech_API.registerCover(aPlate, new GT_RenderedTexture(m.mIconSet.mTextures[71], m.getRGB(), false), null);
        });

        //Handle tiny, small, normal variants of dusts here! NOT IMPURE/PURE!
        DUST.all().forEach(m -> {
            ItemStack dust = m.getDust(1), dustSmall = m.getDustSmall(1), dustTiny = m.getDustTiny(1); //The Big Three          
            RecipeHelper.addShapeless("dust_tiny_to_dust_" + m.getId(), dust, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny, dustTiny);
            RecipeHelper.addShapeless("dust_small_to_dust_" + m.getId(), dust, dustSmall, dustSmall, dustSmall, dustSmall);
            //RecipeHelper.addShaped("dust_small_to_dust_" + m.getId(), dust, "XX ", "XX ", "   ", 'X', dustSmall);
            dustTiny = Utils.ca(9, dustTiny);
            RecipeHelper.addShapeless("dust_to_tiny_dust" + m.getId(), dustTiny, dust);
            if (m.getFuelPower() > 0) {
                //RecipeAdder.addFuel(aDust, null, m.mFuelPower, m.mFuelType);
            }
            Material smeltInto = m.getSmeltInto();
            if (!m.getSmeltInto().has(INGOT)) return;
            ItemStack ingotSmeltInto = smeltInto.getIngot(1);
            if (!m.has(NOSMELT)) {               
                ALLOY_SMELTING.RB().ii(dustTiny, MoldIngot.get(0)).io(ingotSmeltInto).add(130, 3);
                dustTiny = Utils.ca(1, dustTiny);
                RecipeHelper.addSmelting(dustTiny, smeltInto.getNugget(1));             
                RecipeHelper.addSmelting(dust, smeltInto.getDust(1));
                int voltage = m.has(NOSMASH) ? m.getBlastTemp() >= 2800 ? 16 : 4 : m.getBlastTemp() >= 2800 ? 64 : 16;
                EXTRUDING.RB().ii(dust, ShapeIngot.get(0)).io(ingotSmeltInto).add(10, 4 * voltage);
            }
            ALLOY_SMELTING.RB().ii(dust, MoldIngot.get(0)).io(ingotSmeltInto).add(130, 3);
            //TODO GT_RecipeRegistrator.registerUsagesForMaterials(aIngotStack, Prefix.plate.get(m).toString(), !aNoSmashing);
            if (m.needsBlastFurnace()) {
                long duration = Math.max(m.getMass() / 40, 1) * m.getBlastTemp();
                ItemStack blastStack = m.getSmeltInto().has(INGOT_HOT) ? smeltInto.getIngotHot(1) : ingotSmeltInto;
                int temp = m.getBlastTemp();
                BLASTING.RB().ii(dust).io(blastStack).add(duration, 120, temp);
                dustSmall = Utils.ca(4, dustSmall);
                BLASTING.RB().ii(dustSmall).io(blastStack).add(duration, 120, temp);
                if (!m.has(NOSMELT)) {
                    RecipeHelper.removeSmelting(dustTiny);
                    dustTiny.setCount(9);
                    BLASTING.RB().ii(dustTiny).io(blastStack).add(duration, 120, temp);              
                }
//              (int) Math.max(aMaterial.getMass() / 40L, 1L) * aMaterial.mBlastFurnaceTemp, 120, aMaterial.mBlastFurnaceTemp
            }
            /*
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
            */
            //?
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

        //I removed checks for stack count, if it does happen, I'll write a catch for it
        IMaterialTag.all(ELEC, CENT).forEach(m -> {
            int inputCount = 0, inputCellCount = 0;
            ArrayList<ItemStack> outputs = new ArrayList<>();
            ArrayList<FluidStack> fluidOutputs = new ArrayList<>();
            FluidStack[] fluidOutputsArray = new FluidStack[0];
            ItemStack[] outputsArray = new ItemStack[0];
            Material process;
            //We use traditional for-each loop here as a lambda statement can not grab external variables
            for (MaterialStack stack : m.getProcessInto()) {
                process = stack.m;
                inputCount += stack.s;
                if (!process.has(DUST)) {
                    if (process.has(LIQUID)) fluidOutputs.add(process.getLiquid(stack.s * 1000));
                    else if (process.has(GAS)) fluidOutputs.add(process.getGas(stack.s * 1000));
                }
                else {
                    outputs.add(process.getDust(stack.s));
                }
                /*
                if ((process.has(LIQUID) || process.has(GAS)) && !process.has(DUST)) {
                    if (firstFluid == null) {
                        if (process.getLiquid() != null) {
                            firstFluid = process.getLiquid(stack.s * 1000);
                        } else {
                            firstFluid = process.getGas(stack.s * 1000);
                        }
                    } else {
                        outputs.add(process.has(LIQUID) ? process.getCell(stack.s) : process.getCellGas(stack.s));
                        inputCellCount += stack.s;
                    }
                } else {
                    outputs.add(process.getDust(stack.s));
                }
                inputCount += stack.s;
                */
            }
            if (inputCount > 64 || (m.has(GEM_BRITTLE) && (inputCount * 2) > 64)) return;
            ItemStack inputStack = ItemStack.EMPTY;
            long duration = m.has(ELEC) ? Math.max(1, Math.abs(m.getProtons() * 2 * inputCount)) : Math.max(1, Math.abs(m.getProtons() * 4 * inputCount)); 
            long voltage = m.has(ELEC) ? Math.min(4, outputs.size()) * 30 : Math.min(4, outputs.size()) * 5;   
            if (m.has(DUST)) inputStack = m.has(GEM_BRITTLE) ? m.getGemBrittle(inputCount * 2) : m.getDust(inputCount);
            if (inputStack.isEmpty()) return;
            if (!outputs.isEmpty()) outputsArray = outputs.toArray(new ItemStack[outputs.size()]);
            if (!fluidOutputs.isEmpty()) fluidOutputsArray = fluidOutputs.toArray(new FluidStack[fluidOutputs.size()]);
            if (m.has(ELEC)) {
                if (m.has(DUST)) {
                    if (fluidOutputs.isEmpty()) {
                        if (!outputs.isEmpty()) ELECTROLYZING.RB().ii(inputStack).io(outputsArray).add(duration, voltage);
                    }
                    else {
                        if (outputs.isEmpty()) ELECTROLYZING.RB().ii(inputStack).fo(fluidOutputsArray).add(duration, voltage);
                        else ELECTROLYZING.RB().ii(inputStack).io(outputsArray).fo(fluidOutputsArray).add(duration, voltage);
                    }
                } 
                else {
                    FluidStack inputFluid = m.has(LIQUID) ? m.getLiquid(inputCount * 1000) : m.getGas(inputCount * 1000);
                    if (fluidOutputs.isEmpty()) {
                        if (!outputs.isEmpty()) ELECTROLYZING.RB().fi(inputFluid).io(outputsArray).add(duration, voltage);
                    }
                    else {
                        if (outputs.isEmpty()) ELECTROLYZING.RB().fi(inputFluid).fo(fluidOutputsArray).add(duration, voltage);
                        else ELECTROLYZING.RB().fi(inputFluid).io(outputsArray).fo(fluidOutputsArray).add(duration, voltage);
                    }
                }
            }
            else if (m.has(CENT)) {
                if (m.has(DUST)) {
                    if (fluidOutputs.isEmpty()) {
                        if (!outputs.isEmpty()) CENTRIFUGING.RB().ii(inputStack).io(outputsArray).add(duration, voltage);
                    }
                    else {
                        if (outputs.isEmpty()) CENTRIFUGING.RB().ii(inputStack).fo(fluidOutputsArray).add(duration, voltage);
                        else CENTRIFUGING.RB().ii(inputStack).io(outputsArray).fo(fluidOutputsArray).add(duration, voltage);
                    }
                } 
                else { 
                    FluidStack inputFluid = m.has(LIQUID) ? m.getLiquid(inputCount * 1000) : m.getGas(inputCount * 1000);
                    if (fluidOutputs.isEmpty()) {
                        if (!outputs.isEmpty()) CENTRIFUGING.RB().fi(inputFluid).io(outputsArray).add(duration, voltage);
                    }
                    else {
                        if (outputs.isEmpty()) CENTRIFUGING.RB().fi(inputFluid).fo(fluidOutputsArray).add(duration, voltage);
                        else CENTRIFUGING.RB().fi(inputFluid).io(outputsArray).fo(fluidOutputsArray).add(duration, voltage);
                    }
                } 
            }
        });

        NUGGET.all().forEach(m -> {
            ItemStack nugget = m.getNugget(1);
            if (m.has(LIQUID)) FLUID_SOLIDIFYING.RB().ii(MoldNugget.get(0)).fi(m.getLiquid(16)).io(nugget).add(16, 4);
            nugget = Utils.ca(9, nugget);
            ALLOY_SMELTING.RB().ii(nugget, MoldIngot.get(0)).io(m.getSmeltInto().getIngot(1)).add(200, 2);
            if (!m.has(NOSMELT)) ALLOY_SMELTING.RB().ii(m.getIngot(1), MoldNugget.get(0)).io(nugget).add(100, 1);
        });

        INGOT.all().forEach(m -> {
            ItemStack ingot = m.getIngot(1), dust = m.getDust(1);
            if (!m.needsBlastFurnace()) RecipeHelper.addShapeless("nugget_to_ingot_" + m.getId(), ingot, dust, dust, dust, dust, dust, dust, dust, dust, dust);
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
        
        GEM.all().forEach(m -> {
            ItemStack gem = m.getGem(1), dust = m.getDust(1);
            if (m.has(CRYSTALLIZE)) {
                AUTOCLAVING.RB().ii(dust).fi(Materials.Water.getLiquid(200)).io(gem).chances(70).add(2000, 24);
                AUTOCLAVING.RB().ii(dust).fi(Materials.DistilledWater.getLiquid(200)).io(gem).add(1500, 24);
            }
            else {
                dust = Utils.ca(4, dust);
                gem = Utils.ca(3, gem);
                if (m.has(GEM_BRITTLE)) {
                    BLASTING.RB().ii(m.getGemBrittle(8), new ItemStack(Blocks.TNT, 3)).io(gem, Materials.DarkAsh.getDustTiny(5)).add(22, 30, 500); //TODO: special?
                }
                else BLASTING.RB().ii(dust, new ItemStack(Blocks.TNT, 4)).io(gem, Materials.DarkAsh.getDustSmall(3)).add(40, 30, 1000); //TODO: special?
            }
           if (m.has(GEM_BRITTLE)) return;
           dust = Utils.ca(1, dust);
           gem = Utils.ca(1, gem);
           ItemStack block = m.getBlock(1);
           long mass = m.getMass();
           if (m.has(BLOCK)) {
               gem = Utils.ca(9, gem);
               COMPRESSING.RB().ii(gem).io(block).add(300, 2);
               HAMMERING.RB().ii(block).io(gem).add(100, 24);
           }
           if (m.has(NOSMASH)) {
               long minDuration = Math.min(mass * 3, 300);
               PULVERIZING.RB().ii(gem).io(dust).add(minDuration, 24);
               dust = Utils.ca(9, dust);
               PULVERIZING.RB().ii(block).io(dust).add(minDuration * 9, 24);
           }
           if (m.has(ROD_LONG)) LATHING.RB().ii(gem).io(m.getRodLong(1)).add(Math.max(mass, 1) / 3, 8);
        });

        GEM_BRITTLE.all().forEach(m -> {
            ItemStack gem = m.getGem(1), gemBrittle = m.getGemBrittle(1), gemPolished = m.getGemPolished(1), block = m.getBlock(1), dust = m.getDust(1);  
            ItemStack doubleBrittle = Utils.ca(2, gemBrittle);
            long mass = m.getMass();
            HAMMERING.RB().ii(gemPolished).io(gemBrittle).add(Math.min(mass, 40), 2);
            HAMMERING.RB().ii(gem).io(doubleBrittle).add(Math.min(mass * 2, 90), 8);
            if (m.hasByProducts())  CUTTING.RB().ii(gem).io(gemPolished, m.getByProducts().get(0).getDustTiny(1)).chances(100, 10).add(440 + (mass * 2), 24);
            else CUTTING.RB().ii(gem).io(gemPolished).add(440 + (mass * 2), 24);
            if (m.has(SCREW)) LATHING.RB().ii(gemBrittle).io(m.getScrew(1)).add(Math.max(mass / 3, 1) / 3, 8);
            if (m.has(CRYSTALLIZE)) {
                AUTOCLAVING.RB().ii(dust).fi(Materials.Water.getLiquid(200)).io(gem).chances(70).add(2000, 24);
                AUTOCLAVING.RB().ii(dust).fi(Materials.DistilledWater.getLiquid(200)).io(gem).add(1500, 24);
                COMPRESSING.RB().ii(dust).io(m.getPlate(1)).add(350, 2);
            }
            if (!m.has(NOSMASH)) {
                long minDuration = Math.min(mass / 2, 50);
                HAMMERING.RB().ii(doubleBrittle).io(dust).add(minDuration, 18);
                dust = Utils.ca(9, dust);
                HAMMERING.RB().ii(block).io(dust).add(minDuration * 9, 24);
            }
        });
        
        ROCK.all().forEach(m -> {
            if (m == Materials.Flint) return;
            ItemStack rock = m.getRock(1);
            if (m.has(DUST)) {
                ItemStack dustTiny = m.getDustTiny(1);
                //RecipeHelper.addShapeless(m.getId() + "_rock_to_dust", dust, rock, rock, rock, rock, rock, rock, rock, rock, ToolType.MORTAR.getOreDict());
                RecipeHelper.addShapeless(m.getId() + "_rock_to_tiny_dust", dustTiny, rock, AntimatterToolType.MORTAR.getOreDict());
                PULVERIZING.RB().ii(rock).io(dustTiny).add(80, 2);
                HAMMERING.RB().ii(rock).io(dustTiny).add(20, 4);
            }
            if (m.has(NUGGET)) {
                ItemStack nugget = m.getNugget(1);
                RecipeHelper.addSmelting(rock, nugget, 1.0F);
            }
        });

        CRUSHED.all().forEach(m -> {
            ItemStack ore = m.getOre(1), crushed = m.getCrushed(1), dust = m.getDust(1), stoneDust = Materials.Stone.getDust(1);;
            if (m.hasByProducts()) {
                ArrayList<Material> byProducts = m.getByProducts();
                int byProductsCount = byProducts.size();

                ArrayList<ItemStack> ores = new ArrayList<>();
                if (m.has(ORE)) ores.add(ore);
                if (m.has(ROCK)) ores.add(m.getRock(1));
                if (m.has(CRUSHED)) ores.add(crushed);
                if (m.has(CRUSHED_PURIFIED)) ores.add(m.getCrushedPurified(1));
                if (m.has(CRUSHED_CENTRIFUGED)) ores.add(m.getCrushedCentrifuged(1));

                ArrayList<ItemStack> dusts = new ArrayList<>(byProductsCount);
                byProducts.forEach(p -> dusts.add(p.getDust(1)));
                ORE_BYPRODUCTS.RB().ii(ores.toArray(new ItemStack[0])).io(dusts.toArray(new ItemStack[byProductsCount])).add();
            }
            boolean needsBF = m.needsBlastFurnace() || m.getDirectSmeltInto().needsBlastFurnace();
            int multiplier = /*aIsRich ? 2 : */1; //TODO implement in some way, but for now support is coded in
            //RecipeHelper.addShapedToolRecipe(m.getDustIP(1), "h  ", "X  ", "   ", 'X', crushed);

            //TODO better way to do this
            Material aOreByProduct1 = m.getByProducts().size() >= 1 ? m.getByProducts().get(0) : m.getMacerateInto();
            Material aOreByProduct2 = m.getByProducts().size() >= 2 ? m.getByProducts().get(1) : aOreByProduct1;

            PULVERIZING.RB().ii(ore).io(Utils.ca((m.getOreMulti() * multiplier) * 2, crushed), m.getByProducts().size() > 0 ? m.getByProducts().get(0).getDust(1) : dust, stoneDust).chances(100, 10 * multiplier * m.getByProductMulti(), 50).add(400, 2);
            PULVERIZING.RB().ii(crushed).io(m.getMacerateInto().getDustImpure(1), aOreByProduct1.getDust(1)).chances(100, 10).add(400, 2);
            HAMMERING.RB().ii(ore).io(m.has(BRITTLEG) ? m.getGem(1) : crushed).add(16, 10);
            HAMMERING.RB().ii(crushed).io(m.getDustImpure(1)).add(10, 16);
            if (m.has(GEM)) { //Gem Specific Recipes
                ItemStack gem = m.hasDirectSmeltInto() ? m.getDirectSmeltInto().getGem(1) : m.getGem(1);
                RecipeHelper.addSmelting(ore, Utils.ca(multiplier * m.getSmeltingMulti(), gem));
                if (m.has(GEM_BRITTLE)) {
                    ItemStack gemBrittle = m.getGemBrittle(1);
                    SIFTING.RB().ii(crushed).io(m.getGemPolished(1), gem, gem, dust, gemBrittle, gemBrittle).chances(5, 15, 32, 44, 78, 100).add(800, 16);
                } else {
                    SIFTING.RB().ii(crushed).io(gem, gem, gem, gem, dust, dust).chances(1, 4, 15, 20, 40, 50).add(800, 16);
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
                if (needsBF) {
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
                    ItemStack ingotMulti = Utils.mul(multiplier * 3 * m.getSmeltingMulti(), INGOT);
                    ItemStack darkAsh = Materials.DarkAsh.getDustSmall(1);
                    BLASTING.RB().ii(ore, Materials.Calcite.getDust(multiplier)).io(ingotMulti, darkAsh).add(INGOT.getCount() * 500, 120, 1500);
                    BLASTING.RB().ii(ore, Materials.Quicklime.getDust(multiplier)).io(ingotMulti, darkAsh).add(INGOT.getCount() * 500, 120, 1500);
                } else if (m.has(CALCITE2X)) {
                    ItemStack darkAsh = Materials.DarkAsh.getDustSmall(1);
                    BLASTING.RB().ii(ore, Materials.Calcite.getDust(multiplier)).io(Utils.mul(multiplier * mixedOreYield * m.getSmeltingMulti(), INGOT), darkAsh).add( INGOT.getCount() * 500, 120, 1500);
                    BLASTING.RB().ii(ore, Materials.Quicklime.getDustTiny(multiplier * 3)).io(Utils.mul(multiplier * 3 * m.getSmeltingMulti(), INGOT), darkAsh).add(INGOT.getCount() * 500, 120, 1500);
                }
                RecipeHelper.addSmelting(dust, INGOT);
                RecipeHelper.addSmelting(ore, Utils.ca(multiplier * m.getSmeltingMulti(), INGOT));
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
        });

        for (Material m : CRUSHED_CENTRIFUGED.all()) {
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

        for (Material m : CRUSHED_PURIFIED.all()) {
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

        for (Material m : RUBBERTOOLS.all()) {
//            RecipeHelper.addBasicShapedRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, m, Materials.Wood), "XX ", "XXS", "XX ", 'X', m.getIngot(1), 'S', Materials.Wood.getRod(1));
//            RecipeHelper.addBasicShapelessRecipe(INSTANCE.getToolWithStats(SOFTHAMMER, 1, m, Materials.Wood), m.getHeadHammer(1), Materials.Wood.getRod(1));
        }

        TOOLS.all().forEach(m -> {
            //TODO: Scoop done in the Forestry Registrar
            if (m.has(ROD)) {
                ItemStack rod = m.getRod(1);
                RecipeHelper.addShaped("crowbar_" + m.getId(), AntimatterToolType.CROWBAR.get(m), "hDR", "DRD", "RDf", 'D', "dyeBlue", 'R', rod);
                RecipeHelper.addShaped("screwdriver_" + m.getId(), AntimatterToolType.SCREWDRIVER.get(m), " fR", " Rh", "S  ", 'R', rod, 'S', "stickWood");
                if (m.has(SCREW)) {
                    RecipeHelper.addShaped("wire_cutter_" + m.getId(), AntimatterToolType.WIRE_CUTTER.get(m), "PfP", "hPd", "RSR", 'P', m.getPlate(1), 'R', rod, 'S', m.getScrew(1));
                }
            }
            if (m.has(INGOT)) {
                ItemStack main = m.getIngot(1), plate = m.getPlate(1);
                RecipeHelper.addShaped("sword_" + m.getId(), AntimatterToolType.SWORD.get(m), " M ", "fPh", " S ", 'M', main, 'P', plate, 'S', "stickWood");
                RecipeHelper.addShaped("pickaxe_" + m.getId(), AntimatterToolType.PICKAXE.get(m), "MPM", "fSh", " S ", 'P', plate, 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("shovel_" + m.getId(), AntimatterToolType.SHOVEL.get(m), "fM ", " S ", " S ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("axe_" + m.getId(), AntimatterToolType.AXE.get(m), "PMh", "PS ", "fS ", 'M', main, 'P', plate, 'S', "stickWood");
                RecipeHelper.addShaped("hoe_" + m.getId(), AntimatterToolType.HOE.get(m), "MMf", " S ", " S ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("wrench_" + m.getId(), AntimatterToolType.WRENCH.get(m), "IhI", "III", " I ", 'I', main);
                RecipeHelper.addShaped("hammer_" + m.getId(), AntimatterToolType.HAMMER.get(m), "II ", "IIS", "II ", 'I', main, 'S', "stickWood");
                RecipeHelper.addShaped("file_" + m.getId(), AntimatterToolType.FILE.get(m), "P", "P", "S", 'P', plate, 'S', "stickWood");
                RecipeHelper.addShaped("saw_" + m.getId(), AntimatterToolType.SAW.get(m), "PPP", "fSS", "   ", 'P', plate, 'S', "stickWood");
                RecipeHelper.addShaped("knife_" + m.getId(), AntimatterToolType.KNIFE.get(m), "fPh", " S ", "   ", 'P', plate, 'S', "stickWood");
                if (!m.has(NOSMASH)) {
                    RecipeHelper.addShaped("mortar_" + m.getId(), AntimatterToolType.MORTAR.get(m), " M ", "SMS", "SSS", 'M', main, 'S', "stone");
                }
            }
            else if (m.has(GEM)) {
                ItemStack main = m.getGem(1);
                RecipeHelper.addShaped("sword_" + m.getId(), AntimatterToolType.SWORD.get(m), " M ", "fMh", " S ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("pickaxe_" + m.getId(), AntimatterToolType.PICKAXE.get(m), "MMM", "fSh", " S ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("shovel_" + m.getId(), AntimatterToolType.SHOVEL.get(m), "fM ", " S ", " S ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("axe_" + m.getId(), AntimatterToolType.AXE.get(m), "MMh", "MS ", "fS ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("hoe_" + m.getId(), AntimatterToolType.HOE.get(m), "MMf", " S ", " S ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("wrench_" + m.getId(), AntimatterToolType.WRENCH.get(m), "IhI", "III", " I ", 'I', main);
                RecipeHelper.addShaped("hammer_" + m.getId(), AntimatterToolType.HAMMER.get(m), "II ", "IIS", "II ", 'I', main, 'S', "stickWood");
                RecipeHelper.addShaped("file_" + m.getId(), AntimatterToolType.FILE.get(m), "M", "M", "S", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("saw_" + m.getId(), AntimatterToolType.SAW.get(m), "MMM", "fSS", "   ", 'M', main, 'S', "stickWood");
                RecipeHelper.addShaped("knife_" + m.getId(), AntimatterToolType.KNIFE.get(m), "fPh", " S ", "   ", 'P', main, 'S', "stickWood");
                if (!m.has(NOSMASH)) {
                    RecipeHelper.addShaped("mortar_" + m.getId(), AntimatterToolType.MORTAR.get(m), " M ", "SMS", "SSS", 'M', main, 'S', "stone");
                }
            }
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
        });
    }
}
