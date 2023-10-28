package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.network.chat.Component;

import java.util.List;

@Getter
public class Pattern {
    private final BlockInfo[][][] blockInfos;
    private final Component description;
    private final float scale;
    private final Int2ObjectMap<List<PonderTooltip>> ponderTooltipMap;

    public Pattern(BlockInfo[][][] blockInfos, Component description, float scale, Int2ObjectMap<List<PonderTooltip>> ponderTooltipMap) {
        this.blockInfos = blockInfos;
        this.description = description;
        this.scale = scale;
        this.ponderTooltipMap = ponderTooltipMap;
    }


    public record PonderTooltip(int x, int z, String tooltip){}
}
