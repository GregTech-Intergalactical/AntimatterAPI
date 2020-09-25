package muramasa.antimatter.cover;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//The base Cover class. All cover classes extend from this.
public abstract class Cover implements IAntimatterObject,ITextureProvider {

    private GuiData gui;
    private Item item;

    public Cover() {
        gui = new GuiData(this, Data.COVER_MENU_HANDLER);
        gui.setEnablePlayerSlots(false);
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
    public boolean onInteract(CoverInstance<?> cover, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        //Do not consume behaviour per default.
        return false;
    }

    public void onPlace(CoverInstance<?> cover, Direction side) {
        //NOOP
    }

    public void onRemove(CoverInstance<?> cover, Direction side) {
        //NOOP
    }

    public void onUpdate(CoverInstance<?> cover, Direction side) {
        //NOOP
    }

    public void onMachineEvent(CoverInstance<?> cover, TileEntity tile, IMachineEvent event, Object... data) {
        //NOOP
    }

    public void onGuiEvent(CoverInstance<?> cover, TileEntity tile, IGuiEvent event, int... data) {
        //NOOP
    }

    public boolean openGui(CoverInstance<?> cover, PlayerEntity player, Direction side) {
        NetworkHooks.openGui((ServerPlayerEntity) player, cover, packetBuffer -> {
            packetBuffer.writeBlockPos(cover.getTile().getPos());
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

    public boolean hasFilter() {
        return false;
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

    public void setTextures(BiConsumer<String,Texture> texer) {
        texer.accept("overlay",new Texture(getDomain(), "block/cover/" + getRenderId()));
    }

    public Texture[] getTextures() {
        List<Texture> l = new ArrayList<>();
        setTextures((name,tex) -> l.add(tex));
        return l.toArray(new Texture[0]);
    }

    public ResourceLocation getModel() {
        return new ResourceLocation(getDomain() + ":block/cover/" + getRenderId());
    }

    //Useful for using the same model for multiple tiers where id is dependent on tier.
    protected String getRenderId() {
        return getId();
    }

    //The default cover model
    public static ResourceLocation getBasicModel() {
        return new ResourceLocation(Ref.ID + ":block/cover/basic");
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
