package muramasa.gtu.api.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.models.ModelDynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.event.ModelBakeEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class BlockDynamic extends BlockBaked {

    private Int2ObjectOpenHashMap<Supplier<IBakedModel>> LOOKUP = new Int2ObjectOpenHashMap<>();
    private Int2ObjectOpenHashMap<IBakedModel> BAKED_LOOKUP = new Int2ObjectOpenHashMap<>();
    private Set<Texture> TEXTURES = new HashSet<>();

    private boolean defaultModel;

    public BlockDynamic(Block.Properties properties, TextureData data) {
        super(properties, data);
    }

    public int[] getConfig(BlockState state, IBlockReader world, BlockPos.MutableBlockPos mut, BlockPos pos) {
        int[] ct = new int[1];
        for (int s = 0; s < 6; s++) {
            if (canConnect(world, mut.setPos(pos.offset(Ref.DIRECTIONS[s])))) ct[0] += 1 << s;
        }
        return ct;
    }

    public boolean canConnect(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this;
    }

    public void setDefaultModel(boolean value) {
        defaultModel = value;
    }

    public boolean addDefaultModel() {
        return defaultModel;
    }

    public void onConfig() {
        //NOOP
    }

    public void add(int config, Texture... textures) {
        add(config, new TextureData().base(textures));
    }

    public void add(int config, TextureData data) {
        if (data.hasBase()) TEXTURES.addAll(Arrays.asList(data.getBase()));
        if (data.hasOverlay()) TEXTURES.addAll(Arrays.asList(data.getOverlay()));
        add(config, data::bakeAsBlock);
    }

    public void add(int config, Supplier supplier) {
        LOOKUP.put(config, supplier);
    }

    public Int2ObjectOpenHashMap<IBakedModel> getLookup() {
        return BAKED_LOOKUP;
    }

    @Override
    public void onModelRegistration() {
        super.onModelRegistration();
        onConfig();
        if (!customModel && LOOKUP.size() > 0) registerCustomModel(getId(), new ModelDynamic(this), false);
    }

    @Override
    public void getTextures(Set<ResourceLocation> textures) {
        super.getTextures(textures);
        textures.addAll(TEXTURES);
    }

    @Override
    public void onModelBake(ModelBakeEvent e, Map<ResourceLocation, IBakedModel> registry) {
        super.onModelBake(e, registry);
        LOOKUP.forEach((k, v) -> BAKED_LOOKUP.put((int) k, v.get()));
    }

    public void buildBasicConfig(Texture[] textures) {
        if (textures.length < 13) return;
        //Single (1)
        add(1, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        add(2, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        add(4, textures[1], textures[1], textures[0], textures[12], textures[0], textures[0]);
        add(8, textures[1], textures[1], textures[12], textures[0], textures[0], textures[0]);
        add(16, textures[0], textures[0], textures[0], textures[0], textures[0], textures[12]);
        add(32, textures[0], textures[0], textures[0], textures[0], textures[12], textures[0]);

        //Lines (2)
        add(3, textures[12], textures[12], textures[1], textures[1], textures[1], textures[1]);
        add(12, textures[1], textures[1], textures[12], textures[12], textures[0], textures[0]);
        add(48, textures[0], textures[0], textures[0], textures[0], textures[12], textures[12]);

        //Elbows (2)
        add(6, textures[1], textures[12], textures[0], textures[1], textures[10], textures[11]);
        add(5, textures[12], textures[1], textures[12], textures[1], textures[9], textures[8]);
        add(9, textures[12], textures[1], textures[1], textures[12], textures[8], textures[9]);
        add(10, textures[1], textures[12], textures[1], textures[12], textures[11], textures[10]);
        add(17, textures[12], textures[0], textures[8], textures[9], textures[12], textures[1]);
        add(18, textures[0], textures[12], textures[11], textures[10], textures[12], textures[1]);
        add(33, textures[12], textures[0], textures[9], textures[8], textures[1], textures[12]);
        add(34, textures[0], textures[12], textures[10], textures[11], textures[1], textures[10]);
        add(20, textures[10], textures[10], textures[0], textures[0], textures[0], textures[0]);
        add(24, textures[9], textures[9], textures[0], textures[0], textures[0], textures[0]);
        add(36, textures[11], textures[11], textures[0], textures[0], textures[0], textures[0]);
        add(40, textures[8], textures[8], textures[0], textures[0], textures[0], textures[0]);

        //Side (3)
        add(7, textures[12], textures[12], textures[12], textures[1], textures[4], textures[2]);
        add(11, textures[12], textures[12], textures[1], textures[12], textures[2], textures[4]);
        add(13, textures[12], textures[1], textures[12], textures[12], textures[3], textures[3]);
        add(14, textures[1], textures[12], textures[12], textures[12], textures[5], textures[5]);
        add(19, textures[12], textures[12], textures[2], textures[4], textures[12], textures[1]);
        add(28, textures[4], textures[4], textures[12], textures[12], textures[12], textures[0]);
        add(35, textures[12], textures[12], textures[4], textures[2], textures[1], textures[12]);
        add(44, textures[2], textures[2], textures[12], textures[12], textures[0], textures[12]);
        add(49, textures[12], textures[0], textures[3], textures[3], textures[12], textures[12]);
        add(50, textures[0], textures[12], textures[5], textures[5], textures[12], textures[12]);
        add(52, textures[3], textures[5], textures[12], textures[0], textures[12], textures[12]);
        add(56, textures[5], textures[3], textures[0], textures[12], textures[12], textures[12]);

        //Corner (3)
        add(21, textures[10], textures[10], textures[0], textures[9], textures[0], textures[8]);
        add(22, textures[10], textures[10], textures[0], textures[10], textures[0], textures[11]);
        add(25, textures[9], textures[9], textures[8], textures[0], textures[0], textures[9]);
        add(26, textures[9], textures[9], textures[11], textures[0], textures[0], textures[10]);
        add(37, textures[11], textures[11], textures[0], textures[8], textures[9], textures[0]);
        add(38, textures[11], textures[11], textures[0], textures[11], textures[10], textures[0]);
        add(41, textures[8], textures[8], textures[9], textures[0], textures[8], textures[0]);
        add(42, textures[8], textures[8], textures[10], textures[0], textures[11], textures[0]);

        //Arrow (4)
        add(23, textures[12], textures[12], textures[12], textures[4], textures[12], textures[2]);
        add(27, textures[12], textures[12], textures[2], textures[12], textures[12], textures[4]);
        add(29, textures[12], textures[4], textures[12], textures[12], textures[12], textures[3]);
        add(30, textures[4], textures[12], textures[12], textures[12], textures[12], textures[5]);
        add(39, textures[12], textures[12], textures[12], textures[2], textures[4], textures[12]);
        add(43, textures[12], textures[12], textures[4], textures[12], textures[2], textures[12]);
        add(45, textures[12], textures[2], textures[12], textures[12], textures[3], textures[12]);
        add(46, textures[2], textures[12], textures[12], textures[12], textures[5], textures[12]);
        add(53, textures[12], textures[5], textures[12], textures[3], textures[12], textures[12]);
        add(54, textures[3], textures[12], textures[12], textures[5], textures[12], textures[12]);
        add(57, textures[12], textures[3], textures[3], textures[12], textures[12], textures[12]);
        add(58, textures[5], textures[12], textures[5], textures[12], textures[12], textures[12]);

        //Cross (4)
        add(15, textures[12], textures[12], textures[12], textures[12], textures[6], textures[6]);
        add(51, textures[12], textures[12], textures[6], textures[6], textures[12], textures[12]);
        add(60, textures[6], textures[6], textures[12], textures[12], textures[12], textures[12]);

        //Five (5)
        add(31, textures[12], textures[12], textures[12], textures[12], textures[12], textures[6]);
        add(47, textures[12], textures[12], textures[12], textures[12], textures[6], textures[12]);
        add(55, textures[12], textures[12], textures[12], textures[6], textures[12], textures[12]);
        add(59, textures[12], textures[12], textures[6], textures[12], textures[12], textures[12]);
        add(61, textures[12], textures[6], textures[12], textures[12], textures[12], textures[12]);
        add(62, textures[6], textures[12], textures[12], textures[12], textures[12], textures[12]);

        //All (6)
        add(63, textures[12], textures[12], textures[12], textures[12], textures[12], textures[12]);
    }
}
