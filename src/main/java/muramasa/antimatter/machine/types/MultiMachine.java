package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tier.VoltageTier;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

import java.util.Arrays;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.*;

public class MultiMachine extends Machine<MultiMachine> {

    public MultiMachine(String domain, String name, Object... data) {
        super(domain, name, data);
        setTile(() -> new TileEntityMultiMachine(this));
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(Data.MULTI_MENU_HANDLER);
    }

    @Override
    public Texture getBaseTexture(VoltageTier tier) {
        return tiers.size() > 1 ? new Texture(getDomain(), "block/machine/base/" + getId() + '_' + tier.getId()) : new Texture(getDomain(), "block/machine/base/" + getId());
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.add(getBaseTexture(t)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE)));
        return textures;
    }
}
