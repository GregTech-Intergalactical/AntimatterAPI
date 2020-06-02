package muramasa.antimatter.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class CapabilityWrapper implements ICapabilityProvider {

    ItemStack stack;
    Optional<IEnergyHandler> handler;

    public CapabilityWrapper(ItemStack stack, IEnergyHandler handler) {
        this.stack = stack;
        this.handler = Optional.of(handler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AntimatterCaps.ENERGY && handler.isPresent()) return LazyOptional.of(() -> handler.get()).cast();
        return LazyOptional.empty();
    }
}
