package muramasa.antimatter.client;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VertexTransformer {

    private static final int POSITION = findPositionOffset(DefaultVertexFormats.BLOCK);
    private static final int NORMAL = findNormalOffset(DefaultVertexFormats.BLOCK);
    private static final int UV = findUVOffset(DefaultVertexFormats.BLOCK);
    private static final int COLOR = findColorOffset(DefaultVertexFormats.BLOCK);
    public static void processVertices(int[] inData, int[] outData, int color, TextureAtlasSprite sprite)
    {
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b  = (float)(color & 255) / 255.0F;
        float u0 = sprite.getU(0.0f);
        float u1 = sprite.getU(16.0f);
        float v0 = sprite.getU(0.0f);
        float v1 = sprite.getU(16.0f);
        int stride = DefaultVertexFormats.BLOCK.getVertexSize();
        int count = (inData.length * 4) / stride;
        for (int i=0;i<count;i++)
        {
            switch (i) {
                case 0:
                    outData[i*stride/4 + UV] = Float.floatToIntBits(sprite.getU(0.0f));
                    outData[i*stride/4 + UV + 1] = Float.floatToIntBits(sprite.getV(16.0f));
                    break;
                case 1:
                    outData[i*stride/4 + UV] = Float.floatToIntBits(sprite.getU(16.0f));
                    outData[i*stride/4 + UV + 1] = Float.floatToIntBits(sprite.getV(16.0f));
                    break;
                case 2:
                    outData[i*stride/4 + UV] = Float.floatToIntBits(sprite.getU(16.0f));
                    outData[i*stride/4 + UV + 1] = Float.floatToIntBits(sprite.getV(0.0f));
                    break;
                case 3:
                    outData[i*stride/4 + UV] = Float.floatToIntBits(sprite.getU(0.0f));
                    outData[i*stride/4 + UV + 1] = Float.floatToIntBits(sprite.getV(0.0f));
                    break;
            }
            outData[i*stride/4 + COLOR] = Float.floatToIntBits(r);
            outData[i*stride/4 + COLOR + 1] = Float.floatToIntBits(r);
            outData[i*stride/4 + COLOR + 2] = Float.floatToIntBits(r);
        }
    }
/*
  ivertexbuilder.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_228089_4_).endVertex();
         ivertexbuilder.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_228089_4_).endVertex();
         ivertexbuilder.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_228089_4_).endVertex();
         ivertexbuilder.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_228089_4_).endVertex();
 */
    private static int getAtByteOffset(int[] inData, int offset)
    {
        int index = offset / 4;
        int lsb = inData[index];

        int shift = (offset % 4) * 8;
        if (shift == 0)
            return inData[index];

        int msb = inData[index+1];

        return (lsb >>> shift) | (msb << (32-shift));
    }

    private static void putAtByteOffset(int[] outData, int offset, int value)
    {
        int index = offset / 4;
        int shift = (offset % 4) * 8;

        if (shift == 0)
        {
            outData[index] = value;
            return;
        }

        int lsbMask = 0xFFFFFFFF >>> (32-shift);
        int msbMask = 0xFFFFFFFF << shift;

        outData[index] = (outData[index] & lsbMask) | (value << shift);
        outData[index+1] = (outData[index+1] & msbMask) | (value >>> (32-shift));
    }

    private static int findPositionOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++)
        {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.POSITION)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new RuntimeException("Expected vertex format to have a POSITION attribute");
        if (element.getType() != VertexFormatElement.Type.FLOAT)
            throw new RuntimeException("Expected POSITION attribute to have data type FLOAT");
        if (element.getByteSize() < 3)
            throw new RuntimeException("Expected POSITION attribute to have at least 3 dimensions");
        return fmt.getOffset(index)/4;
    }

    private static int findNormalOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++)
        {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.NORMAL)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new IllegalStateException("BLOCK format does not have normals?");
        if (element.getType() != VertexFormatElement.Type.BYTE)
            throw new RuntimeException("Expected NORMAL attribute to have data type BYTE");
        if (element.getByteSize() < 3)
            throw new RuntimeException("Expected NORMAL attribute to have at least 3 dimensions");
        return fmt.getOffset(index);
    }

    private static int findUVOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++)
        {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.UV)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new IllegalStateException("BLOCK format does not have normals?");
        return fmt.getOffset(index)/4;
    }

    private static int findColorOffset(VertexFormat fmt)
    {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++)
        {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.COLOR)
            {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new IllegalStateException("BLOCK format does not have normals?");
        return fmt.getOffset(index)/4;
    }
    /**
     * Processes multiple quads, producing a new array of new quads.
     * @param inputs The list of quads to transform
     * @return A new array of new BakedQuad objects.
     */
    public static List<BakedQuad> processMany(List<BakedQuad> inputs, int color, TextureAtlasSprite sprite)
    {
        if(inputs.size() == 0)
            return Collections.emptyList();

        List<BakedQuad> outputs = Lists.newArrayList();
        for(BakedQuad input : inputs)
        {
            int[] inData = input.getVertices();
            int[] outData = Arrays.copyOf(inData, inData.length);
            processVertices(inData, outData, color, sprite);

            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getDirection(), sprite, input.isShade()));
        }
        return outputs;
    }

    /**
     * Processes multiple quads in place, modifying the input quads.
     * @param inputs The list of quads to transform
     */
    public static void processManyInPlace(List<BakedQuad> inputs)
    {
        if(inputs.size() == 0)
            return;

        for(BakedQuad input : inputs)
        {
            int[] data = input.getVertices();
            processVertices(data, data, 0,null);
        }
    }
}
