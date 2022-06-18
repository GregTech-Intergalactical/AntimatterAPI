package net.minecraftforge.client.extensions;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;

public interface IForgeMatrix4f {
    default void multiplyBackward(Matrix4f other) {
    }

    default void setTranslation(float x, float y, float z) {
    }

    default Matrix4f setMValues(float[] values){
        return new Matrix4f();
    }
}
