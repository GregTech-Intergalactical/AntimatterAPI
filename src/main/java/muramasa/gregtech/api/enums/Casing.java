package muramasa.gregtech.api.enums;

import muramasa.gregtech.Ref;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;

public class Casing implements IStringSerializable {

    private static LinkedHashMap<String, Casing> TYPE_LOOKUP = new LinkedHashMap<>();

    public static Casing ULV = new Casing("ulv");
    public static Casing LV = new Casing("lv");
    public static Casing MV = new Casing("mv");
    public static Casing HV = new Casing("hv");
    public static Casing EV = new Casing("ev");
    public static Casing IV = new Casing("iv");
    public static Casing LUV = new Casing("luv");
    public static Casing ZPM = new Casing("zpm");
    public static Casing UV = new Casing("uv");
    public static Casing MAX = new Casing("max");
    public static Casing BRONZE = new Casing("bronze");
    public static Casing BRICKED_BRONZE = new Casing("bricked_bronze");
    public static Casing BRONZE_PLATED_BRICK = new Casing("bronze_plated_brick");
    public static Casing FIRE_BRICK = new Casing("fire_brick");
    public static Casing STEEL = new Casing("steel");
    public static Casing BRICKED_STEEL = new Casing("bricked_steel");
    public static Casing SOLID_STEEL = new Casing("solid_steel");
    public static Casing STAINLESS_STEEL = new Casing("stainless_steel");
    public static Casing TITANIUM = new Casing("titanium");
    public static Casing TUNGSTENSTEEL = new Casing("tungstensteel");
    public static Casing HEAT_PROOF = new Casing("heat_proof");
    public static Casing FROST_PROOF = new Casing("frost_proof");
    public static Casing RADIATION_PROOF = new Casing("radiation_proof");
    public static Casing FIREBOX_BRONZE = new Casing("firebox_bronze");
    public static Casing FIREBOX_STEEL = new Casing("firebox_steel");
    public static Casing FIREBOX_TITANIUM = new Casing("firebox_titanium");
    public static Casing FIREBOX_TUNGSTENSTEEL = new Casing("firebox_tungstensteel");
    public static Casing GEARBOX_BRONZE = new Casing("gearbox_bronze");
    public static Casing GEARBOX_STEEL = new Casing("gearbox_steel");
    public static Casing GEARBOX_TITANIUM = new Casing("gearbox_titanium");
    public static Casing GEARBOX_TUNGSTENSTEEL = new Casing("gearbox_tungstensteel");
    public static Casing PIPE_BRONZE = new Casing("pipe_bronze");
    public static Casing PIPE_STEEL = new Casing("pipe_steel");
    public static Casing PIPE_TITANIUM = new Casing("pipe_titanium");
    public static Casing PIPE_TUNGSTENSTEEL = new Casing("pipe_tungstensteel");
    public static Casing ENGINE_INTAKE = new Casing("engine_intake");
    public static Casing TURBINE_1 = new Casing("turbine_1");
    public static Casing TURBINE_2 = new Casing("turbine_2");
    public static Casing TURBINE_3 = new Casing("turbine_3");
    public static Casing TURBINE_4 = new Casing("turbine_4");
    public static Casing FUSION_1 = new Casing("fusion_1");
    public static Casing FUSION_2 = new Casing("fusion_2");
    public static Casing FUSION_3 = new Casing("fusion_3");
    
    private String name;

    public Casing(String name) {
        this.name = name;
        TYPE_LOOKUP.put(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    public ResourceLocation getLoc() {
        return new ResourceLocation(Ref.MODID, "blocks/casing/" + name);
    }

    public static Casing get(String name) {
        return TYPE_LOOKUP.get(name);
    }

    public static Collection<Casing> getAll() {
        return TYPE_LOOKUP.values();
    }
}
