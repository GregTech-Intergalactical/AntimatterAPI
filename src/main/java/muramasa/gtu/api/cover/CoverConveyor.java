package muramasa.gtu.api.cover;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;

public class CoverConveyor extends Cover {

    protected Tier tier;

    public CoverConveyor(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String getId() {
        return "conveyor";
    }

    @Override
    public ItemStack getDroppedStack() {
        //TODO maybe a better way to do this? but for now, it works.
        //TODO coverStacks should probably be tier sensitive? this would mean
        //TODO all covers would need a tier member
        switch (tier.getId()) {
            case "lv": return Data.ConveyorLV.get(1);
            case "mv": return Data.ConveyorMV.get(1);
            case "hv": return Data.ConveyorHV.get(1);
            case "ev": return Data.ConveyorEV.get(1);
            case "iv": return Data.ConveyorIV.get(1);
            default: return Data.ConveyorLV.get(1);
        }
    }

    @Override
    public void onUpdate(TileEntity tile, EnumFacing side) {
        if (tile.getWorld().getTotalWorldTime() % 20 != 0) return;
        TileEntity adjTile = tile.getWorld().getTileEntity(tile.getPos().offset(side));
        if (adjTile == null) return;
        if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()) || !adjTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) return;
        Utils.transferItems(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()), adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()));
    }
}
