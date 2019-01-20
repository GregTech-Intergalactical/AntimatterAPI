package muramasa.itech.api.machines;

import muramasa.itech.api.behaviour.BehaviourMultiMachine;
import muramasa.itech.api.machines.objects.MachineStack;
import muramasa.itech.api.machines.objects.SlotData;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.api.machines.types.BasicMachine;
import muramasa.itech.api.machines.types.FluidMachine;
import muramasa.itech.api.machines.types.Machine;
import muramasa.itech.api.machines.types.MultiMachine;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.common.behaviour.BehaviourElectricBlastFurnace;

import java.util.Collection;
import java.util.HashMap;

public class MachineList {

    //TODO move out of API

    public static HashMap<String, BasicMachine> basicTypeLookup = new HashMap<>();
    public static HashMap<String, MultiMachine> multiTypeLookup = new HashMap<>();
//    public static HashMap<String, Hatch> hatchTypeLookup = new HashMap<>();

    public static HashMap<String, MachineStack> basicStackLookup = new HashMap<>();
    public static HashMap<String, MachineStack> multiStackLookup = new HashMap<>();
    public static HashMap<String, MachineStack> hatchStackLookup = new HashMap<>();

    public static Machine ALLOYSMELTER = new BasicMachine("alloysmelter", Tier.getElectric(), new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = new FluidMachine("assembler", true, Tier.getElectric(), new SlotData(0, 17, 16), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 17, 34), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine BENDER = new BasicMachine("bender", Tier.getElectric(), ALLOYSMELTER);
    public static Machine CANNER = new BasicMachine("canner", Tier.getElectric(), new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = new BasicMachine("compressor", Tier.getElectric(), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = new FluidMachine("cutter", true, Tier.getElectric(), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = new BasicMachine("extractor", Tier.getElectric(), COMPRESSOR);
    public static Machine EXTRUDER = new BasicMachine("extruder", Tier.getElectric(), ALLOYSMELTER);
    public static Machine LATHE = new BasicMachine("lathe", Tier.getElectric(), CUTTER);
    public static Machine PULVERIZER = new BasicMachine("pulverizer", Tier.getElectric(), COMPRESSOR);
    public static Machine RECYCLER = new BasicMachine("recycler", Tier.getElectric(), COMPRESSOR);
    public static Machine SCANNER = new FluidMachine("scanner", true, Tier.getElectric(), COMPRESSOR);
    public static Machine WIREMILL = new BasicMachine("wiremill", Tier.getElectric(), COMPRESSOR);
    public static Machine CENTRIFUGE = new FluidMachine("centrifuge", true, Tier.getElectric(), new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = new FluidMachine("electrolyzer", true, Tier.getElectric(), CENTRIFUGE);
    public static Machine THERMALCENTRIFUGE = new BasicMachine("thermalcentrifuge", Tier.getElectric(), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine OREWASHER = new FluidMachine("orewasher", true, Tier.getElectric(), THERMALCENTRIFUGE);
    public static Machine CHEMICALREACTOR = new FluidMachine("chemicalreactor", true, Tier.getElectric(), CANNER);
    public static Machine FLUIDCANNER = new FluidMachine("fluidcanner", true, Tier.getElectric(), COMPRESSOR);
    public static Machine DISASSEMBLER = new BasicMachine("disassembler", Tier.getElectric(), ALLOYSMELTER); //TODO
    public static Machine MASSFABRICATOR = new FluidMachine("massfabricator", true, Tier.getElectric(), COMPRESSOR);
    public static Machine AMPLIFABRICATOR = new FluidMachine("amplifabricator", true, Tier.getElectric(), COMPRESSOR);
    public static Machine REPLICATOR = new FluidMachine("replicator", true, Tier.getElectric(), COMPRESSOR);
    public static Machine FERMENTER = new FluidMachine("fermenter", true, Tier.getElectric(), COMPRESSOR);
    public static Machine FLUIDEXTRACTOR = new FluidMachine("fluidextractor", false, Tier.getElectric(), COMPRESSOR);
    public static Machine FLUIDSOLIDIFIER = new FluidMachine("fluidsolidifier", true, Tier.getElectric(), COMPRESSOR);
    public static Machine DISTILLERY = new FluidMachine("distillery", true, Tier.getElectric(), COMPRESSOR);
    public static Machine CHEMICALBATH = new FluidMachine("chemicalbath", true, Tier.getElectric(), THERMALCENTRIFUGE);
    public static Machine AUTOCLAVE = new FluidMachine("autoclave", true, Tier.getElectric(), COMPRESSOR);
    public static Machine MIXER = new FluidMachine("mixer", true, Tier.getElectric(), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASERENGRAVER = new BasicMachine("laserengraver", Tier.getElectric(), ALLOYSMELTER);
    public static Machine FORMINGPRESS = new BasicMachine("formingpress", Tier.getElectric(), ALLOYSMELTER);
    public static Machine FLUIDHEATER = new FluidMachine("fluidheater", true, Tier.getElectric(), COMPRESSOR);
    public static Machine SIFTER = new BasicMachine("sifter", Tier.getElectric(), DISASSEMBLER);
    public static Machine ARCFURNACE = new FluidMachine("arcfurnace", true, Tier.getElectric(), ALLOYSMELTER); //TODO
    public static Machine PLASMAARCFURNACE = new FluidMachine("plasmaarcfurnace", true, Tier.getElectric(), ARCFURNACE);

    public static Machine STEAMPULVERIZER = new FluidMachine("steampulverizer", true, Tier.getSteam(), PULVERIZER);

    public static MultiMachine BLASTFURNACE = new MultiMachine("blastfurnace", new BehaviourElectricBlastFurnace());
    public static MultiMachine FUSION_REACTOR = new MultiMachine("fusionreactor", new BehaviourMultiMachine());

    public static Machine HATCH_ITEM = new BasicMachine("itemhatch", false, Tier.getElectric(), new SlotData(0, 35, 25));

    public static void init() {
        STEAMPULVERIZER.setGuiTierSensitive();
        BLASTFURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        FUSION_REACTOR.addPattern(StructurePattern.FUSION_REACTOR);
    }

    public static BasicMachine getBasic(String type) {
        return basicTypeLookup.get(type);
    }

    public static MultiMachine getMulti(String type) {
        return multiTypeLookup.get(type);
    }

    public static MachineStack getBasicStack(String type, String tier) {
        return basicStackLookup.get(type + tier);
    }

    public static Collection<BasicMachine> getAllBasicTypes() {
        return basicTypeLookup.values();
    }

    public static Collection<MultiMachine> getAllMultiTypes() {
        return multiTypeLookup.values();
    }

//    public static Collection<Hatch> getAllHatchTypes() {
//        return hatchTypeLookup.values();
//    }

    public static Collection<MachineStack> getAllBasicStacks() {
        return basicStackLookup.values();
    }

    public static Collection<MachineStack> getAllMultiStacks() {
        return multiStackLookup.values();
    }

    public static Collection<MachineStack> getAllHatchStacks() {
        return hatchStackLookup.values();
    }
}
