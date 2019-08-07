package muramasa.gtu.api.capability;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public interface IIntractable {

    //TODO actually, this can probably go in GregTechAPI
    default boolean onInteract(TileEntity tile, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumFacing targetSide = Utils.getInteractSide(side, hitX, hitY, hitZ);
        if (GregTechAPI.placeCover(tile, player, player.getHeldItem(hand), targetSide, hitX, hitY, hitZ)) return true;
        if (tile.hasCapability(GTCapabilities.COVERABLE, targetSide)) {
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
            if (coverHandler != null && coverHandler.onInteract(player, hand, targetSide, ToolType.get(player.getHeldItem(hand)))) return true;
        }
        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, targetSide)) {
            IConfigHandler configHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
            if (configHandler != null && configHandler.onInteract(player, hand, targetSide, ToolType.get(player.getHeldItem(hand)))) return true;
        }
        return false;
    }
}
