package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

//The base Cover class. All cover classes extend from this.
public abstract class Cover implements IAntimatterObject {

    private GuiData gui;
    private Item item;

    public Cover() {
        gui = new GuiData(this, Data.COVER_MENU_HANDLER);
        gui.setEnablePlayerSlots(true);
    }

    public void setGui(GuiData setGui) {
        this.gui = setGui;
    }

    public GuiData getGui() {
        return gui;
    }

    public abstract String getId();

    public ItemStack getDroppedStack() {
        return item == null ? ItemStack.EMPTY : new ItemStack(getItem(), 1);
    }

    /**
     * Fires once per Side. Return defines whether or not to consume the interaction.
     */
    public boolean onInteract(CoverInstance<?> instance, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        //Do not consume behaviour per default.
        return false;
    }

    public void onPlace(CoverInstance<?> instance, Direction side) {
        //NOOP
    }

    public void onRemove(CoverInstance<?> instance, Direction side) {
        //NOOP
    }

    public void onUpdate(CoverInstance<?> instance, Direction side) {
        //NOOP
    }

    public void onMachineEvent(CoverInstance<?> instance, TileEntity tile, IMachineEvent event, Object... data) {
        //NOOP
    }

    public void onGuiEvent(CoverInstance<?> instance, TileEntity tile, IGuiEvent event, int... data) {
        //NOOP
    }

    public boolean openGui(CoverInstance<?> instance, PlayerEntity player, Direction side) {
        NetworkHooks.openGui((ServerPlayerEntity) player, instance, packetBuffer -> {
            packetBuffer.writeBlockPos(instance.getTile().getPos());
            packetBuffer.writeInt(side.getIndex());
        });
        player.playSound(Ref.WRENCH, SoundCategory.BLOCKS, 1.0f, 2.0f);
        return true;
    }

    public AntimatterContainer getContainer() {
        return null;
    }

    public boolean hasGui() {
        return getGui() != null && getGui().getMenuHandler() != null;
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
        return new Texture[]{new Texture(getDomain(), "block/machine/cover/" + getId())};
    }

    public ModelResourceLocation getModel() {
        return new ModelResourceLocation(getDomain() + ":machine/cover/" + getId());
    }

    //The default cover model
    public static ModelResourceLocation getBasicModel() {
        return new ModelResourceLocation(Ref.ID + ":block/cover/basic");
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
