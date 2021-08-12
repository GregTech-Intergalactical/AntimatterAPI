package muramasa.antimatter.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.*;

public interface ICanSyncData {
    <T> void bind(Supplier<T> supplier, Consumer<T> consumer, Function<PacketBuffer, T> reader, BiConsumer<PacketBuffer, T> writer,BiFunction<Object, Object, Boolean> equality);

    default void syncInt(Supplier<Integer> source, Consumer<Integer> onChange) {
        bind(source,onChange,PacketBuffer::readVarInt, PacketBuffer::writeInt, Object::equals);
    }
    default void syncDouble(Supplier<Double> source, Consumer<Double> onChange) {
        bind(source,onChange,PacketBuffer::readDouble, PacketBuffer::writeDouble, Object::equals);
    }
    default void syncString(Supplier<String> source, Consumer<String> onChange) {
        bind(source,onChange,PacketBuffer::readString, PacketBuffer::writeString, Object::equals);
    }
    default void syncBoolean(Supplier<Boolean> source, Consumer<Boolean> onChange) {
        bind(source,onChange,PacketBuffer::readBoolean, PacketBuffer::writeBoolean, Object::equals);
    }
    default void syncFluidStack(Supplier<FluidStack> source, Consumer<FluidStack> onChange) {
        bind(source,onChange,FluidStack::readFromPacket, (a,b) -> b.writeToPacket(a),(a,b) -> {
            FluidStack f = (FluidStack) a;
            if (!(b instanceof FluidStack)) return false;
            return a.equals(b) && ((FluidStack)b).getAmount() == f.getAmount();
        });
    }
    default void syncItemStack(Supplier<ItemStack> source, Consumer<ItemStack> onChange) {
        bind(source,onChange,PacketBuffer::readItemStack, PacketBuffer::writeItemStack, Object::equals);
    }
    /*<T extends Enum<T>> void syncEnum(Supplier<T> source, Consumer<T> onChange) {

    }*/
}
