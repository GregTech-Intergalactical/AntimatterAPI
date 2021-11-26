package muramasa.antimatter;

import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.TileEntityMachine.DynamicKey;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

public class AntimatterProperties {

    /**
     * Block Dynamic Properties
     **/
    public static final ModelProperty<ModelConfig> DYNAMIC_CONFIG = new ModelProperty<>();
    public static final ModelProperty<EnumMap<Direction, Byte>> COVER_REMOVAL = new ModelProperty<>();

    /**
     * Block Machine Properties
     **/
    public static final ModelProperty<Machine<?>> MACHINE_TYPE = new ModelProperty<>();

    public static final ModelProperty<TileEntityBase<?>> TILE_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<BlockState> STATE_MODEL_PROPERTY = new ModelProperty<>();

    public static final ModelProperty<Texture> TEXTURE_MODEL_PROPERTY = new ModelProperty<>();

    public static final ModelProperty<MachineProperties> MACHINE_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Function<Direction, Texture>> MULTI_TEXTURE_PROPERTY = new ModelProperty<>();


    public static class MachineProperties {
        public final ICover[] covers;
        public final MachineState state;
        public final Function<Direction, Texture> machTexture;
        public final Machine<?> type;
        //Not used, just used as index for the dynamic texturer, to get the model.
        private final TileEntityMachine<?> machine;
        public final DynamicTexturer<TileEntityMachine<?>, TileEntityMachine.DynamicKey> machineTexturer;
        @Nullable
        public final Function<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;
        /**
         * @param covers
         * @param state
         * @param machTexture
         * @param multiTextuire
         * @param machineTexturer
         * @param coverTexturer
         */
        public MachineProperties(TileEntityMachine<?> machine, Machine<?> type, ICover[] covers, MachineState state, Function<Direction, Texture> machTexture,
                DynamicTexturer<TileEntityMachine<?>, DynamicKey> machineTexturer,
                Function<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer) {
            this.covers = covers;
            this.state = state;
            this.machTexture = machTexture;
            this.machineTexturer = machineTexturer;
            this.coverTexturer = coverTexturer;
            this.type = type;
            this.machine = machine;
        }

        public TileEntityMachine getTile() {
            return machine;
        }
    }
}
