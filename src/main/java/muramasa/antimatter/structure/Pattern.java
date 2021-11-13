package muramasa.antimatter.structure;

import net.minecraft.util.text.ITextComponent;

public class Pattern {
    private final BlockInfo[][][] blockInfos;
    private final ITextComponent description;

    public Pattern(BlockInfo[][][] blockInfos, ITextComponent description) {
        this.blockInfos = blockInfos;
        this.description = description;
    }

    public BlockInfo[][][] getBlockInfos() {
        return blockInfos;
    }

    public ITextComponent getDescription() {
        return description;
    }
}
