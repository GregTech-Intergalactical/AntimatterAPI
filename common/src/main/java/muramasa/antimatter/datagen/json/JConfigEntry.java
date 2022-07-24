package muramasa.antimatter.datagen.json;

import net.devtech.arrp.json.models.JModel;

import java.util.ArrayList;
import java.util.List;

public class JConfigEntry implements Cloneable {
    int id;
    List<JModel> models = new ArrayList<>();

    public static JConfigEntry configEntry(){
        return new JConfigEntry();
    }

    public static JConfigEntry configEntry(int id, JModel... models){
        JConfigEntry entry = new JConfigEntry();
        entry.setID(id);
        entry.addModels(models);
        return entry;
    }

    public JConfigEntry setID(int id){
        this.id = id;
        return this;
    }

    public JConfigEntry addModels(JModel... models){
        if (this.models == null) {
            this.models = new ArrayList<>();
        }
        this.models.addAll(List.of(models));
        return this;
    }

    @Override
    public JConfigEntry clone() {
        try {
            return (JConfigEntry) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
