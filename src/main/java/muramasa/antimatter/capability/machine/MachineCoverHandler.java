package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

public class MachineCoverHandler<T extends TileEntityMachine> extends RotatableCoverHandler<T> implements IMachineHandler, IGuiHandler, ICapabilityHandler {

    protected Direction output = Direction.SOUTH;

    public MachineCoverHandler(T tile, CompoundNBT tag) {
        super(tile, tile.getValidCovers());
        Direction side = getTile().getFacing().getOpposite();
        covers.put(side, new CoverInstance<>(Data.COVER_OUTPUT, tile, side));
        if (tag != null) deserialize(tag);
    }

    public Direction getOutputFacing() {
        return Utils.rotateFacingAlt(output, getTileFacing());
    }

    public boolean setOutputFacing(Direction side) {
        if (set(side, Data.COVER_OUTPUT)) {
            if (covers.get(output).isEqual(Data.COVER_OUTPUT)) covers.put(output, new CoverInstance<>(Data.COVER_NONE, tile, side));
            output = Utils.rotateFacing(side, getTileFacing());
            return true;
        }
        return false;
    }

    @Override
    public boolean set(Direction side, @Nonnull Cover newCover) {
//        if (newCover.isEqual(Data.COVERNONE) && Utils.rotateFacing(side, getTileFacing()) == output) {
//            super.set(side, Data.COVERNONE);
//            return super.set(side, Data.COVEROUTPUT);
//        }
        return super.set(side, newCover);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nonnull AntimatterToolType type) {
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        for (CoverInstance<T> i : covers.values()) i.onMachineEvent(event, data);
    }

    @Override
    public void onGuiEvent(IGuiEvent event, int...data) {
        if (event instanceof GuiEvent) {
            switch ((GuiEvent) event) {
                case COVER_BUTTON:
                case COVER_SWITCH:
                    covers.get(Ref.DIRS[data[2]]).onGuiEvent(event, data);
                    break;
            }
        }
    }

    @Override
    public boolean isValid(@Nonnull Direction side, @Nonnull Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        if (Utils.rotateFacing(side, getTileFacing()) == output) return false;
        return (get(side).isEmpty() && !replacement.isEmpty()) || super.isValid(side, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return getTile().getFacing();
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.COVERABLE_HANDLER_CAPABILITY;
    }
}
