package muramasa.antimatter.tools;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.items.MaterialTool;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.SoundType;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class AntimatterToolType implements IAntimatterObject {

    public static final AntimatterToolType SWORD = AntimatterToolType.add("sword", false, 2, 1, 10, 4.0F, 1.0F);
    public static final AntimatterToolType PICKAXE = AntimatterToolType.add("pickaxe", false, 2, 1, 10, 1.5F, -2.8F);
    public static final AntimatterToolType SHOVEL = AntimatterToolType.add("shovel", false, 2, 1, 10, 1.5F, -3.0F);
    public static final AntimatterToolType AXE = AntimatterToolType.add("axe", false, 1, 1, 10, 3.0F, -3.0F);
    public static final AntimatterToolType HOE = AntimatterToolType.add("hoe", false, 2, 2, 10, 1.75F, -3.0F);
    public static final AntimatterToolType HAMMER = AntimatterToolType.add("hammer", false, 1, 2, 5, 3.0F, -3.0F).setUseSound(SoundType.HAMMER);
    public static final AntimatterToolType WRENCH = AntimatterToolType.add("wrench", false, 5, 2, 5, 0.0F, -3.0F).setUseSound(SoundType.WRENCH);
    public static final AntimatterToolType SAW = AntimatterToolType.add("saw", false, 5, 2, 2, 1.75F, -3.0F);
    public static final AntimatterToolType FILE = AntimatterToolType.add("file", false, 5, 5, 2, 1.5F, -2.4F);
    public static final AntimatterToolType CROWBAR = AntimatterToolType.add("crowbar", false, 5, 3, 5, 2.0F, -3.0F).setUseSound(SoundType.BREAK);

    /*
    FILE("", "craftingToolFile", Sets.newHashSet("file"), null, false, 0, 1.5f, -2.4f, 1.0f, 1.0f, 50, 200, 400),
    SCREWDRIVER("Adjusts Covers and Machines", "craftingToolScrewdriver", Sets.newHashSet("screwdriver"), SoundType.WRENCH, false, 0, 1.5f, -2.4f, 1.0f, 1.0f, 200, 200, 400),
    CROWBAR("Removes Covers", "craftingToolCrowbar", Sets.newHashSet("crowbar"), SoundType.BREAK, false, 0, 2.0f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    MORTAR("Grinds Ingots into Dust", "craftingToolMortar", Sets.newHashSet("mortar"), null, false, 0, 2.0f, -2.4f, 1.0f, 1.0f, 50, 200, 400),
    WIRE_CUTTER("", "craftingToolWireCutter", Sets.newHashSet("wire_cutter"), null, false, 0, 1.25f, -2.4f, 1.0f, 1.0f, 100, 200, 400),
    SCOOP("Harvests Bees from Hives", "craftingToolScoop", Sets.newHashSet("scoop"), null, false, 0, 1.0f, -3.0f, 1.0f, 1.0f, 200, 200, 200), //TODO: Forestry
    BRANCH_CUTTER("", "craftingToolBranchCutter", Sets.newHashSet("branch_cutter"), null, false, 0, 2.5f, -3.0f, 0.25f, 0.25f, 100, 200, 800), //TODO: Merge functionality with axe?
    UNIVERSAL_SPADE("", "craftingToolUniversalSpade", Sets.newHashSet("shovel"), null, false, 0, 3.0f, -3.0f, 0.75f, 1.0f, 50, 100, 400), //TODO: Universal tool? Rename?
    KNIFE("", "craftingToolKnife", Sets.newHashSet("knife", "sword"), null, false, 0, 2.0f, -2.4f, 0.5f, 1.0f, 100, 200, 100),
    SCYTHE("", "craftingToolScythe", Sets.newHashSet("scythe"), null, false, 0, 3.0f, -3.0f, 1.0f, 4.0f, 100, 200, 800), //TODO: Sort of a dupe with BRANCH_CUTTER
    PLUNGER("", "craftingToolPlunger", Sets.newHashSet("plunger"), null, false, 0, 1.25f, -3.0f, 1.0f, 0.25f, 100, 200, 800),
    DRILL("", "craftingToolDrill", Sets.newHashSet("drill", "pickaxe"), SoundType.DRILL, true, 1, 3.0f, -2.8f, 9.0f, 4.0f, 800, 3200, 12800),
    CHAINSAW("", "craftingToolChainsaw", Sets.newHashSet("axe", "saw", "sword"), null, true, 1, 4.0f, -2.4f, 6.0f, 4.0f, 800, 3200, 12800),
    WRENCH_P("", "craftingToolWrench", Sets.newHashSet("wrench"), SoundType.WRENCH, true, 1, 2.0f, -2.4f, 4.0f, 4.0f, 800, 3200, 12800),
    JACKHAMMER("", "craftingToolJackhammer", Sets.newHashSet("jackhammer", "pickaxe"), null, true, 1, 3.0f, -3.0f, 0.25f, 2.0f, 400, 800, 3200),
    SCREWDRIVER_P("", "craftingToolScrewdriver", Sets.newHashSet("screwdriver"), SoundType.WRENCH, true, 0, 1.0f, -2.4f, 1.0f, 1.0f, 100, 200, 200),
    BUZZSAW("", "craftingToolBuzzsaw", Sets.newHashSet("saw", "buzzsaw"), null, true, 0, 1.0f, -3.0f, 1.0f, 1.0f, 100, 300, 100),
    TURBINE("", "craftingToolTurbine", Sets.newHashSet("turbine"), null, false, 0, 3.0f, -3.0f, 4.0f, 4.0f, 100, 200, 800);
     */

    private final String id;
    private String tooltip = "";
    private Set<Tag<Item>> tags = new HashSet();
    @Nullable private SoundType useSound;
    private boolean powered, repairable;
    private int baseQuality, miningDurability, attackDurability, craftingDurability, overlayLayers;
    private float baseAttackDamage, baseAttackSpeed;
    private ItemGroup itemGroup;
    private final net.minecraftforge.common.ToolType TOOL_TYPE;
    private final Set<net.minecraftforge.common.ToolType> ADDITIONAL_TOOL_TYPES = new HashSet();

    private AntimatterToolType(String id, boolean powered, int miningDurability, int attackDurability, int craftingDurability, float baseAttackDamage, float baseAttackSpeed) {
        this.id = id;
        // tags.add(Utils.getItemTag(new ResourceLocation("antimatter", id)));
        this.useSound = null;
        this.powered = powered;
        this.repairable = true;
        this.baseQuality = 0;
        this.miningDurability = miningDurability;
        this.attackDurability = attackDurability;
        this.craftingDurability = craftingDurability;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.overlayLayers = 1;
        this.itemGroup = Ref.TAB_TOOLS;
        this.TOOL_TYPE = net.minecraftforge.common.ToolType.get(id);
        AntimatterAPI.register(AntimatterToolType.class, this);
    }

    public static AntimatterToolType add(String id, boolean powered, int miningDurability, int attackDurability, int craftingDurability, float baseAttackDamage, float baseAttackSpeed) {
        return new AntimatterToolType(id, powered, miningDurability, attackDurability, craftingDurability, baseAttackDamage, baseAttackSpeed);
    }

    /** MaterialTool Instantiation **/

    public MaterialTool instantiate(String domain, Material primary, Material secondary) {
        Item.Properties properties = new Item.Properties().group(itemGroup);
        if (!repairable) properties.setNoRepair();
        return new MaterialTool(domain, this, primary, secondary, properties, new AntimatterItemTier(primary));
    }

    /** SETTERS **/

    public AntimatterToolType setToolTip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public AntimatterToolType setTags(ResourceLocation... locs) {
        for (ResourceLocation loc : locs) {
            this.tags.add(Utils.getItemTag(loc));
        }
        return this;
    }

    public AntimatterToolType setUseSound(SoundType type) {
        this.useSound = type;
        return this;
    }

    public AntimatterToolType setRepairability(boolean repairable) {
        this.repairable = repairable;
        return this;
    }

    public AntimatterToolType setBaseQuality(int quality) {
        this.baseQuality = quality;
        return this;
    }

    public AntimatterToolType setOverlayLayers(int layers) {
        this.overlayLayers = layers;
        return this;
    }

    public AntimatterToolType setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    public AntimatterToolType addAdditionalTypes(String... types) {
        for (String type : types) {
            ADDITIONAL_TOOL_TYPES.add(net.minecraftforge.common.ToolType.get(type));
        }
        return this;
    }

    /** GETTERS **/

    @Override
    public String getId() {
        return id;
    }

    public net.minecraftforge.common.ToolType getAntimatterToolType() {
        return TOOL_TYPE;
    }

    public Set<net.minecraftforge.common.ToolType> getAdditionalAntimatterToolTypes() {
        return ADDITIONAL_TOOL_TYPES;
    }

    public String getTooltip() {
        return tooltip;
    }

    public Set<Tag<Item>> getTags() {
        return tags;
    }

    public SoundType getUseSound() {
        return useSound;
    }

    public boolean isPowered() {
        return powered;
    }

    public boolean getRepairability() {
        return repairable;
    }

    public int getBaseQuality() {
        return baseQuality;
    }

    public float getBaseAttackDamage() {
        return baseAttackDamage;
    }

    public float getBaseAttackSpeed() {
        return baseAttackSpeed;
    }

    public int getMiningDurability() {
        return miningDurability;
    }

    public int getAttackDurability() {
        return attackDurability;
    }

    public int getCraftingDurability() {
        return craftingDurability;
    }

    public int getOverlayLayers() {
        return overlayLayers;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }
}
