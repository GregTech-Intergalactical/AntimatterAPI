package muramasa.gtu.client.render.bakedmodels;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.overrides.ItemOverridePipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

public class BakedPipe implements IBakedModel {

    protected static ItemOverrideList OVERRIDE;
    protected static TextureAtlasSprite PARTICLE;

    public static Int2ObjectArrayMap<List<BakedQuad>> CACHE = new Int2ObjectArrayMap<>();
    public static int[][] CONFIG = new int[64][];
    public static IBakedModel[][] BAKED;

    static {
        //Default Shape (0 Connections)
        CONFIG[0] = new int[]{0};

        //Single Shapes (1 Connections)
        CONFIG[1] = new int[]{1, DOWN.getIndex()};
        CONFIG[2] = new int[]{1, UP.getIndex()};
        CONFIG[4] = new int[]{1};
        CONFIG[8] = new int[]{1, SOUTH.getIndex()};
        CONFIG[16] = new int[]{1, WEST.getIndex()};
        CONFIG[32] = new int[]{1, EAST.getIndex()};

        //Line Shapes (2 Connections)
        CONFIG[3] = new int[]{2, UP.getIndex()};
        CONFIG[12] = new int[]{2};
        CONFIG[48] = new int[]{2, WEST.getIndex()};

        //Elbow Shapes (2 Connections)
        CONFIG[5] = new int[]{3, WEST.getIndex(), UP.getIndex(), EAST.getIndex()};
        CONFIG[6] = new int[]{3, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[9] = new int[]{3, EAST.getIndex(), UP.getIndex(), EAST.getIndex()};
        CONFIG[10] = new int[]{3, EAST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[17] = new int[]{3, NORTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[18] = new int[]{3, SOUTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[20] = new int[]{3, WEST.getIndex()};
        CONFIG[24] = new int[]{3, SOUTH.getIndex()};
        CONFIG[33] = new int[]{3, NORTH.getIndex(), UP.getIndex(), EAST.getIndex()};
        CONFIG[34] = new int[]{3, NORTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[36] = new int[]{3};
        CONFIG[40] = new int[]{3, EAST.getIndex()};

        //Side Shapes (3 Connections)
        CONFIG[7] = new int[]{4, SOUTH.getIndex(), UP.getIndex()};
        CONFIG[11] = new int[]{4, NORTH.getIndex(), UP.getIndex()};
        CONFIG[13] = new int[]{4, DOWN.getIndex(), DOWN.getIndex()};
        CONFIG[14] = new int[]{4};
        CONFIG[19] = new int[]{4, EAST.getIndex(), UP.getIndex()};
        CONFIG[28] = new int[]{4, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[35] = new int[]{4, WEST.getIndex(), UP.getIndex()};
        CONFIG[44] = new int[]{4, EAST.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[49] = new int[]{4, EAST.getIndex(), DOWN.getIndex(), DOWN.getIndex()};
        CONFIG[50] = new int[]{4, EAST.getIndex()};
        CONFIG[52] = new int[]{4, NORTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[56] = new int[]{4, SOUTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};

        //Corner Shapes (3 Connections)
        CONFIG[21] = new int[]{5, WEST.getIndex(), DOWN.getIndex()};
        CONFIG[22] = new int[]{5, WEST.getIndex()};
        CONFIG[25] = new int[]{5, SOUTH.getIndex(), DOWN.getIndex()};
        CONFIG[26] = new int[]{5, SOUTH.getIndex()};
        CONFIG[41] = new int[]{5, EAST.getIndex(), DOWN.getIndex()};
        CONFIG[42] = new int[]{5, EAST.getIndex()};
        CONFIG[37] = new int[]{5, NORTH.getIndex(), DOWN.getIndex()};
        CONFIG[38] = new int[]{5};

        //Arrow Shapes (4 Connections)
        CONFIG[23] = new int[]{6, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[27] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
        CONFIG[29] = new int[]{6, WEST.getIndex(), DOWN.getIndex()};
        CONFIG[30] = new int[]{6, WEST.getIndex()};
        CONFIG[39] = new int[]{6, EAST.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[43] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
        CONFIG[45] = new int[]{6, EAST.getIndex(), DOWN.getIndex()};
        CONFIG[46] = new int[]{6, EAST.getIndex()};
        CONFIG[53] = new int[]{6, DOWN.getIndex()};
        CONFIG[54] = new int[]{6};
        CONFIG[57] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex()};
        CONFIG[58] = new int[]{6, SOUTH.getIndex()};

        //Cross Shapes (4 Connections)
        CONFIG[15] = new int[]{7, WEST.getIndex(), UP.getIndex()};
        CONFIG[51] = new int[]{7, UP.getIndex()};
        CONFIG[60] = new int[]{7};

        //Five Shapes (5 Connections)
        CONFIG[31] = new int[]{8, EAST.getIndex(), UP.getIndex()};
        CONFIG[47] = new int[]{8, WEST.getIndex(), UP.getIndex()};
        CONFIG[55] = new int[]{8, SOUTH.getIndex(), UP.getIndex()};
        CONFIG[59] = new int[]{8, NORTH.getIndex(), UP.getIndex()};
        CONFIG[61] = new int[]{8, DOWN.getIndex(), DOWN.getIndex()};
        CONFIG[62] = new int[]{8};

        //All Shapes (6 Connections)
        CONFIG[63] = new int[]{9};
    }

    public BakedPipe(IBakedModel[][] baked) {
        BAKED = baked;
        PARTICLE = Textures.PIPE.getSprite();
        OVERRIDE = new ItemOverridePipe();
    }


    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        int size = state.getValue(GTProperties.SIZE);
        int connections = exState.getValue(GTProperties.CONNECTIONS);
        TextureData data = exState.getValue(GTProperties.TEXTURE);

        //List<BakedQuad> quads = CACHE.get((size * 100) + connections);
        List<BakedQuad> quads = null;
        if (quads == null) {
            int[] config = connections > 63 ? CONFIG[connections - 64] : CONFIG[connections];
            quads = new ArrayList<>(BAKED[size][config[0]].getQuads(state, side, rand));
            if (connections > 63) quads = ModelUtils.remove(quads, 1);
            if (config.length > 1) quads = ModelUtils.trans(quads, 1, config);
            ModelUtils.tex(quads, 0, 1, data.getBase()[0]);
            ModelUtils.tex(quads, 2, data.getOverlay()[size]);
        }
        //CACHE.put((size * 100) + connections, quads);
        return quads;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this, ModelUtils.getBlockTransform(cameraTransformType));
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return PARTICLE;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return OVERRIDE;
    }
}
