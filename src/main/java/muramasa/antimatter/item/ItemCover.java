package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.impl.MachineCoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;

public class ItemCover extends ItemBasic<ItemCover> {
    public ItemCover(String domain, String id, Properties properties) {
        super(domain, id, properties);
    }

    public ItemCover(String domain, String id, Cover cover) {
        super(domain,id);
        AntimatterAPI.registerCover(cover);
        AntimatterAPI.registerCoverStack(this.get(1),cover);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        //TODO: only on server? Sync covers. For now do on both
     //   if (context.getWorld().isRemote) return super.onItemUse(context);
        TileEntity te = context.getWorld().getTileEntity(context.getPos());
        if (te == null) {
            return ActionResultType.PASS;
        }
        if (!(te instanceof TileEntityMachine)) {
            return ActionResultType.PASS;
        }
        //TODO: TileEntityBase for pipes.
        TileEntityMachine m = (TileEntityMachine) te;
        if (!m.coverHandler.isPresent()) {
            return ActionResultType.PASS;
        }
        MachineCoverHandler h = m.coverHandler.get();
        //Get a new cover instance.
        Cover c = AntimatterAPI.getCoverFromCatalyst(context.getItem()).onNewInstance(context.getItem());
        return h.onPlace(context.getFace(), c) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

}
