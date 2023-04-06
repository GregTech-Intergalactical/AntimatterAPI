package muramasa.antimatter.fluid;

import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IColorHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;

public class AntimatterLiquidBlock extends LiquidBlock implements IColorHandler {
    int color;
    public AntimatterLiquidBlock(FlowingFluid flowingFluid, Properties properties, int color) {
        super(flowingFluid, properties);
        this.color = color;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return color;
    }
}
