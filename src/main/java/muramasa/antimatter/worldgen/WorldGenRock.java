package muramasa.antimatter.worldgen;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import muramasa.gtu.data.Materials;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.util.XSTR;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class WorldGenRock extends WorldGenBase {

    @Expose private int rockAmount;

    public WorldGenRock(String id, int rockAmount, int... dimensions) {
        super(id, dimensions);
        this.rockAmount = rockAmount;
    }

    @Override
    public WorldGenBase onDataOverride(LinkedTreeMap dataMap) {
        super.onDataOverride(dataMap);
        if (dataMap.containsKey("rockAmount")) rockAmount = Utils.parseInt(dataMap.get("rockAmount"), rockAmount);
        return this;
    }

    @Override
    public WorldGenBase build() {
        super.build();
        if (rockAmount <= 0) throw new IllegalStateException("WorldGenRock - " + getId() + ": rockAmount must be more than 0");
        return this;
    }

    @Override
    public boolean generate(World world, XSTR rand, int passedX, int passedZ, BlockPos.Mutable pos, BlockState state, ChunkGenerator generator, AbstractChunkProvider provider) {
        int j = Math.max(1, rockAmount + rand.nextInt(rockAmount));
        for (int i = 0; i < j; i++) {
            pos.setPos(passedX + 8 + rand.nextInt(16), 0, passedZ + 8 + rand.nextInt(16));
            pos.setY(world.getHeight(Heightmap.Type.WORLD_SURFACE, pos).getY());
            WorldGenHelper.setRock(world, pos, Materials.NULL);
        }
        return true;
    }
}
