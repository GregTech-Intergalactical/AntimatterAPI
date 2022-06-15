package muramasa.antimatter.client.modeldata;

import org.jetbrains.annotations.Nullable;

public enum AntimatterEmptyModelData implements IAntimatterModelData{
    INSTANCE;
    @Override
    public boolean hasProperty(Class<?> prop) {
        return false;
    }

    @Nullable
    @Override
    public <T> T getData(Class<T> prop) {
        return null;
    }

    @Nullable
    @Override
    public <T> T setData(Class<T> prop, T data) {
        return null;
    }
}
