package muramasa.antimatter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface IInfoProvider {

    List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos, boolean simple);
}
