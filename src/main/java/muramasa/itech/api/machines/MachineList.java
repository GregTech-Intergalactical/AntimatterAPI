package muramasa.itech.api.machines;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.common.behaviour.BehaviourElectricBlastFurnace;
import muramasa.itech.common.tileentities.TileEntityMachine;
import muramasa.itech.common.tileentities.multi.TileEntityHatch;
import muramasa.itech.common.tileentities.multi.TileEntityMultiMachine;
import muramasa.itech.common.utils.Ref;
import muramasa.itech.loaders.ContentLoader;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

import static muramasa.itech.api.enums.MachineFlag.*;

public class MachineList {

    static HashMap<String, Machine> machineTypeLookup = new HashMap<>();

    public static Machine INVALID = new Machine("invalid", ContentLoader.blockMachines, TileEntityMachine.class).setTiers(Tier.LV).setBlock(ContentLoader.blockMachines);

    public static Machine ALLOYSMELTER = asBasic("alloysmelter", new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = asBasic("assembler", new SlotData(0, 17, 16), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 17, 34), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine BENDER = asBasic("bender", ALLOYSMELTER);
    public static Machine CANNER = asBasic("canner", new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = asBasic("compressor", new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = asBasic("cutter", new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = asBasic("extractor", COMPRESSOR);
    public static Machine EXTRUDER = asBasic("extruder", ALLOYSMELTER);
    public static Machine LATHE = asBasic("lathe", CUTTER);
    public static Machine PULVERIZER = asBasic("pulverizer", COMPRESSOR);
    public static Machine RECYCLER = asBasic("recycler", COMPRESSOR);
    public static Machine SCANNER = asFluid("scanner", COMPRESSOR);
    public static Machine WIREMILL = asBasic("wiremill", COMPRESSOR);
    public static Machine CENTRIFUGE = asFluid("centrifuge", new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = asFluid("electrolyzer", CENTRIFUGE);
    public static Machine THERMALCENTRIFUGE = asBasic("thermalcentrifuge", new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine OREWASHER = asFluid("orewasher", THERMALCENTRIFUGE);
    public static Machine CHEMICALREACTOR = asFluid("chemicalreactor", CANNER);
    public static Machine FLUIDCANNER = asFluid("fluidcanner", COMPRESSOR);
    public static Machine DISASSEMBLER = asBasic("disassembler", ALLOYSMELTER); //TODO
    public static Machine MASSFABRICATOR = asFluid("massfabricator", COMPRESSOR);
    public static Machine AMPLIFABRICATOR = asFluid("amplifabricator", COMPRESSOR);
    public static Machine REPLICATOR = asFluid("replicator", COMPRESSOR);
    public static Machine FERMENTER = asFluid("fermenter", COMPRESSOR);
    public static Machine FLUIDEXTRACTOR = asFluid("fluidextractor", COMPRESSOR);
    public static Machine FLUIDSOLIDIFIER = asFluid("fluidsolidifier", COMPRESSOR);
    public static Machine DISTILLERY = asFluid("distillery", COMPRESSOR);
    public static Machine CHEMICALBATH = asFluid("chemicalbath", THERMALCENTRIFUGE);
    public static Machine AUTOCLAVE = asFluid("autoclave", COMPRESSOR);
    public static Machine MIXER = asFluid("mixer", new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASERENGRAVER = asBasic("laserengraver", ALLOYSMELTER);
    public static Machine FORMINGPRESS = asBasic("formingpress", ALLOYSMELTER);
    public static Machine FLUIDHEATER = asFluid("fluidheater", COMPRESSOR);
    public static Machine SIFTER = asBasic("sifter", DISASSEMBLER);
    public static Machine ARCFURNACE = asFluid("arcfurnace", ALLOYSMELTER); //TODO
    public static Machine PLASMAARCFURNACE = asFluid("plasmaarcfurnace", ARCFURNACE);

    public static Machine STEAMPULVERIZER = asSteam("steampulverizer", PULVERIZER);

    public static Machine BLASTFURNACE = asMulti("blastfurnace", ITEM, FLUID);
    public static Machine FUSIONREACTOR = asMulti("fusionreactor", FLUID);

    public static Machine HATCHITEM = asHatch("itemhatch", ITEM).addGUI(0, true, new SlotData(0, 35, 25));

    public static void finish() {
        BLASTFURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        BLASTFURNACE.addBehaviour(new BehaviourElectricBlastFurnace());

        FUSIONREACTOR.addPattern(StructurePattern.FUSION_REACTOR);
        FUSIONREACTOR.addBehaviour(new BehaviourElectricBlastFurnace());
    }

    public static Machine asBasic(String name, Machine machineToCopy) {
        return asBasic(name, machineToCopy.getSlots());
    }
    
    public static Machine asBasic(String name, SlotData... slots) {
        Machine basic = new Machine(name, ContentLoader.blockMachines, TileEntityMachine.class);
        basic.add(BASIC, POWERED, GUI);
        basic.setTiers(Tier.getStandard());
        basic.addRecipeMap();
        basic.addGUI(Ref.MACHINE_ID, false, slots);
        return basic;
    }
    
    public static Machine asFluid(String name, Machine machineToCopy) {
        return asFluid(name, machineToCopy.getSlots());
    }

    public static Machine asFluid(String name, SlotData... slots) {
        Machine fluid = asBasic(name, slots);
        fluid.add(FLUID);
        return fluid;
    }

    public static Machine asSteam(String name, Machine machineToCopy) {
        return asSteam(name, machineToCopy.getSlots());
    }
    
    public static Machine asSteam(String name, SlotData... slots) {
        Machine steam = new Machine(name, ContentLoader.blockMachines, TileEntityMachine.class);
        steam.add(BASIC, GUI, FLUID);
        steam.setTiers(Tier.getSteam());
        steam.addRecipeMap();
        steam.addGUI(Ref.MACHINE_ID, true, slots);
        return steam;
    }

    public static Machine asMulti(String name, /*StructurePattern pattern, BehaviourMultiMachine behaviour, */MachineFlag... extraFlags) {
        Machine multi = new Machine(name, ContentLoader.blockMultiMachines, TileEntityMultiMachine.class);
        multi.add(MULTI, GUI);
        multi.add(extraFlags);
//        structurePattern = pattern;
//        multiBehaviour = behaviour;
        multi.setBaseTexture(new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + name));
        return multi;
    }

    public static Machine asHatch(String name, MachineFlag... extraFlags) {
        Machine hatch = new Machine(name, ContentLoader.blockHatches, TileEntityHatch.class);
        hatch.add(GUI);
        hatch.add(extraFlags);
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
