package muramasa.antimatter.material;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluids;

import java.util.Arrays;

public class MaterialTypeFluid<T> extends MaterialType<T> {

    public MaterialTypeFluid(String id, int layers, boolean visible, long unitValue) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeFluid.class, this);
    }

    public static FluidHolder getEmptyFluidAndLog(MaterialType<?> type, IAntimatterObject... objects) {
        Utils.onInvalidData("Tried to create " + type.getId() + " for objects: " + Arrays.toString(Arrays.stream(objects).map(IAntimatterObject::getId).toArray(String[]::new)));
        return FluidHooks.newFluidHolder(Fluids.WATER, 1, null);
    }

    @Override
    protected TagKey<?> tagFromString(String name) {
        return TagUtils.getForgelikeFluidTag(name);
    }

    public interface IFluidGetter {
        FluidHolder get(Material m, long amount);
    }
}
