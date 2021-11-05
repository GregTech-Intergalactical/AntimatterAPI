package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public interface ICover extends ITextureProvider, IDynamicModelProvider, INamedContainerProvider, IGuiHandler {
    ResourceLocation PIPE_COVER_MODEL = new ResourceLocation(Ref.ID, "block/cover/cover_pipe");

    default void onPlace() {

    }

    @Nonnull
    default ITextComponent getDisplayName() {
        return new StringTextComponent(Utils.underscoreToUpperCamel(this.getId()));
    }

    default void onGuiEvent(IGuiEvent event, PlayerEntity player, int... data) {
        // NOOP
    }

    Direction side();

    default ResourceLocation getLoc() {
        return new ResourceLocation(getDomain(), getId());
    }

    @Override
    default String getId() {
        return getFactory().getId();
    }

    @Override
    default String getDomain() {
        return getFactory().getDomain();
    }

    CoverFactory getFactory();

    Tier getTier();

    // Called right after the cover being removed from the tile.
    default void onRemove() {

    }

    // Called on update of the world.
    default void onUpdate() {

    }

    default void onBlockUpdate() {

    }

    @Override
    default String handlerDomain() {
        return getDomain();
    }

    default void onMachineEvent(TileEntityMachine<?> tile, IMachineEvent event, int... data) {
        // NOOP
    }

    default boolean hasGui() {
        return false;
    }

    default boolean openGui(PlayerEntity player, Direction side) {
        if (!hasGui())
            return false;
        NetworkHooks.openGui((ServerPlayerEntity) player, this, packetBuffer -> {
            packetBuffer.writeBlockPos(this.source().getTile().getPos());
            packetBuffer.writeInt(side.getIndex());
        });
        player.playSound(Ref.WRENCH, SoundCategory.BLOCKS, 1.0f, 2.0f);
        return true;
    }

    default int getWeakPower() {
        return 0;
    }

    default int getStrongPower() {
        return 0;
    }

    /**
     * Fires once per Side. Return defines whether or not to consume the
     * interaction.
     **/
    default boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        // Do not consume behaviour per default.
        return false;
    }

    default boolean ticks() {
        return true;
    }

    void deserialize(CompoundNBT nbt);

    CompoundNBT serialize();

    ItemStack getItem();

    // Stack is not guaranteed to contain a real tile and side is nullable.
    default <T> boolean blocksCapability(Capability<T> cap, @Nullable Direction side) {
        return false;
    }

    default <T> boolean blocksInput(Capability<T> cap, @Nullable Direction side) {
        return false;
    }

    default <T> boolean blocksOutput(Capability<T> cap, @Nullable Direction side) {
        return false;
    }

    void setTextures(BiConsumer<String, Texture> texer);

    default ItemStack getDroppedStack() {
        return getItem();
    }

    default boolean isEqual(ICover cover) {
        return this.getLoc().equals(cover.getLoc());
    }

    //Does not consider Tier.
    default boolean isEqual(CoverFactory fac) {
        return this.getLoc().equals(new ResourceLocation(fac.domain, fac.getId()));
    }

    ICoverHandler<?> source();

    default GuiData getGui() {
        return null;
    }

    default List<BakedQuad> transformQuads(BlockState state, List<BakedQuad> quads) {
        /*
         * if (state.getBlock() instanceof IColorHandler) { quads.forEach(t -> {
         * RenderHelper.colorQuad(t,
         * ((IColorHandler)state.getBlock()).getBlockColor(state, null, null,
         * t.getTintIndex())); }); }
         */
        return quads;
    }

    default boolean isEmpty() {
        return this == empty;
    }


    ICover empty = new ICover() {
        @Override
        public Direction side() {
            return null;
        }

        @Override
        public CoverFactory getFactory() {
            return emptyFactory;
        }

        @Override
        public Tier getTier() {
            return null;
        }

        @Override
        public void deserialize(CompoundNBT nbt) {

        }

        @Override
        public CompoundNBT serialize() {
            return new CompoundNBT();
        }

        @Override
        public ItemStack getItem() {
            return ItemStack.EMPTY;
        }

        @Override
        public ICoverHandler<?> source() {
            return null;
        }

        @Override
        public boolean isRemote() {
            return false;
        }

        @Override
        public ResourceLocation getGuiTexture() {
            return null;
        }

        @Override
        public AbstractGuiEventPacket createGuiPacket(IGuiEvent event, int... data) {
            return null;
        }

        @Override
        public ResourceLocation getModel(String type, Direction dir, Direction facing) {
            return null;
        }

        @Override
        public Texture[] getTextures() {
            return new Texture[0];
        }

        @Override
        public void setTextures(BiConsumer<String, Texture> texer) {

        }

        @Override
        public boolean ticks() {
            return false;
        }

        @Nullable
        @Override
        public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
            return null;
        }
    };
    CoverFactory emptyFactory = CoverFactory.builder((a, b, c, d) -> empty).build(Ref.ID, "none");

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
