package muramasa.antimatter.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.system.CallbackI;

import java.util.function.*;

public interface ICanSyncData {
    <T> void bind(Supplier<T> supplier, Consumer<T> consumer, Function<PacketBuffer, T> reader, BiConsumer<PacketBuffer, T> writer,BiFunction<Object, Object, Boolean> equality);

    default void syncInt(Supplier<Integer> source, Consumer<Integer> onChange) {
        bind(source,onChange,PacketBuffer::readVarInt, PacketBuffer::writeVarInt, Object::equals);
    }
    default void syncLong(Supplier<Long> source, Consumer<Long> onChange) {
        bind(source,onChange,PacketBuffer::readLong, PacketBuffer::writeLong, Object::equals);
    }
    default void syncDouble(Supplier<Double> source, Consumer<Double> onChange) {
        bind(source,onChange,PacketBuffer::readDouble, PacketBuffer::writeDouble, Object::equals);
    }
    default void syncFloat(Supplier<Float> source, Consumer<Float> onChange) {
        bind(source,onChange,PacketBuffer::readFloat, PacketBuffer::writeFloat, Object::equals);
    }
    default void syncString(Supplier<String> source, Consumer<String> onChange) {
        bind(source,onChange,a -> a.readString(32767), PacketBuffer::writeString, Object::equals);
    }
    default void syncBoolean(Supplier<Boolean> source, Consumer<Boolean> onChange) {
        bind(source,onChange,PacketBuffer::readBoolean, PacketBuffer::writeBoolean, Object::equals);
    }
    default void syncFluidStack(Supplier<FluidStack> source, Consumer<FluidStack> onChange) {
        bind(() -> source.get().copy(),onChange,FluidStack::readFromPacket, (a,b) -> b.writeToPacket(a),(a,b) -> {
            FluidStack f = (FluidStack) a;
            if (!(b instanceof FluidStack)) return false;
            return a.equals(b) && ((FluidStack)b).getAmount() == f.getAmount();
        });
    }
    default void syncItemStack(Supplier<ItemStack> source, Consumer<ItemStack> onChange) {
        bind(() -> source.get().copy(),onChange,PacketBuffer::readItemStack, PacketBuffer::writeItemStack, Object::equals);
    }
    default <T extends Enum<T>> void syncEnum(Supplier<T> source, Consumer<T> onChange, Class<T> clazz) {
        bind(source,onChange,b -> b.readEnumValue(clazz), PacketBuffer::writeEnumValue, Object::equals);
    }
}
