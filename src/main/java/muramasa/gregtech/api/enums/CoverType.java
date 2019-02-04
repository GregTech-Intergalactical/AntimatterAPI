package muramasa.gregtech.api.enums;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public enum CoverType implements IStringSerializable {

    NONE(),
    BLANK(false),
    ITEM_PORT(true),
    FLUID_PORT(true),
    ENERGY_PORT(false);

    private boolean canBeRendered, canWrenchToggleState;

    CoverType() {

    }

    CoverType(boolean wrenchToggle) {
        canBeRendered = true;
        canWrenchToggleState = wrenchToggle;
    }

    public ModelResourceLocation getModelLoc() {
        return new ModelResourceLocation(Ref.MODID + ":machine_part/covers/" + getName());
    }

    public ResourceLocation getTextureLoc() {
        return new ResourceLocation(Ref.MODID, "blocks/machines/covers/" + getName());
    }

    public boolean canBeRendered() {
        return canBeRendered;
    }

    public boolean canWrenchToggleState() {
        return canWrenchToggleState;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
