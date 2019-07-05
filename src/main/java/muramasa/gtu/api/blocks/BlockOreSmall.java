package muramasa.gtu.api.blocks;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

import static muramasa.gtu.api.properties.GTProperties.ORE_STONE;

public class BlockOreSmall extends BlockOre {

    private static XSTR DROP_RAND = new XSTR();
    private static List<MaterialType> DROPS_POOL = new ArrayList<>();
    private static List<MaterialType> DROPS_POOL_GEM = new ArrayList<>();
    private static List<MaterialType> DROPS_POOL_GEM_VARIANTS = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            DROPS_POOL.add(MaterialType.CRUSHED);
            DROPS_POOL.add(MaterialType.DUST_IMPURE);
            DROPS_POOL_GEM_VARIANTS.add(MaterialType.CRUSHED);
            DROPS_POOL_GEM_VARIANTS.add(MaterialType.DUST_IMPURE);
        }
        for (int i = 0; i < 2; i++) DROPS_POOL_GEM_VARIANTS.add(MaterialType.GEM_POLISHED);
        for (int i = 0; i < 8; i++) {
            DROPS_POOL_GEM.add(MaterialType.GEM);
            DROPS_POOL_GEM_VARIANTS.add(MaterialType.GEM);
        }
        for (int i = 0; i < 14; i++) DROPS_POOL_GEM_VARIANTS.add(MaterialType.GEM_BRITTLE);
    }

    public BlockOreSmall(Material material) {
        super(material);
    }

    @Override
    protected void register() {
        GregTechAPI.register(BlockOreSmall.class, this);
    }

    @Override
    public String getId() {
        return "small_".concat(super.getId());
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        return MaterialType.ORE_SMALL.getDisplayName(material);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        DROP_RAND.setSeed(pos.getX() ^ pos.getY() ^ pos.getZ());
        int i = 0;
        List<MaterialType> pool = material.has(MaterialType.GEM) ? material.has(MaterialType.GEM_BRITTLE) ? DROPS_POOL_GEM_VARIANTS : DROPS_POOL_GEM : DROPS_POOL;
        for (int j = Math.max(1, material.getOreMulti() + (fortune > 0 ? DROP_RAND.nextInt(1 + fortune * material.getOreMulti()) : 0) / 2); i < j; i++) {
            drops.add(MaterialItem.get(pool.get(DROP_RAND.nextInt(pool.size())), material, 1));
        }
        if (DROP_RAND.nextInt(3 + fortune) > 1) {
            Material stoneMat = StoneType.getAll().get(state.getValue(ORE_STONE)).getMaterial();
            MaterialType type = DROP_RAND.nextInt(3) > 0 ? MaterialType.DUST_IMPURE : MaterialType.DUST;
            if (material.has(type)) drops.add(MaterialItem.get(type, stoneMat, 1));
        }
    }
}
