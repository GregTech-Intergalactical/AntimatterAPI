package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AntimatterToolType implements IAntimatterObject {

    private final String domain, id;
    private final ToolType TOOL_TYPE;
    private final Set<ToolType> TOOL_TYPES = new HashSet<>();
    private final Set<Block> EFFECTIVE_BLOCKS = new HashSet<>();
    private final Set<net.minecraft.block.material.Material> EFFECTIVE_MATERIALS = new HashSet<>();
    private List<ITextComponent> tooltip = new ArrayList<>();
    @Nullable private SoundEvent useSound;
    private boolean powered, repairable, blockBreakability, autogenerate;
    private long baseMaxEnergy;
    private int[] energyTiers;
    private int baseQuality, useDurability, attackDurability, craftingDurability, overlayLayers;
    private float baseAttackDamage, baseAttackSpeed;
    private ItemGroup itemGroup;
    private Tag<Item> tag; // Set?
    private UseAction useAction;
    @Nullable private IMaterialTag primaryMaterialRequirement, secondaryMaterialRequirement;
    private Class<? extends IAntimatterTool> toolClass;
    private Object2ObjectOpenHashMap<String, IBehaviour<MaterialTool>> behaviours = new Object2ObjectOpenHashMap<>();

    /**
     * Instantiates a AntimatterToolType with its basic values
     * @param domain             unique identifier provided by the mod
     * @param id                 unique identifier
     * @param useDurability      durability that is lost during an 'use' or 'blockDestroyed' operation
     * @param attackDurability   durability that is lost on 'hitEntity'
     * @param craftingDurability durability that is lost on 'getContainerItem' (when item is crafted as well as this tool is in the crafting matrix)
     * @param baseAttackDamage   base attack damage that would be applied to the item's attributes
     * @param baseAttackSpeed    base attack speed that would be applied to the item's attributes
     * @return a brand new AntimatterToolType for enjoyment
     */
    public AntimatterToolType(String domain, String id, int useDurability, int attackDurability, int craftingDurability, float baseAttackDamage, float baseAttackSpeed) {
        if (domain.isEmpty()) Utils.onInvalidData("AntimatterToolType registered with no domain name!");
        this.domain = domain;
        if (id.isEmpty()) Utils.onInvalidData("AntimatterToolType registered with an empty ID!");
        this.id = id;
        if (useDurability < 0) Utils.onInvalidData(id + " cannot have a negative use durability loss!");
        if (attackDurability < 0) Utils.onInvalidData(id + " cannot have a negative attack durability loss!");
        if (craftingDurability < 0) Utils.onInvalidData(id + " cannot have a negative crafting durability loss!");
        this.useSound = null;
        this.repairable = true;
        this.blockBreakability = true;
        this.autogenerate = true;
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
        if (toolClass.equals(MaterialTool.class) || toolClass.equals(MaterialSword.class)) {
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
     * Instantiates a MaterialTool::isPowered with AntimatterToolTier
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
            poweredTools.add(new MaterialTool(domain, this, tier, properties, primary, secondary, energyTier));
        }
        return poweredTools;
    }

    /**
     * Instantiates a MaterialTool with AntimatterToolTier
     * @param domain    namespace
     * @param primary   must not be null
     * @param secondary can be null
     * @return a brand new MaterialTool for enjoyment
     */
    public MaterialTool instantiate(String domain, Material primary, @Nullable Material secondary) {
        AntimatterItemTier tier = new AntimatterItemTier(this, primary, secondary);
        return new MaterialTool(domain, this, tier, prepareInstantiation(domain, tier), primary, secondary);
    }

    private Item.Properties prepareInstantiation(String domain, @Nonnull IItemTier tier) {
        if (domain.isEmpty()) Utils.onInvalidData("An AntimatterToolType was instantiated with an empty domain name!");
        Item.Properties properties = new Item.Properties().group(itemGroup);
        if (!repairable) properties.setNoRepair();
        if (!TOOL_TYPES.isEmpty()) TOOL_TYPES.forEach(t -> properties.addToolType(t, tier.getHarvestLevel()));
        return properties;
    }

    /** SETTERS **/

    public AntimatterToolType setToolTip(ITextComponent... tooltip) {
        this.tooltip.addAll(Arrays.asList(tooltip));
        return this;
    }

    public AntimatterToolType setInheritTag(AntimatterToolType toolType) {
        this.tag = toolType.getTag();
        return this;
    }

    public AntimatterToolType setTag(ResourceLocation loc) {
        this.tag = Utils.getItemTag(loc);
        return this;
    }

    public AntimatterToolType setPowered(long baseMaxEnergy, int... energyTiers) {
        this.powered = true;
        this.baseMaxEnergy = baseMaxEnergy;
        this.energyTiers = energyTiers;
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

    public void addBehaviour(IBehaviour<MaterialTool>... behaviours) {
        Arrays.stream(behaviours).forEach(b -> this.behaviours.put(b.getId(), b));
    }

    public IBehaviour<MaterialTool> getBehaviour(String id) {
        return behaviours.get(id);
    }

    public void removeBehaviour(String... ids) {
        Arrays.stream(ids).forEach(s -> behaviours.remove(s));
    }

    /** GETTERS **/

    /**
     * @param primary   material
     * @param secondary material, it is nullable
     * @return Item variant of an AntimatterToolType
     */
    public Item get(Material primary, Material secondary) {
        if (overlayLayers == 0 && secondary != null) {
            Utils.onInvalidData("GET ERROR - SECONDARY MATERIAL SHOULD BE NULL: T(" + id + ") M(" + secondary.getId() + ")");
        }
        if (primary.equals(secondary)) {
            Utils.onInvalidData("GET ERROR - PRIMARY AND SECONDARY MATERIALS SHOULD BE DIFFERENT: T(" + id + ") M(" + primary.getId() + ") AND M(" + secondary.getId() + ")");
        }
        if (secondary != null && (secondaryMaterialRequirement != null && !secondary.has(secondaryMaterialRequirement))) {
            Utils.onInvalidData("GET ERROR - SECONDARY MATERIAL REQUIREMENT MISMATCH: T(" + id + ") M(" + secondary.getId() + ")");
        }
        IAntimatterTool item = AntimatterAPI.get(IAntimatterTool.class, primary.getId() + "_" + (secondary == null ? "" : secondary.getId() + "_") + id);
        if (item == null) {
            Utils.onInvalidData("GET ERROR - TOOL ITEM NULL: T(" + id + ") M(" + primary.getId() + ") AND M(" + (secondary == null ? "" : secondary.getId()) + ")");
        }
        return item != null ? item.asItem() : Items.AIR;
    }

    /**
     * @param primary   material
     * @param secondary material, it is nullable
     * @return ItemStack variant of an AntimatterToolType, it will come with an enchantment if the primary and/or secondary has native enchants
     */
    public ItemStack get(Material primary, Material secondary, int count) {
        Item item = get(primary, secondary);
        if (item == null) {
            Utils.onInvalidData("GET ERROR - TOOL ITEM NULL: T(" + id + ") M(" + primary.getId() + ") AND M(" + (secondary == null ? "" : secondary.getId()) + ")");
        }
        ItemStack stack = new ItemStack(item, count);
        if (stack.isEmpty()) {
            Utils.onInvalidData("GET ERROR - TOOL STACK EMPTY: T(" + id + ") M(" + primary.getId() + ") AND M(" + (secondary == null ? "" : secondary.getId()) + ")");
        }
        // TODO: DISABLE UNBREAKING TO BE APPLIED ONTO TOOLS
        if (!primary.getEnchantments().isEmpty()) {
            primary.getEnchantments().forEach(stack::addEnchantment);
        }
        if (secondary != null && secondary.getEnchantments() != null && !secondary.getEnchantments().isEmpty()) {
            secondary.getEnchantments().forEach(stack::addEnchantment);
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

    public boolean isAutogenerated() {
        return autogenerate;
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

    public Object2ObjectOpenHashMap<String, IBehaviour<MaterialTool>> getBehaviours() {
        return behaviours;
    }
}
