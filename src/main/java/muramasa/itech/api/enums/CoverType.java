package muramasa.itech.api.enums;

import muramasa.itech.ITech;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;

public enum CoverType implements IStringSerializable {

    NONE(),
    BLANK(false),
    ITEMPORT(true),
    FLUIDPORT(true),
    ENERGYPORT(false);

    private ModelResourceLocation modelLocation;
    private boolean canWrenchToggleState;

    CoverType() {

    }

    CoverType(boolean canWrenchToggleState) {
        modelLocation = new ModelResourceLocation(ITech.MODID + ":machineparts/covers/" + getName());
        this.canWrenchToggleState = canWrenchToggleState;
    }

    public ModelResourceLocation getModelLocation() {
        return modelLocation;
    }

    public boolean canWrenchToggleState() {
        return canWrenchToggleState;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
