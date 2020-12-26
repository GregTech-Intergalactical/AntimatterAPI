package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

//The base Cover class. All cover classes extend from this.
public abstract class Cover implements IAntimatterObject, ITextureProvider, IDynamicModelProvider {



    protected GuiData gui;
    @Nullable
    private Item item;

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getModel();
    }

    public Cover() {
        this.gui = new GuiData(this, Data.COVER_MENU_HANDLER);
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
     **/
    public boolean onInteract(CoverStack<?> instance, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        //Do not consume behaviour per default.
        return false;
    }

    public void onPlace(CoverStack<?> instance, Direction side) {

    }

    public void onRemove(CoverStack<?> instance, Direction side) {

    }

    //Called on update of the world.
    public void onUpdate(CoverStack<?> instance, Direction side) {

    }

    public void onMachineEvent(CoverStack<?> instance, TileEntityMachine tile, IMachineEvent event) {
        //NOOP
    }

    public boolean openGui(CoverStack<?> instance, PlayerEntity player, Direction side) {
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
        return getId().equals(Data.COVERNONE.getId());
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

    // TODO: refactor this if/when covers will be singletons
    public void onRegister() {
        String id = getId();
        if (AntimatterAPI.get(Cover.class, id) == null)
            AntimatterAPI.register(Cover.class, this);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void deserialize(CompoundNBT nbt) {

    }

    public void serialize(CompoundNBT nbt) {
        //Write to the NBT at root level
    }

    /**
     * The key used to build dynamic textures for covers.
     */
    public static class DynamicKey {
        public final Direction facing;
        public final Texture machineTexture;

        public DynamicKey(Direction facing, Texture tex) {
            this.facing = facing;
            this.machineTexture = tex;
        }

        @Override
        public int hashCode() {
            return facing.hashCode() + machineTexture.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DynamicKey) {
                DynamicKey k = (DynamicKey) o;
                return k.facing == this.facing && k.machineTexture.equals(this.machineTexture);
            } else {
                return false;
            }
        }
    }
}
