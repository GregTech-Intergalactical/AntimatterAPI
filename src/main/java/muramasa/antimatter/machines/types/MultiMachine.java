package muramasa.antimatter.machines.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.machines.MachineState;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tileentities.multi.TileEntityMultiMachine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.*;

public class MultiMachine extends Machine {

    public MultiMachine(String namespace, String name, Supplier<? extends TileEntityMultiMachine> tile, Object... data) {
        super(namespace, name, tile, data);
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(Data.MULTI_MENU_HANDLER);
    }

    @Override
    public Texture getBaseTexture(Tier tier) {
        return tiers.size() > 1 ? new Texture(getNamespace(), "block/machine/base/" + getId() + "_" + tier.getId()) : new Texture(getNamespace(), "block/machine/base/" + getId());
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.add(getBaseTexture(t)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE)));
        return textures;
    }
}
