package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.texture.Texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Class tileClass, Object... data) {
        super(name, tileClass);
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(GregTech.INSTANCE, Ref.GUI_ID_MULTI_MACHINE);

        ArrayList<Tier> tiers = new ArrayList<>();
        ArrayList<MachineFlag> flags = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof RecipeMap) {
                recipeMap = (RecipeMap) data[i];
                flags.add(RECIPE);
            }
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
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE)));
        return textures;
    }
}
