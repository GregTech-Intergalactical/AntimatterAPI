package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockMachine;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.tileentities.TileEntityTank;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class TankMachine extends Machine {

    public TankMachine(String name) {
        super(name, new BlockMachine(name), TileEntityTank.class);
        setTiers(Tier.getStandard());
        setFlags(BASIC, ITEM, FLUID, COVERABLE, CONFIGURABLE);
        setGUI(GregTech.INSTANCE, Ref.GUI_ID_MACHINE);
    }
}
