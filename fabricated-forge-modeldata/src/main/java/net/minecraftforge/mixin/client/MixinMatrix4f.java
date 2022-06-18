package net.minecraftforge.mixin.client;

import com.mojang.math.Matrix4f;
import net.minecraftforge.client.extensions.IForgeMatrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract class MixinMatrix4f implements IForgeMatrix4f {
    @Shadow public abstract void load(Matrix4f other);

    @Shadow protected float m00;

    @Shadow protected float m11;

    @Shadow protected float m22;

    @Shadow protected float m33;

    @Shadow protected float m03;

    @Shadow protected float m13;

    @Shadow protected float m23;

    @Shadow protected float m01;

    @Shadow protected float m02;

    @Shadow protected float m10;

    @Shadow protected float m12;

    @Shadow protected float m20;

    @Shadow protected float m21;

    @Shadow protected float m30;

    @Shadow protected float m31;

    @Shadow protected float m32;

    @Override
    public void multiplyBackward(Matrix4f other) {
        Matrix4f copy = other.copy();
        copy.multiply((Matrix4f)(Object) this);
        this.load(copy);
    }

    @Override
    public void setTranslation(float x, float y, float z) {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
        this.m22 = 1.0F;
        this.m33 = 1.0F;
        this.m03 = x;
        this.m13 = y;
        this.m23 = z;
    }

    public Matrix4f setMValues(float[] values){
        this.m00 = values[0];
        this.m01 = values[1];
        this.m02 = values[2];
        this.m03 = values[3];
        this.m10 = values[4];
        this.m11 = values[5];
        this.m12 = values[6];
        this.m13 = values[7];
        this.m20 = values[8];
        this.m21 = values[9];
        this.m22 = values[10];
        this.m23 = values[11];
        this.m30 = values[12];
        this.m31 = values[13];
        this.m32 = values[14];
        this.m33 = values[15];
        return (Matrix4f) (Object)this;
    }
}
