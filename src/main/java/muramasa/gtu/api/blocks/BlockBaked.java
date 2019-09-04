package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.GTModelLoader;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Set;

//TODO support blockstate baking?
public abstract class BlockBaked extends Block implements IGregTechObject, IModelOverride {

    protected TextureData data;
    protected IBakedModel baked;

    private boolean bakeItem = true, bakeBlock = true;

    public BlockBaked(net.minecraft.block.material.Material material, TextureData data) {
        super(material);
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
        GTModelLoader.register(id, model);
        bakeItem = !hasItemOverride;
        bakeBlock = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTextures(Set<ResourceLocation> textures) {
        textures.add(data.getBase(0));
        if (data.hasOverlay()) textures.addAll(Arrays.asList(data.getOverlay()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + getId(), "inventory"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {
        baked = ModelUtils.bakeTextureData(data);
        if (bakeItem) registry.putObject(new ModelResourceLocation(Ref.MODID + ":" + getId(), "inventory"), baked);
        if (bakeBlock) registry.putObject(new ModelResourceLocation(Ref.MODID + ":" + getId(), "normal"), baked);
    }
}
