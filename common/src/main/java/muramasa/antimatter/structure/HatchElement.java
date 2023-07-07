package muramasa.antimatter.structure;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.machine.types.HatchMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record HatchElement<T extends TileEntityBasicMultiMachine<T>>(HatchMachine machine) implements IStructureElement<T> {

    @Override
    public boolean check(T basicMultiMachine, Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IComponent component) {
            if (component.getComponentHandler().isPresent()) {
                IComponentHandler componentHandler = component.getComponentHandler().orElse(null);
                if (machine.getComponentId().equals(componentHandler.getId())) {
                    basicMultiMachine.addComponent(machine.getComponentId(), componentHandler);
                    return true;
                }
                return false;
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
