
package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class ItemCover extends ItemBasic<ItemCover> {
    private Cover cover;
    public ItemCover(String domain, String id, Properties properties) {
        super(domain, id, properties);
    }
    public ItemCover(String domain, String id, Cover cover) {
        super(domain,id);
        cover.onRegister();
        this.cover = cover;
        cover.setItem(this);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        if (tile != null) {
            LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE, context.getFace());
            return coverable.map(i -> i.onPlace(context.getFace(), cover.onNewInstance(context.getItem()))).orElse(false) ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        return ActionResultType.PASS;
    }
}