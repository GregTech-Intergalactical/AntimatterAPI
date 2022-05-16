package muramasa.antimatter.gui;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.container.ContainerCover;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public abstract class MenuHandlerCover<T extends ContainerCover> extends MenuHandler<T> {

    public MenuHandlerCover(String domain, String id) {
        super(domain, id);
    }

    @Override
    public T onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockEntity tile = Utils.getTileFromBuf(data);
        if (tile != null) {
            Direction dir = Direction.from3DDataValue(data.readInt());
            LazyOptional<ICoverHandler<?>> coverHandler = tile.getCapability(AntimatterPlatformUtils.getCoverCap(), dir);
            return menu(coverHandler.map(ch -> ch.get(dir)).orElse(null), inv, windowId);
        }
        return null;
    }
}
