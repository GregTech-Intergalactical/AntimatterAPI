package muramasa.antimatter.cover;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class CoverMuffler extends BaseCover {

    public CoverMuffler(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
        if (type.equals("pipe"))
            return PIPE_COVER_MODEL;
        return getBasicModel();
    }

    @Override
    public boolean ticks() {
        return true;
    }

    @Override
    public void onUpdate() {
        BlockPos pos = handler.getTile().getPos();
        World world = this.handler.getTile().getWorld();
        if (world.isRemote) {
            TileEntityMultiMachine<?> tile = StructureCache.getAnyMulti(world, pos, TileEntityMultiMachine.class);
            if (tile == null || tile.getMachineState() != MachineState.ACTIVE) return;
            Random rand = world.rand;
            Direction dir = this.side;
            double d0 = (double) pos.getX() + 0.5D + (dir.getAxis() == Direction.Axis.X ? 0f : (rand.nextDouble() - 0.5f) / 2);
            double d1 = (double) pos.getY() + 0.5D + (dir.getAxis() == Direction.Axis.Y ? 0f : (rand.nextDouble() - 0.5f) / 2);
            double d2 = (double) pos.getZ() + 0.5D + (dir.getAxis() == Direction.Axis.Z ? 0f : (rand.nextDouble() - 0.5f) / 2);
            if (rand.nextDouble() < 0.1D) {
                //world.playSound(d0, d1, d2, SoundEvents.FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
            world.addParticle(ParticleTypes.SMOKE, d0 + 0.6f * this.side.getXOffset(), d1 + 0.6 * this.side.getYOffset(), d2 + 0.6f * this.side.getZOffset(),
                    this.side.getXOffset(), this.side.getYOffset(), this.side.getZOffset());
        }
    }
}
