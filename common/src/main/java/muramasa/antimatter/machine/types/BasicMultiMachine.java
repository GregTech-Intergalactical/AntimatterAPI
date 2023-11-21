package muramasa.antimatter.machine.types;

import lombok.Getter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.gui.widget.ProgressWidget;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.structure.PatternBuilder;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.MULTI;

public class BasicMultiMachine<T extends BasicMultiMachine<T>> extends Machine<T> {

    @Getter
    Function<Tier, BlockBasic> textureBlock;

    public BasicMultiMachine(String domain, String name) {
        super(domain, name);
        setTile(BlockEntityBasicMultiMachine::new);
        setBlock(BlockMultiMachine::new);
        setItemBlockClass(() -> BlockMultiMachine.class);
        addFlags(MULTI, COVERABLE);
        setClientTicking();
        setGUI(Data.BASIC_MENU_HANDLER);
        covers((CoverFactory[]) null);
        addTooltipInfo((machine, stack, world, tooltip, flag) -> {
            if (machine.getType().getStructure(machine.getTier()) != null) {
                tooltip.add(Utils.translatable("machine.structure.form"));
            }
        });
        this.baseTexture((type, tier, state) -> type.getTiers().size() > 1 ? new Texture[]{new Texture(domain, "block/machine/base/" + type.getId() + "_" + tier.getId())} : new Texture[]{new Texture(domain, "block/machine/base/" + type.getId())});
     }

    @Override
    protected void setupGui() {
        super.setupGui();
        if (!(this instanceof MultiMachine)) addGuiCallback(t -> t.addWidget(ProgressWidget.build()));
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        getTiers().forEach(t -> textures.addAll(Arrays.asList(getBaseTexture(t, MachineState.INVALID_STRUCTURE))));
        for (int i = 0; i < overlayLayers; i++) {
            int finalI = i;
            getTiers().forEach(t -> textures.addAll(Arrays.asList(getOverlayTextures(MachineState.INVALID_STRUCTURE, t, finalI))));
        }

        return textures;
    }

    public final void setStructurePattern(Function<PatternBuilder, Pattern> patterns) {
        setStructurePattern(patterns.apply(new PatternBuilder()));
    }
    
    public final void setStructurePattern(Pattern... patterns) {
        if (AntimatterAPI.getSIDE().isClient()) {
            if (patterns.length == 0) return;
            AntimatterJEIREIPlugin.registerPatternForJei(this, Arrays.stream(patterns).collect(Collectors.toList()));
        }
    }

    public final void setStructurePattern(Tier tier, Function<PatternBuilder, Pattern> patterns) {
        setStructurePattern(tier, patterns.apply(new PatternBuilder()));
    }

    public final void setStructurePattern(Tier tier,  Pattern... patterns) {
        if (AntimatterAPI.getSIDE().isClient()) {
            if (patterns.length == 0) return;
            AntimatterJEIREIPlugin.registerPatternForJei(this, tier, Arrays.stream(patterns).collect(Collectors.toList()));
        }
    }

    public T setTextureBlock(BlockBasic textureBlock){
        this.textureBlock = t -> textureBlock;
        return (T) this;
    }

    public T setTextureBlock(Function<Tier, BlockBasic> textureBlock){
        this.textureBlock = textureBlock;
        return (T) this;
    }
}
