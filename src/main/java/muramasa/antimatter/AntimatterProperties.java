package muramasa.antimatter;

import muramasa.antimatter.client.ModelConfig;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.pipe.PipeSize;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;

public class AntimatterProperties {

    /** Block Dynamic Properties **/
    public static final ModelProperty<ModelConfig> DYNAMIC_CONFIG = new ModelProperty<>();

    /** Block Machine Properties **/
    public static final ModelProperty<Machine> MACHINE_TYPE = new ModelProperty<>();
    public static final ModelProperty<Direction> MACHINE_FACING = new ModelProperty<>();
    public static final ModelProperty<Cover[]> MACHINE_COVER = new ModelProperty<>();

    /** Block Pipe Properties **/
    //public static PropertyBool PIPE_INSULATED = PropertyBool.create("insulated");
    //public static PropertyBool PIPE_RESTRICTIVE = PropertyBool.create("restrictive");
    public static final ModelProperty<PipeSize> PIPE_SIZE = new ModelProperty<>();
    public static final ModelProperty<Byte> PIPE_CONNECTIONS = new ModelProperty<>();
}
