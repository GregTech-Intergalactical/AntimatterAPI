package muramasa.gregtech.api.cover;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;

public class Cover implements IStringSerializable {

    private static HashMap<String, Cover> TYPE_LOOKUP = new HashMap<>();

    private static int lastInternalId = 0;

    public static Cover NONE = new Cover("none", false);
    public static Cover BLANK = new Cover("blank", false);
    public static Cover ITEM_PORT = new Cover("item_port", true);
    public static Cover FLUID_PORT = new Cover("fluid_port", true);
    public static Cover ENERGY_PORT = new Cover("energy_port", true);

    private String name;
    private int internalId;
    private boolean canWrenchToggleState;

    public Cover(String name, boolean canWrenchToggleState) {
        this.name = name;
        internalId = lastInternalId++;
        this.canWrenchToggleState = canWrenchToggleState;
        TYPE_LOOKUP.put(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    public int getInternalId() {
        return internalId;
    }

    public boolean canWrenchToggleState() {
        return canWrenchToggleState;
    }

    public ModelResourceLocation getModelLoc() {
        return new ModelResourceLocation(Ref.MODID + ":machine_part/covers/" + getName());
    }

    public ResourceLocation getTextureLoc() {
        return new ResourceLocation(Ref.MODID, "blocks/machines/covers/" + getName());
    }

//    public boolean isEqual(Cover cover) {
//        return cover.getName().equals(name);
//    }

    public static Cover get(String name) {
        return TYPE_LOOKUP.get(name);
    }

    public static Collection<Cover> getAll() {
        return TYPE_LOOKUP.values();
    }

    public static int getLastInternalId() {
        return lastInternalId;
    }
}
