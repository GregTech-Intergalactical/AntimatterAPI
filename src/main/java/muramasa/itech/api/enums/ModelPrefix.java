package muramasa.itech.api.enums;

import net.minecraft.util.IStringSerializable;

public enum ModelPrefix implements IStringSerializable {

    BASE(),
    OVERLAY(),
    ITEM(),
    TEXTURE(),
    COVER();

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
