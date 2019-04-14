package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.common.blocks.BlockMachine;
import muramasa.gtu.Ref;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String name, Class tileClass) {
        super(name, new BlockMachine(name), tileClass);
        setTiers(Tier.getStandard());
        setFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.GUI_ID_MACHINE);
    }
}
