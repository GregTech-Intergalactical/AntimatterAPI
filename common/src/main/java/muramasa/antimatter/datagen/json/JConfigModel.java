package muramasa.antimatter.datagen.json;

import net.devtech.arrp.json.models.JModel;

import java.util.ArrayList;
import java.util.List;

public class JConfigModel extends JModel {
    String loader;
    List<JConfigEntry> config = new ArrayList<>();

    public static JConfigModel model() {
        return new JConfigModel();
    }

    public static JConfigModel model(String parent) {
        JConfigModel model = new JConfigModel();
        model.parent(parent);
        return model;
    }

    public JConfigModel loader(String loader){
        this.loader = loader;
        return this;
    }

    public JConfigModel configEntry(JConfigEntry... entry){
        if (this.config == null){
            this.config = new ArrayList<>();
        }
        config.addAll(List.of(entry));
        return this;
    }
}
