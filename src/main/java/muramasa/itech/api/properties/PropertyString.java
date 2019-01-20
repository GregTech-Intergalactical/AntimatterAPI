package muramasa.itech.api.properties;

import com.google.common.base.Optional;
import net.minecraft.block.properties.PropertyHelper;
import scala.actors.threadpool.Arrays;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PropertyString extends PropertyHelper<String> {

    private String name;
    private List<String> values;

    public PropertyString(String name, String... values) {
        super(name, String.class);
        this.name = name;
        this.values = new LinkedList<>(Arrays.asList(values));
    }

    @Override
    public Collection<String> getAllowedValues() {
        return values;
    }

    @Override
    public Optional<String> parseValue(String value) {
        return values.contains(value) ? Optional.of(value) : Optional.absent();
    }

    @Override
    public String getName(String value) {
        return name;
    }
}
