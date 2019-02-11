package muramasa.gregtech.api.data;

import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.SlotData;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.*;
import muramasa.gregtech.api.structure.StructurePattern;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.overrides.multi.TileEntityElectricBlastFurnace;
import muramasa.gregtech.common.tileentities.overrides.multi.TileEntityFusionReactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import static muramasa.gregtech.api.machines.MachineFlag.FLUID;
import static muramasa.gregtech.api.machines.MachineFlag.ITEM;

public class Machines {

    public static LinkedHashMap<String, Machine> machineTypeLookup = new LinkedHashMap<>();

    public static Machine INVALID = new Machine("invalid", new BlockMachine("invalid"), TileEntityMachine.class).setTiers(Tier.LV);

    public static Machine ALLOY_SMELTER = new BasicMachine("alloy_smelter").addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = new BasicMachine("assembler").addSlots(new SlotData(0, 17, 16), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 17, 34), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine BENDER = new BasicMachine("bender").addSlots(ALLOY_SMELTER);
    public static Machine CANNER = new BasicMachine("canner").addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = new BasicMachine("compressor").addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = new BasicMachine("cutter").addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = new BasicMachine("extractor").addSlots(COMPRESSOR);
    public static Machine EXTRUDER = new BasicMachine("extruder").addSlots(ALLOY_SMELTER);
    public static Machine LATHE = new BasicMachine("lathe").addSlots(CUTTER);
    public static Machine PULVERIZER = new BasicMachine("pulverizer").addSlots(COMPRESSOR);
    public static Machine RECYCLER = new BasicMachine("recycler").addSlots(COMPRESSOR);
    public static Machine SCANNER = new ItemFluidMachine("scanner").addSlots(COMPRESSOR);
    public static Machine WIRE_MILL = new BasicMachine("wire_mill").addSlots(COMPRESSOR);
    public static Machine CENTRIFUGE = new ItemFluidMachine("centrifuge").addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = new ItemFluidMachine("electrolyzer").addSlots(CENTRIFUGE);
    public static Machine THERMAL_CENTRIFUGE = new BasicMachine("thermal_centrifuge").addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine ORE_WASHER = new ItemFluidMachine("ore_washer").addSlots(THERMAL_CENTRIFUGE).addFluidSlots(new SlotData(2, 53, 64));
    public static Machine CHEMICAL_REACTOR = new ItemFluidMachine("chemical_reactor").addSlots(CANNER);
    public static Machine FLUID_CANNER = new ItemFluidMachine("fluid_canner").addSlots(COMPRESSOR);
    public static Machine DISASSEMBLER = new BasicMachine("disassembler").addSlots(ALLOY_SMELTER); //TODO
    public static Machine MASS_FABRICATOR = new ItemFluidMachine("mass_fabricator").addSlots(COMPRESSOR);
    public static Machine AMP_FABRICATOR = new BasicMachine("amp_fabricator").addSlots(COMPRESSOR);
    public static Machine REPLICATOR = new ItemFluidMachine("replicator").addSlots(COMPRESSOR);
    public static Machine FERMENTER = new ItemFluidMachine("fermenter").addSlots(COMPRESSOR);
    public static Machine FLUID_EXTRACTOR = new BasicMachine("fluid_extractor").addSlots(COMPRESSOR);
    public static Machine FLUID_SOLIDIFIER = new ItemFluidMachine("fluid_solidifier").addSlots(COMPRESSOR);
    public static Machine DISTILLERY = new ItemFluidMachine("distillery").addSlots(COMPRESSOR);
    public static Machine CHEMICAL_BATH = new ItemFluidMachine("chemical_bath").addSlots(THERMAL_CENTRIFUGE);
    public static Machine AUTOCLAVE = new ItemFluidMachine("autoclave").addSlots(COMPRESSOR);
    public static Machine MIXER = new ItemFluidMachine("mixer").addSlots(new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASER_ENGRAVER = new BasicMachine("laser_engraver").addSlots(ALLOY_SMELTER);
    public static Machine FORMING_PRESS = new BasicMachine("forming_press").addSlots(ALLOY_SMELTER);
    public static Machine SIFTER = new BasicMachine("sifter").addSlots(DISASSEMBLER);
    public static Machine ARC_FURNACE = new ItemFluidMachine("arc_furnace").addSlots(ALLOY_SMELTER); //TODO
    public static Machine PLASMA_ARC_FURNACE = new ItemFluidMachine("plasma_arc_furnace").addSlots(ARC_FURNACE);

    public static Machine STEAM_PULVERIZER = new SteamMachine("steam_pulverizer").addSlots(PULVERIZER);

    public static Machine BLAST_FURNACE = new MultiMachine("blast_furnace", TileEntityElectricBlastFurnace.class, ITEM, FLUID);
    public static Machine FUSION_REACTOR = new MultiMachine("fusion_reactor", TileEntityFusionReactor.class, FLUID);

    public static Machine HATCH_ITEM_INPUT = new HatchMachine("item_hatch_input").addSlots(new SlotData(0, 35, 25));
    public static Machine HATCH_ITEM_OUTPUT = new HatchMachine("item_hatch_output").addSlots(new SlotData(1, 35, 25));

    public static void init() {
        BLAST_FURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        FUSION_REACTOR.addPattern(StructurePattern.FUSION_REACTOR);
    }

    public static Machine get(String name) {
        Machine machine = machineTypeLookup.get(name);
        return machine != null ? machine : INVALID;
    }

    public static MachineStack get(String name, String tier) {
        return new MachineStack(get(name), Tier.get(tier));
    }

    public static Collection<Machine> getAll() {
        return machineTypeLookup.values();
    }

    public static int getCount() {
        return machineTypeLookup.size();
    }

    public static Collection<Machine> getTypes(MachineFlag... flags) {
        ArrayList<Machine> types = new ArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }

    public static Collection<MachineStack> getStacks(MachineFlag... flags) {
        ArrayList<MachineStack> stacks = new ArrayList<>();
        for (MachineFlag flag : flags) {
            stacks.addAll(flag.getStacks());
        }
        return stacks;
    }
}
