package muramasa.gregtech.api.properties;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedResourceLocation implements IUnlistedProperty<ResourceLocation> {

    @Override
    public String getName() {
        return "resourceLocation";
    }

    @Override
    public boolean isValid(ResourceLocation value) {
        return true;
    }

    @Override
    public Class<ResourceLocation> getType() {
        return ResourceLocation.class;
    }

    @Override
    public String valueToString(ResourceLocation value) {
        return value.toString();
    }
}
