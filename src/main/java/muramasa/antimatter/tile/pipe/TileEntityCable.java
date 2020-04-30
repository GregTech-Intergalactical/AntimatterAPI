package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class TileEntityCable extends TileEntityPipe implements IElectricCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);

        World world = getWorld();
        if (world != null && !world.isRemote())
            TesseractAPI.registerElectricCable(world.getDimension().getType().getId(), pos.toLong(), this);
    }

    @Override
    public void remove() {
        World world = getWorld();
        if (world != null && !world.isRemote())
            TesseractAPI.removeElectric(world.getDimension().getType().getId(), getPos().toLong());
        super.remove();
    }

    @Override
    public int getVoltage() {
        return ((Cable<?>)getPipeType()).getTier().getVoltage();
    }

    @Override
    public int getLoss() {
        return ((Cable<?>)getPipeType()).getLoss();
    }

    @Override
    public int getAmps() {
        return ((Cable<?>)getPipeType()).getAmps(getPipeSize());
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }
}
