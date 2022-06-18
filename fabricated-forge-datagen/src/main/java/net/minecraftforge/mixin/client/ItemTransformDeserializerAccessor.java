package net.minecraftforge.mixin.client;

import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTransform.Deserializer.class)
public interface ItemTransformDeserializerAccessor {
    @Accessor
    static Vector3f getDEFAULT_ROTATION(){
        throw new AssertionError();
    }

    @Accessor
    static Vector3f getDEFAULT_TRANSLATION(){
        throw new AssertionError();
    }

    @Accessor
    static Vector3f getDEFAULT_SCALE(){
        throw new AssertionError();
    }
}
