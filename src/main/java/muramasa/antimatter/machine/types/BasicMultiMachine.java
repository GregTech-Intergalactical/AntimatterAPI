package muramasa.antimatter.machine.types;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.widget.ProgressWidget;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.texture.ITextureHandler;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.List;

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

    public BasicMultiMachine(String domain, String name) {
        super(domain, name);
        setTile(() -> new TileEntityBasicMultiMachine<>(this));
        addFlags(MULTI, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
        covers((ICover[]) null);
        this.baseTexture((type, tier) -> type.getTiers().size() > 1 ? new Texture[]{new Texture(domain, "block/machine/base/" + type.getId() + "_" + tier.getId())} : new Texture[]{new Texture(domain, "block/machine/base/" + type.getId())});
        addGuiCallback(t -> t.addWidget(ProgressWidget.build(BarDir.LEFT, true)));
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getBaseTexture(t))));
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE, t))));
        return textures;
    }
}
