package muramasa.itech.api.machines;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.AbilityFlag;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.common.behaviour.BehaviourElectricBlastFurnace;
import muramasa.itech.loaders.ContentLoader;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

import static muramasa.itech.api.enums.AbilityFlag.*;

public class MachineList {

    static HashMap<String, Machine> machineTypeLookup = new HashMap<>();

    public static Machine INVALID = new Machine("invalid").setTiers(Tier.LV).setBlock(ContentLoader.blockMachines);

    public static Machine ALLOYSMELTER = asBasic("alloysmelter").addGUI(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = asBasic("assembler");
    public static Machine BENDER = asBasic("bender").addGUI(ALLOYSMELTER);
    public static Machine CANNER = asBasic("canner").addGUI(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = asBasic("compressor").addGUI(new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = asBasic("cutter").addGUI(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = asBasic("extractor").addGUI(COMPRESSOR);
    public static Machine EXTRUDER = asBasic("extruder").addGUI(ALLOYSMELTER);
    public static Machine LATHE = asBasic("lathe").addGUI(CUTTER);
    public static Machine PULVERIZER = asBasic("pulverizer").addGUI(COMPRESSOR);
    public static Machine RECYCLER = asBasic("recycler").addGUI(COMPRESSOR);
    public static Machine SCANNER = asFluid("scanner").addGUI(COMPRESSOR);
    public static Machine WIREMILL = asBasic("wiremill").addGUI(COMPRESSOR);
    public static Machine CENTRIFUGE = asFluid("centrifuge").addGUI(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = asFluid("electrolyzer").addGUI(CENTRIFUGE);
    public static Machine THERMALCENTRIFUGE = asBasic("thermalcentrifuge").addGUI(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine OREWASHER = asFluid("orewasher").addGUI(THERMALCENTRIFUGE);
    public static Machine CHEMICALREACTOR = asFluid("chemicalreactor").addGUI(CANNER);
    public static Machine FLUIDCANNER = asFluid("fluidcanner").addGUI(COMPRESSOR);
    public static Machine DISASSEMBLER = asBasic("disassembler").addGUI(ALLOYSMELTER); //TODO
    public static Machine MASSFABRICATOR = asFluid("massfabricator").addGUI(COMPRESSOR);
    public static Machine AMPLIFABRICATOR = asFluid("amplifabricator").addGUI(COMPRESSOR);
    public static Machine REPLICATOR = asFluid("replicator").addGUI(COMPRESSOR);
    public static Machine FERMENTER = asFluid("fermenter").addGUI(COMPRESSOR);
    public static Machine FLUIDEXTRACTOR = asFluid("fluidextractor").addGUI(COMPRESSOR);
    public static Machine FLUIDSOLIDIFIER = asFluid("fluidsolidifier").addGUI(COMPRESSOR);
    public static Machine DISTILLERY = asFluid("distillery").addGUI(COMPRESSOR);
    public static Machine CHEMICALBATH = asFluid("chemicalbath").addGUI(THERMALCENTRIFUGE);
    public static Machine AUTOCLAVE = asFluid("autoclave").addGUI(COMPRESSOR);
    public static Machine MIXER = asFluid("mixer").addGUI(new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASERENGRAVER = asBasic("laserengraver").addGUI(ALLOYSMELTER);
    public static Machine FORMINGPRESS = asBasic("formingpress").addGUI(ALLOYSMELTER);
    public static Machine FLUIDHEATER = asFluid("fluidheater").addGUI(COMPRESSOR);
    public static Machine SIFTER = asBasic("sifter").addGUI(DISASSEMBLER);
    public static Machine ARCFURNACE = asFluid("arcfurnace").addGUI(ALLOYSMELTER); //TODO
    public static Machine PLASMAARCFURNACE = asFluid("plasmaarcfurnace").addGUI(ARCFURNACE);

    public static Machine STEAMPULVERIZER = asSteam("steampulverizer").addGUI(PULVERIZER);

    public static Machine BLASTFURNACE = asMulti("blastfurnace", ITEM, FLUID);
    public static Machine FUSIONREACTOR = asMulti("fusionreactor", FLUID);

    public static Machine HATCHITEM = asHatch("itemhatch").addGUI(new SlotData(0, 35, 25));

    public static void finish() {
        BLASTFURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        BLASTFURNACE.addBehaviour(new BehaviourElectricBlastFurnace());

        FUSIONREACTOR.addPattern(StructurePattern.FUSION_REACTOR);
        FUSIONREACTOR.addBehaviour(new BehaviourElectricBlastFurnace());
    }

    public static Machine asBasic(String name, AbilityFlag... extraFlags) {
        Machine basic = new Machine(name);
        basic.setBlock(ContentLoader.blockMachines);
        basic.add(BASIC);
        basic.add(extraFlags);
        basic.setTiers(Tier.getStandard());
        basic.addRecipeMap();
        return basic;
    }

    public static Machine asFluid(String name, AbilityFlag... extraFlags) {
        Machine fluid = asBasic(name, FLUID);
        fluid.add(extraFlags);
        return fluid;
    }

    public static Machine asSteam(String name, AbilityFlag... extraFlags) {
        Machine steam = asFluid(name);
        steam.add(extraFlags);
        steam.setTiers(Tier.getSteam());
        steam.setGuiTierSensitive();
        return steam;
    }

    public static Machine asMulti(String name, /*StructurePattern pattern, BehaviourMultiMachine behaviour, */AbilityFlag... extraFlags) {
        Machine multi = new Machine(name);
        multi.setBlock(ContentLoader.blockMultiMachines);
        multi.add(MULTI);
        multi.add(extraFlags);
//        structurePattern = pattern;
//        multiBehaviour = behaviour;
        multi.setBaseTexture(new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + name));
        return multi;
    }

    public static Machine asHatch(String name, AbilityFlag... extraFlags) {
        Machine hatch = new Machine(name);
        hatch.setBlock(ContentLoader.blockHatches);
        hatch.setTiers(Tier.getStandard());
        return hatch;
    }

    public static Machine get(String name) {
        Machine machine = machineTypeLookup.get(name);
        return machine != null ? machine : INVALID;
    }

    public static MachineStack get(String name, String tier) {
        if (Tier.get(tier) == null) {
            throw new NullPointerException();
        }
        return new MachineStack(get(name), Tier.get(tier));
    }
}
