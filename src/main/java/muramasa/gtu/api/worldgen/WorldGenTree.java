package muramasa.gtu.api.worldgen;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import muramasa.gtu.api.tree.BlockSaplingBase;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldGenTree extends WorldGenBase {

    protected BlockSaplingBase blockSapling;
    @Expose protected int chance;
    protected int treeWidth;

    public WorldGenTree(String id, int chance, int treeWidth, BlockSaplingBase blockSapling, int... dimensions) {
        super(id, dimensions);
        this.blockSapling = blockSapling;
        this.chance = chance;
        this.treeWidth = treeWidth;
    }

    @Override
    public WorldGenBase onDataOverride(LinkedTreeMap dataMap) {
        super.onDataOverride(dataMap);
        if (dataMap.containsKey("chance")) chance = Utils.parseInt(dataMap.get("chance"), chance);
        return this;
    }

    @Override
    public WorldGenBase build() {
        super.build();
        if (blockSapling == null) throw new IllegalArgumentException("WorldGenTree - " + getId() + ": blockSapling cannot be null");
        if (chance < 0) throw new IllegalArgumentException("WorldGenTree - " + getId() + ": chance cannot be less than 0. Set to 0 for a 100% chance to spawn in a chunk");
        return this;
    }

    @Override
    public boolean generate(World world, XSTR rand, int passedX, int passedZ, BlockPos.MutableBlockPos pos, IBlockState state, IChunkGenerator generator, IChunkProvider provider) {
        if (chance == 0 || rand.nextInt(chance) == 0) {
            pos.setPos((passedX + (treeWidth / 2)) + rand.nextInt(16 - (treeWidth / 2)), 0, (passedZ + (treeWidth / 2)) + rand.nextInt(16 - (treeWidth / 2)));
            pos.setY(world.getHeight(pos.getX(), pos.getZ()));
            if (WorldGenHelper.canSetTree(world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()))) {
                blockSapling.generateTree(world, pos, rand);
                return true;
            }
        }
        return false;
    }
}
