package muramasa.antimatter.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import tesseract.api.Transaction;

import java.util.function.Consumer;

public interface IHeatHandler {

    HeatTransaction extract();

    void insert(HeatTransaction transaction);

    int getHeat();
    int getHeatCap();
    void update(boolean active);

    default int getTemperature() {
        return getHeat() / 100;
    }

    Capability<IHeatHandler> HEAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    static void register(RegisterCapabilitiesEvent ev) {
        ev.register(IHeatHandler.class);
    }

    class HeatTransaction extends Transaction<Integer> {

        private int heatSize;
        private int temperature;
        private int usedHeat;

        public HeatTransaction(int heatSize, int temperature, Consumer<Integer> con) {
            super(con);
            this.heatSize = heatSize;
            this.temperature = temperature;
        }

        public void limitHeat(int heat) {
            this.heatSize = Math.min(heat, heatSize);
            this.heatSize = Math.max(heatSize, 0);
        }

        public int getTemperature() {
            return temperature;
        }

        public HeatTransaction ignoreTemperature() {
            this.temperature = -1;
            return this;
        }

        @Override
        public boolean isValid() {
            return heatSize > 0 && this.temperature > 0;
        }

        @Override
        public boolean canContinue() {
            return usedHeat < heatSize;
        }

        public int available() {
            return heatSize - usedHeat;
        }

        public int getUsedHeat() {
            return usedHeat;
        }

        public void addData(int heatAmount, int temperature, Consumer<Integer> consumer) {
            if (heatAmount == 0) return;
            if (temperature > this.temperature && this.temperature != -1 && temperature != -1) return;
            this.usedHeat += heatAmount;
            this.addData(heatAmount);
            this.onCommit(consumer);
        }
    }
}
