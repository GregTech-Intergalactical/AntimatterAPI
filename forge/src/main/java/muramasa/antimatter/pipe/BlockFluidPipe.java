package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.ITickingController;
import tesseract.api.fluid.FluidHolder;
import tesseract.forge.TesseractImpl;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluidPipe<T extends FluidPipe<T>> extends BlockPipe<T> {

    public BlockFluidPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
    }

    @Override
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos) {
        if (world.isClientSide) return info;
        ITickingController<?, ?, ?> controller = TesseractImpl.FLUID.getController(world, pos.asLong());
        controller.getInfo(pos.asLong(), info);
        info.add("Pressure: " + getType().getPressure(getSize()));
        info.add("Capacity: " + getType().getCapacity(getSize()));
        info.add("Max temperature: " + getType().getTemperature());
        info.add(getType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        return info;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslatableComponent("antimatter.tooltip.pressure").append(": " +getType().getPressure(getSize())));
        tooltip.add(new TranslatableComponent("antimatter.tooltip.capacity").append(": "+ getType().getCapacity(getSize())));
        tooltip.add(new TranslatableComponent("antimatter.tooltip.max_temperature").append(": " +getType().getTemperature()));

        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("antimatter.tooltip.more").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.add(new TextComponent("----------"));
            tooltip.add(new TranslatableComponent("antimatter.pipe.fluid.info").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(new TextComponent("----------"));
        }
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (!(entityIn instanceof LivingEntity)) return;
        TileEntityFluidPipe pipe = (TileEntityFluidPipe) worldIn.getBlockEntity(pos);
        FluidHolder holder = pipe.getHolder();
        if (holder == null) return;
        long max = 0;
        for (FluidHolder.SetHolder fluid : holder.getFluids()) {
            max = Math.max(max, fluid.fluid.getAttributes().getTemperature());
        }
        if (max >= (295 + 100)) {
            entityIn.hurt(DamageSource.GENERIC, Mth.clamp(((max + 200) - (295 + 100)) / 100, 2, 20));
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
