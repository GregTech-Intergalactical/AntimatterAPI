package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerInteractHandler extends MachineInteractHandler<TileEntityMachine> {

    public ControllerInteractHandler(TileEntityMachine tile, CompoundNBT tag) {
        super(tile, tag);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
        if (type == Data.HAMMER) {
            TileEntityMultiMachine machine = (TileEntityMultiMachine) getTile();
            if (!machine.isStructureValid()) {
                machine.checkStructure();
                return true;
            }
        }
        return super.onInteract(player, hand, side, type);
    }
}
