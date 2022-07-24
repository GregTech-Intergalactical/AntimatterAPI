package muramasa.antimatter.datagen.json;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.json.models.JModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JMachineModel extends JModel {
    String loader;
    List<JModel> idle;
    List<JModel> active;
    List<JModel> disabled;
    List<JModel> invalid_structure;
    List<JModel> invalid_tier;
    List<JModel> output_full;
    List<JModel> no_power;
    List<JModel> power_loss;

    public static JMachineModel model() {
        return new JMachineModel();
    }

    public static JMachineModel model(String parent) {
        JMachineModel model = new JMachineModel();
        model.parent(parent);
        return model;
    }

    public JMachineModel loader(String loader){
        this.loader = loader;
        return this;
    }

    public JMachineModel idle(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        idle = Arrays.asList(models);
        return this;
    }

    public JMachineModel active(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        active = Arrays.asList(models);
        return this;
    }

    public JMachineModel disabled(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        disabled = Arrays.asList(models);
        return this;
    }

    public JMachineModel invaledStructure(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        invalid_structure = Arrays.asList(models);
        return this;
    }

    public JMachineModel invalidTier(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        invalid_tier = Arrays.asList(models);
        return this;
    }

    public JMachineModel outputFull(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        output_full = Arrays.asList(models);
        return this;
    }

    public JMachineModel noPower(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        no_power = Arrays.asList(models);
        return this;
    }

    public JMachineModel powerLoss(JModel... models){
        if (models.length != 6){
            throw new IllegalStateException("Models must have a length of 6!");
        }
        power_loss = Arrays.asList(models);
        return this;
    }
}
