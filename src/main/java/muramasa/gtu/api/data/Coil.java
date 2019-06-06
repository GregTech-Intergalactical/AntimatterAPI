package muramasa.gtu.api.data;

import muramasa.gtu.api.registration.IGregTechObject;

import java.util.Collection;
import java.util.LinkedHashMap;

public class Coil implements IGregTechObject {

    private static LinkedHashMap<String, Coil> TYPE_LOOKUP = new LinkedHashMap<>();

    public static Coil CUPRONICKEL = new Coil("cupronickel", 113); //1808
    public static Coil KANTHAL = new Coil("kanthal", 169); //2704
    public static Coil NICHROME = new Coil("nichrome", 225); //3600
    public static Coil TUNGSTENSTEEL = new Coil("tungstensteel", 282); //4512
    public static Coil HSSG = new Coil("hssg", 338); //5408
    public static Coil NAQUADAH = new Coil("naquadah", 450); //7200
    public static Coil NAQUADAH_ALLOY = new Coil("naquadah_alloy", 563); //9008
    public static Coil FUSION = new Coil("fusion", 563); //9008
    public static Coil SUPERCONDUCTOR = new Coil("superconductor", 563); //9008

    private String name;
    private int heatingCapacity;

    public Coil(String name, int heatingCapacity) {
        this.name = name;
        this.heatingCapacity = heatingCapacity;
        TYPE_LOOKUP.put(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    public int getHeatingCapacity() {
        return heatingCapacity;
    }

    public static Coil get(String name) {
        return TYPE_LOOKUP.get(name);
    }

    public static Collection<Coil> getAll() {
        return TYPE_LOOKUP.values();
    }
}
