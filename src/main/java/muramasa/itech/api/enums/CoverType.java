package muramasa.itech.api.enums;

import muramasa.itech.ITech;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public enum CoverType implements IStringSerializable {

    NONE(),
    BLANK(false),
    ITEMPORT(true),
    FLUIDPORT(true),
    ENERGYPORT(false);

    private boolean canBeRendered, canWrenchToggleState;

    CoverType() {

    }

    CoverType(boolean wrenchToggle) {
        canBeRendered = true;
        canWrenchToggleState = wrenchToggle;
    }

    public ModelResourceLocation getModelLoc() {
        return new ModelResourceLocation(ITech.MODID + ":machineparts/covers/" + getName());
    }

    public ResourceLocation getTextureLoc() {
        return new ResourceLocation(ITech.MODID, "blocks/machines/covers/" + getName());
    }

    public boolean canBeRendered() {
        return canBeRendered;
    }

    public boolean canWrenchToggleState() {
        return canWrenchToggleState;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
