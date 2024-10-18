package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.FakeTrackedItemHandler;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.gui.widget.CoverModeHandlerWidget;
import muramasa.antimatter.gui.widget.SlotWidget;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.api.item.ExtendedItemContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//The base Cover class. All cover classes extend from this.
public abstract class BaseCover implements ICover, IGuiHandler.IHaveWidgets {
    @NotNull
    public final CoverFactory factory;
    @NotNull
    public final ICoverHandler<?> handler;
    @Nullable
    public final Tier tier;
    @Nullable
    public final GuiData gui;
    public final Direction side;
    private final List<Consumer<GuiInstance>> guiCallbacks = new ObjectArrayList<>();

    protected Object2ObjectMap<SlotType<?>, TrackedItemHandler<?>> inventories = null;

    @Override
    public ResourceLocation getModel(String type, Direction dir) {
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

    public BaseCover(@NotNull ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        this.factory = Objects.requireNonNull(factory, "Missing factory in BaseCover");
        this.handler = source;
        this.tier = tier;
        this.side = side;
        if (factory.hasGui()) {
            this.gui = new GuiData(this, factory.getMenuHandler());
            gui.setEnablePlayerSlots(true);
            gui.setSlots(ISlotProvider.DEFAULT());
            this.addGuiCallback(t -> {
                t.addWidget(BackgroundWidget.build(t.handler.getGuiTexture(), t.handler.guiSize(), t.handler.guiHeight(), t.handler.guiTextureSize(), t.handler.guiTextureHeight()));
                if (BaseCover.this instanceof ICoverModeHandler){
                    t.addWidget(CoverModeHandlerWidget.build());
                }
                List<SlotData<?>> slots = tier == null ? gui.getSlots().getAnySlots() : gui.getSlots().getSlots(tier);
                slots.forEach(s ->{
                    t.addWidget(SlotWidget.build(s));
                });
            });
        } else {
            this.gui = null;
        }
    }

    @Override
    public void onPlace() {
        onCreate();
    }

    @Override
    public void onCreate() {
        setInventory();
    }

    private void setInventory(){
        if (factory.hasGui()){
            if (inventories == null){
                inventories = new Object2ObjectOpenHashMap<>();
            }
            List<SlotData<?>> slots = tier == null ? gui.getSlots().getAnySlots() : gui.getSlots().getSlots(tier);
            Map<SlotType<?>, List<SlotData<?>>> map = slots.stream().collect(Collectors.groupingBy(SlotData::getType));
            slots.forEach(s ->{
                for (Map.Entry<SlotType<?>, List<SlotData<?>>> entry : map.entrySet()) {
                    SlotType<?> type = entry.getKey();
                    int count = gui.getSlots().getCount(tier, entry.getKey());
                    if (type == SlotType.DISPLAY_SETTABLE || type == SlotType.DISPLAY) {
                        inventories.put(type, new FakeTrackedItemHandler<>(this, type, count, type.output, type.input, type.tester));
                    } else {
                        inventories.put(type, new TrackedItemHandler<>(this, type, count, type.output, type.input, type.tester));
                    }

                }
            });
        }
    }

    @Override
    public @Nullable Tier getTier() {
        return tier;
    }

    @Override
    public List<Consumer<GuiInstance>> getCallbacks() {
        return this.guiCallbacks;
    }

    @Override
    public Map<SlotType<?>, ExtendedItemContainer> getAll() {
        return (Map<SlotType<?>, ExtendedItemContainer>) (Object) inventories;
    }
    public ExtendedItemContainer getInventory(SlotType<?> type){
        return inventories.get(type);
    }

    @Override
    public ItemStack getDroppedStack() {
        ItemStack stack =  ICover.super.getDroppedStack();
        if (inventories != null && getFactory().hasGui()){
            CompoundTag nbt = new CompoundTag();
            this.inventories.forEach((f, i) -> {
                if (i.isEmpty()) return;
                nbt.put(f.getId(), i.serialize(new CompoundTag()));
            });
            if (!nbt.isEmpty()) {
                stack.getOrCreateTag().put("coverInventories", nbt);
            }
        }
        return stack;
    }

    @Override
    public void addInfoFromStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("coverInventories")){
            CompoundTag nbt = tag.getCompound("coverInventories");
            if (inventories != null && getFactory().hasGui()){
                this.inventories.forEach((f, i) -> {
                    if (!nbt.contains(f.getId())) return;
                    i.deserialize(nbt.getCompound(f.getId()));
                });
                handler.getTile().setChanged();
            }
        }
    }

    @Override
    public void setTextures(BiConsumer<String, Texture> texer) {
        texer.accept("overlay", factory.getTextures().isEmpty() ? new Texture(factory.getDomain(), "block/cover/" + getRenderId()) : factory.getTextures().get(factory.getTextures().size() == 6 ? side.get3DDataValue() : 0));
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
    public void deserialize(CompoundTag nbt) {
        if (getFactory().hasGui()){
            this.inventories.forEach((f, i) -> {
                if (!nbt.contains(f.getId())) return;
                i.deserialize(nbt.getCompound(f.getId()));
            });
        }
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
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        if (inventories != null && getFactory().hasGui()){
            this.inventories.forEach((f, i) -> {
                if (i.isEmpty()) return;
                nbt.put(f.getId(), i.serialize(new CompoundTag()));
            });
        }
        return nbt;
    }

    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return hasGui() ? getGui().getMenuHandler().menu(this, p_createMenu_3_.getInventory(), p_createMenu_1_) : null;
    }

    @Override
    public boolean isRemote() {
        return handler.getTile().getLevel().isClientSide();
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(Ref.ID, "textures/gui/background/machine_basic.png");
    }

    @Override
    public AbstractGuiEventPacket createGuiPacket(IGuiEvent event) {
        return new CoverGuiEventPacket(event, this.handler.getTile().getBlockPos(), this.side);
    }

    @Override
    public CoverFactory getFactory() {
        return factory;
    }

    protected void markAndNotifySource(){
        AntimatterPlatformUtils.INSTANCE.markAndNotifyBlock(source().getTile().getLevel(), source().getTile().getBlockPos(), source().getTile().getLevel().getChunkAt(source().getTile().getBlockPos()), source().getTile().getBlockState(), source().getTile().getBlockState(), 1, 512);
    }

}
