package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public abstract class CoverMaterial extends BaseCover {


    public CoverMaterial(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    public abstract MaterialType<?> getType();

    public abstract Material getMaterial();

    @Override
    public <T> boolean blocksCapability(Capability<T> cap, Direction side) {
        return side != null;
    }

    @Override
    public void onRemove() {
        TileEntity tile = handler.getTile();
        if (tile instanceof TileEntityMachine) {
            ((TileEntityMachine) tile).refreshCaps();
        }
    }

    @Override
    public void onPlace() {
        TileEntity tile = handler.getTile();
        if (tile instanceof TileEntityMachine) {
            ((TileEntityMachine) tile).refreshCaps();
        }
    }
}
