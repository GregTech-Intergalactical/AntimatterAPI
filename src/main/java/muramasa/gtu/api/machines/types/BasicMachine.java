package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockMachine;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.tileentities.TileEntityRecipeMachine;

import java.util.ArrayList;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class BasicMachine<B extends RecipeBuilder> extends RecipeMachine<B> {

    public BasicMachine(String name, B builder, Class tileClass, Object... data) {
        super(name, new BlockMachine(name), builder, tileClass);
        setFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        setGUI(GregTech.INSTANCE, Ref.GUI_ID_MACHINE);

        ArrayList<Tier> tiers = new ArrayList<>();
        ArrayList<MachineFlag> flags = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof Tier) tiers.add((Tier) data[i]);
            if (data[i] instanceof MachineFlag) flags.add((MachineFlag) data[i]);
        }
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        addFlags(flags.toArray(new MachineFlag[0]));
    }

    public BasicMachine(String name, B builder, Object... data) {
        this(name, builder, TileEntityRecipeMachine.class, data);
    }
}
