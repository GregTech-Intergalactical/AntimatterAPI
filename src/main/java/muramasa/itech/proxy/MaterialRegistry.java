//package muramasa.itech.proxy;
//
//import java.util.Collection;
//import java.util.HashMap;
//
//public class Material {
//
//    /** Master Material Map **/
//    private static HashMap<String, Material> generatedMap = new HashMap<>();
//
//    /** Material Instances **/
//    public static Material Aluminium = new Material("Aluminium", 0x80c8f0, DUST, INGOT, PLATE);
//
//    /** Instance Members **/
//    private String name, displayName;
//    private int mask;
//    private int rgb;
//    //Expand for needs
//
//    public Material(String displayName, int rgb, MaterialFlag... flags) {
//        this.displayName = displayName;
//        this.name = displayName.toLowerCase().replaceAll("-", "").replaceAll(" ", "");
//        this.rgb = rgb;
//        for (MaterialFlag flag : flags) {
//            mask |= flag.getMask();
//        }
//        generatedMap.put(name, this);
//    }
//
//    public boolean hasFlag(MaterialFlag flag) {
//        return (mask & flag.getMask()) != 0;
//    }
//
//    /** Getters/Setters **/
//
//    public int getRGB() {
//        return rgb;
//    }
//
//    /** Map Get **/
//    public Material get(String name) {
//        //This is where you could do error handling if you want
//        return generatedMap.get(name);
//    }
//
//    /** Helper for looping **/
//    public Collection<Material> values() {
//        return generatedMap.values();
//    }
//}
//
//public enum MaterialFlag {
//
//    INGOT,
//    DUST,
//    PLATE;
//
//    private int mask;
//
//    MaterialFlag() {
//        this.mask = 1 << ordinal();
//    }
//
//    public int getMask() {
//        return mask;
//    }
//}
