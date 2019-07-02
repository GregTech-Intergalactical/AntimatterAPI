package muramasa.gtu.api.worldgen;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockOreSmall;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldGenOreSmall extends WorldGenBase {

    private String primary;
    @Expose private int minY, maxY, amount;

    private BlockOreSmall block;
    private IBlockState oreSmall;

    public WorldGenOreSmall(String id, int minY, int maxY, int amount, Material primary, int... dimensions) {
        super(id, dimensions);
        this.minY = minY;
        this.maxY = maxY;
        this.amount = amount;
        this.primary = primary.getId();
    }

    @Override
    public WorldGenBase onDataOverride(LinkedTreeMap dataMap) {
        super.onDataOverride(dataMap);
        if (dataMap.containsKey("minY")) minY = Utils.parseInt(dataMap.get("minY"), minY);
        if (dataMap.containsKey("maxY")) maxY = Utils.parseInt(dataMap.get("maxY"), maxY);
        if (dataMap.containsKey("amount")) amount = Utils.parseInt(dataMap.get("amount"), amount);
        return this;
    }

    @Override
    public WorldGenBase build() {
        super.build();
        this.block = GregTechAPI.get(BlockOreSmall.class, "small_" + primary);
        if (block == null) throw new IllegalArgumentException("WorldGenOreSmall - " + getId() + ": " + primary + " does not have the ORE_SMALL tag");
        this.oreSmall = block.getDefaultState();
        return this;
    }

    @Override
    public boolean generate(World world, XSTR rand, int passedX, int passedZ, BlockPos.MutableBlockPos pos, IBlockState state, IChunkGenerator generator, IChunkProvider provider) {
        //int count=0;
        int j = Math.max(1, amount / 2 + rand.nextInt(amount) / 2);
        for (int i = 0; i < j; i++) {
            pos.setPos(passedX + 8 + rand.nextInt(16), minY + rand.nextInt(Math.max(1, maxY - minY)), passedZ + 8 + rand.nextInt(16));
            WorldGenHelper.setStateOre(world, pos, oreSmall);
            //count++;
        }
        //if (Ref.debugSmallOres) GregTech.LOGGER.info("Small Ore:" + id + " @ dim="+world.provider.getDimension()+ " mX="+chunkX/16+ " mZ="+chunkZ/16+ " oreSmall="+count);
        return true;
    }
}