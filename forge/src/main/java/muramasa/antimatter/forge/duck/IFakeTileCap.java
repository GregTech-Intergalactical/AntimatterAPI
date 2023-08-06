package muramasa.antimatter.forge.duck;

import muramasa.antimatter.cover.ICover;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IFakeTileCap {
    <T> LazyOptional<T> getCapabilityFromFake(@NotNull Capability<T> cap, @Nullable Direction side, ICover cover);
}
