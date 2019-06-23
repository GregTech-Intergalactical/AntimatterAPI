package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.blocks.BlockMachine;

import java.util.ArrayList;
import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class MultiMachine<B extends RecipeBuilder> extends RecipeMachine<B> {

    public MultiMachine(String name, B builder, Class tileClass, Object... data) {
        super(name, new BlockMachine(name), builder, tileClass);
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(GregTech.INSTANCE, Ref.GUI_ID_MULTI_MACHINE);

        ArrayList<Tier> tiers = new ArrayList<>();
        ArrayList<MachineFlag> flags = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof Tier) tiers.add((Tier) data[i]);
            if (data[i] instanceof MachineFlag) flags.add((MachineFlag) data[i]);
        }
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        addFlags(flags.toArray(new MachineFlag[0]));
    }

    @Override
    public Texture getBaseTexture(Tier tier) {
        return tiers.size() > 1 ? new Texture("blocks/machine/base/" + id + "_" + tier.getId()) : new Texture("blocks/machine/base/" + id);
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.add(getBaseTexture(t)));
        return textures;
    }
}
