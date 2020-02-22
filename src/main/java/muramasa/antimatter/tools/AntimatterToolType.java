package muramasa.antimatter.tools;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.IMaterialTag;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialTag;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static net.minecraft.block.material.Material.*;

public class AntimatterToolType implements IAntimatterObject {

    public static AntimatterToolType SWORD;
    public static AntimatterToolType PICKAXE;
    public static AntimatterToolType SHOVEL;
    public static AntimatterToolType AXE;
    public static AntimatterToolType HOE;
    public static AntimatterToolType HAMMER;
    public static AntimatterToolType WRENCH;
    public static AntimatterToolType SAW;
    public static AntimatterToolType FILE;
    public static AntimatterToolType CROWBAR;
    public static AntimatterToolType DRILL;
    public static AntimatterToolType SCREWDRIVER;
    public static AntimatterToolType MORTAR;
    public static AntimatterToolType WIRE_CUTTER;
    public static AntimatterToolType KNIFE;
    public static AntimatterToolType PLUNGER;
    public static AntimatterToolType CHAINSAW;
    public static AntimatterToolType ELECTRIC_WRENCH;
    public static AntimatterToolType ELECTRIC_SCREWDRIVER;
    public static AntimatterToolType JACKHAMMER;
    public static AntimatterToolType BUZZSAW;

