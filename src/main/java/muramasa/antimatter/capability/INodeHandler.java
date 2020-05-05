package muramasa.antimatter.capability;

import muramasa.antimatter.cover.Cover;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public interface INodeHandler {

    void onRemove(@Nullable Direction side);

    void onUpdate(Direction side, Cover cover);

    boolean isValid();
}
