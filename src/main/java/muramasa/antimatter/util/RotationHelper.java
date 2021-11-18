package muramasa.antimatter.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copied forge classes for rotations, which are needed for covers.
 */
public class RotationHelper {

    public static ModelRotation getModelRotation(int x, int y) {
        return ModelRotation.getModelRotation(x, y);
    }

    protected enum ModelRotation {
        X0_Y0(0, 0),
        X0_Y90(0, 90),
        X0_Y180(0, 180),
        X0_Y270(0, 270),
        X90_Y0(90, 0),
        X90_Y90(90, 90),
        X90_Y180(90, 180),
        X90_Y270(90, 270),
        X180_Y0(180, 0),
        X180_Y90(180, 90),
        X180_Y180(180, 180),
        X180_Y270(180, 270),
        X270_Y0(270, 0),
        X270_Y90(270, 90),
        X270_Y180(270, 180),
        X270_Y270(270, 270);

        private static final Map<Integer, ModelRotation> MAP_ROTATIONS = Arrays.stream(values()).collect(Collectors.toMap((rotation) -> rotation.combinedXY, (rotation) -> rotation));
        private final int combinedXY;
        private final TransformationMatrix transformation;

        private static int combineXY(int x, int y) {
            return x * 360 + y;
        }

        ModelRotation(int x, int y) {
            this.combinedXY = combineXY(x, y);
            Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float) (-y), true);
            quaternion.mul(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float) (-x), true));
            this.transformation = new TransformationMatrix(quaternion);
        }

        public TransformationMatrix getRotation() {
            return this.transformation;
        }

        public static ModelRotation getModelRotation(int x, int y) {
            return MAP_ROTATIONS.get(combineXY(MathHelper.positiveModulo(x, 360), MathHelper.positiveModulo(y, 360)));
        }
    }

    public static final class TransformationMatrix {
        private final Matrix4f matrix;

        public TransformationMatrix(@Nullable Quaternion rotationLeftIn) {
            this.matrix = composeVanilla(rotationLeftIn);
        }


        private static Matrix4f composeVanilla(@Nullable Quaternion rotationLeft) {
            Matrix4f matrix4f = new Matrix4f(new float[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});
            if (rotationLeft != null) {
                matrix4f.mul(new Matrix4f(rotationLeft));
            }

            return matrix4f;
        }


        public Direction rotateFace(Direction facing) {
            Vector3i vector3i = facing.getNormal();
            Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
            vector4f.transform(new net.minecraft.util.math.vector.Matrix4f(matrix.values()));
            return Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
        }
    }

    public static final class Matrix4f {
        protected float m00;
        protected float m01;
        protected float m02;
        protected float m03;
        protected float m10;
        protected float m11;
        protected float m12;
        protected float m13;
        protected float m20;
        protected float m21;
        protected float m22;
        protected float m23;
        protected float m30;
        protected float m31;
        protected float m32;
        protected float m33;

        public float[] values() {
            return new float[]{m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33};
        }

        public Matrix4f(float[] values) {
            m00 = values[0];
            m01 = values[1];
            m02 = values[2];
            m03 = values[3];
            m10 = values[4];
            m11 = values[5];
            m12 = values[6];
            m13 = values[7];
            m20 = values[8];
            m21 = values[9];
            m22 = values[10];
            m23 = values[11];
            m30 = values[12];
            m31 = values[13];
            m32 = values[14];
            m33 = values[15];
        }

        public void mul(Matrix4f matrix) {
            float f = this.m00 * matrix.m00 + this.m01 * matrix.m10 + this.m02 * matrix.m20 + this.m03 * matrix.m30;
            float f1 = this.m00 * matrix.m01 + this.m01 * matrix.m11 + this.m02 * matrix.m21 + this.m03 * matrix.m31;
            float f2 = this.m00 * matrix.m02 + this.m01 * matrix.m12 + this.m02 * matrix.m22 + this.m03 * matrix.m32;
            float f3 = this.m00 * matrix.m03 + this.m01 * matrix.m13 + this.m02 * matrix.m23 + this.m03 * matrix.m33;
            float f4 = this.m10 * matrix.m00 + this.m11 * matrix.m10 + this.m12 * matrix.m20 + this.m13 * matrix.m30;
            float f5 = this.m10 * matrix.m01 + this.m11 * matrix.m11 + this.m12 * matrix.m21 + this.m13 * matrix.m31;
            float f6 = this.m10 * matrix.m02 + this.m11 * matrix.m12 + this.m12 * matrix.m22 + this.m13 * matrix.m32;
            float f7 = this.m10 * matrix.m03 + this.m11 * matrix.m13 + this.m12 * matrix.m23 + this.m13 * matrix.m33;
            float f8 = this.m20 * matrix.m00 + this.m21 * matrix.m10 + this.m22 * matrix.m20 + this.m23 * matrix.m30;
            float f9 = this.m20 * matrix.m01 + this.m21 * matrix.m11 + this.m22 * matrix.m21 + this.m23 * matrix.m31;
            float f10 = this.m20 * matrix.m02 + this.m21 * matrix.m12 + this.m22 * matrix.m22 + this.m23 * matrix.m32;
            float f11 = this.m20 * matrix.m03 + this.m21 * matrix.m13 + this.m22 * matrix.m23 + this.m23 * matrix.m33;
            float f12 = this.m30 * matrix.m00 + this.m31 * matrix.m10 + this.m32 * matrix.m20 + this.m33 * matrix.m30;
            float f13 = this.m30 * matrix.m01 + this.m31 * matrix.m11 + this.m32 * matrix.m21 + this.m33 * matrix.m31;
            float f14 = this.m30 * matrix.m02 + this.m31 * matrix.m12 + this.m32 * matrix.m22 + this.m33 * matrix.m32;
            float f15 = this.m30 * matrix.m03 + this.m31 * matrix.m13 + this.m32 * matrix.m23 + this.m33 * matrix.m33;
            this.m00 = f;
            this.m01 = f1;
            this.m02 = f2;
            this.m03 = f3;
            this.m10 = f4;
            this.m11 = f5;
            this.m12 = f6;
            this.m13 = f7;
            this.m20 = f8;
            this.m21 = f9;
            this.m22 = f10;
            this.m23 = f11;
            this.m30 = f12;
            this.m31 = f13;
            this.m32 = f14;
            this.m33 = f15;
        }

        public Matrix4f(Quaternion quaternionIn) {
            float f = quaternionIn.i();
            float f1 = quaternionIn.j();
            float f2 = quaternionIn.k();
            float f3 = quaternionIn.r();
            float f4 = 2.0F * f * f;
            float f5 = 2.0F * f1 * f1;
            float f6 = 2.0F * f2 * f2;
            this.m00 = 1.0F - f5 - f6;
            this.m11 = 1.0F - f6 - f4;
            this.m22 = 1.0F - f4 - f5;
            this.m33 = 1.0F;
            float f7 = f * f1;
            float f8 = f1 * f2;
            float f9 = f2 * f;
            float f10 = f * f3;
            float f11 = f1 * f3;
            float f12 = f2 * f3;
            this.m10 = 2.0F * (f7 + f12);
            this.m01 = 2.0F * (f7 - f12);
            this.m20 = 2.0F * (f9 - f11);
            this.m02 = 2.0F * (f9 + f11);
            this.m21 = 2.0F * (f8 + f10);
            this.m12 = 2.0F * (f8 - f10);
        }
    }
}
