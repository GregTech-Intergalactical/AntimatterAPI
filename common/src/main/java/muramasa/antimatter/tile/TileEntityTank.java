package muramasa.antimatter.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.machine.types.TankMachine;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class TileEntityTank<T extends TileEntityMachine<T>> extends TileEntityMachine<T> implements IInfoRenderer<TankMachine.TankRenderWidget> {

    public TileEntityTank(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    public int drawInfo(TankMachine.TankRenderWidget instance, PoseStack stack, Font renderer, int left, int top) {
        left = left + 55;
        top = top + 24;
        renderer.draw(stack, instance.stack.getDisplayName().getString(), left, top, 16448255);
        StringBuilder fluidAmount = new StringBuilder().append(instance.stack.getAmount());
        if (AntimatterPlatformUtils.isFabric()){
            fluidAmount.append(" ").append(instance.stack.getRealAmount() % 81L).append("/81");
        }
        renderer.draw(stack, fluidAmount.toString() + " mb", left, top + 8, 16448255);
        return 16;
    }
}
