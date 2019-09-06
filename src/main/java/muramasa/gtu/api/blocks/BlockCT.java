package muramasa.gtu.api.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.gtu.api.properties.UnlistedIntArray;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.models.ModelCT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Arrays;
import java.util.Set;

public abstract class BlockCT extends BlockBaked {

    public static UnlistedIntArray CT = new UnlistedIntArray();
    private Int2ObjectOpenHashMap<IBakedModel> LOOKUP = new Int2ObjectOpenHashMap<>();
    private Int2ObjectOpenHashMap<TextureData> TEXTURES = new Int2ObjectOpenHashMap<>();

    public BlockCT(Material material, TextureData data) {
        super(material, data);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CT).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        int[] ct = new int[6];
        if (LOOKUP.size() == 0) return ((IExtendedBlockState) state).withProperty(CT, ct);
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
        for (int s = 0; s < 6; s++) {
            if (canConnect(world, mut.setPos(pos.offset(EnumFacing.VALUES[s])))) ct[0] += 1 << s;
        }
        return ((IExtendedBlockState) state).withProperty(CT, ct);
    }

    public boolean canConnect(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this;
    }

    public void onConfig() {
        //NOOP
    }

    public void addConfig(int config, Texture texture) {
        TEXTURES.put(config, new TextureData().base(texture));
    }

    public void addConfig(int config, Texture... textures) {
        TEXTURES.put(config, new TextureData().base(textures));
    }

    public void addConfig(int config, IBakedModel baked) {
        LOOKUP.put(config, baked);
    }

    public Int2ObjectOpenHashMap<IBakedModel> getLookup() {
        return LOOKUP;
    }

    @Override
    public void onModelRegistration() {
        super.onModelRegistration();
        onConfig();
        if (!customModel && TEXTURES.size() > 0) {
            registerCustomModel(getId(), new ModelCT(this), false);
        }
    }

    @Override
    public void getTextures(Set<ResourceLocation> textures) {
        super.getTextures(textures);
        TEXTURES.forEach((k, v) -> textures.addAll(Arrays.asList(v.getBase())));
    }

    @Override
    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {
        super.onModelBake(registry);
        TEXTURES.forEach((k, v) -> LOOKUP.put((int) k, v.bake()));
    }

    public void buildBasicConfig(Texture[] textures) {
        if (textures.length < 13) return;
        //Single (1)
        addConfig(1, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        addConfig(2, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        addConfig(4, textures[1], textures[1], textures[0], textures[12], textures[0], textures[0]);
        addConfig(8, textures[1], textures[1], textures[12], textures[0], textures[0], textures[0]);
        addConfig(16, textures[0], textures[0], textures[0], textures[0], textures[0], textures[12]);
        addConfig(32, textures[0], textures[0], textures[0], textures[0], textures[12], textures[0]);

        //Lines (2)
        addConfig(3, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        addConfig(12, textures[1], textures[1], textures[12], textures[12], textures[0], textures[0]);
        addConfig(48, textures[0], textures[0], textures[0], textures[0], textures[12], textures[12]);

        //Elbows (2)
        addConfig(6, textures[1], textures[12], textures[0], textures[1], textures[10], textures[11]);
        addConfig(5, textures[12], textures[1], textures[12], textures[1], textures[9], textures[8]);
        addConfig(9, textures[12], textures[1], textures[1], textures[12], textures[8], textures[9]);
        addConfig(10, textures[1], textures[12], textures[1], textures[12], textures[11], textures[10]);
        addConfig(17, textures[12], textures[0], textures[8], textures[9], textures[12], textures[1]);
        addConfig(18, textures[0], textures[12], textures[11], textures[10], textures[12], textures[1]);
        addConfig(33, textures[12], textures[0], textures[9], textures[8], textures[1], textures[12]);
        addConfig(34, textures[0], textures[12], textures[10], textures[11], textures[1], textures[10]);
        addConfig(20, textures[10], textures[10], textures[0], textures[0], textures[0], textures[0]);
        addConfig(24, textures[9], textures[9], textures[0], textures[0], textures[0], textures[0]);
        addConfig(36, textures[11], textures[11], textures[0], textures[0], textures[0], textures[0]);
        addConfig(40, textures[8], textures[8], textures[0], textures[0], textures[0], textures[0]);

        //Side (3)
        addConfig(7, textures[12], textures[12], textures[12], textures[1], textures[4], textures[2]);
        addConfig(11, textures[12], textures[12], textures[1], textures[12], textures[2], textures[4]);
        addConfig(13, textures[12], textures[1], textures[12], textures[12], textures[3], textures[3]);
        addConfig(14, textures[1], textures[12], textures[12], textures[12], textures[5], textures[5]);
        addConfig(19, textures[12], textures[12], textures[2], textures[4], textures[12], textures[1]);
        addConfig(28, textures[4], textures[4], textures[12], textures[12], textures[12], textures[0]);
        addConfig(35, textures[12], textures[12], textures[4], textures[2], textures[1], textures[12]);
        addConfig(44, textures[2], textures[2], textures[12], textures[12], textures[0], textures[12]);
        addConfig(49, textures[12], textures[0], textures[3], textures[3], textures[12], textures[12]);
        addConfig(50, textures[0], textures[12], textures[5], textures[5], textures[12], textures[12]);
        addConfig(52, textures[3], textures[5], textures[12], textures[0], textures[12], textures[12]);
        addConfig(56, textures[5], textures[3], textures[0], textures[12], textures[12], textures[12]);

        //Corner (3)
        addConfig(21, textures[10], textures[10], textures[0], textures[9], textures[0], textures[8]);
        addConfig(22, textures[10], textures[10], textures[0], textures[10], textures[0], textures[11]);
        addConfig(25, textures[9], textures[9], textures[8], textures[0], textures[0], textures[9]);
        addConfig(26, textures[9], textures[9], textures[11], textures[0], textures[0], textures[10]);
        addConfig(37, textures[11], textures[11], textures[0], textures[8], textures[9], textures[0]);
        addConfig(38, textures[11], textures[11], textures[0], textures[11], textures[10], textures[0]);
        addConfig(41, textures[8], textures[8], textures[9], textures[0], textures[8], textures[0]);
        addConfig(42, textures[8], textures[8], textures[10], textures[0], textures[11], textures[0]);

        //Arrow (4)
        addConfig(23, textures[12], textures[12], textures[12], textures[4], textures[12], textures[2]);
        addConfig(27, textures[12], textures[12], textures[2], textures[12], textures[12], textures[4]);
        addConfig(29, textures[12], textures[4], textures[12], textures[12], textures[12], textures[3]);
        addConfig(30, textures[4], textures[12], textures[12], textures[12], textures[12], textures[5]);
        addConfig(39, textures[12], textures[12], textures[12], textures[2], textures[4], textures[12]);
        addConfig(43, textures[12], textures[12], textures[4], textures[12], textures[2], textures[12]);
        addConfig(45, textures[12], textures[2], textures[12], textures[12], textures[3], textures[12]);
        addConfig(46, textures[2], textures[12], textures[12], textures[12], textures[5], textures[12]);
        addConfig(53, textures[12], textures[5], textures[12], textures[3], textures[12], textures[12]);
        addConfig(54, textures[3], textures[12], textures[12], textures[5], textures[12], textures[12]);
        addConfig(57, textures[12], textures[3], textures[3], textures[12], textures[12], textures[12]);
        addConfig(58, textures[5], textures[12], textures[5], textures[12], textures[12], textures[12]);

        //Cross (4)
        addConfig(15, textures[12], textures[12], textures[12], textures[12], textures[6], textures[6]);
        addConfig(51, textures[12], textures[12], textures[6], textures[6], textures[12], textures[12]);
        addConfig(60, textures[6], textures[6], textures[12], textures[12], textures[12], textures[12]);

        //Five (5)
        addConfig(31, textures[12], textures[12], textures[12], textures[12], textures[12], textures[6]);
        addConfig(47, textures[12], textures[12], textures[12], textures[12], textures[6], textures[12]);
        addConfig(55, textures[12], textures[12], textures[12], textures[6], textures[12], textures[12]);
        addConfig(59, textures[12], textures[12], textures[6], textures[12], textures[12], textures[12]);
        addConfig(61, textures[12], textures[6], textures[12], textures[12], textures[12], textures[12]);
        addConfig(62, textures[6], textures[12], textures[12], textures[12], textures[12], textures[12]);

        //All (6)
        addConfig(63, textures[12], textures[12], textures[12], textures[12], textures[12], textures[12]);
    }
}
