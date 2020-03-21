package muramasa.antimatter.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IInfoProvider {

    List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos);
}
