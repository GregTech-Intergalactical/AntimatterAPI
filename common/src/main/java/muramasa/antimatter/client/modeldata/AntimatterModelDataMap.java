package muramasa.antimatter.client.modeldata;

import com.google.common.base.Preconditions;

import java.util.IdentityHashMap;
import java.util.Map;

public class AntimatterModelDataMap implements IAntimatterModelData {
    private final Map<Class<?>, Object> backingMap;

    private AntimatterModelDataMap(Map<Class<?>, Object> map)
    {
        this.backingMap = new IdentityHashMap<>(map);
    }

    protected AntimatterModelDataMap()
    {
        this.backingMap = new IdentityHashMap<>();
    }

    @Override
    public boolean hasProperty(Class<?> prop)
    {
        return backingMap.containsKey(prop);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData(Class<T> prop)
    {
        return (T) backingMap.get(prop);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T setData(Class<T> prop, T data)
    {
        //todo figure out if this is necessary
        //Preconditions.checkArgument(prop.test(data), "Value is invalid for this property");
        return (T) backingMap.put(prop, data);
    }

    public static class Builder
    {
        private final Map<Class<?>, Object> defaults = new IdentityHashMap<>();

        public Builder withProperty(Class<?> prop)
        {
            return withInitial(prop, null);
        }

        public <T> Builder withInitial(Class<T> prop, T data)
        {
            this.defaults.put(prop, data);
            return this;
        }

        public AntimatterModelDataMap build()
        {
            return new AntimatterModelDataMap(defaults);
        }
    }
}
