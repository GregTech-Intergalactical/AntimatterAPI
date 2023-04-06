package muramasa.antimatter.fluid;

import muramasa.antimatter.registration.IColorHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.Nullable;

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
