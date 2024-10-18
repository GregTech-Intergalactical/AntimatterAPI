package muramasa.antimatter.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.fluid.FluidTank;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.machine.types.TankMachine;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;

import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class BlockEntityTank<T extends BlockEntityMachine<T>> extends BlockEntityMachine<T> implements IInfoRenderer<TankMachine.TankRenderWidget> {

    public BlockEntityTank(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int capacity = type instanceof TankMachine tankMachine ? tankMachine.getCapacityPerTier().apply(tier) : 8000 * (1 + tier.getIntegerId());
        fluidHandler.set(() -> new MachineFluidHandler<T>((T) this, capacity, 8000) {
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
        if (instance.stack.isEmpty()){
            renderer.draw(stack, "Empty", left, top, 0xFAFAFF);
            return 8;
        }
        renderer.draw(stack, FluidPlatformUtils.INSTANCE.getFluidDisplayName(instance.stack).getString(), left, top, 0xFAFAFF);
        StringBuilder fluidAmount = new StringBuilder().append(instance.stack.getFluidAmount() / TesseractGraphWrappers.dropletMultiplier);
        if (AntimatterPlatformUtils.INSTANCE.isFabric()){
            fluidAmount.append(" ").append(intToSuperScript((int) (instance.stack.getFluidAmount() % 81L))).append("/₈₁");
        }
        renderer.draw(stack, fluidAmount.toString() + " mb", left, top + 8, 16448255);
        return 16;
    }

    private String intToSuperScript(int i){
        String intString = String.valueOf(i);
        StringBuilder builder = new StringBuilder();
        for (char c : intString.toCharArray()) {
            builder.append(charToSuperScript(c));
        }
        return builder.toString();
    }

    private String charToSuperScript(char c){
        return switch (c){
            case '0' -> "⁰";
            case '1' -> "¹";
            case '2' -> "²";
            case '3' -> "³";
            case '4' -> "⁴";
            case '5' -> "⁵";
            case '6' -> "⁶";
            case '7' -> "⁷";
            case '8' -> "⁸";
            case '9' -> "⁹";
            default -> String.valueOf(c);
        };
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        setAutoOutput();
    }

    protected void setAutoOutput(){
        coverHandler.ifPresent(t -> {
            ICover cover = t.getOutputCover();
            if (!(cover instanceof CoverOutput))
                return;
            ((CoverOutput) cover).setEjects(has(FLUID), has(ITEM));
        });
    }
}
