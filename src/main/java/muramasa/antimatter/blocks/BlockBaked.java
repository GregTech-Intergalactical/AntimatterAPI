package muramasa.antimatter.blocks;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.client.model.IModel;

//TODO pointless with automatic model gen?
//TODO support blockstate baking?
@Deprecated
public abstract class BlockBaked extends Block implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected TextureData data;
    protected IBakedModel baked;

    private boolean bakeItem = true, bakeBlock = true;
    protected boolean customModel;

    public BlockBaked(Block.Properties properties, TextureData data) {
        super(properties);
        this.data = data;
    }

    protected void register(Class c, IAntimatterObject o) {
        AntimatterAPI.register(c, o);
        AntimatterAPI.register(BlockBaked.class, this);
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
