package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public interface ICover extends IAntimatterObject, ITextureProvider, IDynamicModelProvider {

    default void onPlace(CoverStack<?> instance, Direction side) {

    }
    //Called right after the cover being removed from the tile.
    default void onRemove(CoverStack<?> instance, Direction side) {

    }

    //Called on update of the world.
    default void onUpdate(CoverStack<?> instance, Direction side) {

    }

    default void onMachineEvent(CoverStack<?> instance, TileEntityMachine tile, IMachineEvent event, int... data) {
        //NOOP
    }

    default void onGuiEvent(CoverStack<?> instance, IGuiEvent event, int... data) {
        //NOOP
    }

    default boolean openGui(CoverStack<?> instance, PlayerEntity player, Direction side) {
        if (!hasGui()) return false;
        NetworkHooks.openGui((ServerPlayerEntity) player, instance, packetBuffer -> {
            packetBuffer.writeBlockPos(instance.getTile().getPos());
            packetBuffer.writeInt(side.getIndex());
        });
        player.playSound(Ref.WRENCH, SoundCategory.BLOCKS, 1.0f, 2.0f);
        return true;
    }

    /**
     * Fires once per Side. Return defines whether or not to consume the interaction.
     **/
    default boolean onInteract(CoverStack<?> instance, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        //Do not consume behaviour per default.
        return false;
    }

    void deserialize(CoverStack<?> stack, CompoundNBT nbt);
    void serialize(CoverStack<?> stack, CompoundNBT nbt);

    Item getItem();

    boolean hasGui();

    GuiData getGui();

    default <T> boolean blocksCapability(CoverStack<?> stack, Capability<T> cap, Direction side) {
        return false;
    }

    /**
     * No guarantee to be implemented. Override the item of this cover.
     * @param item the item to set.
     */
    default void setItem(Item item) {

    }

    default ITextComponent getDisplayName() {
        return new StringTextComponent(Utils.underscoreToUpperCamel(this.getId()));
    }

    void setTextures(BiConsumer<String,Texture> texer);

    default ItemStack getDroppedStack() {
        return getItem() == null ? ItemStack.EMPTY : new ItemStack(getItem(), 1);
    }

    default boolean isEqual(ICover cover) {
        return this == cover;
    }

    default List<BakedQuad> transformQuads(List<BakedQuad> quads) {
        return quads;
    }
    /**
     * The key used to build dynamic textures for covers.
     */
    class DynamicKey {
        public final Direction facing;
        public final Texture machineTexture;
        public final String coverId;

        public DynamicKey(Direction facing, Texture tex, String cover) {
            this.facing = facing;
            this.machineTexture = tex;
            this.coverId = cover;
        }

        @Override
        public int hashCode() {
            return facing.hashCode() + machineTexture.hashCode() + coverId.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DynamicKey) {
                BaseCover.DynamicKey k = (DynamicKey) o;
                return k.facing == this.facing && k.machineTexture.equals(this.machineTexture) && coverId.equals(k.coverId);
            } else {
                return false;
            }
        }
    }
}
