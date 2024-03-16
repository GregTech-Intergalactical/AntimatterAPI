package muramasa.antimatter.item;

import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;

public class ItemStoneCover extends ItemCover {
    final ITextureProvider stone;
    public ItemStoneCover(String domain, String id, ITextureProvider stone) {
        super(domain, id);
        this.stone = stone;
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(prov.existing("antimatter", "block/cover/cover_inventory")).texture("overlay", stone.getTextures()[stone.getTextures().length == 6 ? Direction.NORTH.get3DDataValue() : 0]);
    }
}
