package muramasa.antimatter.machine;

import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockMultiMachine extends BlockMachine {
    public static final EnumProperty<MachineState> ACTIVE_IDLE_INVALID = EnumProperty.create("machine_state", MachineState.class, MachineState.ACTIVE, MachineState.IDLE, MachineState.INVALID_STRUCTURE);

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
}
