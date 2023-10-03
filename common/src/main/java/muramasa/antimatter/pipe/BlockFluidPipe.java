package muramasa.antimatter.pipe;

import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tesseract.TesseractGraphWrappers;
import tesseract.api.ITickingController;

import java.util.List;

public class BlockFluidPipe<T extends FluidPipe<T>> extends BlockPipe<T> {

    public BlockFluidPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
    }

    @Override
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos) {
        if (world.isClientSide) return info;
        ITickingController<?, ?, ?> controller = TesseractGraphWrappers.FLUID.getController(world, pos.asLong());
        controller.getInfo(pos.asLong(), info);
        info.add("Pressure: " + getType().getPressure(getSize()));
        info.add("Max temperature: " + getType().getTemperature());
        info.add(getType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        info.add(getType().isAcidProof() ? "Acid proof." : "Cannot handle acids.");
        return info;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Utils.translatable("antimatter.tooltip.bandwidth", getType().getPressure(getSize()) + " L/t").withStyle(ChatFormatting.AQUA));
        tooltip.add(Utils.translatable("antimatter.tooltip.capacity", (getType().getPressure(getSize()) * 2) + "L").withStyle(ChatFormatting.AQUA));
        if (getType().isGasProof()){
            tooltip.add(Utils.translatable("antimatter.tooltip.gas_proof").withStyle(ChatFormatting.GOLD));
        }
        if (getType().isAcidProof()){
            tooltip.add(Utils.translatable("antimatter.tooltip.acid_proof").withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Utils.translatable("antimatter.tooltip.max_temperature").append(": " +getType().getTemperature()).withStyle(ChatFormatting.DARK_RED));
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (worldIn.isClientSide) return;
        if (entityIn instanceof LivingEntity entity) {
            if (worldIn.getBlockEntity(pos) instanceof BlockEntityFluidPipe<?> fluidPipe) {
                long temp = fluidPipe.getCurrentTemperature();
                applyTemperatureDamage(entity, temp, 1.0f, 1.0f);
            }
        }
    }

    public static boolean applyTemperatureDamage(Entity entity, long temperature, float multiplier, float cap) {
        if (temperature > 320) {
            entity.hurt(DamageSource.HOT_FLOOR, Math.max(1, Math.min(cap, (multiplier * (temperature - 300)) / 50.0F)));
            return true;
        }
        if (temperature < 260) {
            entity.hurt(DamageSource.FREEZE, Math.max(1, Math.min(cap, (multiplier * (270 - temperature)) / 25.0F)));
            return true;
        }
        return false;
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
