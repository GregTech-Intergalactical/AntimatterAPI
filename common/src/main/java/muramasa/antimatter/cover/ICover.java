package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.api.item.ExtendedItemContainer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public interface ICover extends ITextureProvider, IDynamicModelProvider, MenuProvider, IGuiHandler {
    ResourceLocation PIPE_COVER_MODEL = new ResourceLocation(Ref.ID, "block/cover/cover_pipe");

    default void onPlace() {

    }

    default void onCreate(){

    }

    default boolean canPlace(){
        return true;
    }

    @NotNull
    default Component getDisplayName() {
        return Utils.literal(Utils.underscoreToUpperCamel(this.getId()));
    }

    default void onGuiEvent(IGuiEvent event, Player player) {
        // NOOP
    }

    default boolean onTransfer(Object object, boolean inputSide, boolean simulate) {
        return false;
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

    default void onMachineEvent(IGuiHandler tile, IMachineEvent event, int... data) {
        // NOOP
    }

    default boolean hasGui() {
        return false;
    }

    default boolean openGui(Player player, Direction side) {
        if (!hasGui())
            return false;
        AntimatterPlatformUtils.openGui((ServerPlayer) player, this, packetBuffer -> {
            packetBuffer.writeBlockPos(this.source().getTile().getBlockPos());
            packetBuffer.writeInt(side.get3DDataValue());
        });
        player.playNotifySound(Ref.WRENCH, SoundSource.BLOCKS, 1.0f, 2.0f);
        return true;
    }

    default Map<SlotType<?>, ExtendedItemContainer> getAll(){
        return null;
    }

    default int getWeakPower() {
        return 0;
    }

    default int getStrongPower() {
        return 0;
    }

    default int getPriority(Class<?> clazz){
        return 0;
    }

    /**
     * Fires once per Side. Return defines whether or not to consume the
     * interaction.
     **/
    default InteractionResult onInteract(Player player, InteractionHand hand, Direction side, @Nullable AntimatterToolType type) {
        // Do not consume behaviour per default.
        return InteractionResult.PASS;
    }

    default boolean ticks() {
        return true;
    }

    void deserialize(CompoundTag nbt);

    CompoundTag serialize();

    ItemStack getItem();

    // Stack is not guaranteed to contain a real tile and side is nullable.
    default <T> boolean blocksCapability(Class<T> cap, @Nullable Direction side) {
        return false;
    }

    default <T> boolean blocksInput(Class<T> cap, @Nullable Direction side) {
        return false;
    }

    default <T> boolean blocksOutput(Class<T> cap, @Nullable Direction side) {
        return false;
    }

    void setTextures(BiConsumer<String, Texture> texer);

    default ItemStack getDroppedStack() {
        return getItem();
    }

    default void addInfoFromStack(ItemStack stack){

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
        public void deserialize(CompoundTag nbt) {

        }

        @Override
        public CompoundTag serialize() {
            return new CompoundTag();
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
        public AbstractGuiEventPacket createGuiPacket(IGuiEvent event) {
            return null;
        }

        @Override
        public ResourceLocation getModel(String type, Direction dir) {
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
        public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
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

        public DynamicKey(BlockState state, Texture tex, String cover) {
            if (state.hasProperty(BlockStateProperties.FACING)) {
                this.facing = state.getValue(BlockStateProperties.FACING);
            } else {
                this.facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            }
            this.machineTexture = tex;
            this.coverId = cover;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.facing, machineTexture, coverId);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DynamicKey) {
                DynamicKey k = (DynamicKey) o;
                return k.facing == this.facing && k.machineTexture.equals(this.machineTexture) && coverId.equals(k.coverId);
            } else {
                return false;
            }
        }
    }

    static void init() {

    }
}
