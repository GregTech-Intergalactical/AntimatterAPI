package muramasa.antimatter.util.fabric;

import com.mojang.math.Matrix4f;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class AntimatterPlatformUtilsImpl {
    public static CreativeModeTab createTab(String domain, String id, Supplier<ItemStack> iconSupplier){
        return FabricItemGroupBuilder.build(new ResourceLocation(domain, id), iconSupplier);
    }

    public static Matrix4f createMatrix4f(float[] values){
        return new com.mojang.math.Matrix4f().setMValues(values);
    }
}
