package muramasa.antimatter.tesseract;

import muramasa.antimatter.cover.Cover;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public interface ITileWrapper {

    void onRemove(@Nullable Direction side);

    void onUpdate(Direction side, Cover cover);

    boolean isRemoved();
}
