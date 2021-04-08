package muramasa.antimatter.machine.types;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.texture.ITextureHandler;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.CONFIGURABLE;
import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.MULTI;

public class BasicMultiMachine<T extends BasicMultiMachine<T>> extends Machine<T> {
    @Override
    protected Block getBlock(Machine<T> type, Tier tier) {
        return new BlockMultiMachine(type, tier);
    }

    @Override
    public Item getItem(Tier tier) {
        return BlockItem.BLOCK_TO_ITEM.get(AntimatterAPI.get(BlockMultiMachine.class,this.getId() + "_" + tier.getId()));
    }

    public BasicMultiMachine(String domain, String name, Object... data) {
        super(domain, name, getData(domain,data));
        setTile(() -> new TileEntityBasicMultiMachine(this));
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
    }

    //TODO: How else to do this?
    protected static Object[] getData(String domain, Object[] data) {
        ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(data));
        //Register a multi texture handler.
        arrayList.add((ITextureHandler) (type, tier) -> type.getTiers().size() > 1 ? new Texture[]{new Texture(domain, "block/machine/base/" + type.getId() + "_" + tier.getId())} : new Texture[]{new Texture(domain, "block/machine/base/" + type.getId())});
        return arrayList.toArray(new Object[0]);
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getBaseTexture(t))));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE)));
        return textures;
    }
}
