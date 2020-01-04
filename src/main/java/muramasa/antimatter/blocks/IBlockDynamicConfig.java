package muramasa.antimatter.blocks;

import muramasa.antimatter.texture.Texture;

public interface IBlockDynamicConfig {

    default void buildBasic(BlockDynamic block, Texture[] textures) {
        if (textures.length < 13) return;
        //Single (1)
        block.add(1, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        block.add(2, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        block.add(4, textures[1], textures[1], textures[0], textures[12], textures[0], textures[0]);
        block.add(8, textures[1], textures[1], textures[12], textures[0], textures[0], textures[0]);
        block.add(16, textures[0], textures[0], textures[0], textures[0], textures[0], textures[12]);
        block.add(32, textures[0], textures[0], textures[0], textures[0], textures[12], textures[0]);

        //Lines (2)
        block.add(3, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        block.add(12, textures[1], textures[1], textures[12], textures[12], textures[0], textures[0]);
        block.add(48, textures[0], textures[0], textures[0], textures[0], textures[12], textures[12]);

        //Elbows (2)
        block.add(6, textures[1], textures[12], textures[0], textures[1], textures[10], textures[11]);
        block.add(5, textures[12], textures[1], textures[12], textures[1], textures[9], textures[8]);
        block.add(9, textures[12], textures[1], textures[1], textures[12], textures[8], textures[9]);
        block.add(10, textures[1], textures[12], textures[1], textures[12], textures[11], textures[10]);
        block.add(17, textures[12], textures[0], textures[8], textures[9], textures[12], textures[1]);
        block.add(18, textures[0], textures[12], textures[11], textures[10], textures[12], textures[1]);
        block.add(33, textures[12], textures[0], textures[9], textures[8], textures[1], textures[12]);
        block.add(34, textures[0], textures[12], textures[10], textures[11], textures[1], textures[10]);
        block.add(20, textures[10], textures[10], textures[0], textures[0], textures[0], textures[0]);
        block.add(24, textures[9], textures[9], textures[0], textures[0], textures[0], textures[0]);
        block.add(36, textures[11], textures[11], textures[0], textures[0], textures[0], textures[0]);
        block.add(40, textures[8], textures[8], textures[0], textures[0], textures[0], textures[0]);

        //Side (3)
        block.add(7, textures[12], textures[12], textures[12], textures[1], textures[4], textures[2]);
        block.add(11, textures[12], textures[12], textures[1], textures[12], textures[2], textures[4]);
        block.add(13, textures[12], textures[1], textures[12], textures[12], textures[3], textures[3]);
        block.add(14, textures[1], textures[12], textures[12], textures[12], textures[5], textures[5]);
        block.add(19, textures[12], textures[12], textures[2], textures[4], textures[12], textures[1]);
        block.add(28, textures[4], textures[4], textures[12], textures[12], textures[12], textures[0]);
        block.add(35, textures[12], textures[12], textures[4], textures[2], textures[1], textures[12]);
        block.add(44, textures[2], textures[2], textures[12], textures[12], textures[0], textures[12]);
        block.add(49, textures[12], textures[0], textures[3], textures[3], textures[12], textures[12]);
        block.add(50, textures[0], textures[12], textures[5], textures[5], textures[12], textures[12]);
        block.add(52, textures[3], textures[5], textures[12], textures[0], textures[12], textures[12]);
        block.add(56, textures[5], textures[3], textures[0], textures[12], textures[12], textures[12]);

        //Corner (3)
        block.add(21, textures[10], textures[10], textures[0], textures[9], textures[0], textures[8]);
        block.add(22, textures[10], textures[10], textures[0], textures[10], textures[0], textures[11]);
        block.add(25, textures[9], textures[9], textures[8], textures[0], textures[0], textures[9]);
        block.add(26, textures[9], textures[9], textures[11], textures[0], textures[0], textures[10]);
        block.add(37, textures[11], textures[11], textures[0], textures[8], textures[9], textures[0]);
        block.add(38, textures[11], textures[11], textures[0], textures[11], textures[10], textures[0]);
        block.add(41, textures[8], textures[8], textures[9], textures[0], textures[8], textures[0]);
        block.add(42, textures[8], textures[8], textures[10], textures[0], textures[11], textures[0]);

        //Arrow (4)
        block.add(23, textures[12], textures[12], textures[12], textures[4], textures[12], textures[2]);
        block.add(27, textures[12], textures[12], textures[2], textures[12], textures[12], textures[4]);
        block.add(29, textures[12], textures[4], textures[12], textures[12], textures[12], textures[3]);
        block.add(30, textures[4], textures[12], textures[12], textures[12], textures[12], textures[5]);
        block.add(39, textures[12], textures[12], textures[12], textures[2], textures[4], textures[12]);
        block.add(43, textures[12], textures[12], textures[4], textures[12], textures[2], textures[12]);
        block.add(45, textures[12], textures[2], textures[12], textures[12], textures[3], textures[12]);
        block.add(46, textures[2], textures[12], textures[12], textures[12], textures[5], textures[12]);
        block.add(53, textures[12], textures[5], textures[12], textures[3], textures[12], textures[12]);
        block.add(54, textures[3], textures[12], textures[12], textures[5], textures[12], textures[12]);
        block.add(57, textures[12], textures[3], textures[3], textures[12], textures[12], textures[12]);
        block.add(58, textures[5], textures[12], textures[5], textures[12], textures[12], textures[12]);

        //Cross (4)
        block.add(15, textures[12], textures[12], textures[12], textures[12], textures[6], textures[6]);
        block.add(51, textures[12], textures[12], textures[6], textures[6], textures[12], textures[12]);
        block.add(60, textures[6], textures[6], textures[12], textures[12], textures[12], textures[12]);

        //Five (5)
        block.add(31, textures[12], textures[12], textures[12], textures[12], textures[12], textures[6]);
        block.add(47, textures[12], textures[12], textures[12], textures[12], textures[6], textures[12]);
        block.add(55, textures[12], textures[12], textures[12], textures[6], textures[12], textures[12]);
        block.add(59, textures[12], textures[12], textures[6], textures[12], textures[12], textures[12]);
        block.add(61, textures[12], textures[6], textures[12], textures[12], textures[12], textures[12]);
        block.add(62, textures[6], textures[12], textures[12], textures[12], textures[12], textures[12]);

        //All (6)
        block.add(63, textures[12], textures[12], textures[12], textures[12], textures[12], textures[12]);
    }
}
