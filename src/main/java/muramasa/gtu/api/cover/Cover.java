package muramasa.gtu.api.cover;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.texture.Texture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Cover {

    private static int lastInternalId = 0;

    private int internalId;

    protected ItemStack catalystUsed = ItemStack.EMPTY;

    public abstract String getName();

    public int getInternalId() {
        return internalId;
    }

    public ItemStack getCatalystUsed() {
        return catalystUsed;
    }

    public final void onRegister() {
        internalId = lastInternalId++;
    }

    public final Cover getNewInstance(ItemStack stack) {
        int id = internalId;
        Cover cover = onPlace(stack);
        cover.internalId = id;
        return cover;
    }

    public Cover onPlace(ItemStack stack) {
        catalystUsed = stack;
        return this;
    }

    /** Fires once per Side **/
    public boolean onInteract(TileEntity tile, EntityPlayer player, EnumHand hand, EnumFacing side, @Nullable ToolType type) {
        return true;
    }

    public void onUpdate(TileEntity tile) {
        //NOOP
    }

    public List<BakedQuad> onRender(List<BakedQuad> quads, int side) {
        return quads;
    }

    public boolean isEqual(Cover cover) {
        return internalId == cover.getInternalId();
    }

    public boolean isEmpty() {
        return internalId == GregTechAPI.CoverNone.internalId;
    }

    public Texture[] getTextures() {
        return new Texture[] {
            new Texture("blocks/machine/cover/" + getName())
        };
    }

    public ModelResourceLocation getModel() {
        return new ModelResourceLocation(Ref.MODID + ":machine/cover/" + getName());
    }

    public static ModelResourceLocation getBasicModel() {
        return new ModelResourceLocation(Ref.MODID + ":machine/cover/basic");
    }

    public static int getLastInternalId() {
        return lastInternalId;
    }
}
