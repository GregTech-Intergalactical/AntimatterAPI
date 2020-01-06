package muramasa.gtu.client;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.pipe.BlockFluidPipe;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.model.ModelDynamic;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.client.render.models.ModelNichrome;

import static muramasa.gtu.common.Data.CASING_FUSION_3;
import static muramasa.gtu.common.Data.COIL_NICHROME;
import static muramasa.gtu.data.Textures.FUSION_3_CT;
import static muramasa.gtu.data.Textures.PIPE;
import static net.minecraft.util.Direction.*;

public class Models {

    public static void init() {
        AntimatterModelLoader.put(COIL_NICHROME, new ModelNichrome());

        AntimatterModelLoader.put(CASING_FUSION_3, new ModelDynamic(CASING_FUSION_3)
            .config(m -> basic(m, FUSION_3_CT))
            .add(32, b -> b.of("block/preset/simple").tex("all", "minecraft:block/diamond_block"))
        );

        ModelDynamic modelPipe = new ModelDynamic(PIPE).config(m -> pipe(m)).staticBaking();

        BlockFluidPipe pipe = AntimatterAPI.get(BlockFluidPipe.class, "fluid_pipe_tungstensteel_normal");
        if (pipe != null) {
            AntimatterModelLoader.put(pipe, modelPipe);
        }
    }

