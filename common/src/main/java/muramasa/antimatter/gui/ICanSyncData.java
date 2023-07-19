package muramasa.antimatter.gui;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ICanSyncData {

    enum SyncDirection {
        SERVER_TO_CLIENT,
        //TODO: This direction has not been tested yet.
        CLIENT_TO_SERVER
    }

    <T> void bind(Supplier<T> supplier, Consumer<T> consumer, Function<FriendlyByteBuf, T> reader, BiConsumer<FriendlyByteBuf, T> writer, BiFunction<Object, Object, Boolean> equality, SyncDirection direction);

    default void syncInt(Supplier<Integer> source, Consumer<Integer> onChange, SyncDirection direction) {
        bind(source, onChange, FriendlyByteBuf::readVarInt, FriendlyByteBuf::writeVarInt, Object::equals, direction);
    }

    default void syncLong(Supplier<Long> source, Consumer<Long> onChange, SyncDirection direction) {
        bind(source, onChange, FriendlyByteBuf::readLong, FriendlyByteBuf::writeLong, Object::equals, direction);
    }

    default void syncDouble(Supplier<Double> source, Consumer<Double> onChange, SyncDirection direction) {
        bind(source, onChange, FriendlyByteBuf::readDouble, FriendlyByteBuf::writeDouble, Object::equals, direction);
    }

    default void syncFloat(Supplier<Float> source, Consumer<Float> onChange, SyncDirection direction) {
        bind(source, onChange, FriendlyByteBuf::readFloat, FriendlyByteBuf::writeFloat, Object::equals, direction);
    }

    default void syncString(Supplier<String> source, Consumer<String> onChange, SyncDirection direction) {
        bind(source, onChange, a -> a.readUtf(32767), FriendlyByteBuf::writeUtf, Object::equals, direction);
    }

    default void syncBoolean(Supplier<Boolean> source, Consumer<Boolean> onChange, SyncDirection direction) {
        bind(source, onChange, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean, Object::equals, direction);
    }

    default void syncFluidStack(Supplier<FluidHolder> source, Consumer<FluidHolder> onChange, SyncDirection direction) {
        bind(() -> source.get().copyHolder(), onChange, FluidPlatformUtils::readFromPacket, FluidPlatformUtils::writeToPacket, (a, b) -> {
            FluidHolder f = (FluidHolder) a;
            if (!(b instanceof FluidHolder h)) return false;
            return a.equals(b) && h.getFluidAmount() == f.getFluidAmount();
        }, direction);
    }

    default void syncItemStack(Supplier<ItemStack> source, Consumer<ItemStack> onChange, SyncDirection direction) {
        bind(() -> source.get().copy(), onChange, FriendlyByteBuf::readItem, FriendlyByteBuf::writeItem, Object::equals, direction);
    }

    default <T extends Enum<T>> void syncEnum(Supplier<T> source, Consumer<T> onChange, Class<T> clazz, SyncDirection direction) {
        bind(source, onChange, b -> b.readEnum(clazz), FriendlyByteBuf::writeEnum, Object::equals, direction);
    }
}
