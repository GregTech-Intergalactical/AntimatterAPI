package muramasa.antimatter.recipe.condition;

import com.google.gson.JsonObject;
import muramasa.antimatter.Ref;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Ref.ID, "config");
    private final String configName;

    ConfigCondition(String config) {
        this.configName = config;
    }

    public ConfigCondition(Class clazz, String variableName) {
        this(clazz.getCanonicalName() + "." + variableName);
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        try {
            int lastIndex = this.configName.lastIndexOf('.');
            return Class.forName(this.configName.substring(0, lastIndex)).getField(this.configName.substring(lastIndex + 1)).getBoolean(null);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition> {

        public static final ConfigCondition.Serializer INSTANCE = new ConfigCondition.Serializer();

        @Override
        public void write(JsonObject json, ConfigCondition value) {
            json.addProperty("config", value.configName);
        }

        @Override
        public ConfigCondition read(JsonObject json) {
            return new ConfigCondition(JSONUtils.getString(json, "config"));
        }

        @Override
        public ResourceLocation getID() {
            return ConfigCondition.NAME;
        }
    }
}
