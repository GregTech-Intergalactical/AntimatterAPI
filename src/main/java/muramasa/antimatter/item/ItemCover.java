package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.machine.Tier;

import java.util.Objects;

public class ItemCover extends ItemBasic<ItemCover> {

    private Cover cover;

    public ItemCover(String domain, String id, Properties properties) {
        super(domain, id, properties);
    }

    public ItemCover(String domain, String id) {
        super(domain, id);
        cover = Objects.requireNonNull(AntimatterAPI.get(Cover.class, this.getId()));
        if (cover instanceof CoverTiered) {
            throw new RuntimeException("Invalid non-tiered cover instantiation");
        }
        cover.setItem(this);
    }

    public Cover getCover() {
        return cover;
    }

    public ItemCover(String domain, String id, Tier tier) {
        super(domain,id + "_" + tier.getId());
        cover = Objects.requireNonNull(AntimatterAPI.get(Cover.class, this.getId()));
        cover.setItem(this);
    }

    //TODO: This works but it also opens the gui :/
    /*
    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        return itemPlaceCover(context.getPlayer(),context.getItem(),tile, context.getFace()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    private boolean itemPlaceCover(PlayerEntity player, ItemStack stack, TileEntity tile, Direction dir) {
        if (tile != null) {
            LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY,dir);
            //since we are holding an ItemCover it will try to place it
            return coverable.map(i -> i.placeCover(player,dir,stack,((ItemCover) stack.getItem()).getCover())).orElse(false);
        }
        return false;
    }*/
}
