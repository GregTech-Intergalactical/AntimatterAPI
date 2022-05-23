package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.TesseractPlatformUtils;
import tesseract.api.TesseractCaps;
import tesseract.api.heat.HeatTransaction;
import tesseract.api.heat.IHeatHandler;

public class DefaultHeatHandler implements IHeatHandler, Dispatch.Sided<IHeatHandler> {

    public final int heatCap;
    public final int temperaturesize;
    protected int currentHeat;

    public final TileEntityBase<?> tile;

    public DefaultHeatHandler(TileEntityBase<?> tile, int heatCap, int temperatureSize) {
        this.heatCap = heatCap;
        this.tile = tile;
        this.temperaturesize = temperatureSize;
    }
    @Override
    public HeatTransaction extract() {
        //if (this.currentHeat < this.temperaturesize) return new HeatTransaction(0,0, Utils.sink());
        if (this.currentHeat < this.temperaturesize) return new HeatTransaction(0,0, Utils.sink());
        return new HeatTransaction(this.temperaturesize, getTemperature(), this::sub);
    }

    @Override
    public void insert(HeatTransaction transaction) {
        int availableToInsert = transaction.available();
        availableToInsert = Math.min(heatCap - currentHeat, availableToInsert);
        if (availableToInsert > 0) {
            transaction.addData(availableToInsert, getTemperature(), this::add);
        }
    }

    protected void sub(Integer temp) {
        this.currentHeat -= temp;
    }

    protected void add(Integer temp) {
        this.currentHeat += temp;
    }

    @Override
    public int getTemperature() {
        return this.currentHeat / temperaturesize;
    }

    public void update(boolean active) {
        boolean doTransfer = tile.getLevel().getGameTime() % 20 == 0;

        if (doTransfer) {
            //Transfer 1 degree of power each second.
            HeatTransaction tx = extract();
            if (tx.isValid()) {
                Utils.entitiesAround(tile.getLevel(), tile.getBlockPos(), (dir, ent) -> TesseractPlatformUtils.getCapability(ent,TesseractCaps.getHEAT_CAPABILITY(), dir.getOpposite()).ifPresent(t -> t.insert(tx)));
                tx.commit();
            }
        }
        if (!active) {
            this.currentHeat -= temperaturesize / 40;
            this.currentHeat = Math.max(0, this.currentHeat);
        }
    }

    @Override
    public int getHeat() {
        return currentHeat;
    }

    @Override
    public int getHeatCap() {
        return heatCap;
    }

    @Override
    public LazyOptional<? extends IHeatHandler> forSide(Direction side) {
        if (tile instanceof TileEntityMachine<?> m) {
            if (side == null) return LazyOptional.of(() -> this);
            if (m.coverHandler.map(t -> t.get(side).getFactory() == Data.COVERHEAT).orElse(false)) {
                return LazyOptional.of(() -> this);
            } else {
                return LazyOptional.empty();
            }
        }
        return LazyOptional.of(() -> this);
    }

    @Override
    public LazyOptional<? extends IHeatHandler> forNullSide() {
        return forSide(null);
    }
}
