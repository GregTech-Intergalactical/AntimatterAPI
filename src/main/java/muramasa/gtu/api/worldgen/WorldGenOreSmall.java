package muramasa.gtu.api.worldgen;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockOreSmall;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialTag;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.ArrayList;
import java.util.List;

public class WorldGenOreSmall extends WorldGenBase {

    public static Int2ObjectArrayMap<List<WorldGenOreSmall>> ALL = new Int2ObjectArrayMap<>();

    static {
        ALL.put(0, new ArrayList<>());
        ALL.put(-1, new ArrayList<>());
        ALL.put(1, new ArrayList<>());
    }

    public String id;
    public int minY, maxY, amount;
    public BlockOreSmall block;
    public IBlockState ore;

    public WorldGenOreSmall(String id, int minY, int maxY, int amount, Material primary, MaterialTag... tags) {
        super(id, tags);
        this.id = id;
        this.minY = minY;
        this.maxY = maxY;
        this.amount = amount;
        this.block = GregTechAPI.get(BlockOreSmall.class, "small_" + primary.getId());
        if (block == null)
            throw new IllegalArgumentException(primary.getId() + " in WorldGenOreSmall: " + id + " does not have the ORE_SMALL tag");
        this.ore = block.getDefaultState();
        if (dims.contains(MaterialTag.OVERWORLD)) ALL.get(0).add(this);
        if (dims.contains(MaterialTag.NETHER)) ALL.get(-1).add(this);
        if (dims.contains(MaterialTag.END)) ALL.get(1).add(this);
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, IChunkGenerator generator, IChunkProvider provider) {
        //int count=0;
        int j = Math.max(1, amount / 2 + rand.nextInt(amount) / 2);
        for (int i = 0; i < j; i++) {
            WorldGenHelper.setStateOre(world, passedX + 8 + rand.nextInt(16), minY + rand.nextInt(Math.max(1, maxY - minY)), passedZ + 8 + rand.nextInt(16), ore);
            //count++;
        }
        //if (Ref.debugSmallOres) GregTech.LOGGER.info("Small Ore:" + id + " @ dim="+world.provider.getDimension()+ " mX="+chunkX/16+ " mZ="+chunkZ/16+ " ore="+count);
        return true;
    }
}