    public static void init() {
        SWORD = AntimatterToolType.add(Ref.ID, "sword", 2, 1, 10, 4.0F, -2.4F).setToolClass(MaterialSword.class);
        PICKAXE = AntimatterToolType.add(Ref.ID, "pickaxe", 1, 2, 10, 1.5F, -2.8F).addEffectiveMaterials(PACKED_ICE, IRON, ROCK, ANVIL, PISTON);
        SHOVEL = AntimatterToolType.add(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F).addEffectiveMaterials(CLAY, SAND, SNOW, SNOW_BLOCK, EARTH);
        AXE = AntimatterToolType.add(Ref.ID, "axe", 1, 1, 10, 3.0F, -3.0F).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO);
        HOE = AntimatterToolType.add(Ref.ID, "hoe", 1, 2, 10, -1.0F, -3.0F);
        HAMMER = AntimatterToolType.add(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F).addToolTypes("pickaxe").addEffectiveMaterials(IRON, ROCK).setUseSound(SoundEvents.BLOCK_ANVIL_PLACE);
        WRENCH = AntimatterToolType.add(Ref.ID, "wrench", 2, 2, 2, 1.5F, -2.8F).setUseSound(Ref.WRENCH).setOverlayLayers(0);
        SAW = AntimatterToolType.add(Ref.ID, "saw", 2, 2, 2, 1.75F, -3.0F);
        FILE = AntimatterToolType.add(Ref.ID, "file", 2, 2, 2, 1.0F, -2.4F);
        CROWBAR = AntimatterToolType.add(Ref.ID, "crowbar", 2, 10, 5, 2.0F, -3.0F).setUseSound(SoundEvents.ENTITY_ITEM_BREAK).setSecondaryRequirement(MaterialTag.RUBBERTOOLS);
        DRILL = AntimatterToolType.add(Ref.ID, "drill", 1, 2, 10, 0.0F, -1.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addToolTypes("pickaxe").addEffectiveMaterials(PACKED_ICE, IRON, ROCK, ANVIL, PISTON).setMultiBlockBreakability(1, 1, 1);
        SCREWDRIVER = AntimatterToolType.add(Ref.ID, "screwdriver", 2, 2, 2, 0.0F, -1.0F).setUseSound(Ref.WRENCH);
        MORTAR = AntimatterToolType.add(Ref.ID, "mortar", 5, 5, 2, -3.0F, -1.0F).setUseSound(SoundEvents.BLOCK_GRINDSTONE_USE).setBlockBreakability(false);
        WIRE_CUTTER = AntimatterToolType.add(Ref.ID, "wire_cutter", 5, 3, 2, 0.5F, -1.5F).setUseSound(SoundEvents.ENTITY_SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CARPET);
        KNIFE = AntimatterToolType.add(Ref.ID, "knife", 2, 2, 5, 1.8F, -1.8F).setToolClass(MaterialSword.class);
        PLUNGER = AntimatterToolType.add(Ref.ID, "plunger", 5, 5, 10, -1.0F, -3.0F).setUseSound(SoundEvents.ITEM_BUCKET_EMPTY).setPrimaryRequirement(MaterialTag.RUBBERTOOLS);
        CHAINSAW = AntimatterToolType.add(Ref.ID, "chainsaw", 1, 1, 5, 2.0F, -3.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.BOW).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO, LEAVES).addToolTypes("axe", "saw").setMultiBlockBreakability(1, 1, 1);
        ELECTRIC_WRENCH = AntimatterToolType.add(Ref.ID, "electric_wrench", 2, 2, 2, 1.5F, -2.8F).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH);
        ELECTRIC_SCREWDRIVER = AntimatterToolType.add(Ref.ID, "electric_screwdriver", 2, 2, 2, 0.0F, -1.0F).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).setOverlayLayers(2);
        JACKHAMMER = AntimatterToolType.add(Ref.ID, "jackhammer", 1, 2, 10, 1.0F, -3.2F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addEffectiveMaterials(ROCK, EARTH, SAND, ORGANIC).setMultiBlockBreakability(1, 0, 2);
        BUZZSAW = AntimatterToolType.add(Ref.ID, "buzzsaw", 2, 2, 2, 0.5F, -2.7F).setPowered(100000, 1, 2, 3).setOverlayLayers(2);
    }

    private final String domain, id;
    private final ToolType TOOL_TYPE;
    private final Set<ToolType> TOOL_TYPES = new HashSet<>();
    private final Set<Block> EFFECTIVE_BLOCKS = new HashSet<>();
    private final Set<net.minecraft.block.material.Material> EFFECTIVE_MATERIALS = new HashSet<>();
    private List<ITextComponent> tooltip = new ArrayList<>();
    @Nullable private SoundEvent useSound;
    private boolean powered, repairable, blockBreakability, multiBlockBreakability, autogenerate;
    private long baseMaxEnergy;
    private int[] energyTiers;
    private int baseQuality, useDurability, attackDurability, craftingDurability, overlayLayers, multiBlockColumn, multiBlockRow, multiBlockDepth;
    private float baseAttackDamage, baseAttackSpeed;
    private ItemGroup itemGroup;
    private Tag<Item> tag; // Set?
    private UseAction useAction;
    @Nullable private IMaterialTag primaryMaterialRequirement, secondaryMaterialRequirement;
    private Class<? extends IAntimatterTool> toolClass;

    private AntimatterToolType(String domain, String id, int useDurability, int attackDurability, int craftingDurability, float baseAttackDamage, float baseAttackSpeed) {
        this.domain = domain;
        this.id = id;
        this.useSound = null;
        this.repairable = true;
        this.blockBreakability = true;
        this.multiBlockBreakability = false;
        this.autogenerate = true;
        this.multiBlockColumn = 0;
        this.multiBlockRow = 0;
        this.multiBlockDepth = 1;
        this.baseQuality = 0;
        this.useDurability = useDurability;
        this.attackDurability = attackDurability;
        this.craftingDurability = craftingDurability;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.overlayLayers = 1;
        this.itemGroup = Ref.TAB_TOOLS;
        this.tag = Utils.getItemTag(new ResourceLocation(Ref.ID, id));
        this.useAction = UseAction.NONE;
        this.toolClass = MaterialTool.class;
        this.TOOL_TYPE = net.minecraftforge.common.ToolType.get(id);
        this.TOOL_TYPES.add(TOOL_TYPE);
        AntimatterAPI.register(AntimatterToolType.class, this);
    }

    /**
     * Instantiates a AntimatterToolType with its basic values
     * @param id                 unique identifier
     * @param useDurability      durability that is lost during an 'use' or 'blockDestroyed' operation
     * @param attackDurability   durability that is lost on 'hitEntity'
     * @param craftingDurability durability that is lost on 'getContainerItem' (when item is crafted as well as this tool is in the crafting matrix)
     * @param baseAttackDamage   base attack damage that would be applied to the item's attributes
     * @param baseAttackSpeed    base attack speed that would be applied to the item's attributes
     * @return a brand new AntimatterToolType for enjoyment
     */
    public static AntimatterToolType add(String domain, String id, int useDurability, int attackDurability, int craftingDurability, float baseAttackDamage, float baseAttackSpeed) {
        if (domain.isEmpty()) Utils.onInvalidData("AntimatterToolType registered with no domain name!");
        if (id.isEmpty()) Utils.onInvalidData("AntimatterToolType registered with an empty ID!");
        return new AntimatterToolType(domain, id, useDurability, attackDurability, craftingDurability, baseAttackDamage, baseAttackSpeed);
    }

    /** IAntimatterTool Instantiation **/

    /**
     * Instantiates a MaterialTool with Reflection (only use this when you have done setToolClass when creating your AntimatterToolType)
     * @param domain  namespace
     * @param objects an Object array that should be ordered same way as your custom ToolClass constructor
     *                (e.g. for MaterialItem: String domain, AntimatterToolType type, IItemTier tier, Item.Properties properties, Material primary, Material secondary)
     * @return a brand new custom implementation of IAntimatterTool for enjoyment
     */
    public IAntimatterTool instantiateExplicitly(String domain, Object... objects) {
        if (domain.isEmpty()) Utils.onInvalidData("An AntimatterToolType was instantiated with an empty domain name!");
        if (objects.length == 0) {
            Utils.onInvalidData("An AntimatterToolType was instantiated with an empty arguments list!");
        }
        if (toolClass.equals(MaterialTool.class)) {
            Utils.onInvalidData("Please use the correct instantiation method in AntimatterToolType to return the correct instance!");
        }
        try {
            return ConstructorUtils.invokeConstructor(toolClass, domain, objects);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Instantiates a MaterialTool
     * @param domain    namespace
     * @param primary   must not be null
     * @param secondary can be null
     * @param tier      must not be null, can use Vanilla's (or own's) implementation of IItemTier as well as AntimatterItemTier
     * @return a brand new implementation of IAntimatterTool for enjoyment
     */
    public IAntimatterTool instantiate(String domain, @Nonnull Material primary, @Nullable Material secondary, @Nonnull IItemTier tier) {
        Item.Properties properties = prepareInstantiation(domain, tier);
        if (!toolClass.equals(MaterialTool.class)) {
            try {
                // We only use this for MaterialSword.class in vanilla Antimatter right now
                return ConstructorUtils.invokeConstructor(toolClass, domain, this, tier, properties, primary, secondary);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                Antimatter.LOGGER.fatal("Use instantiateExplicitly() in AntimatterToolType!");
                e.printStackTrace();
            }
        }
        return new MaterialTool(domain, this, tier, properties, primary, secondary);
    }

    /**
     * Instantiates a MaterialElectricTool with AntimatterToolTier
     * @param domain    namespace
     * @param primary   must not be null
     * @param secondary can be null
     * @return
     */
    public List<IAntimatterTool> instantiatePoweredVariants(String domain, Material primary, @Nullable Material secondary) {
        AntimatterItemTier tier = new AntimatterItemTier(this, primary, secondary);
        List<IAntimatterTool> poweredTools = new ArrayList<>();
        Item.Properties properties = prepareInstantiation(domain, tier);
        for (int energyTier : energyTiers) {
            System.out.println(Ref.VN[energyTier].toLowerCase(Locale.ENGLISH));
            poweredTools.add(new MaterialElectricTool(domain, this, tier, properties, primary, secondary, energyTier));
        }
        return poweredTools;
    }

    private Item.Properties prepareInstantiation(String domain, @Nonnull IItemTier tier) {
        if (domain.isEmpty()) Utils.onInvalidData("An AntimatterToolType was instantiated with an empty domain name!");
        Item.Properties properties = new Item.Properties().group(itemGroup);
        if (!repairable) properties.setNoRepair();
        if (!TOOL_TYPES.isEmpty()) TOOL_TYPES.forEach(t -> properties.addToolType(t, tier.getHarvestLevel()));
        return properties;
    }

    /**
     * Instantiates a MaterialTool with AntimatterToolTier
     * @param domain    namespace
     * @param primary   must not be null
     * @param secondary can be null
     * @return a brand new MaterialTool for enjoyment
     */
    public IAntimatterTool instantiate(String domain, Material primary, @Nullable Material secondary) {
        AntimatterItemTier tier = new AntimatterItemTier(this, primary, secondary);
        return instantiate(domain, primary, secondary, tier);
    }

    /** SETTERS **/

    public AntimatterToolType setToolTip(ITextComponent... tooltip) {
        this.tooltip.addAll(Arrays.asList(tooltip));
        return this;
    }

    public AntimatterToolType setMultiBlockBreakability(int column, int row, int depth) {
        if (column == 0 && row == 0) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to break empty rows and columns!");
        this.multiBlockBreakability = true;
        this.multiBlockColumn = column;
        this.multiBlockRow = row;
        this.multiBlockDepth = depth;
        return this;
    }

    public AntimatterToolType setPowered(long baseMaxEnergy, int... energyTiers) {
        this.powered = true;
        this.baseMaxEnergy = baseMaxEnergy;
        this.energyTiers = energyTiers;
        this.toolClass = MaterialElectricTool.class;
        return this;
    }

    public AntimatterToolType setAutogenerate(boolean autogenerate) {
        this.autogenerate = autogenerate;
        return this;
    }

    public AntimatterToolType addToolTypes(String... types) {
        if (types.length == 0) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no additional tool types even when it was explicitly called!");
        for (String type : types) {
            this.TOOL_TYPES.add(net.minecraftforge.common.ToolType.get(type));
        }
        return this;
    }

    public AntimatterToolType addEffectiveBlocks(Block... blocks) {
        if (blocks.length == 0) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no effective blocks even when it was explicitly called!");
        this.EFFECTIVE_BLOCKS.addAll(Arrays.asList(blocks));
        return this;
    }

    public AntimatterToolType addEffectiveMaterials(net.minecraft.block.material.Material... materials) {
        if (materials.length == 0) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no effective materials even when it was explicitly called!");
        this.EFFECTIVE_MATERIALS.addAll(Arrays.asList(materials));
        return this;
    }

    public AntimatterToolType setPrimaryRequirement(IMaterialTag tag) {
        if (tag == null) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no primary material requirement even when it was explicitly called!");
        this.primaryMaterialRequirement = tag;
        return this;
    }

    public AntimatterToolType setSecondaryRequirement(IMaterialTag tag) {
        if (tag == null) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no secondary material requirement even when it was explicitly called!");
        this.secondaryMaterialRequirement = tag;
        return this;
    }

    public AntimatterToolType setToolClass(Class<? extends IAntimatterTool> toolClass) {
        this.toolClass = toolClass;
        return this;
    }

    public boolean getBlockBreakability() {
        return blockBreakability;
    }

    public AntimatterToolType setBlockBreakability(boolean breakable) {
        this.blockBreakability = breakable;
        return this;
    }

    public AntimatterToolType setBaseQuality(int quality) {
        if (quality < 0) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have negative Base Quality!");
        this.baseQuality = quality;
        return this;
    }


    public AntimatterToolType setUseSound(SoundEvent sound) {
        this.useSound = sound;
        return this;
    }

    public boolean getRepairability() {
        return repairable;
    }

    public AntimatterToolType setRepairability(boolean repairable) {
        this.repairable = repairable;
        return this;
    }

    public AntimatterToolType setOverlayLayers(int layers) {
        if (layers < 0) Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have less than 1 overlayer layers!");
        this.overlayLayers = layers;
        return this;
    }

    public AntimatterToolType setItemGroup(@Nonnull ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    public AntimatterToolType setUseAction(@Nonnull UseAction useAction) {
        this.useAction = useAction;
        return this;
    }

    /** GETTERS **/

    /**
     * @param primary   material
     * @param secondary material, it is nullable
     * @return Item variant of an AntimatterToolType
     */
    public Item get(Material primary, @Nullable Material secondary) {
        if (overlayLayers == 0 && secondary != null) {
            Utils.onInvalidData("GET ERROR - SECONDARY MATERIAL SHOULD BE NULL: T(" + id + ") M(" + secondary.getId() + ")");
        }
        if (primary.equals(secondary)) {
            Utils.onInvalidData("GET ERROR - PRIMARY AND SECONDARY MATERIALS SHOULD BE DIFFERENT: T(" + id + ") M(" + primary.getId() + ") AND M(" + secondary.getId() + ")");
        }
        if (secondary == null && (secondaryMaterialRequirement != null && !secondary.has(secondaryMaterialRequirement))) {
            Utils.onInvalidData("GET ERROR - SECONDARY MATERIAL REQUIREMENT MISMATCH: T(" + id + ") M(" + secondary.getId() + ")");
        }
        IAntimatterTool item = AntimatterAPI.get(IAntimatterTool.class, primary.getId() + "_" + (secondary == null ? "" : secondary.getId() + "_") + id);
        if (item == null) {
            Utils.onInvalidData("GET ERROR - TOOL ITEM NULL: T(" + id + ") M(" + primary.getId() + ") AND M(" + secondary.getId() + ")");
        }
        return item.asItem();
    }

    /**
     * @param primary   material
     * @param secondary material, it is nullable
     * @return ItemStack variant of an AntimatterToolType, it will come with an enchantment if the primary and/or secondary has native enchants
     */
    public ItemStack get(Material primary, @Nullable Material secondary, int count) {
        Item item = get(primary, secondary);
        if (item == null) {
            Utils.onInvalidData("GET ERROR - TOOL ITEM NULL: T(" + id + ") M(" + primary.getId() + ") AND M(" + secondary.getId() + ")");
        }
        ItemStack stack = new ItemStack(item, count);
        if (stack.isEmpty()) {
            Utils.onInvalidData("GET ERROR - TOOL STACK EMPTY: T(" + id + ") M(" + primary.getId() + ") AND M(" + secondary.getId() + ")");
        }
        // TODO: DISABLE UNBREAKING TO BE APPLIED ONTO TOOLS
        if (!primary.getEnchantments().isEmpty()) {
            primary.getEnchantments().entrySet().forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
        }
        if (secondary != null && secondary.getEnchantments() != null && !secondary.getEnchantments().isEmpty()) {
            secondary.getEnchantments().entrySet().forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
        }
        return stack;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public ToolType getToolType() {
        return TOOL_TYPE;
    }

    public Set<ToolType> getToolTypes() {
        return TOOL_TYPES;
    }

    public List<ITextComponent> getTooltip() {
        return tooltip;
    }

    public SoundEvent getUseSound() {
        return useSound;
    }

    public boolean isPowered() {
        return powered;
    }

    public long getBaseMaxEnergy() {
        return baseMaxEnergy;
    }

    public int[] getEnergyTiers() {
        return energyTiers;
    }

    public Class<? extends IAntimatterTool> getToolClass() {
        return toolClass;
    }

    public boolean getMultiBlockBreakability() {
        return multiBlockBreakability;
    }

    public boolean isAutogenerated() {
        return autogenerate;
    }

    public int getMultiBlockBreakColumn() {
        return multiBlockColumn;
    }

    public int getMultiBlockBreakRow() {
        return multiBlockRow;
    }

    public int getMultiBlockBreakDepth() {
        return multiBlockDepth;
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

    public int getUseDurability() {
        return useDurability;
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

    public Tag<Item> getTag() {
        return tag;
    }

    public UseAction getUseAction() {
        return useAction;
    }

    @Nullable
    public IMaterialTag getPrimaryMaterialRequirement() {
        return primaryMaterialRequirement;
    }

    @Nullable
    public IMaterialTag getSecondaryMaterialRequirement() {
        return secondaryMaterialRequirement;
    }

    public Set<Block> getEffectiveBlocks() {
        return EFFECTIVE_BLOCKS;
    }

    public Set<net.minecraft.block.material.Material> getEffectiveMaterials() {
        return EFFECTIVE_MATERIALS;
    }
}
