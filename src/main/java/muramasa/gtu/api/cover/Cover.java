package muramasa.gtu.api.cover;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Cover {

    public abstract String getId();

    public final void onRegister() {
        //NOOP
    }

    public ItemStack getDroppedStack() {
        return ItemStack.EMPTY;
    }

    public final Cover onNewInstance(ItemStack stack) {
        return onPlace(stack);
    }

    public Cover onPlace(ItemStack stack) {
        return this;
    }

    /** Fires once per Side **/
    public boolean onInteract(TileEntity tile, EntityPlayer player, EnumHand hand, EnumFacing side, @Nullable ToolType type) {
        return true;
    }

    /**
     * @param tile containing the cover
     * @param side global side of the cover
     */
    public void onUpdate(TileEntity tile, EnumFacing side) {
        //NOOP
    }

    public void onMachineEvent(TileEntityMachine tile, MachineEvent event) {
        //NOOP
    }

    public List<BakedQuad> onRender(IBakedModel baked, List<BakedQuad> quads, int side) {
        return quads;
    }

    public boolean isEqual(Cover cover) {
        return getId().equals(cover.getId());
    }

    public boolean isEmpty() {
        return getId().equals(GregTechAPI.CoverNone.getId());
    }

    public Texture[] getTextures() {
        return new Texture[] {
            new Texture("blocks/machine/cover/" + getId())
        };
    }

    public ModelResourceLocation getModel() {
        return new ModelResourceLocation(Ref.MODID + ":machine/cover/" + getId());
    }

    public static ModelResourceLocation getBasicModel() {
        return new ModelResourceLocation(Ref.MODID + ":machine/cover/basic");
    }
}
