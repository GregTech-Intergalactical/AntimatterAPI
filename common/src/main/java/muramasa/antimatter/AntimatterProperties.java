package muramasa.antimatter;

import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine.DynamicKey;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.function.Function;

public class AntimatterProperties {
    public static class MachineProperties {
        public final ICover[] covers;
        public final MachineState state;
        public final Function<Direction, Texture> machTexture;
        public final Machine<?> type;
        public final Tier tier;
        //Not used, just used as index for the dynamic texturer, to get the model.
        public final DynamicTexturer<Machine<?>, DynamicKey> machineTexturer;
        @Nullable
        public final Function<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;
        /**
         * @param covers
         * @param state
         * @param machTexture
         * @param machineTexturer
         * @param coverTexturer
         */
        public MachineProperties(Machine<?> type, Tier tier, ICover[] covers, MachineState state, Function<Direction, Texture> machTexture,
                DynamicTexturer<Machine<?>, DynamicKey> machineTexturer,
                Function<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer) {
            this.covers = covers;
            this.state = state;
            this.machTexture = machTexture;
            this.tier = tier;
            this.machineTexturer = machineTexturer;
            this.coverTexturer = coverTexturer;
            this.type = type;
        }
    }
}
