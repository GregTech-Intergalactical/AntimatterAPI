package muramasa.gregtech.api.cover;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class CoverHelper {

    public static boolean placeCover(TileEntity tile, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
            ICoverHandler coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, side);
            if (coverHandler == null) return false;
            CoverBehaviour cover = GregTechAPI.getCover(stack);
            if (cover == null) return false;
            EnumFacing targetSide = Utils.determineInteractionSide(side, hitX, hitY, hitZ);
            if (cover.needsNewInstance()) {
                cover = cover.getNewInstance(stack);
            }
            if (coverHandler.setCover(targetSide, cover)) {
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }
}
