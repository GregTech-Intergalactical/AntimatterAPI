package muramasa.antimatter.cover;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public abstract class CoverMaterial extends BaseCover {

    public abstract MaterialType<?> getType();

    public abstract Material getMaterial();

    @Override
    public <T> boolean blocksCapability(CoverStack<?> stack, Capability<T> cap, Direction side) {
        return side != null;
    }

    @Override
    public void onRemove(CoverStack<?> instance, Direction side) {
        TileEntity tile = instance.getTile();
        if (tile instanceof TileEntityMachine) {
            ((TileEntityMachine)tile).refreshCaps();
        }
    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        TileEntity tile = instance.getTile();
        if (tile instanceof TileEntityMachine) {
            ((TileEntityMachine)tile).refreshCaps();
        }
    }
}
