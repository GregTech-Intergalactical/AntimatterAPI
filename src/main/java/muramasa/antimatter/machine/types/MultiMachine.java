package muramasa.antimatter.machine.types;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.*;

public class MultiMachine extends Machine<MultiMachine> {

    @Override
    protected Block getBlock(Machine<MultiMachine> type, Tier tier) {
        return new BlockMultiMachine(type, tier);
    }

    @Override
    public Item getItem(Tier tier) {
        return BlockItem.BLOCK_TO_ITEM.get(AntimatterAPI.get(BlockMultiMachine.class,this.getId() + "_" + tier.getId()));
    }

    public MultiMachine(String domain, String name, Object... data) {
        super(domain, name, data);
        setTile(() -> new TileEntityMultiMachine(this));
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(Data.MULTI_MENU_HANDLER);
    }

    @Override
    public Texture getBaseTexture(Tier tier) {
        return tiers.size() > 1 ? new Texture(domain, "block/machine/base/" + getId() + "_" + tier.getId()) : new Texture(domain, "block/machine/base/" + getId());
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.add(getBaseTexture(t)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE)));
        return textures;
    }
}
