package muramasa.antimatter.gui;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.TesseractPlatformUtils;

public abstract class MenuHandlerCover<T extends ContainerCover> extends MenuHandler<T> {

    public MenuHandlerCover(String domain, String id) {
        super(domain, id);
    }

    @Override
    public T onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockEntity tile = Utils.getTileFromBuf(data);
        if (tile != null) {
            Direction dir = Direction.from3DDataValue(data.readInt());
            LazyOptional<ICoverHandler<?>> coverHandler = TesseractPlatformUtils.getCapability(tile, AntimatterCaps.getCOVERABLE_HANDLER_CAPABILITY(), dir);
            return menu(coverHandler.map(ch -> ch.get(dir)).orElse(null), inv, windowId);
        }
        return null;
    }
}
