package muramasa.antimatter.pipe;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class FluidPipeTicker {
    public static final List<BlockEntityFluidPipe<?>> SERVER_TICK_PRE = new ArrayList<>(), SERVER_TICK_PR2 = new ArrayList<>();

    public static void onServerWorldTick(MinecraftServer server){
        for (int i = 0; i < SERVER_TICK_PRE.size(); i++) {
            BlockEntityFluidPipe<?> tTileEntity = SERVER_TICK_PRE.get(i);
            if (tTileEntity == null || tTileEntity.isRemoved()) {
                SERVER_TICK_PRE.remove(i--);
                if (tTileEntity != null){
                    tTileEntity.onUnregisterPre();
                }
            } else {
                try {
                    tTileEntity.onServerTickPre(tTileEntity.getLevel(), tTileEntity.getBlockPos(), true);
                } catch(Throwable e) {
                    SERVER_TICK_PRE.remove(i--);
                    //tTileEntity.setError("Server Tick Pre 1 - " + e);
                    Antimatter.LOGGER.error("Pipe errored", e);
                }
            }
        }
        for (int i = 0; i < SERVER_TICK_PR2.size(); i++) {
            BlockEntityFluidPipe<?> tTileEntity = SERVER_TICK_PR2.get(i);
            if (tTileEntity == null || tTileEntity.isRemoved()) {
                SERVER_TICK_PR2.remove(i--);
                if (tTileEntity != null){
                    tTileEntity.onUnregisterPre();
                }
            } else {
                try {
                    tTileEntity.onServerTickPre(tTileEntity.getLevel(), tTileEntity.getBlockPos(), false);
                } catch(Throwable e) {
                    SERVER_TICK_PR2.remove(i--);
                    //tTileEntity.setError("Server Tick Pre 1 - " + e);
                    Antimatter.LOGGER.error("Pipe errored", e);
                }
            }
        }
    }
}
