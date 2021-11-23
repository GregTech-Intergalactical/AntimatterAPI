package muramasa.antimatter.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

    private static final int UV = findUVOffset(DefaultVertexFormats.BLOCK);
    private static final int COLOR = findColorOffset(DefaultVertexFormats.BLOCK);
    public static void processVertices(int[] inData, int[] outData, BakedQuad old, int color, TextureAtlasSprite sprite)
    {
        int stride = DefaultVertexFormats.BLOCK.getVertexSize();
        int count = (inData.length * 4) / stride;
        for (int i=0;i<count;i++)
        {
            float u_old = Float.intBitsToFloat(inData[i*stride/4+UV]);
            float v_old = Float.intBitsToFloat(inData[i*stride/4+UV+1]);
            float u = (u_old - old.getSprite().getU0())*16.0f/(old.getSprite().getU1() - old.getSprite().getU0());
            float v = (v_old - old.getSprite().getV0())*16.0f/(old.getSprite().getV1() - old.getSprite().getV0());
            outData[i*stride/4 + UV] = Float.floatToIntBits(sprite.getU(u));
            outData[i*stride/4 + UV + 1] = Float.floatToIntBits(sprite.getV(v));
            outData[i*stride/4 + COLOR] = RenderHelper.convertRGB2ABGR(color);
        }
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

        List<BakedQuad> outputs = new ObjectArrayList<>(inputs.size());
        for(BakedQuad input : inputs)
        {
            int[] inData = input.getVertices();
            int[] outData = Arrays.copyOf(inData, inData.length);
            processVertices(inData, outData, input, color, sprite);

            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getDirection(), sprite,false));
        }
        return outputs;
    }
}
