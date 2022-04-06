package muramasa.antimatter.tesseract;

import muramasa.antimatter.capability.IHeatHandler;
import tesseract.api.IConnectable;

public interface IHeatPipe extends IConnectable, IHeatHandler {

    /**
     * Returns the heat coefficient of this heat pipes material, q = -k*delta => k
     * @return the value.
     */
    int temperatureCoefficient();
}
