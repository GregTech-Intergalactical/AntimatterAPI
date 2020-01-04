package muramasa.antimatter.client.model;

import muramasa.antimatter.texture.Texture;

public interface IModelDynamicConfig {
    
    default void basic(ModelDynamic model, Texture[] textures) {
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
}