    public static void basic(ModelDynamic model, Texture[] tex) {
        if (tex.length < 13) return;
        //Single (1)
        model.add(1, tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]);
        model.add(2, tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]);
        model.add(4, tex[1], tex[1], tex[0], tex[12], tex[0], tex[0]);
        model.add(8, tex[1], tex[1], tex[12], tex[0], tex[0], tex[0]);
        model.add(16, tex[0], tex[0], tex[0], tex[0], tex[0], tex[12]);
        model.add(32, tex[0], tex[0], tex[0], tex[0], tex[12], tex[0]);

        //Lines (2)
        model.add(3, tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]);
        model.add(12, tex[1], tex[1], tex[12], tex[12], tex[0], tex[0]);
        model.add(48, tex[0], tex[0], tex[0], tex[0], tex[12], tex[12]);

        //Elbows (2)
        model.add(6, tex[1], tex[12], tex[0], tex[1], tex[10], tex[11]);
        model.add(5, tex[12], tex[1], tex[12], tex[1], tex[9], tex[8]);
        model.add(9, tex[12], tex[1], tex[1], tex[12], tex[8], tex[9]);
        model.add(10, tex[1], tex[12], tex[1], tex[12], tex[11], tex[10]);
        model.add(17, tex[12], tex[0], tex[8], tex[9], tex[12], tex[1]);
        model.add(18, tex[0], tex[12], tex[11], tex[10], tex[12], tex[1]);
        model.add(33, tex[12], tex[0], tex[9], tex[8], tex[1], tex[12]);
        model.add(34, tex[0], tex[12], tex[10], tex[11], tex[1], tex[10]);
        model.add(20, tex[10], tex[10], tex[0], tex[0], tex[0], tex[0]);
        model.add(24, tex[9], tex[9], tex[0], tex[0], tex[0], tex[0]);
        model.add(36, tex[11], tex[11], tex[0], tex[0], tex[0], tex[0]);
        model.add(40, tex[8], tex[8], tex[0], tex[0], tex[0], tex[0]);

        //Side (3)
        model.add(7, tex[12], tex[12], tex[12], tex[1], tex[4], tex[2]);
        model.add(11, tex[12], tex[12], tex[1], tex[12], tex[2], tex[4]);
        model.add(13, tex[12], tex[1], tex[12], tex[12], tex[3], tex[3]);
        model.add(14, tex[1], tex[12], tex[12], tex[12], tex[5], tex[5]);
        model.add(19, tex[12], tex[12], tex[2], tex[4], tex[12], tex[1]);
        model.add(28, tex[4], tex[4], tex[12], tex[12], tex[12], tex[0]);
        model.add(35, tex[12], tex[12], tex[4], tex[2], tex[1], tex[12]);
        model.add(44, tex[2], tex[2], tex[12], tex[12], tex[0], tex[12]);
        model.add(49, tex[12], tex[0], tex[3], tex[3], tex[12], tex[12]);
        model.add(50, tex[0], tex[12], tex[5], tex[5], tex[12], tex[12]);
        model.add(52, tex[3], tex[5], tex[12], tex[0], tex[12], tex[12]);
        model.add(56, tex[5], tex[3], tex[0], tex[12], tex[12], tex[12]);

        //Corner (3)
        model.add(21, tex[10], tex[10], tex[0], tex[9], tex[0], tex[8]);
        model.add(22, tex[10], tex[10], tex[0], tex[10], tex[0], tex[11]);
        model.add(25, tex[9], tex[9], tex[8], tex[0], tex[0], tex[9]);
        model.add(26, tex[9], tex[9], tex[11], tex[0], tex[0], tex[10]);
        model.add(37, tex[11], tex[11], tex[0], tex[8], tex[9], tex[0]);
        model.add(38, tex[11], tex[11], tex[0], tex[11], tex[10], tex[0]);
        model.add(41, tex[8], tex[8], tex[9], tex[0], tex[8], tex[0]);
        model.add(42, tex[8], tex[8], tex[10], tex[0], tex[11], tex[0]);

        //Arrow (4)
        model.add(23, tex[12], tex[12], tex[12], tex[4], tex[12], tex[2]);
        model.add(27, tex[12], tex[12], tex[2], tex[12], tex[12], tex[4]);
        model.add(29, tex[12], tex[4], tex[12], tex[12], tex[12], tex[3]);
        model.add(30, tex[4], tex[12], tex[12], tex[12], tex[12], tex[5]);
        model.add(39, tex[12], tex[12], tex[12], tex[2], tex[4], tex[12]);
        model.add(43, tex[12], tex[12], tex[4], tex[12], tex[2], tex[12]);
        model.add(45, tex[12], tex[2], tex[12], tex[12], tex[3], tex[12]);
        model.add(46, tex[2], tex[12], tex[12], tex[12], tex[5], tex[12]);
        model.add(53, tex[12], tex[5], tex[12], tex[3], tex[12], tex[12]);
        model.add(54, tex[3], tex[12], tex[12], tex[5], tex[12], tex[12]);
        model.add(57, tex[12], tex[3], tex[3], tex[12], tex[12], tex[12]);
        model.add(58, tex[5], tex[12], tex[5], tex[12], tex[12], tex[12]);

        //Cross (4)
        model.add(15, tex[12], tex[12], tex[12], tex[12], tex[6], tex[6]);
        model.add(51, tex[12], tex[12], tex[6], tex[6], tex[12], tex[12]);
        model.add(60, tex[6], tex[6], tex[12], tex[12], tex[12], tex[12]);

        //Five (5)
        model.add(31, tex[12], tex[12], tex[12], tex[12], tex[12], tex[6]);
        model.add(47, tex[12], tex[12], tex[12], tex[12], tex[6], tex[12]);
        model.add(55, tex[12], tex[12], tex[12], tex[6], tex[12], tex[12]);
        model.add(59, tex[12], tex[12], tex[6], tex[12], tex[12], tex[12]);
        model.add(61, tex[12], tex[6], tex[12], tex[12], tex[12], tex[12]);
        model.add(62, tex[6], tex[12], tex[12], tex[12], tex[12], tex[12]);

        //All (6)
        model.add(63, tex[12], tex[12], tex[12], tex[12], tex[12], tex[12]);
    }

    public static void pipe(ModelDynamic model) {
        for (PipeSize s : new PipeSize[]{PipeSize.NORMAL}) {
            //Default Shape (0 Connections)
            model.add(0, b -> b.of(s.getLoc("base")).tex("0", PIPE));

            //Single Shapes (1 Connections)
            model.add(1, b -> b.of(s.getLoc("single")).tex("0", PIPE).rot(DOWN));
            model.add(2, b -> b.of(s.getLoc("single")).tex("0", PIPE).rot(UP));
            model.add(4, b -> b.of(s.getLoc("single")).tex("0", PIPE));
            model.add(8, b -> b.of(s.getLoc("single")).tex("0", PIPE).rot(SOUTH));
            model.add(16, b -> b.of(s.getLoc("single")).tex("0", PIPE).rot(WEST));
            model.add(32, b -> b.of(s.getLoc("single")).tex("0", PIPE).rot(EAST));

            //Line Shapes (2 Connections)
            model.add(3, b -> b.of(s.getLoc("line")).tex("0", PIPE).rot(UP));
            model.add(12, b -> b.of(s.getLoc("line")).tex("0", PIPE));
            model.add(48, b -> b.of(s.getLoc("line")).tex("0", PIPE).rot(WEST));

            //Elbow Shapes (2 Connections)
            model.add(5, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(WEST, UP, EAST));
            model.add(6, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(WEST, DOWN, EAST));
            model.add(9, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(EAST, UP, EAST));
            model.add(10, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(EAST, DOWN, EAST));
            model.add(17, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(NORTH, DOWN, WEST));
            model.add(18, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(SOUTH, DOWN, EAST));
            model.add(20, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(WEST));
            model.add(24, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(SOUTH));
            model.add(33, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(NORTH, UP, EAST));
            model.add(34, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(NORTH, DOWN, EAST));
            model.add(36, b -> b.of(s.getLoc("elbow")).tex("0", PIPE));
            model.add(40, b -> b.of(s.getLoc("elbow")).tex("0", PIPE).rot(EAST));

            //Side Shapes (3 Connections)
            model.add(7, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(SOUTH, UP));
            model.add(11, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(NORTH, UP));
            model.add(13, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(DOWN, DOWN));
            model.add(14, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(EAST, UP));
            model.add(19, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(WEST, DOWN, EAST));
            model.add(28, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(WEST, UP));
            model.add(35, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(EAST, DOWN, WEST));
            model.add(44, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(EAST, DOWN, DOWN));
            model.add(49, b -> b.of(s.getLoc("side")).tex("0", PIPE));
            model.add(50, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(EAST));
            model.add(52, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(NORTH, DOWN, WEST));
            model.add(56, b -> b.of(s.getLoc("side")).tex("0", PIPE).rot(SOUTH, DOWN, WEST));

            //Corner Shapes (3 Connections)
            model.add(21, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(WEST, DOWN));
            model.add(22, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(WEST));
            model.add(25, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(SOUTH, DOWN));
            model.add(26, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(SOUTH));
            model.add(41, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(EAST, DOWN));
            model.add(42, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(EAST));
            model.add(37, b -> b.of(s.getLoc("corner")).tex("0", PIPE).rot(NORTH, DOWN));
            model.add(38, b -> b.of(s.getLoc("corner")).tex("0", PIPE));

            //Arrow Shapes (4 Connections)
            model.add(23, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(WEST, DOWN, EAST));
            model.add(27, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(SOUTH, DOWN, EAST));
            model.add(29, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(WEST, DOWN));
            model.add(30, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(WEST));
            model.add(39, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(EAST, DOWN, WEST));
            model.add(43, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(SOUTH, DOWN, WEST));
            model.add(45, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(EAST, DOWN));
            model.add(46, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(WEST));
            model.add(53, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(DOWN));
            model.add(54, b -> b.of(s.getLoc("arrow")).tex("0", PIPE));
            model.add(57, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(SOUTH, DOWN));
            model.add(58, b -> b.of(s.getLoc("arrow")).tex("0", PIPE).rot(SOUTH));

            //Cross Shapes (4 Connections)
            model.add(15, b -> b.of(s.getLoc("cross")).tex("0", PIPE).rot(WEST, UP));
            model.add(51, b -> b.of(s.getLoc("cross")).tex("0", PIPE).rot(UP));
            model.add(60, b -> b.of(s.getLoc("cross")).tex("0", PIPE));

            //Five Shapes (5 Connections)
            model.add(31, b -> b.of(s.getLoc("five")).tex("0", PIPE).rot(EAST, UP));
            model.add(47, b -> b.of(s.getLoc("five")).tex("0", PIPE).rot(WEST, UP));
            model.add(55, b -> b.of(s.getLoc("five")).tex("0", PIPE).rot(SOUTH, UP));
            model.add(59, b -> b.of(s.getLoc("five")).tex("0", PIPE).rot(NORTH, UP));
            model.add(61, b -> b.of(s.getLoc("five")).tex("0", PIPE).rot(DOWN, DOWN));
            model.add(62, b -> b.of(s.getLoc("five")).tex("0", PIPE));

            //All Shapes (6 Connections)
            model.add(63, b -> b.of(s.getLoc("all")).tex("0", PIPE));
        }
    }
}
