package muramasa.antimatter.datagen.base;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;

@VisibleForTesting
public interface IGeneratedBlockstate {
    JsonObject toJson();
}
