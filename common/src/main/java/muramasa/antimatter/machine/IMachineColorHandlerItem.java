package muramasa.antimatter.machine;

import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IMachineColorHandlerItem {
    int getItemColor(ItemStack stack, @Nullable Block block, int i);
}
