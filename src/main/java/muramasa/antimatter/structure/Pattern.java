package muramasa.antimatter.structure;

import net.minecraft.network.chat.Component;

public class Pattern {
    private final BlockInfo[][][] blockInfos;
    private final Component description;

    public Pattern(BlockInfo[][][] blockInfos, Component description) {
        this.blockInfos = blockInfos;
        this.description = description;
    }

    public BlockInfo[][][] getBlockInfos() {
        return blockInfos;
    }

    public Component getDescription() {
        return description;
    }
}
