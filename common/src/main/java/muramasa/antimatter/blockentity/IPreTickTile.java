package muramasa.antimatter.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPreTickTile {
    void onUnregisterPre();
    void onServerTickPre(Level level, BlockPos pos, boolean aFirst);

    default BlockEntity getBlockEntity(){
        return this instanceof BlockEntity be ? be : null;
    }
}
