package muramasa.antimatter.item;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.item.types.CoverType;
import muramasa.antimatter.item.types.ItemType;
import muramasa.antimatter.machine.Tier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class ItemCover<T extends CoverType<?>> extends ItemComponent<T> {

    private Cover cover;

    public ItemCover(ItemType<?> type, Tier tier) {
        super(type, tier);
        cover = getType().getCover(tier);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        if (tile != null) {
            LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE, context.getFace());
            return coverable.map(i -> i.onPlace(context.getFace(), getCover().onNewInstance(context.getItem()))).orElse(false) ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        return ActionResultType.PASS;
    }

    public Cover getCover() {
        return cover;
    }
}
