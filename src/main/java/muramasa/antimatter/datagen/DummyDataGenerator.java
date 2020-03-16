package muramasa.antimatter.datagen;

import net.minecraft.data.DataGenerator;

import java.io.File;
import java.util.Collections;

public class DummyDataGenerator extends DataGenerator {

    public DummyDataGenerator() {
        super(new File("").toPath(), Collections.emptySet());
    }
}
