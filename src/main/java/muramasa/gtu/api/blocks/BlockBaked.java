package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO support blockstate baking?
public abstract class BlockBaked extends Block implements IGregTechObject, IModelOverride {

    protected TextureData data;

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

    @Override
    @SideOnly(Side.CLIENT)
    //TODO this should probably just be a Texture collection
    public void onTextureStitch(TextureMap map) {
        map.registerSprite(data.getBase()[0]);
        if (data.getOverlay() != null) {
            for (int i = 0; i < data.getOverlay().length; i++) {
                map.registerSprite(data.getOverlay()[i]);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + getId(), "normal"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {
        ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId(), "normal");
        registry.putObject(loc, ModelUtils.getBakedTextureData(getData()));
    }
}
