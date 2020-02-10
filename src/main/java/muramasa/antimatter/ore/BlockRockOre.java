package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockBasic;
import muramasa.antimatter.texture.Texture;

public class BlockRockOre extends BlockBasic {

    public BlockRockOre(String domain, String id, Texture... textures) {
        super(domain, "basic_" + id, textures);
        AntimatterAPI.register(BlockRockOre.class, this);
    }
}
