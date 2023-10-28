package muramasa.antimatter.structure;

import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public class Pattern {
    private final BlockInfo[][][] blockInfos;
    private final Component description;
    private final float scale;

    public Pattern(BlockInfo[][][] blockInfos, Component description, float scale) {
        this.blockInfos = blockInfos;
        this.description = description;
        this.scale = scale;
    }

}
