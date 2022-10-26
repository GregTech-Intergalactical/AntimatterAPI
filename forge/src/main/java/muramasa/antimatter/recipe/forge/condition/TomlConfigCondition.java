package muramasa.antimatter.recipe.forge.condition;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.toml.TomlParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import muramasa.antimatter.Ref;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;

public class TomlConfigCondition implements ICondition {
    private static final TomlParser PARSER = new TomlParser();
    private static final ResourceLocation NAME = new ResourceLocation(Ref.ID, "toml_config");

    private final String config, configPath;

    public TomlConfigCondition(String config, String configPath) {
        this.config = config;
        this.configPath = configPath;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        if (config.isEmpty() || configPath.isEmpty()){
            throw new JsonParseException("config and configPath cannot be empty!");
        }
        Path configFile = Path.of(FMLPaths.CONFIGDIR.get().toString(), config + ".toml");
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

    public static class Serializer implements IConditionSerializer<TomlConfigCondition> {

        public static final TomlConfigCondition.Serializer INSTANCE = new TomlConfigCondition.Serializer();

        @Override
        public void write(JsonObject json, TomlConfigCondition value) {
            JsonObject object = new JsonObject();
            object.addProperty("config", value.config);
            object.addProperty("configPath", value.configPath);
            json.add("toml_config", object);
        }

        @Override
        public TomlConfigCondition read(JsonObject json) {
            JsonObject object = json.getAsJsonObject("toml_config");
            return new TomlConfigCondition(GsonHelper.getAsString(object, "config"), GsonHelper.getAsString(object, "configPath"));
        }

        @Override
        public ResourceLocation getID() {
            return TomlConfigCondition.NAME;
        }
    }
}
