package muramasa.antimatter.tesseract;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.fluids.FluidStack;
import tesseract.api.fluid.FluidController;
import tesseract.api.fluid.FluidData;

import javax.annotation.Nonnull;

// TODO: Make explosions depend on pressure, capacity, temperature
public class AntimatterFluidController extends FluidController {

    /**
     * Creates instance of the controller.
     *
     * @param dim The dimension id.
     */
    public AntimatterFluidController(int dim) {
        super(dim);
    }

    @Override
    public void onPipeOverPressure(int dim, long pos, int pressure) {
        Utils.getServerWorld(dim).ifPresent(w -> Utils.createExplosion(w, BlockPos.fromLong(pos), 4.0F, Explosion.Mode.BREAK));
    }

    @Override
    public void onPipeOverCapacity(int dim, long pos, int capacity) {
        Utils.getServerWorld(dim).ifPresent(w -> Utils.createExplosion(w, BlockPos.fromLong(pos), 1.0F, Explosion.Mode.NONE));
    }

    @Override
    public void onPipeOverTemp(int dim, long pos, int temperature) {
        Utils.getServerWorld(dim).ifPresent(w -> w.setBlockState(BlockPos.fromLong(pos), temperature >= Fluids.LAVA.getAttributes().getTemperature() ? Blocks.LAVA.getDefaultState() : Blocks.FIRE.getDefaultState()));
    }

    @Override
    public void onPipeGasLeak(int dim, long pos, @Nonnull FluidData fluid) {
        FluidStack resource = (FluidStack) fluid.getFluid();
        resource.setAmount((int)(resource.getAmount() * AntimatterConfig.GAMEPLAY.PIPE_LEAK));
    }
}
