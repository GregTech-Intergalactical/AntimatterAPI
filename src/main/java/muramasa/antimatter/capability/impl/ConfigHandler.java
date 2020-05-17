package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IConfigHandler;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigHandler implements IConfigHandler {

    protected TileEntity tile;

    public ConfigHandler(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
        return false;
    }

    @Nonnull
    @Override
    public TileEntity getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
