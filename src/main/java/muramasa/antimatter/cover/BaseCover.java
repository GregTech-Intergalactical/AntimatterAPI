package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.texture.Texture;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//The base Cover class. All cover classes extend from this.
public abstract class BaseCover implements ICover, IGuiHandler.IHaveWidgets {

    private final List<Consumer<GuiInstance>> guiCallbacks = new ObjectArrayList<>();

    protected GuiData gui;
    @Nullable
    private Item item;
    //For multi-covers.
    @Nullable
    protected String id;

    @Override
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
        if (type.equals("pipe")) return PIPE_COVER_MODEL;
        return new ResourceLocation(getDomain() + ":block/cover/" + getRenderId());
    }

    public BaseCover() {
        if (hasGui()) {
            this.gui = new GuiData(this, Data.COVER_MENU_HANDLER);
            gui.setEnablePlayerSlots(true);
            this.addGuiCallback(t -> t.addWidget(BackgroundWidget.build(t.handler.getGuiTexture(),t.handler.guiSize(), t.handler.guiHeight())));
        }
    }

    protected void register() {
        AntimatterAPI.register(ICover.class, this);
        AntimatterAPI.register(getClass(), this);
    }
    //Extra constructor for multicovers. TODO?
    protected BaseCover(String id) {
        this.id = id;
        if (hasGui()) {
            this.gui = new GuiData(this, Data.COVER_MENU_HANDLER);
            gui.setEnablePlayerSlots(true);
        }
    }

    public void setGui(GuiData setGui) {
        this.gui = setGui;
    }

    @Override
    public GuiData getGui() {
        return gui;
    }

    @Override
    public boolean hasGui() {
        return false;
    }

    public abstract String getId();

    @Override
    public boolean isEqual(ICover cover) {
        return getId().equals(cover.getId());
    }

    public boolean isEmpty() {
        return getId().equals(Data.COVERNONE.getId());
    }

    @Override
    public void setTextures(BiConsumer<String,Texture> texer) {
        texer.accept("overlay",new Texture(getDomain(), "block/cover/" + getRenderId()));
    }

    public Texture[] getTextures() {
        List<Texture> l = new ArrayList<>();
        setTextures((name,tex) -> l.add(tex));
        return l.toArray(new Texture[0]);
    }

    //Useful for using the same model for multiple tiers where id is dependent on tier.
    protected String getRenderId() {
        return getId();
    }

    //The default cover model
    public static ResourceLocation getBasicModel() {
        return new ResourceLocation(Ref.ID + ":block/cover/basic");
    }

    //The default cover model with depth, see Output and Conveyor cover.
    public static ResourceLocation getBasicDepthModel() {
        return new ResourceLocation(Ref.ID + ":block/cover/basic_depth");
    }

    @Override
    public List<Consumer<GuiInstance>> getCallbacks() {
        return this.guiCallbacks;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public void deserialize(CoverStack<?> stack, CompoundNBT nbt) {

    }

    @Override
    public void serialize(CoverStack<?> stack, CompoundNBT nbt) {

    }

}
