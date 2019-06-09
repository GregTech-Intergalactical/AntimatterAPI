package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.blocks.BlockMachine;

import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Class tileClass, Tier... tiers) {
        super(name, new BlockMachine(name), tileClass);
        setTiers(tiers);
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.GUI_ID_MULTI_MACHINE);
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
