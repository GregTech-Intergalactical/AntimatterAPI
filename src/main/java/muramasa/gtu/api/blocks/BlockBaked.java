package muramasa.gtu.api.blocks;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelProvider;
import muramasa.gtu.api.registration.ITextureProvider;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.client.model.IModel;

//TODO support blockstate baking?
public abstract class BlockBaked extends Block implements IGregTechObject, ITextureProvider, IModelProvider {

    protected TextureData data;
    protected IBakedModel baked;

    private boolean bakeItem = true, bakeBlock = true;
    protected boolean customModel;

    public BlockBaked(Block.Properties properties, TextureData data) {
        super(properties);
        this.data = data;
    }

    protected void register(Class c, IGregTechObject o) {
        GregTechAPI.register(c, o);
        GregTechAPI.register(BlockBaked.class, this);
    }

    public TextureData getData() {
        return data;
    }

    public IBakedModel getBaked() {
        return baked;
    }

    public void registerCustomModel(String id, IModel model, boolean hasItemOverride) {
        //TODO GTModelLoader.register(id, model);
        bakeItem = !hasItemOverride;
        bakeBlock = false;
        customModel = true;
    }

    @Override
    public Texture[] getTextures() {
        return data.hasOverlay() ? new Texture[]{data.getBase(0), data.getOverlay(0)} : new Texture[]{data.getBase(0)};
    }

    //    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void getTextures(Set<ResourceLocation> textures) {
//        textures.add(data.getBase(0));
//        if (data.hasOverlay()) textures.addAll(Arrays.asList(data.getOverlay()));
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void onModelRegistration() {
//        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + getId(), "inventory"));
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void onModelBuild(ModelBakeEvent e, Map<ResourceLocation, IBakedModel> registry) {
//        baked = data.bakeAsBlock();
//        if (bakeItem) registry.put(new ModelResourceLocation(Ref.MODID + ":" + getId(), "inventory"), baked);
//        if (bakeBlock) registry.put(new ModelResourceLocation(Ref.MODID + ":" + getId(), ""), baked);
//    }
}
