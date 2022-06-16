package muramasa.antimatter.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

public class SimpleModelState implements ModelState {
    public static final SimpleModelState IDENTITY = new SimpleModelState(Transformation.identity());

    private final ImmutableMap<?, Transformation> map;
    private final Transformation base;

    public SimpleModelState(ImmutableMap<?, Transformation> map)
    {
        this(map, Transformation.identity());
    }

    public SimpleModelState(Transformation base)
    {
        this(ImmutableMap.of(), base);
    }

    public SimpleModelState(ImmutableMap<?, Transformation> map, Transformation base)
    {
        this.map = map;
        this.base = base;
    }

    @Override
    public Transformation getRotation()
    {
        return base;
    }

    //todo figure out if this is needed on fabric
    //@Override
    public Transformation getPartTransformation(Object part)
    {
        return map.getOrDefault(part, Transformation.identity());
    }
}
