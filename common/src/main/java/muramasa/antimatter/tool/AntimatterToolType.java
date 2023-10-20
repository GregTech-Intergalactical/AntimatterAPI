package muramasa.antimatter.tool;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterToolType implements ISharedAntimatterObject {

    private final String domain, id;
    private TagKey<Block> TOOL_TYPE;
    private final Set<TagKey<Block>> TOOL_TYPES = new ObjectOpenHashSet<>();
    private final Set<Block> EFFECTIVE_BLOCKS = new ObjectOpenHashSet<>();
    private final Set<net.minecraft.world.level.material.Material> EFFECTIVE_MATERIALS = new ObjectOpenHashSet<>();
    private final Object2ObjectMap<String, IBehaviour<IAntimatterTool>> behaviours = new Object2ObjectOpenHashMap<>();
    private ImmutableMap<String, Function<ItemStack, ItemStack>> brokenItems = ImmutableMap.of();
    private final List<Component> tooltip = new ObjectArrayList<>();
    private boolean powered, repairable, blockBreakability, hasContainer, simple, hasSecondary;
    private long baseMaxEnergy;
    private int[] energyTiers;
    private final int useDurability, attackDurability, craftingDurability;
    private float durabilityMultiplier = 1;
    private float miningSpeedMultiplier = 1.0f;
    private int baseQuality, overlayLayers;
    private final float baseAttackDamage, baseAttackSpeed;
    private CreativeModeTab itemGroup;
    protected TagKey<Item> tag, forgeTag; // Set?
    private UseAnim useAction;
    private Class<? extends IAntimatterTool> toolClass;
    private IToolSupplier toolSupplier = null;
    @Nullable
    private SoundEvent useSound;
    @Nullable
    private IMaterialTag primaryMaterialRequirement, secondaryMaterialRequirement;
    @Nullable
    private MaterialTypeItem<?> materialTypeItem;

    /**
     * Instantiates a AntimatterToolType with its basic values
     *
     * @param domain             unique identifier provided by the mod
     * @param id                 unique identifier
     * @param useDurability      durability that is lost during an 'use' or 'blockDestroyed' operation
     * @param attackDurability   durability that is lost on 'hitEntity'
     * @param craftingDurability durability that is lost on 'getContainerItem' (when item is crafted as well as this tool is in the crafting matrix)
     * @param baseAttackDamage   base attack damage that would be applied to the item's attributes
     * @param baseAttackSpeed    base attack speed that would be applied to the item's attributes
     * @param vanillaType          if the mining type uses vanilla resource location for tool tag.
     */
    public AntimatterToolType(String domain, String id, int useDurability, int attackDurability, int craftingDurability, float baseAttackDamage, float baseAttackSpeed, boolean vanillaType) {
        if (domain.isEmpty()) Utils.onInvalidData("AntimatterToolType registered with no domain name!");
        this.domain = domain;
        if (id.isEmpty()) Utils.onInvalidData("AntimatterToolType registered with an empty ID!");
        this.id = id;
        if (useDurability < 0) Utils.onInvalidData(id + " cannot have a negative use durability value!");
        if (attackDurability < 0) Utils.onInvalidData(id + " cannot have a negative attack durability value!");
        if (craftingDurability < 0) Utils.onInvalidData(id + " cannot have a negative crafting durability value!");
        this.useSound = null;
        this.repairable = true;
        this.blockBreakability = true;
        this.hasContainer = true;
        this.baseQuality = 0;
        this.useDurability = useDurability;
        this.attackDurability = attackDurability;
        this.craftingDurability = craftingDurability;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.overlayLayers = 1;
        this.itemGroup = Ref.TAB_TOOLS;
        this.tag = TagUtils.getItemTag(new ResourceLocation(Ref.ID, id));
        this.forgeTag = TagUtils.getForgelikeItemTag("tools/".concat(id));
        this.useAction = UseAnim.NONE;
        this.toolClass = MaterialTool.class;
        this.simple = true;
        hasSecondary = true;
        if (vanillaType) {
            this.TOOL_TYPE = TagUtils.getBlockTag(new ResourceLocation("minecraft","mineable/".concat(id)));
        } else {
            this.TOOL_TYPE = TagUtils.getBlockTag(new ResourceLocation(Ref.ID, "mineable/".concat(id)));
        }
        this.TOOL_TYPES.add(this.TOOL_TYPE);
        setBrokenItems(ImmutableMap.of(id, (i) -> ItemStack.EMPTY));
    }

    public AntimatterToolType(String domain, String id, AntimatterToolType inheritType) {
        this(domain, id, inheritType.useDurability, inheritType.attackDurability, inheritType.craftingDurability, inheritType.baseAttackDamage, inheritType.baseAttackSpeed, false);
    }

    /* IAntimatterTool Instantiations */

    /**
     * Instantiates a MaterialTool with Reflection (only use this when you have done setToolClass when creating your AntimatterToolType)
     *
     * @param domain  namespace
     * @param objects an Object array that should be ordered same way as your custom ToolClass constructor
     *                (e.g. for MaterialItem: String domain, AntimatterToolType type, IItemTier tier, Item.Properties properties, Material primary, Material secondary)
     * @return a brand new custom implementation of IAntimatterTool for enjoyment
     */
    public IAntimatterTool instantiateTools(String domain, Object... objects) {
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
     * Instantiates powered MaterialTools
     */
    public List<IAntimatterTool> instantiatePoweredTools(String domain) {
        Item.Properties properties = prepareInstantiation(domain);
        return instantiatePoweredTools(domain, () -> properties);
    }

    public List<IAntimatterTool> instantiatePoweredTools(String domain, Supplier<Item.Properties> properties) {
        List<IAntimatterTool> poweredTools = new ObjectArrayList<>();
        for (int energyTier : energyTiers) {
            poweredTools.add(instantiatePoweredTool(domain, AntimatterItemTier.NULL, properties, energyTier));
        }
        return poweredTools;
    }

    /**
     * Instantiates a MaterialTool
     */
    public List<IAntimatterTool> instantiateTools(String domain) {
        return instantiateTools(domain, () -> prepareInstantiation(domain));
    }

    protected IAntimatterTool instantiatePoweredTool(String domain, AntimatterItemTier tier, Supplier<Item.Properties> properties, int energyTier) {
        if (toolSupplier != null) return toolSupplier.create(domain, this, tier, properties.get(), energyTier);
        if (toolClass == MaterialSword.class) return new MaterialSword(domain, this, tier, properties.get(), energyTier);
        return new MaterialTool(domain, this, tier, properties.get(), energyTier);
    }

    protected IAntimatterTool instantiateTool(String domain, AntimatterItemTier tier, Supplier<Item.Properties> properties) {
        if (toolSupplier != null) return toolSupplier.create(domain, this, tier, properties.get());
        if (toolClass == MaterialSword.class) return new MaterialSword(domain, this, tier, properties.get());
        return new MaterialTool(domain, this, tier, properties.get());
    }

    public List<IAntimatterTool> instantiateTools(String domain, Supplier<Item.Properties> properties) {
        List<IAntimatterTool> tools = new ArrayList<>();
        if (simple){
            MaterialTags.TOOLS.getAll().forEach((m, t) -> {
                if (primaryMaterialRequirement != null && !m.has(primaryMaterialRequirement)) return;
                if (t.toolTypes().contains(this)){
                    tools.add(instantiateTool(domain, AntimatterItemTier.getOrCreate(m, hasSecondary ? AntimatterMaterials.Wood : Material.NULL), properties));
                }
            });
        } else {
            tools.add(instantiateTool(domain, AntimatterItemTier.NULL, properties));
        }
        return tools;
    }

    protected Item.Properties prepareInstantiation(String domain) {
        if (domain.isEmpty()) Utils.onInvalidData("An AntimatterToolType was instantiated with an empty domain name!");
        return ToolUtils.getToolProperties(itemGroup, repairable);
    }

    /* SETTERS */

    public AntimatterToolType setToolTip(Component... tooltip) {
        this.tooltip.addAll(Arrays.asList(tooltip));
        return this;
    }

    public AntimatterToolType setBrokenItems(ImmutableMap<String, Function<ItemStack, ItemStack>> map) {
        this.brokenItems = map;
        return this;
    }

    public AntimatterToolType setMaterialType(MaterialTypeItem<?> materialTypeItem){
        this.materialTypeItem = materialTypeItem;
        return this;
    }

    public AntimatterToolType setTag(AntimatterToolType tag) {
        this.tag = tag.getTag();
        this.forgeTag = tag.getForgeTag();
        return this;
    }

    public AntimatterToolType setType(AntimatterToolType tag) {
        this.TOOL_TYPES.remove(this.TOOL_TYPE);
        this.TOOL_TYPE = tag.getToolType();
        this.TOOL_TYPES.add(this.TOOL_TYPE);
        return this;
    }
    public AntimatterToolType setTag(ResourceLocation loc) {
        this.tag = TagUtils.getItemTag(loc);
        this.forgeTag = TagUtils.getForgelikeItemTag("tools/" + loc.getPath());
        return this;
    }

    public AntimatterToolType setPowered(long baseMaxEnergy, int... energyTiers) {
        this.powered = true;
        this.baseMaxEnergy = baseMaxEnergy;
        this.energyTiers = energyTiers;
        this.simple = false;
        return this;
    }

    public AntimatterToolType setSimple(boolean simple){
        this.simple = simple;
        return this;
    }

    public AntimatterToolType setHasSecondary(boolean hasSecondary){
        this.hasSecondary = hasSecondary;
        return this;
    }

    public AntimatterToolType setHasContainer(boolean container) {
        this.hasContainer = container;
        return this;
    }

    public AntimatterToolType addTags(String... types) {
        if (types.length == 0)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no additional tool types even when it was explicitly called!");
        Arrays.stream(types).map(t -> TagUtils.getForgelikeBlockTag(t)).forEach(t -> this.TOOL_TYPES.add(t));
        return this;
    }

    public AntimatterToolType addEffectiveBlocks(Block... blocks) {
        if (blocks.length == 0)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no effective blocks even when it was explicitly called!");
        this.EFFECTIVE_BLOCKS.addAll(Arrays.asList(blocks));
        return this;
    }

    public AntimatterToolType addEffectiveMaterials(net.minecraft.world.level.material.Material... materials) {
        if (materials.length == 0)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no effective materials even when it was explicitly called!");
        this.EFFECTIVE_MATERIALS.addAll(Arrays.asList(materials));
        return this;
    }

    public AntimatterToolType setPrimaryRequirement(IMaterialTag tag) {
        if (tag == null)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no primary material requirement even when it was explicitly called!");
        this.primaryMaterialRequirement = tag;
        return this;
    }

    public AntimatterToolType setSecondaryRequirement(IMaterialTag tag) {
        if (tag == null)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have no secondary material requirement even when it was explicitly called!");
        this.secondaryMaterialRequirement = tag;
        return this;
    }

    public AntimatterToolType setToolClass(Class<? extends IAntimatterTool> toolClass) {
        this.toolClass = toolClass;
        return this;
    }

    public AntimatterToolType setToolSupplier(IToolSupplier toolSupplier) {
        this.toolSupplier = toolSupplier;
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
        if (quality < 0)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have negative Base Quality!");
        this.baseQuality = quality;
        return this;
    }

    public AntimatterToolType setToolSpeedMultiplier(float multiplier){
        if (multiplier < 0)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have negative Speed Multiplier!");
        this.miningSpeedMultiplier = multiplier;
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

    public AntimatterToolType setDurabilityMultiplier(float durabilityMultiplier){
        this.durabilityMultiplier = durabilityMultiplier;
        return this;
    }

    public AntimatterToolType setOverlayLayers(int layers) {
        if (layers < 0)
            Utils.onInvalidData(StringUtils.capitalize(id) + " AntimatterToolType was set to have less than 0 overlayer layers!");
        this.overlayLayers = layers;
        return this;
    }

    public AntimatterToolType setItemGroup(CreativeModeTab itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    public AntimatterToolType setUseAction(UseAnim useAction) {
        this.useAction = useAction;
        return this;
    }

    public void addBehaviour(IBehaviour<IAntimatterTool>... behaviours) {
        Arrays.stream(behaviours).forEach(b -> this.behaviours.put(b.getId(), b));
    }

    public IBehaviour<IAntimatterTool> getBehaviour(String id) {
        return behaviours.get(id);
    }

    public void removeBehaviour(String... ids) {
        Arrays.stream(ids).forEach(behaviours::remove);
    }

    /* GETTERS */

    public ItemStack getToolStack(Material primary, Material secondary) {
        String id = simple ? primary.getId() + "_" + this.id : this.id;
        return Objects.requireNonNull(AntimatterAPI.get(IAntimatterTool.class, id)).asItemStack(primary, secondary);
    }

    public ItemStack getToolStack(Material primary) {
        String id = simple ? primary.getId() + "_" + this.id : this.id;
        return Objects.requireNonNull(AntimatterAPI.get(IAntimatterTool.class, id)).asItemStack(primary, Material.NULL);
    }

    @Override
    public String getId() {
        return id;
    }

    public Set<TagKey<Block>> getActualTags() {
        return TOOL_TYPES;
    }

    public TagKey<Block> getToolType() {
        return TOOL_TYPE;
    }

    public List<Component> getTooltip() {
        return tooltip;
    }

    public ImmutableMap<String, Function<ItemStack, ItemStack>> getBrokenItems() {
        return brokenItems;
    }

    public boolean isPowered() {
        return powered;
    }

    public boolean isSimple() {
        return simple;
    }

    public boolean hasContainer() {
        return hasContainer;
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

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public float getMiningSpeedMultiplier() {
        return miningSpeedMultiplier;
    }

    public int getOverlayLayers() {
        return overlayLayers;
    }

    public CreativeModeTab getItemGroup() {
        return itemGroup;
    }

    public TagKey<Item> getTag() {
        return tag;
    }

    public TagKey<Item> getForgeTag() {
        return forgeTag;
    }

    public UseAnim getUseAction() {
        return useAction;
    }

    @Nullable
    public SoundEvent getUseSound() {
        return useSound;
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

    public Set<net.minecraft.world.level.material.Material> getEffectiveMaterials() {
        return EFFECTIVE_MATERIALS;
    }

    public Object2ObjectMap<String, IBehaviour<IAntimatterTool>> getBehaviours() {
        return behaviours;
    }

    @Nullable
    public MaterialTypeItem<?> getMaterialTypeItem() {
        return materialTypeItem;
    }

    public interface IToolSupplier{
        IAntimatterTool create(String domain, AntimatterToolType toolType, AntimatterItemTier tier, Item.Properties properties);

        default IAntimatterTool create(String domain, AntimatterToolType toolType, AntimatterItemTier tier, Item.Properties properties, int energyTier){
            return create(domain, toolType, tier, properties);
        }
    }
}
