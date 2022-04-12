package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IHeatHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import tesseract.api.Controller;
import tesseract.api.GraphWrapper;
import tesseract.api.ITickingController;
import tesseract.api.capability.ITransactionModifier;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;
import tesseract.graph.*;
import tesseract.util.Pos;

import java.util.List;
import java.util.Map;

public class HeatController extends Controller<IHeatHandler.HeatTransaction,IHeatPipe,IHeatHandler> {

    public static void init() {

    }

    public static final GraphWrapper<IHeatHandler.HeatTransaction,IHeatPipe,IHeatHandler> HEAT_CONTROLLER =
            new GraphWrapper<>(HeatController::new, ((level, pos, capSide, invalidate) -> {
                BlockEntity tile = level.getBlockEntity(BlockPos.of(pos));
                if (tile == null) return null;
                LazyOptional<IHeatHandler> capability = tile.getCapability(IHeatHandler.HEAT_CAPABILITY, capSide);
                if (capability.isPresent()) {
                    if (invalidate != null) capability.addListener(t -> invalidate.run());
                    return capability.resolve().get();
                }
                return null;
            }));

    private final Long2IntMap previousTemperature = new Long2IntOpenHashMap();
    private final Long2IntMap currentTemperature = new Long2IntOpenHashMap();
    private final Long2IntMap connectedCount = new Long2IntOpenHashMap();

    /**
     * Creates instance of the controller.
     *
     * @param supplier The world.
     * @param getter the getter for nodes.
     */
    protected HeatController(Level supplier, Graph.INodeGetter<IHeatHandler> getter) {
        super(supplier, getter);
        previousTemperature.defaultReturnValue(-1);
        currentTemperature.defaultReturnValue(-1);
        connectedCount.defaultReturnValue(1);
    }

    @Override
    protected void onFrame() {
        for (Long2ObjectMap.Entry<Cache<IHeatPipe>> entry : this.group.connectorsEntries()) {
            long pos = entry.getLongKey();
            IHeatPipe value = entry.getValue().value();
            final int counts = connectedCount.get(pos);
            value.update(true);
            if (counts == 0) {
                IHeatHandler.HeatTransaction transaction = value.extract();
                transaction.addData(transaction.available(), -1, a -> {});
                transaction.commit();
                continue;
            }
            /*Connectivity.connectivityFor(value, dir -> {
                IHeatHandler.HeatTransaction transaction = value.extract();
                if (!transaction.isValid()) return;
                transaction.limitHeat(value.temperatureCoefficient() / counts);
                transfer(pos, dir,transaction);
                transaction.addData(transaction.available(), -1, a -> {});
                transaction.commit();
            });*/

            //Transfer q to both directions.
        }
    }

    @Override
    public void change() {
        connectedCount.clear();
        for (Long2ObjectMap.Entry<Cache<IHeatPipe>> entry : this.group.connectorsEntries()) {
            long pos = entry.getLongKey();
            IHeatPipe value = entry.getValue().value();
            int count = 0;
            for (Direction dir : Ref.DIRS) {
                if (value.validate(dir)) count++;
            }
            connectedCount.put(pos, count);
        }
    }

    private int approximateDerivative(long pos, IHeatPipe pipe) {
        int prev = previousTemperature.get(pos);
        int current = pipe.getTemperature();
        if (prev == -1) return current;
        //1 / (1/20)
        return 20*(current - prev);
    }

    @Override
    public void tick() {
        super.tick();
    }

    private void transfer(long pos, Direction dir, IHeatHandler.HeatTransaction tx) {
        long off = Pos.offset(pos, dir);
        Cache<IHeatPipe> conn = this.group.getConnector(off);
        if (conn != null) {
            conn.value().insert(tx);
        } else {
            NodeCache<IHeatHandler> node = this.group.getNodes().get(off);
            if (node != null) node.value(dir.getOpposite()).insert(tx);
        }
    }

    @Override
    public ITickingController<IHeatHandler.HeatTransaction, IHeatPipe, IHeatHandler> clone(INode group) {
        return new HeatController(this.dim, this.getter).set(group);
    }

    @Override
    public void getInfo(long pos, @NotNull List<String> list) {

    }

    @Override
    public void insert(long producerPos, Direction side, IHeatHandler.HeatTransaction transaction, ITransactionModifier modifier) {

    }
}
