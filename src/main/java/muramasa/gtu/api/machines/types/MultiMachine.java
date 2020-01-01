package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Supplier<? extends TileEntityMultiMachine> tile, Object... data) {
        super(name, tile, data);
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(Guis.MULTI_MENU_HANDLER);
    }

    @Override
    public Texture getBaseTexture(Tier tier) {
        return tiers.size() > 1 ? new Texture("block/machine/base/" + id + "_" + tier.getId()) : new Texture("block/machine/base/" + id);
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.add(getBaseTexture(t)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE)));
        return textures;
    }
}
