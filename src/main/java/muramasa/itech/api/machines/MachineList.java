package muramasa.itech.api.machines;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.itech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.itech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.itech.common.tileentities.overrides.multi.TileEntityElectricBlastFurnace;
import muramasa.itech.common.tileentities.overrides.multi.TileEntityFusionReactor;
import muramasa.itech.common.utils.Ref;
import muramasa.itech.loaders.ContentLoader;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

import static muramasa.itech.api.enums.MachineFlag.*;

public class MachineList {

    static HashMap<String, Machine> machineTypeLookup = new HashMap<>();

    public static Machine INVALID = new Machine("invalid", ContentLoader.blockMachines, TileEntityMachine.class).setTiers(Tier.LV).setBlock(ContentLoader.blockMachines);

    public static Machine ALLOYSMELTER = asBasic("alloysmelter", ITEM).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = asBasic("assembler", ITEM).addSlots(new SlotData(0, 17, 16), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 17, 34), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine BENDER = asBasic("bender", ITEM).addSlots(ALLOYSMELTER);
    public static Machine CANNER = asBasic("canner", ITEM).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = asBasic("compressor", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = asBasic("cutter", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = asBasic("extractor", ITEM).addSlots(COMPRESSOR);
    public static Machine EXTRUDER = asBasic("extruder", ITEM).addSlots(ALLOYSMELTER);
    public static Machine LATHE = asBasic("lathe", ITEM).addSlots(CUTTER);
    public static Machine PULVERIZER = asBasic("pulverizer", ITEM).addSlots(COMPRESSOR);
    public static Machine RECYCLER = asBasic("recycler", ITEM).addSlots(COMPRESSOR);
    public static Machine SCANNER = asBasic("scanner", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine WIREMILL = asBasic("wiremill", ITEM).addSlots(COMPRESSOR);
    public static Machine CENTRIFUGE = asBasic("centrifuge", ITEM, FLUID).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = asBasic("electrolyzer", ITEM, FLUID).addSlots(CENTRIFUGE);
    public static Machine THERMALCENTRIFUGE = asBasic("thermalcentrifuge", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine OREWASHER = asBasic("orewasher", ITEM, FLUID).addSlots(THERMALCENTRIFUGE);
    public static Machine CHEMICALREACTOR = asBasic("chemicalreactor", ITEM, FLUID).addSlots(CANNER);
    public static Machine FLUIDCANNER = asBasic("fluidcanner", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine DISASSEMBLER = asBasic("disassembler", ITEM).addSlots(ALLOYSMELTER); //TODO
    public static Machine MASSFABRICATOR = asBasic("massfabricator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine AMPLIFABRICATOR = asBasic("amplifabricator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine REPLICATOR = asBasic("replicator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FERMENTER = asBasic("fermenter", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FLUIDEXTRACTOR = asBasic("fluidextractor", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FLUIDSOLIDIFIER = asBasic("fluidsolidifier", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine DISTILLERY = asBasic("distillery", FLUID).addSlots(COMPRESSOR);
    public static Machine CHEMICALBATH = asBasic("chemicalbath", ITEM, FLUID).addSlots(THERMALCENTRIFUGE);
    public static Machine AUTOCLAVE = asBasic("autoclave", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine MIXER = asBasic("mixer", ITEM, FLUID).addSlots(new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASERENGRAVER = asBasic("laserengraver", ITEM).addSlots(ALLOYSMELTER);
    public static Machine FORMINGPRESS = asBasic("formingpress", ITEM).addSlots(ALLOYSMELTER);
    public static Machine FLUIDHEATER = asBasic("fluidheater", FLUID).addSlots(COMPRESSOR);
    public static Machine SIFTER = asBasic("sifter", ITEM).addSlots(DISASSEMBLER);
    public static Machine ARCFURNACE = asBasic("arcfurnace", ITEM, FLUID).addSlots(ALLOYSMELTER); //TODO
    public static Machine PLASMAARCFURNACE = asBasic("plasmaarcfurnace", ITEM, FLUID).addSlots(ARCFURNACE);

    public static Machine STEAMPULVERIZER = asSteam("steampulverizer").addSlots(PULVERIZER);

    public static Machine BLASTFURNACE = asMulti("blastfurnace", TileEntityElectricBlastFurnace.class, ITEM, FLUID);
    public static Machine FUSIONREACTOR = asMulti("fusionreactor", TileEntityFusionReactor.class, FLUID);

    public static Machine HATCHITEM = asHatch("itemhatch").addSlots(new SlotData(0, 35, 25));

    public static void finish() {
        BLASTFURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        FUSIONREACTOR.addPattern(StructurePattern.FUSION_REACTOR);
    }
    
    public static Machine asBasic(String name, MachineFlag... extraFlags) {
        Machine basic = new Machine(name, ContentLoader.blockMachines, TileEntityBasicMachine.class);
        basic.setTiers(Tier.getStandard());
        basic.addFlags(BASIC, ENERGY, GUI, COVERABLE, CONFIGURABLE);
        basic.addFlags(extraFlags);
        basic.addRecipeMap();
        basic.addGUI(Ref.MACHINE_ID, false);
        return basic;
    }
    
    public static Machine asSteam(String name, MachineFlag... extraFlags) {
        Machine steam = new Machine(name, ContentLoader.blockMachines, TileEntitySteamMachine.class);
        steam.setTiers(Tier.getSteam());
        steam.addFlags(STEAM, FLUID, GUI, COVERABLE, CONFIGURABLE);
        steam.addFlags(extraFlags);
        steam.addRecipeMap();
        steam.addGUI(Ref.MACHINE_ID, true);
        return steam;
    }

    public static Machine asMulti(String name, Class tileClass, MachineFlag... extraFlags) {
        Machine multi = new Machine(name, ContentLoader.blockMultiMachines, tileClass);
        multi.setTiers(Tier.getMulti());
        multi.addFlags(MULTI, GUI);
        multi.addFlags(extraFlags);
//        structurePattern = pattern;
        multi.addGUI(Ref.MULTI_MACHINE_ID, false);
        multi.setBaseTexture(new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + name));
        return multi;
    }

    public static Machine asHatch(String name, MachineFlag... extraFlags) {
        Machine hatch = new Machine(name, ContentLoader.blockHatches, TileEntityHatch.class);
        hatch.setTiers(Tier.getStandard());
        hatch.addFlags(HATCH, GUI);
        hatch.addFlags(extraFlags);
        return hatch;
    }

    public static Machine get(String name) {
        Machine machine = machineTypeLookup.get(name);
        return machine != null ? machine : INVALID;
    }

    public static MachineStack get(String name, String tier) {
        return new MachineStack(get(name), Tier.get(tier));
    }
}
