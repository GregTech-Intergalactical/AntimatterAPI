package muramasa.gregtech.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.EnumMap;
import java.util.function.Function;

public class ModelUtils {

    private static Function<ResourceLocation, TextureAtlasSprite> TEXTURE_GETTER;

    private static EnumMap<ItemCameraTransforms.TransformType, Matrix4f> TRANSFORM_MAP_ITEM = new EnumMap<>(ItemCameraTransforms.TransformType.class);
    private static EnumMap<ItemCameraTransforms.TransformType, Matrix4f> TRANSFORM_MAP_BLOCK = new EnumMap<>(ItemCameraTransforms.TransformType.class);

    static {
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.GUI, get(0, 0, 0, 0, 0, 0, 1f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 3, 1, 0, 0, 0, 0.55f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, get(0f, 4.0f, 0.5f, 0, 90, -55, 0.85f).getMatrix());

        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.25f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 0, 0, 0.4f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 45, 0, 0, 0.4f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, get(0, 0, 0, 45, 0, 0, 0.4f).getMatrix());
    }

    public static Function<ResourceLocation, TextureAtlasSprite> getTextureGetter() {
        if (TEXTURE_GETTER == null) TEXTURE_GETTER = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        return TEXTURE_GETTER;
    }

    public static Matrix4f getItemTransform(ItemCameraTransforms.TransformType type) {
        Matrix4f mat = TRANSFORM_MAP_ITEM.get(type);
        return mat != null ? mat : TRSRTransformation.identity().getMatrix();
    }

    public static Matrix4f getBlockTransform(ItemCameraTransforms.TransformType type) {
        Matrix4f mat = TRANSFORM_MAP_BLOCK.get(type);
        return mat != null ? mat : TRSRTransformation.identity().getMatrix();
    }

    public static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }
}
