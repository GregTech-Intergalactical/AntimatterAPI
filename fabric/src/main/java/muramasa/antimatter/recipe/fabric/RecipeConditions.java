package muramasa.antimatter.recipe.fabric;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.toml.TomlParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import muramasa.antimatter.Ref;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;

public class RecipeConditions {
    private static final ResourceLocation TOML_CONFIG = new ResourceLocation(Ref.ID, "toml_config");
    private static final ResourceLocation CONFIG = new ResourceLocation(Ref.ID, "config");
    public static ConditionJsonProvider tomlConfig(String config, String configPath) {

        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return TOML_CONFIG;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonObject object1 = new JsonObject();
                object1.addProperty("config", config);
                object1.addProperty("configPath", configPath);
                object.add("toml_config", object1);
            }
        };
    }

    public static ConditionJsonProvider config(Class clazz, String variableName) {

        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return CONFIG;
            }

            @Override
            public void writeParameters(JsonObject object) {
                object.addProperty("config", clazz.getCanonicalName() + "." + variableName);
            }
        };
    }

    private static final TomlParser PARSER = new TomlParser();
    public static boolean tomlConfigMatch(JsonObject object) {
        JsonObject obj = object.getAsJsonObject("toml_config");
        String config = obj.getAsJsonPrimitive("config").getAsString();
        String configPath = obj.getAsJsonPrimitive("configPath").getAsString();
        if (config.isEmpty() || configPath.isEmpty()){
            throw new JsonParseException("config and configPath cannot be empty!");
        }
        Path configFile = Path.of(FabricLoader.getInstance().getConfigDir().toString(), config + ".toml");
        if (!Files.exists(configFile)){
            throw new JsonParseException("Config file " + configFile + " does not exist!");
        }
        CommentedConfig config1 = PARSER.parse(configFile, FileNotFoundAction.READ_NOTHING);
        if (!config1.contains(configPath)){
            throw new JsonParseException("Config path " + configPath + " not found!");
        }
        if (!(config1.get(configPath) instanceof Boolean)){
            throw new JsonParseException("Config path " + configPath + " must be a boolean!");
        }
        return config1.get(configPath);
    }

    public static boolean configMatch(JsonObject object){
        String config = object.getAsJsonPrimitive("config").getAsString();
        try {
            int lastIndex = config.lastIndexOf('.');
            return Class.forName(config.substring(0, lastIndex)).getField(config.substring(lastIndex + 1)).getBoolean(null);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException | NullPointerException e) {
            return true;
        }
    }

    public static void init(){
        ResourceConditions.register(CONFIG, RecipeConditions::configMatch);
        ResourceConditions.register(TOML_CONFIG, RecipeConditions::tomlConfigMatch);
    }
}
