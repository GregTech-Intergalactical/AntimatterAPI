package muramasa.antimatter.machine.types;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.gui.widget.ProgressWidget;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.structure.PatternBuilder;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.MULTI;

public class BasicMultiMachine<T extends BasicMultiMachine<T>> extends Machine<T> {

    public BasicMultiMachine(String domain, String name) {
        super(domain, name);
        setTile(BlockEntityBasicMultiMachine::new);
        setBlock(BlockMultiMachine::new);
        setItemBlockClass(() -> BlockMultiMachine.class);
        addFlags(MULTI, COVERABLE);
        setClientTick();
        setGUI(Data.BASIC_MENU_HANDLER);
        covers((CoverFactory[]) null);
        setTooltipInfo((machine, stack, world, tooltip, flag) -> {
            if (machine.getType().getStructure(machine.getTier()) != null) {
                tooltip.add(Utils.translatable("machine.structure.form"));
            }
        });
        this.baseTexture((type, tier) -> type.getTiers().size() > 1 ? new Texture[]{new Texture(domain, "block/machine/base/" + type.getId() + "_" + tier.getId())} : new Texture[]{new Texture(domain, "block/machine/base/" + type.getId())});
     }

    @Override
    protected void setupGui() {
        super.setupGui();
        if (!(this instanceof MultiMachine)) addGuiCallback(t -> t.addWidget(ProgressWidget.build()));
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getBaseTexture(t))));
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE, t))));
        return textures;
    }

    public final void setStructurePattern(Function<PatternBuilder, Pattern> patterns) {
        setStructurePattern(patterns.apply(new PatternBuilder()));
    }
    
    public final void setStructurePattern(Pattern... patterns) {
        if (AntimatterAPI.getSIDE().isClient()) {
            if (patterns.length <= 0) return;
            AntimatterPlatformUtils.addMultiMachineInfo(this, Arrays.stream(patterns).collect(Collectors.toList()));
        }
    }
}
