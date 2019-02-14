//package muramasa.gregtech.api.cover;
//
//import muramasa.gregtech.api.cover.behaviour.CoverBehaviourEnergy;
//import muramasa.gregtech.api.cover.behaviour.CoverBehaviourFluid;
//import muramasa.gregtech.api.cover.behaviour.CoverBehaviourItem;
//import net.minecraft.util.IStringSerializable;
//
//import java.util.Collection;
//import java.util.HashMap;
//
//public class Cover implements IStringSerializable {
//
//    private static HashMap<String, Cover> TYPE_LOOKUP = new HashMap<>();
//
//    private static int lastInternalId = 0;
//
//    public static Cover NONE = new Cover("none", null);
//    public static Cover BLANK = new Cover("blank", null);
//    public static Cover ITEM_PORT = new Cover("item_port", new CoverBehaviourItem());
//    public static Cover FLUID_PORT = new Cover("fluid_port", new CoverBehaviourFluid());
//    public static Cover ENERGY_PORT = new Cover("energy_port", new CoverBehaviourEnergy());
//
//    private String name;
//    private int internalId;
//    private CoverBehaviour behaviour;
//
//    public Cover(String name, CoverBehaviour behaviour) {
//        this.name = name;
//        internalId = lastInternalId++;
//        this.behaviour = behaviour;
//        TYPE_LOOKUP.put(name, this);
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    public int getInternalId() {
//        return internalId;
//    }
//
//    public boolean hasBehaviour() {
//        return behaviour != null;
//    }
//
//    public CoverBehaviour getBehaviour() {
//        return behaviour;
//    }
//
//
//
//    public boolean isEqual(Cover cover) {
//        return cover.getName().equals(name);
//    }
//
//    public static Cover get(String name) {
//        return TYPE_LOOKUP.get(name);
//    }
//
//    public static Collection<Cover> getAll() {
//        return TYPE_LOOKUP.values();
//    }
//
//    public static int getLastInternalId() {
//        return lastInternalId;
//    }
//}
