package muramasa.gtu.client;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.pipe.BlockFluidPipe;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.model.ModelDynamic;
import muramasa.antimatter.client.model.ModelPipe;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.client.render.models.ModelNichrome;
import muramasa.gtu.data.Textures;
import net.minecraft.util.Direction;

import static muramasa.gtu.common.Data.CASING_FUSION_3;
import static muramasa.gtu.common.Data.COIL_NICHROME;

public class Models {

    public static void init() {
        AntimatterModelLoader.put(COIL_NICHROME, new ModelNichrome());

        AntimatterModelLoader.put(CASING_FUSION_3, new ModelDynamic(CASING_FUSION_3)
            .config(m -> basic(m, Textures.FUSION_3_CT))
            .add(32, b -> b.of("block/preset/simple").tex("all", "minecraft:block/diamond_block"))
        );

        ModelDynamic modelPipe = new ModelPipe(Textures.PIPE).config(Models::pipe);

        BlockFluidPipe pipe = AntimatterAPI.get(BlockFluidPipe.class, "fluid_pipe_tungstensteel_normal");
        if (pipe != null) {
            AntimatterModelLoader.put(pipe, modelPipe);
        }
    }

    public static void basic(ModelDynamic model, Texture[] textures) {
        if (textures.length < 13) return;
        //Single (1)
        model.add(1, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        model.add(2, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        model.add(4, textures[1], textures[1], textures[0], textures[12], textures[0], textures[0]);
        model.add(8, textures[1], textures[1], textures[12], textures[0], textures[0], textures[0]);
        model.add(16, textures[0], textures[0], textures[0], textures[0], textures[0], textures[12]);
        model.add(32, textures[0], textures[0], textures[0], textures[0], textures[12], textures[0]);

        //Lines (2)
        model.add(3, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        model.add(12, textures[1], textures[1], textures[12], textures[12], textures[0], textures[0]);
        model.add(48, textures[0], textures[0], textures[0], textures[0], textures[12], textures[12]);

        //Elbows (2)
        model.add(6, textures[1], textures[12], textures[0], textures[1], textures[10], textures[11]);
        model.add(5, textures[12], textures[1], textures[12], textures[1], textures[9], textures[8]);
        model.add(9, textures[12], textures[1], textures[1], textures[12], textures[8], textures[9]);
        model.add(10, textures[1], textures[12], textures[1], textures[12], textures[11], textures[10]);
        model.add(17, textures[12], textures[0], textures[8], textures[9], textures[12], textures[1]);
        model.add(18, textures[0], textures[12], textures[11], textures[10], textures[12], textures[1]);
        model.add(33, textures[12], textures[0], textures[9], textures[8], textures[1], textures[12]);
        model.add(34, textures[0], textures[12], textures[10], textures[11], textures[1], textures[10]);
        model.add(20, textures[10], textures[10], textures[0], textures[0], textures[0], textures[0]);
        model.add(24, textures[9], textures[9], textures[0], textures[0], textures[0], textures[0]);
        model.add(36, textures[11], textures[11], textures[0], textures[0], textures[0], textures[0]);
        model.add(40, textures[8], textures[8], textures[0], textures[0], textures[0], textures[0]);

        //Side (3)
        model.add(7, textures[12], textures[12], textures[12], textures[1], textures[4], textures[2]);
        model.add(11, textures[12], textures[12], textures[1], textures[12], textures[2], textures[4]);
        model.add(13, textures[12], textures[1], textures[12], textures[12], textures[3], textures[3]);
        model.add(14, textures[1], textures[12], textures[12], textures[12], textures[5], textures[5]);
        model.add(19, textures[12], textures[12], textures[2], textures[4], textures[12], textures[1]);
        model.add(28, textures[4], textures[4], textures[12], textures[12], textures[12], textures[0]);
        model.add(35, textures[12], textures[12], textures[4], textures[2], textures[1], textures[12]);
        model.add(44, textures[2], textures[2], textures[12], textures[12], textures[0], textures[12]);
        model.add(49, textures[12], textures[0], textures[3], textures[3], textures[12], textures[12]);
        model.add(50, textures[0], textures[12], textures[5], textures[5], textures[12], textures[12]);
        model.add(52, textures[3], textures[5], textures[12], textures[0], textures[12], textures[12]);
        model.add(56, textures[5], textures[3], textures[0], textures[12], textures[12], textures[12]);

        //Corner (3)
        model.add(21, textures[10], textures[10], textures[0], textures[9], textures[0], textures[8]);
        model.add(22, textures[10], textures[10], textures[0], textures[10], textures[0], textures[11]);
        model.add(25, textures[9], textures[9], textures[8], textures[0], textures[0], textures[9]);
        model.add(26, textures[9], textures[9], textures[11], textures[0], textures[0], textures[10]);
        model.add(37, textures[11], textures[11], textures[0], textures[8], textures[9], textures[0]);
        model.add(38, textures[11], textures[11], textures[0], textures[11], textures[10], textures[0]);
        model.add(41, textures[8], textures[8], textures[9], textures[0], textures[8], textures[0]);
        model.add(42, textures[8], textures[8], textures[10], textures[0], textures[11], textures[0]);

        //Arrow (4)
        model.add(23, textures[12], textures[12], textures[12], textures[4], textures[12], textures[2]);
        model.add(27, textures[12], textures[12], textures[2], textures[12], textures[12], textures[4]);
        model.add(29, textures[12], textures[4], textures[12], textures[12], textures[12], textures[3]);
        model.add(30, textures[4], textures[12], textures[12], textures[12], textures[12], textures[5]);
        model.add(39, textures[12], textures[12], textures[12], textures[2], textures[4], textures[12]);
        model.add(43, textures[12], textures[12], textures[4], textures[12], textures[2], textures[12]);
        model.add(45, textures[12], textures[2], textures[12], textures[12], textures[3], textures[12]);
        model.add(46, textures[2], textures[12], textures[12], textures[12], textures[5], textures[12]);
        model.add(53, textures[12], textures[5], textures[12], textures[3], textures[12], textures[12]);
        model.add(54, textures[3], textures[12], textures[12], textures[5], textures[12], textures[12]);
        model.add(57, textures[12], textures[3], textures[3], textures[12], textures[12], textures[12]);
        model.add(58, textures[5], textures[12], textures[5], textures[12], textures[12], textures[12]);

        //Cross (4)
        model.add(15, textures[12], textures[12], textures[12], textures[12], textures[6], textures[6]);
        model.add(51, textures[12], textures[12], textures[6], textures[6], textures[12], textures[12]);
        model.add(60, textures[6], textures[6], textures[12], textures[12], textures[12], textures[12]);

        //Five (5)
        model.add(31, textures[12], textures[12], textures[12], textures[12], textures[12], textures[6]);
        model.add(47, textures[12], textures[12], textures[12], textures[12], textures[6], textures[12]);
        model.add(55, textures[12], textures[12], textures[12], textures[6], textures[12], textures[12]);
        model.add(59, textures[12], textures[12], textures[6], textures[12], textures[12], textures[12]);
        model.add(61, textures[12], textures[6], textures[12], textures[12], textures[12], textures[12]);
        model.add(62, textures[6], textures[12], textures[12], textures[12], textures[12], textures[12]);

        //All (6)
        model.add(63, textures[12], textures[12], textures[12], textures[12], textures[12], textures[12]);
    }

    public static void pipe(ModelDynamic model) {

        model.add(0, b -> b.of("block/pipe/normal/base").tex("0", Textures.PIPE));

        model.add(4, b -> b.of("block/pipe/normal/single").tex("0", Textures.PIPE));
        model.add(8, b -> b.of("block/pipe/normal/single").tex("0", Textures.PIPE).rot(Direction.SOUTH));

        model.add(16, b -> b.of("block/pipe/normal/single").tex("0", Textures.PIPE).rot(Direction.WEST));
        model.add(32, b -> b.of("block/pipe/normal/single").tex("0", Textures.PIPE).rot(Direction.EAST));


//        //Default Shape (0 Connections)
//        CONFIG[0] = new int[]{0};
//
//        //Single Shapes (1 Connections)
//        CONFIG[1] = new int[]{1, DOWN.getIndex()};
//        CONFIG[2] = new int[]{1, UP.getIndex()};
//        CONFIG[4] = new int[]{1};
//        CONFIG[8] = new int[]{1, SOUTH.getIndex()};
//        CONFIG[16] = new int[]{1, WEST.getIndex()};
//        CONFIG[32] = new int[]{1, EAST.getIndex()};
//
//        //Line Shapes (2 Connections)
//        CONFIG[3] = new int[]{2, UP.getIndex()};
//        CONFIG[12] = new int[]{2};
//        CONFIG[48] = new int[]{2, WEST.getIndex()};
//
//        //Elbow Shapes (2 Connections)
//        CONFIG[5] = new int[]{3, WEST.getIndex(), UP.getIndex(), EAST.getIndex()};
//        CONFIG[6] = new int[]{3, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[9] = new int[]{3, EAST.getIndex(), UP.getIndex(), EAST.getIndex()};
//        CONFIG[10] = new int[]{3, EAST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[17] = new int[]{3, NORTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
//        CONFIG[18] = new int[]{3, SOUTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[20] = new int[]{3, WEST.getIndex()};
//        CONFIG[24] = new int[]{3, SOUTH.getIndex()};
//        CONFIG[33] = new int[]{3, NORTH.getIndex(), UP.getIndex(), EAST.getIndex()};
//        CONFIG[34] = new int[]{3, NORTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[36] = new int[]{3};
//        CONFIG[40] = new int[]{3, EAST.getIndex()};
//
//        //Side Shapes (3 Connections)
//        CONFIG[7] = new int[]{4, SOUTH.getIndex(), UP.getIndex()};
//        CONFIG[11] = new int[]{4, NORTH.getIndex(), UP.getIndex()};
//        CONFIG[13] = new int[]{4, DOWN.getIndex(), DOWN.getIndex()};
//        CONFIG[14] = new int[]{4};
//        CONFIG[19] = new int[]{4, EAST.getIndex(), UP.getIndex()};
//        CONFIG[28] = new int[]{4, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[35] = new int[]{4, WEST.getIndex(), UP.getIndex()};
//        CONFIG[44] = new int[]{4, EAST.getIndex(), DOWN.getIndex(), WEST.getIndex()};
//        CONFIG[49] = new int[]{4, EAST.getIndex(), DOWN.getIndex(), DOWN.getIndex()};
//        CONFIG[50] = new int[]{4, EAST.getIndex()};
//        CONFIG[52] = new int[]{4, NORTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
//        CONFIG[56] = new int[]{4, SOUTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
//
//        //Corner Shapes (3 Connections)
//        CONFIG[21] = new int[]{5, WEST.getIndex(), DOWN.getIndex()};
//        CONFIG[22] = new int[]{5, WEST.getIndex()};
//        CONFIG[25] = new int[]{5, SOUTH.getIndex(), DOWN.getIndex()};
//        CONFIG[26] = new int[]{5, SOUTH.getIndex()};
//        CONFIG[41] = new int[]{5, EAST.getIndex(), DOWN.getIndex()};
//        CONFIG[42] = new int[]{5, EAST.getIndex()};
//        CONFIG[37] = new int[]{5, NORTH.getIndex(), DOWN.getIndex()};
//        CONFIG[38] = new int[]{5};
//
//        //Arrow Shapes (4 Connections)
//        CONFIG[23] = new int[]{6, WEST.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[27] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex(), EAST.getIndex()};
//        CONFIG[29] = new int[]{6, WEST.getIndex(), DOWN.getIndex()};
//        CONFIG[30] = new int[]{6, WEST.getIndex()};
//        CONFIG[39] = new int[]{6, EAST.getIndex(), DOWN.getIndex(), WEST.getIndex()};
//        CONFIG[43] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex(), WEST.getIndex()};
//        CONFIG[45] = new int[]{6, EAST.getIndex(), DOWN.getIndex()};
//        CONFIG[46] = new int[]{6, EAST.getIndex()};
//        CONFIG[53] = new int[]{6, DOWN.getIndex()};
//        CONFIG[54] = new int[]{6};
//        CONFIG[57] = new int[]{6, SOUTH.getIndex(), DOWN.getIndex()};
//        CONFIG[58] = new int[]{6, SOUTH.getIndex()};
//
//        //Cross Shapes (4 Connections)
//        CONFIG[15] = new int[]{7, WEST.getIndex(), UP.getIndex()};
//        CONFIG[51] = new int[]{7, UP.getIndex()};
//        CONFIG[60] = new int[]{7};
//
//        //Five Shapes (5 Connections)
//        CONFIG[31] = new int[]{8, EAST.getIndex(), UP.getIndex()};
//        CONFIG[47] = new int[]{8, WEST.getIndex(), UP.getIndex()};
//        CONFIG[55] = new int[]{8, SOUTH.getIndex(), UP.getIndex()};
//        CONFIG[59] = new int[]{8, NORTH.getIndex(), UP.getIndex()};
//        CONFIG[61] = new int[]{8, DOWN.getIndex(), DOWN.getIndex()};
//        CONFIG[62] = new int[]{8};
//
//        //All Shapes (6 Connections)
//        CONFIG[63] = new int[]{9};
    }
}
