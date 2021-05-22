package muramasa.antimatter.machine;

import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tool.MaterialTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import static muramasa.antimatter.Data.HAMMER;

public class BlockMultiMachine extends BlockMachine {

    public BlockMultiMachine(Machine<?> type, Tier tier) {
        super(type, tier);
    }

    @Override
    protected ActionResultType onBlockActivatedBoth(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (player.getHeldItem(hand).getItem() instanceof MaterialTool && ((MaterialTool) player.getHeldItem(hand).getItem()).getAntimatterToolType() == HAMMER) {
            TileEntityBasicMultiMachine machine = (TileEntityBasicMultiMachine) world.getTileEntity(pos);
            if (machine != null) {
                if (!machine.isStructureValid()) {
                    return machine.checkStructure() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
                }
            }
        }
        return super.onBlockActivatedBoth(state, world, pos, player, hand, hit);
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
    protected int getModelId(Direction facing, Direction overlay, MachineState state) {
        state = (state == MachineState.INVALID_STRUCTURE ? MachineState.INVALID_STRUCTURE : ((state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE));
        return ((state.ordinal() + 1) * 10000) + ((facing.getIndex() + 1) * 1000) + (overlay.getIndex() + 1);
    }
}
