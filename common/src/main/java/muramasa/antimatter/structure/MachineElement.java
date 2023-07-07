package muramasa.antimatter.structure;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public record MachineElement<T extends TileEntityBasicMultiMachine<T>>(Machine<?> machine) implements IStructureElement<T> {

    @Override
    public boolean check(T basicMultiMachine, Level world, int x, int y, int z) {
        BlockState compare = world.getBlockState(new BlockPos(x, y, z));
        for (Tier tier : machine.getTiers()) {
            if (compare.is(machine.getBlockState(tier))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean spawnHint(T basicMultiMachine, Level world, int x, int y, int z, ItemStack trigger) {
        return false;
    }

    @Override
    public boolean placeBlock(T basicMultiMachine, Level world, int x, int y, int z, ItemStack trigger) {
        return false;
    }

}
