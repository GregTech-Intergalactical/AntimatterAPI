package muramasa.antimatter.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;

public class TextureMap {

    private Int2ObjectOpenHashMap<HashMap<String, Texture[]>> map = new Int2ObjectOpenHashMap<>();

    public TextureMap() {

    }

//    public TextureMap of(int layer, Texture...   textures) {
//        map.put(layer, )
//    }
}
