package net.minecraftforge.mixin.client;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import net.minecraftforge.client.extensions.IForgeTransformation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Transformation.class)
public class MixinTransformation implements IForgeTransformation {
    @Shadow @Final private Matrix4f matrix;
    @Unique
    private Matrix3f normalTransform = null;

    @Override
    public Matrix3f getNormalMatrix() {
        this.checkNormalTransform();
        return this.normalTransform;
    }

    private void checkNormalTransform() {
        if (this.normalTransform == null) {
            this.normalTransform = new Matrix3f(this.matrix);
            this.normalTransform.invert();
            this.normalTransform.transpose();
        }

    }
}
