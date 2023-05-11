package muramasa.antimatter.machine;

import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.VariantBlockStateBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import static net.minecraft.core.Direction.*;
import static net.minecraft.core.Direction.WEST;

public class BlockMultiMachine extends BlockMachine {
    public static final EnumProperty<MachineState> MACHINE_STATE = EnumProperty.create("machine_state", MachineState.class, MachineState.INVALID_STRUCTURE, MachineState.IDLE, MachineState.ACTIVE);

    public BlockMultiMachine(Machine<?> type, Tier tier) {
        super(type, tier);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (type == null) return; // means this is the first run
        if (type.allowVerticalFacing()) {
            builder.add(BlockStateProperties.FACING).add(HORIZONTAL_FACING).add(MACHINE_STATE);
        } else {
            builder.add(BlockStateProperties.HORIZONTAL_FACING).add(MACHINE_STATE);
        }
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        AntimatterBlockModelBuilder builderActive = prov.models().getBuilder(this.getId() + "_active");
        AntimatterBlockModelBuilder builderInvalid = prov.models().getBuilder(this.getId() + "_invalid_structure");
        buildModelsForState(builder, MachineState.IDLE);
        buildModelsForState(builderActive, MachineState.ACTIVE);
        buildModelsForState(builderActive, MachineState.INVALID_STRUCTURE);
        //builder.loader(AntimatterModelManager.LOADER_MACHINE);
        //prov.state(block, builder);
        prov.getVariantBuilder(block).forAllStates(s -> {
            var builderFinal = s.getValue(MACHINE_STATE) == MachineState.ACTIVE ? builderActive : s.getValue(MACHINE_STATE) == MachineState.INVALID_STRUCTURE ? builderInvalid : builder;
            Direction hf;
            Integer x = null;
            if (s.hasProperty(HORIZONTAL_FACING)){
                hf = s.getValue(HORIZONTAL_FACING);
                Direction vf = s.getValue(BlockStateProperties.FACING);
                if (vf.getAxis() == Direction.Axis.Y){
                    switch (hf){
                        case NORTH -> {
                            x = vf == UP ? 270 : 90;
                        }
                        case SOUTH -> {
                            x = vf == UP ? 90 : 270;
                        }
                        case WEST -> {
                            x = vf == UP ? 180 : null;
                        }
                        case EAST -> {
                            x = vf == UP ? null : 180;
                        }
                    }
                }
            } else {
                hf = s.getValue(BlockStateProperties.HORIZONTAL_FACING);
            }
            Integer y = hf == NORTH ? null : hf == SOUTH ? 180 : hf == WEST ? 270 : 90;
            VariantBlockStateBuilder.VariantBuilder vb = new VariantBlockStateBuilder.VariantBuilder();
            vb.modelFile(builderFinal);
            if (y != null){
                vb.rotationY(y);
            }
            if (x != null){
                vb.rotationX(x);
            }
            return vb;
        });
        /*AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        buildModelsForState(builder, MachineState.IDLE);
        buildModelsForState(builder, MachineState.ACTIVE);
        buildModelsForState(builder, MachineState.INVALID_STRUCTURE);
        builder.loader(AntimatterModelManager.LOADER_MACHINE);
        builder.property("particle", getType().getBaseTexture(tier)[0].toString());
        prov.state(block, builder);*/
    }
}
