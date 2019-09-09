package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.tileentities.TileEntityMachine;

import java.util.Arrays;
import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Class<? extends TileEntityMachine> tileClass, Object... data) {
        super(name, tileClass, data);
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(GregTech.INSTANCE, Ref.GUI_ID_MULTI_MACHINE);
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
