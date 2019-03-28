package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.client.render.ModelUtils;
import muramasa.gregtech.client.render.overrides.ItemOverridePipe;
import muramasa.gregtech.common.blocks.pipe.BlockPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedPipe extends BakedBase {

    private static ItemOverrideList OVERRIDE = new ItemOverridePipe();
    public static IBakedModel[][] BAKED;

    public static HashMap<Integer, int[]> CONFIG_MAP = new HashMap<>();

    public static HashMap<Integer, List<BakedQuad>> CACHE = new HashMap<>();

    static {
        //Default Shape (0 Connections)
        CONFIG_MAP.put(0, new int[]{0}); //0 Connections, No Rotation

        //Single Shapes (1 Connections)
        CONFIG_MAP.put(4, new int[]{1}); //1 Connection, No Rotations
        CONFIG_MAP.put(8, new int[]{1, EnumFacing.SOUTH.getIndex()}); //1 Connection, facing South
        CONFIG_MAP.put(16, new int[]{1, EnumFacing.WEST.getIndex()});
        CONFIG_MAP.put(32, new int[]{1, EnumFacing.EAST.getIndex()});

        //Line Shapes (2 Connections)
        CONFIG_MAP.put(12, new int[]{2});
        CONFIG_MAP.put(48, new int[]{2, EnumFacing.WEST.getIndex()});

        //Corner Shapes (2 Connections)
        CONFIG_MAP.put(20, new int[]{3, EnumFacing.WEST.getIndex()});
        CONFIG_MAP.put(24, new int[]{3, EnumFacing.SOUTH.getIndex()});
        CONFIG_MAP.put(36, new int[]{3});
        CONFIG_MAP.put(40, new int[]{3, EnumFacing.EAST.getIndex()});

        //Side Shapes (3 Connections)
        CONFIG_MAP.put(28, new int[]{4, EnumFacing.SOUTH.getIndex()});
        CONFIG_MAP.put(44, new int[]{4});
        CONFIG_MAP.put(52, new int[]{4, EnumFacing.WEST.getIndex()});
        CONFIG_MAP.put(56, new int[]{4, EnumFacing.EAST.getIndex()});

        //Cross Shapes (4 Connections)
        CONFIG_MAP.put(60, new int[]{5});
    }

    public BakedPipe(IBakedModel[][] baked) {
        BAKED = baked;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        int size = exState.getValue(BlockPipe.SIZE);
        int connections = exState.getValue(BlockPipe.CONNECTIONS);


//        List<BakedQuad> quads = CACHE.get(connections);
        List<BakedQuad> quads = null;
        if (quads == null) {
            quads = new LinkedList<>();
            int[] config = CONFIG_MAP.get(connections);
            quads.addAll(BAKED[size][config[0]].getQuads(state, side, rand));
            if (config.length >= 2) quads = ModelUtils.trans(quads, config[1]);
            CACHE.put(connections, quads);
        }

        return quads;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return super.handlePerspective(cameraTransformType);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return OVERRIDE;
    }
}
