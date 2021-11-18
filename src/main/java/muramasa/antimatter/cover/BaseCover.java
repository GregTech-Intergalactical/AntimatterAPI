package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.texture.Texture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//The base Cover class. All cover classes extend from this.
public abstract class BaseCover implements ICover, IGuiHandler.IHaveWidgets {
    public final CoverFactory factory;
    public final ICoverHandler<?> handler;
    @Nullable
    public final Tier tier;
    @Nullable
    public final GuiData gui;
    public final Direction side;

    private final List<Consumer<GuiInstance>> guiCallbacks = new ObjectArrayList<>();

    @Override
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
        if (type.equals("pipe"))
            return PIPE_COVER_MODEL;
        return new ResourceLocation(getDomain() + ":block/cover/" + getRenderId());
    }

    @Override
    public Direction side() {
        return side;
    }

    @Override
    public ICoverHandler<?> source() {
        return handler;
    }

    public BaseCover(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        this.factory = Objects.requireNonNull(factory, "Missing factory in BaseCover");
        this.handler = source;
        this.tier = tier;
        this.side = side;
        if (factory.hasGui()) {
            this.gui = new GuiData(this, factory.getMenuHandler());
            gui.setEnablePlayerSlots(true);
            this.addGuiCallback(t -> t
                    .addWidget(BackgroundWidget.build(t.handler.getGuiTexture(), t.handler.guiSize(), t.handler.guiHeight())));
        } else {
            this.gui = null;
        }
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    @Override
    public List<Consumer<GuiInstance>> getCallbacks() {
        return this.guiCallbacks;
    }

    @Override
    public void setTextures(BiConsumer<String, Texture> texer) {
        texer.accept("overlay", new Texture(factory.getDomain(), "block/cover/" + getRenderId()));
    }

    public Texture[] getTextures() {
        List<Texture> l = new ArrayList<>();
        setTextures((name, tex) -> l.add(tex));
        return l.toArray(new Texture[0]);
    }

    // Useful for using the same model for multiple tiers where id is dependent on
    // tier.
    protected String getRenderId() {
        return getId();
    }

    // The default cover model
    public static ResourceLocation getBasicModel() {
        return new ResourceLocation(Ref.ID + ":block/cover/basic");
    }

    // The default cover model with depth, see Output and Conveyor cover.
    public static ResourceLocation getBasicDepthModel() {
        return new ResourceLocation(Ref.ID + ":block/cover/basic_depth");
    }

    @Override
    public ItemStack getItem() {
        return factory.getItem(tier);
    }

    @Override
    public void deserialize(CompoundNBT nbt) {

    }

    @Override
    public boolean hasGui() {
        return factory.hasGui();
    }

    @Override
    public GuiData getGui() {
        return gui;
    }

    @Override
    public CompoundNBT serialize() {
        return new CompoundNBT();
    }

    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return hasGui() ? getGui().getMenuHandler().menu(this, p_createMenu_3_.inventory, p_createMenu_1_) : null;
    }

    @Override
    public boolean isRemote() {
        return handler.getTile().getLevel().isClientSide();
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(factory.getDomain(), "gui/cover/" + getId());
    }

    @Override
    public AbstractGuiEventPacket createGuiPacket(IGuiEvent event) {
        return new CoverGuiEventPacket(event, this.handler.getTile().getBlockPos(), this.side);
    }

    @Override
    public CoverFactory getFactory() {
        return factory;
    }

}
