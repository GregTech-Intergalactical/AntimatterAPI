package muramasa.antimatter.cover;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public abstract class CoverMaterial extends BaseCover {

    public abstract MaterialType<?> getType();

    public abstract Material getMaterial();

    @Override
    public <T> boolean blocksCapability(CoverStack<?> stack, Capability<T> cap, Direction side) {
        return true;
    }
}
