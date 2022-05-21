package muramasa.antimatter.material;

public class SubTag implements IAntimatterObject {

    public static final SubTag GOOD_SOLDER = new SubTag("solder_good");
    public static final SubTag BAD_SOLDER = new SubTag("solder_bad");
    public static final SubTag COPPER_WIRE = new SubTag("copper_wire");

    public final String id;

    public SubTag(String id) {
        this.id = id + "_subtag";
        AntimatterAPI.register(SubTag.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public static void init() {
    }
}
