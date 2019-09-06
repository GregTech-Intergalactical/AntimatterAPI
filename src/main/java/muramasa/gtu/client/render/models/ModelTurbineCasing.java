package muramasa.gtu.client.render.models;

import muramasa.gtu.api.blocks.BlockTurbineCasing;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedTurbineCasing;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

public class ModelTurbineCasing extends ModelBase {

    private BlockTurbineCasing block;

    public ModelTurbineCasing(BlockTurbineCasing block) {
        this.block = block;
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        if (BakedTurbineCasing.LOOKUP.size() == 0) {
            BakedTurbineCasing.LOOKUP.put(216, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[3]).getQuads(null, EnumFacing.NORTH, -1)); //North
            BakedTurbineCasing.LOOKUP.put(232, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[5]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(201, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[1]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(202, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[7]).getQuads(null, EnumFacing.NORTH, -1));

            BakedTurbineCasing.LOOKUP.put(332, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[3]).getQuads(null, EnumFacing.SOUTH, -1)); //South
            BakedTurbineCasing.LOOKUP.put(316, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[5]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(301, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[1]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(302, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[7]).getQuads(null, EnumFacing.SOUTH, -1));

            BakedTurbineCasing.LOOKUP.put(408, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[3]).getQuads(null, EnumFacing.WEST, -1)); //West
            BakedTurbineCasing.LOOKUP.put(404, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[5]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(401, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[1]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(402, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[7]).getQuads(null, EnumFacing.WEST, -1));

            BakedTurbineCasing.LOOKUP.put(504, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[3]).getQuads(null, EnumFacing.EAST, -1)); //East
            BakedTurbineCasing.LOOKUP.put(508, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[5]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(501, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[1]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(502, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[7]).getQuads(null, EnumFacing.EAST, -1));

            BakedTurbineCasing.LOOKUP.put(217, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[0]).getQuads(null, EnumFacing.NORTH, -1)); //North Corners
            BakedTurbineCasing.LOOKUP.put(233, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[2]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(218, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[6]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(234, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE[8]).getQuads(null, EnumFacing.NORTH, -1));

            BakedTurbineCasing.LOOKUP.put(333, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[0]).getQuads(null, EnumFacing.SOUTH, -1)); //South Corners
            BakedTurbineCasing.LOOKUP.put(317, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[2]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(334, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[6]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(318, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE[8]).getQuads(null, EnumFacing.SOUTH, -1));

            BakedTurbineCasing.LOOKUP.put(409, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[0]).getQuads(null, EnumFacing.WEST, -1)); //West Corners
            BakedTurbineCasing.LOOKUP.put(405, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[2]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(410, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[6]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(406, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE[8]).getQuads(null, EnumFacing.WEST, -1));

            BakedTurbineCasing.LOOKUP.put(505, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[0]).getQuads(null, EnumFacing.EAST, -1)); //East Corners
            BakedTurbineCasing.LOOKUP.put(509, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[2]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(506, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[6]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(510, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE[8]).getQuads(null, EnumFacing.EAST, -1));

            BakedTurbineCasing.LOOKUP.put(1216, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[3]).getQuads(null, EnumFacing.NORTH, -1)); //North
            BakedTurbineCasing.LOOKUP.put(1232, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[5]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(1201, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[1]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(1202, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[7]).getQuads(null, EnumFacing.NORTH, -1));

            BakedTurbineCasing.LOOKUP.put(1332, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[3]).getQuads(null, EnumFacing.SOUTH, -1)); //South
            BakedTurbineCasing.LOOKUP.put(1316, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[5]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(1301, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[1]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(1302, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[7]).getQuads(null, EnumFacing.SOUTH, -1));

            BakedTurbineCasing.LOOKUP.put(1408, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[3]).getQuads(null, EnumFacing.WEST, -1)); //West
            BakedTurbineCasing.LOOKUP.put(1404, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[5]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(1401, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[1]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(1402, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[7]).getQuads(null, EnumFacing.WEST, -1));

            BakedTurbineCasing.LOOKUP.put(1504, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[3]).getQuads(null, EnumFacing.EAST, -1)); //East
            BakedTurbineCasing.LOOKUP.put(1508, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[5]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(1501, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[1]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(1502, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[7]).getQuads(null, EnumFacing.EAST, -1));

            BakedTurbineCasing.LOOKUP.put(1217, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[0]).getQuads(null, EnumFacing.NORTH, -1)); //North Corners
            BakedTurbineCasing.LOOKUP.put(1233, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[2]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(1218, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[6]).getQuads(null, EnumFacing.NORTH, -1));
            BakedTurbineCasing.LOOKUP.put(1234, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "2", Textures.LARGE_TURBINE_ACTIVE[8]).getQuads(null, EnumFacing.NORTH, -1));

            BakedTurbineCasing.LOOKUP.put(1333, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[0]).getQuads(null, EnumFacing.SOUTH, -1)); //South Corners
            BakedTurbineCasing.LOOKUP.put(1317, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[2]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(1334, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[6]).getQuads(null, EnumFacing.SOUTH, -1));
            BakedTurbineCasing.LOOKUP.put(1318, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "3", Textures.LARGE_TURBINE_ACTIVE[8]).getQuads(null, EnumFacing.SOUTH, -1));

            BakedTurbineCasing.LOOKUP.put(1409, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[0]).getQuads(null, EnumFacing.WEST, -1)); //West Corners
            BakedTurbineCasing.LOOKUP.put(1405, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[2]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(1410, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[6]).getQuads(null, EnumFacing.WEST, -1));
            BakedTurbineCasing.LOOKUP.put(1406, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "4", Textures.LARGE_TURBINE_ACTIVE[8]).getQuads(null, EnumFacing.WEST, -1));

            BakedTurbineCasing.LOOKUP.put(1505, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[0]).getQuads(null, EnumFacing.EAST, -1)); //East Corners
            BakedTurbineCasing.LOOKUP.put(1509, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[2]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(1506, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[6]).getQuads(null, EnumFacing.EAST, -1));
            BakedTurbineCasing.LOOKUP.put(1510, ModelUtils.texBake(ModelUtils.MODEL_BASIC_FULL, "5", Textures.LARGE_TURBINE_ACTIVE[8]).getQuads(null, EnumFacing.EAST, -1));
        }
        return new BakedTurbineCasing(block, block.getData().bake());
    }
}
