package muramasa.antimatter.machine.types;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.widget.ProgressWidget;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoCategory;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoPage;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.structure.PatternBuilder;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.MULTI;

public class BasicMultiMachine<T extends BasicMultiMachine<T>> extends Machine<T> {
    @Override
    protected Block getBlock(Machine<T> type, Tier tier) {
        return new BlockMultiMachine(type, tier);
    }

    public BlockMachine getBlockState(Tier tier) {
        if (tileType == null) return null;
        return AntimatterAPI.get(BlockMultiMachine.class, this.getId() + "_" + tier.getId(), this.getDomain());
    }

    @Override
    public Item getItem(Tier tier) {
        return BlockItem.BLOCK_TO_ITEM.get(AntimatterAPI.get(BlockMultiMachine.class, this.getId() + "_" + tier.getId(), this.getDomain()));
    }

    public BasicMultiMachine(String domain, String name) {
        super(domain, name);
        setTile(() -> new TileEntityBasicMultiMachine<>(this));
        addFlags(MULTI, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
        covers((CoverFactory[]) null);
        this.baseTexture((type, tier) -> type.getTiers().size() > 1 ? new Texture[]{new Texture(domain, "block/machine/base/" + type.getId() + "_" + tier.getId())} : new Texture[]{new Texture(domain, "block/machine/base/" + type.getId())});
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        if (!(this instanceof MultiMachine)) addGuiCallback(t -> t.addWidget(ProgressWidget.build(BarDir.LEFT, true)));
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getBaseTexture(t))));
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE, t))));
        return textures;
    }
    
    public final void setStructurePattern(Pattern... patterns) {
        if (FMLEnvironment.dist.isClient() && AntimatterAPI.isModLoaded(Ref.MOD_JEI)) {
            if (patterns.length <= 0) return;
            MultiMachineInfoCategory.addMultiMachine(new MultiMachineInfoPage(this, Arrays.stream(patterns).collect(Collectors.toList())
            ));
        }
    }
}
