package muramasa.antimatter.capability;

import jdk.internal.jline.internal.Nullable;
import muramasa.antimatter.cover.Cover;
import net.minecraft.util.Direction;

public interface INodeHandler {

    void onRemove(@Nullable Direction side);

    void onUpdate(Direction side, Cover cover);
}
