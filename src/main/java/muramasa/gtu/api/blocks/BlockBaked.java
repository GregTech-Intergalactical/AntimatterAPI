package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO probably very future, but add system to build dynamic baked models from
//TODO a TextureData object.
//TODO Maybe some base class could have a overridable String getVariant method
//TODO for automatic basic model injection
//TODO allow to specify textures by passing TextureData
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

    public String getVariant() {
        return "normal";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + getId(), getVariant()));
    }
}
