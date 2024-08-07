package muramasa.antimatter.pipe;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.blockentity.IPostTickTile;
import muramasa.antimatter.blockentity.IPreTickTile;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class TileTicker {
    public static final List<IPreTickTile> SERVER_TICK_PRE = new ArrayList<>(), SERVER_TICK_PR2 = new ArrayList<>();
    public static final List<IPostTickTile> SERVER_TICK_POST = new ArrayList<>(), SERVER_TICK_PO2T = new ArrayList<>();

    private static final List<Runnable> TICK_FUNCTIONS = new ArrayList<>();

    public static void onServerWorldTick(MinecraftServer server, boolean pre){
        if (pre){
            synchronized (TICK_FUNCTIONS){
                TICK_FUNCTIONS.forEach(Runnable::run);
                TICK_FUNCTIONS.clear();
            }
            for (int i = 0; i < SERVER_TICK_PRE.size(); i++) {
                IPreTickTile tTileEntity = SERVER_TICK_PRE.get(i);
                if (tTileEntity == null || tTileEntity.getBlockEntity().isRemoved()) {
                    SERVER_TICK_PRE.remove(i--);
                    if (tTileEntity != null){
                        tTileEntity.onUnregisterPre();
                    }
                } else {
                    try {
                        tTileEntity.onServerTickPre(tTileEntity.getBlockEntity().getLevel(), tTileEntity.getBlockEntity().getBlockPos(), true);
                    } catch(Throwable e) {
                        SERVER_TICK_PRE.remove(i--);
                        //tTileEntity.setError("Server Tick Pre 1 - " + e);
                        Antimatter.LOGGER.error("Pipe errored", e);
                    }
                }
            }
            for (int i = 0; i < SERVER_TICK_PR2.size(); i++) {
                IPreTickTile tTileEntity = SERVER_TICK_PR2.get(i);
                if (tTileEntity == null || tTileEntity.getBlockEntity().isRemoved()) {
                    SERVER_TICK_PR2.remove(i--);
                    if (tTileEntity != null){
                        tTileEntity.onUnregisterPre();
                    }
                } else {
                    try {
                        tTileEntity.onServerTickPre(tTileEntity.getBlockEntity().getLevel(), tTileEntity.getBlockEntity().getBlockPos(), false);
                    } catch(Throwable e) {
                        SERVER_TICK_PR2.remove(i--);
                        //tTileEntity.setError("Server Tick Pre 1 - " + e);
                        Antimatter.LOGGER.error("Pipe errored", e);
                    }
                }
            }
        } else {
            for (int i = 0; i < SERVER_TICK_POST.size(); i++) {
                IPostTickTile tTileEntity = SERVER_TICK_POST.get(i);
                if (tTileEntity == null || tTileEntity.getBlockEntity().isRemoved() || (tTileEntity.getBlockEntity().getLevel() != null && tTileEntity.getBlockEntity().getLevel().isClientSide())) {
                    SERVER_TICK_POST.remove(i--);
                    if (tTileEntity != null){
                        tTileEntity.onUnregisterPost();
                    }
                } else {
                    try {
                        tTileEntity.onServerTickPost(tTileEntity.getBlockEntity().getLevel(), tTileEntity.getBlockEntity().getBlockPos(), true);
                    } catch(Throwable e) {
                        SERVER_TICK_POST.remove(i--);
                        //tTileEntity.setError("Server Tick Pre 1 - " + e);
                        Antimatter.LOGGER.error("Pipe errored", e);
                    }
                }
            }
            for (int i = 0; i < SERVER_TICK_PO2T.size(); i++) {
                IPostTickTile tTileEntity = SERVER_TICK_PO2T.get(i);
                if (tTileEntity == null || tTileEntity.getBlockEntity().isRemoved() || (tTileEntity.getBlockEntity().getLevel() != null && tTileEntity.getBlockEntity().getLevel().isClientSide())) {
                    SERVER_TICK_PO2T.remove(i--);
                    if (tTileEntity != null){
                        tTileEntity.onUnregisterPost();
                    }
                } else {
                    try {
                        tTileEntity.onServerTickPost(tTileEntity.getBlockEntity().getLevel(), tTileEntity.getBlockEntity().getBlockPos(), false);
                    } catch(Throwable e) {
                        SERVER_TICK_PO2T.remove(i--);
                        //tTileEntity.setError("Server Tick Pre 1 - " + e);
                        Antimatter.LOGGER.error("Pipe errored", e);
                    }
                }
            }
        }

    }

    public static void addTickFunction(Runnable function){
        synchronized (TICK_FUNCTIONS){
            TICK_FUNCTIONS.add(function);
        }
    }
}
