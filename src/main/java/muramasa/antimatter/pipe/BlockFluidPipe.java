package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.api.fluid.FluidController;
import tesseract.api.fluid.FluidHolder;
import tesseract.api.gt.GTController;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BlockFluidPipe<T extends FluidPipe<T>> extends BlockPipe<T> {

    public BlockFluidPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController<?, ?, ?> controller = Tesseract.FLUID.getController(world, pos.toLong());
        if (controller != null) controller.getInfo(pos.toLong(), info);
        info.add("Pressure: " + getType().getPressure(getSize()));
        info.add("Capacity: " + getType().getCapacity(getSize()));
        info.add("Max temperature: " + getType().getTemperature());
        info.add(getType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        return info;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent("Pressure: " + getType().getPressure(getSize())));
        tooltip.add(new StringTextComponent("Capacity: " + getType().getCapacity(getSize())));
        tooltip.add(new StringTextComponent("Max temperature: " + getType().getTemperature()));
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        if (!(entityIn instanceof LivingEntity)) return;
        ITickingController<?, ?, ?> controller = Tesseract.FLUID.getController(worldIn, pos.toLong());
        if (!(controller instanceof FluidController)) return;
        FluidController gt = (FluidController) controller;
        FluidHolder holder = gt.getCableHolder(pos.toLong());
        if (holder == null) return;
        long max = 0;
        for (Fluid fluid : holder.getFluids()) {
            max = Math.max(max, fluid.getAttributes().getTemperature());
        }
        if (max >= (295 + 100)) {
            entityIn.attackEntityFrom(DamageSource.GENERIC, MathHelper.clamp(((max + 200) - (295 + 100)) / 100, 2, 20));
        }
    }

    //    @Override
//    public ITextComponent getDisplayName(ItemStack stack) {
//        //TODO add prefix and suffix for local
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + material.getDisplayName() + " Fluid Pipe";
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        //TODO localize
//        tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
//        tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
//    }
}
