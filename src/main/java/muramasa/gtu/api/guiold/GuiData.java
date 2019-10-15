package muramasa.gtu.api.guiold;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.util.int4;
import muramasa.gtu.integration.jei.renderer.IInfoRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GuiData {

    //TODO This whole class needs rethought

    //TODO make sure addons can redirect gui opening now that Mod instance isn't used

    private static final String ANY = "any";

    protected String id;
//    protected ContainerType<?> containerType;
    protected Tier highestTier = Tier.LV;
    protected boolean enablePlayerSlots = true;

    protected int4 area = new int4(3, 3, 170, 80), padding = new int4(0, 55, 0, 0);
    protected BarDir side = BarDir.LEFT;
    protected IInfoRenderer infoRenderer;

//    protected IContainerHandler containerHandler = (windowId, world, pos, player, inv, data) -> {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityMachine) return new ContainerMachine(windowId, (TileEntityMachine) tile, inv, player);
//        return null;
//    };

    protected LinkedHashMap<String, ArrayList<SlotData>> SLOT_LOOKUP = new LinkedHashMap<>();
    protected Object2IntOpenHashMap<SlotType> COUNT_LOOKUP = new Object2IntOpenHashMap<>();

    public GuiData(String id) {
        this.id = id;
    }

    public GuiData(Machine type) {
        this(type.getId());
    }

//    public ContainerType buildContainerType() {
//        return containerType = IForgeContainerType.create((windowId, inv, data) -> containerHandler.getContainer(windowId, GregTech.PROXY.getClientWorld(), data.readBlockPos(), GregTech.PROXY.getClientPlayer(), inv, data)).setRegistryName(type.getId());
//    }
//
//    public IContainerHandler getContainerHandler() {
//        return containerHandler;
//    }
//
//    public GuiData setContainer(IContainerHandler containerHandler) {
//        this.containerHandler = containerHandler;
//        return this;
//    }
//
//    public ContainerType getContainerType() {
//        return containerType;
//    }

    public ResourceLocation getTexture(Tier tier) {
        if (hasSlots(tier)) {
            return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + id + "_" + tier.getId() + ".png");
        } else {
            return new ResourceLocation(Ref.MODID, "textures/gui/machine/" + id + ".png");
        }
    }

    public int4 getArea() {
        return area;
    }

    public int4 getPadding() {
        return padding;
    }

    public BarDir getDir() {
        return side;
    }

    public boolean enablePlayerSlots() {
        return enablePlayerSlots;
    }

    public void setEnablePlayerSlots(boolean enablePlayerSlots) {
        this.enablePlayerSlots = enablePlayerSlots;
    }

    public GuiData setArea(int x, int y, int z, int w) {
        area.set(x, y, z, w);
        return this;
    }

    public GuiData setPadding(int x, int y, int z, int w) {
        padding.set(x, y, z, w);
        return this;
    }

    public GuiData setDir(BarDir side) {
        this.side = side;
        return this;
    }

    public IInfoRenderer getInfoRenderer() {
        return infoRenderer;
    }

    public GuiData setInfoRenderer(IInfoRenderer infoRenderer) {
        this.infoRenderer = infoRenderer;
        return this;
    }

    /** Adds a slot for ANY **/
    public GuiData add(SlotType type, int x, int y) {
        return add(ANY, new SlotData(type, x, y));
    }

    /** Adds a slot for the given Tier **/
    public GuiData add(Tier tier, SlotType type, int x, int y) {
        return add(tier.getId(), new SlotData(type, x, y));
    }

    /** Copies ALL slots from an existing GuiData **/
    public GuiData add(GuiData data) {
        for (SlotData slot : data.getAnySlots()) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from an existing Machine **/
    public GuiData add(Machine type) {
        for (SlotData slot : type.getGui().getAnySlots()) {
            add(ANY, slot);
        }
        return this;
    }

    /** Copies ALL slots from type into toTier slots **/
    public GuiData add(Tier toTier, Machine type) {
        for (SlotData slot : type.getGui().getAnySlots()) {
            add(toTier.getId(), slot);
        }
        return this;
    }

    /** Copies fromTier slots from type into toTier slots **/
    public GuiData add(Tier toTier, Machine type, Tier fromTier) {
        for (SlotData slot : type.getGui().getSlots(fromTier)) {
            add(toTier.getId(), slot);
        }
        return this;
    }

    public GuiData add(String key, SlotData slot) {
        //TODO figure out better way to do this
        Tier tier = Tier.get(key);
        if (tier != null && tier.getInternalId() > highestTier.getInternalId()) highestTier = tier;

        COUNT_LOOKUP.addTo(slot.type, 1);
        if (SLOT_LOOKUP.containsKey(key)) {
            SLOT_LOOKUP.get(key).add(slot);
        } else {
            ArrayList<SlotData> list = new ArrayList<>();
            list.add(slot);
            SLOT_LOOKUP.put(key, list);
        }
        return this;
    }

    public boolean hasSlots() {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(ANY);
        return slots != null && slots.size() > 0;
    }

    public boolean hasSlots(Tier tier) {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getId());
        return slots != null && slots.size() > 0;
    }

    public List<SlotData> getAnySlots() {
        return SLOT_LOOKUP.get(ANY);
    }

    public Tier getHighestTier() {
        return highestTier;
    }

    public List<SlotData> getSlots(Tier tier) {
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getId());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        return slots != null ? slots : new ArrayList<>();
    }

    public List<SlotData> getSlots(SlotType type, Tier tier) {
        ArrayList<SlotData> types = new ArrayList<>();
        ArrayList<SlotData> slots = SLOT_LOOKUP.get(tier.getId());
        if (slots == null) slots = SLOT_LOOKUP.get(ANY);
        if (slots == null) return types; //No slots found
        for (SlotData slot : slots) {
            if (slot.type == type) types.add(slot);
        }
        return types;
    }

    public boolean hasType(SlotType type) {
        return getCount(type) > 0;
    }

    public boolean hasAnyItem(Tier tier) {
        return getSlots(SlotType.IT_IN, tier).size() > 0 || getSlots(SlotType.IT_OUT, tier).size() > 0;
    }

    public boolean hasAnyFluid(Tier tier) {
        return getSlots(SlotType.FL_IN, tier).size() > 0 || getSlots(SlotType.FL_OUT, tier).size() > 0;
    }

    //TODO broken
    public int getCount(SlotType type) {
        return COUNT_LOOKUP.get(type);
    }
}
