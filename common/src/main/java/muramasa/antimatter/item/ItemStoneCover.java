package muramasa.antimatter.item;

import lombok.Getter;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;

public class ItemStoneCover extends ItemCover {
    final ITextureProvider stone;
    final String stoneId;
    @Getter
    final String suffix;
    public ItemStoneCover(String domain, String stoneId, String suffix, ITextureProvider stone) {
        super(domain, (suffix.isEmpty() ? stoneId : stoneId + "_" + suffix) + "_cover");
        this.stone = stone;
        this.stoneId = stoneId;
        this.suffix = suffix;
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(prov.existing("antimatter", "block/cover/cover_inventory")).texture("overlay", stone.getTextures()[stone.getTextures().length == 6 ? Direction.NORTH.get3DDataValue() : 0]);
    }
}
