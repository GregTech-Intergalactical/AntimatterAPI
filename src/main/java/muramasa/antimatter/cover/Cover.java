package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

//The base Cover class. All cover classes extend from this.
public abstract class Cover implements INamedContainerProvider, IAntimatterObject {

    protected Tier tier;

    protected GuiData gui;

    protected TileEntity tile;

    public Cover() {

    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    public abstract String getId();

    public GuiData getGui() {
        return gui;
    }

    public TileEntity getTileOn() {
        return tile;
    }

    public ItemStack getDroppedStack() {
        return ItemStack.EMPTY;
    }

    //Called on generating a new instance of this cover. For stateful covers this
    //creates a new cover instance.
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

    public void onPlace(TileEntity tile, Direction side) {
        this.tile = tile;
    }

    public void onRemove(TileEntity tile, Direction side) {
        if (!tile.getWorld().isRemote) {
            BlockPos pos = tile.getPos();
            ItemEntity itementity = new ItemEntity(tile.getWorld(), (double) pos.getX(), (double) pos.getY() + 5, (double) pos.getZ(), getDroppedStack());
            tile.getWorld().addEntity(itementity);
        }
        }
    //Called on update of the world.
    public void onUpdate(TileEntity tile, Direction side) {

    }

    public void onMachineEvent(TileEntityMachine tile, IMachineEvent event) {
        //NOOP
    }

    public AntimatterContainer getContainer() {
        return null;
    }

    public boolean hasGui() {
        return true;
    }

    public TileEntityMachine getConnectedEntity() {
        return null;
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

    public void onRegister() {

    }
}
