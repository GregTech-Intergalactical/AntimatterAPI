package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.machine.Tier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
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

    public ItemCover(String domain, String id, Tier tier) {
        super(domain,id + "_" + tier.getId());
        cover = Objects.requireNonNull(AntimatterAPI.get(Cover.class, this.getId()));
        cover.setItem(this);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        if (tile != null) {
            LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE, context.getFace());
            return coverable.map(i -> i.onPlace(context.getFace(),this.cover)).orElse(false) ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        return ActionResultType.PASS;
    }
}
