package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.texture.Texture;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

//The base Cover class. All cover classes extend from this.
public abstract class BaseCover implements ICover {

    protected GuiData gui;
    @Nullable
    private Item item;
    //For multi-covers.
    @Nullable
    protected String id;

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return new ResourceLocation(getDomain() + ":block/cover/" + getRenderId());
    }

    public BaseCover() {
        if (hasGui()) {
            this.gui = new GuiData(this, Data.COVER_MENU_HANDLER);
            gui.setEnablePlayerSlots(true);
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
