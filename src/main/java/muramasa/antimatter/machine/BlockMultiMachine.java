package muramasa.antimatter.machine;

import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;

public class BlockMultiMachine extends BlockMachine {

    public BlockMultiMachine(Machine<?> type, Tier tier) {
        super(type, tier);
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        buildModelsForState(builder, MachineState.IDLE);
        buildModelsForState(builder, MachineState.ACTIVE);
        buildModelsForState(builder, MachineState.INVALID_STRUCTURE);
        builder.loader(AntimatterModelManager.LOADER_MACHINE);
        builder.property("particle", getType().getBaseTexture(tier)[0].toString());
        prov.state(block, builder);
    }

    @Override
    protected int getModelId(Direction facing, Direction horizontalFacing, Direction overlay, MachineState state) {
        state = (state == MachineState.INVALID_STRUCTURE ? MachineState.INVALID_STRUCTURE : ((state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE));
        return ((state.ordinal() + 1) * 10000) + ((facing.get3DDataValue() + 1) * 1000) + ((horizontalFacing.get3DDataValue() + 1) * 100) + (overlay.get3DDataValue() + 1);
    }

    @Override
    protected int getModelId(Direction facing, Direction overlay, MachineState state) {
        state = (state == MachineState.INVALID_STRUCTURE ? MachineState.INVALID_STRUCTURE : ((state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE));
        return ((state.ordinal() + 1) * 10000) + ((facing.get3DDataValue() + 1) * 1000) + (overlay.get3DDataValue() + 1);
    }
}
