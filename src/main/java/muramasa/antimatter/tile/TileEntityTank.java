package muramasa.antimatter.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.machine.types.TankMachine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class TileEntityTank<T extends TileEntityMachine<T>> extends TileEntityMachine<T> implements IInfoRenderer<TankMachine.TankRenderWidget> {

    public TileEntityTank(Machine<?> type) {
        super(type);
        fluidHandler.set(() -> new MachineFluidHandler<T>((T) this) {
            @Nullable
            @Override
            public FluidTanks getOutputTanks() {
                return super.getInputTanks();
            }

            @Override
            protected FluidTank getTank(int tank) {
                return getInputTanks().getTank(tank);
            }

            @Override
            public FluidTanks getTanks(int tank) {
                return getInputTanks();
            }
        });
    }

    @Override
    public int drawInfo(TankMachine.TankRenderWidget instance, MatrixStack stack, FontRenderer renderer, int left, int top) {
        left = left + 55;
        top = top + 24;
        renderer.drawString(stack, instance.stack.getDisplayName().getString(), left, top, 16448255);
        renderer.drawString(stack, instance.stack.getAmount() + " mb", left, top + 8, 16448255);
        return 16;
    }
}
