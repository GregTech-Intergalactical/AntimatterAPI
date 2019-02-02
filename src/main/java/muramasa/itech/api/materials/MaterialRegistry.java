package muramasa.itech.api.materials;

import java.util.HashMap;

public class Material {

    /** Master Material Map **/
    private static HashMap<String, Material> generatedMap = new HashMap<>();

    /** Material Instances **/
    public static Material Aluminium = new Material("Aluminium", 0x80c8f0);

    /** Instance Members **/
    private String name, displayName;
    private int rgb;
    //Expand for needs

    public Material(String displayName, int rgb) {
        this.displayName = displayName;
        this.name = displayName.toLowerCase().replaceAll("-", "").replaceAll(" ", "");
        this.rgb = rgb;
        generatedMap.put(name, this);
    }

    /** Getters/Setters **/

    public int getRGB() {
        return rgb;
    }

    /** Map Get **/
    public Material get(String name) {
        //This is where you could do error handling if you want
        return generatedMap.get(name);
    }
}
