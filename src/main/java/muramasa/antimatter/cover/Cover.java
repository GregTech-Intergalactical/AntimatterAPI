package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.machine.MachineEvent;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

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
    public boolean onInteract(TileEntity tile, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return true;
    }

    /**
     * @param tile containing the cover
     * @param side global side of the cover
     */
    public void onUpdate(TileEntity tile, Direction side) {
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
        return getId().equals(Data.COVER_NONE.getId());
    }

    public Texture[] getTextures() {
        return new Texture[] {
            new Texture(Ref.ID, "block/machine/cover/" + getId())
        };
    }

    public ModelResourceLocation getModel() {
        return new ModelResourceLocation(Ref.ID + ":machine/cover/" + getId());
    }

    //The default cover model
    public static ModelResourceLocation getBasicModel() {
        return new ModelResourceLocation(Ref.ID + ":block/cover/basic");
    }
}
