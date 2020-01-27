package muramasa.antimatter.tools;

import com.google.common.collect.Sets;
import muramasa.antimatter.items.MaterialTool;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.SoundType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//TODO refactor to class? yeah...
//TODO maybe extend forge ToolType?
public enum AntimatterToolType {

    SWORD("", "craftingToolSword", null, Sets.newHashSet("sword"), null, false, 0, 4.0f, -2.4f, 1.0f, 1.0f, 200, 100, 100),
    PICKAXE("", "craftingToolPickaxe", null, Sets.newHashSet("pickaxe"), null, false, 0, 1.5f, -2.8f, 1.0f, 1.0f, 50, 200, 100),
    SHOVEL("", "craftingToolShovel", null, Sets.newHashSet("shovel"), null, false, 0, 1.5f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    AXE("", "craftingToolAxe", ToolAxe.class, Sets.newHashSet("axe"), null, false, 0, 3.0f, -3.0f, 2.0f, 1.0f, 50, 200, 100),
    HOE("", "craftingToolHoe", null, Sets.newHashSet("hoe"), null, false, 0, 1.75f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    SAW("", "craftingToolSaw", null, Sets.newHashSet("saw"), null, false, 0, 1.75f, -3.0f, 1.0f, 1.0f, 50, 200, 200),
    HAMMER("", "craftingToolForgeHammer", null, Sets.newHashSet("hammer"), SoundType.HAMMER, false, 0, 3.0f, -3.0f, 0.75f, 1.0f, 50, 200, 400),
    WRENCH("", "craftingToolWrench", null, Sets.newHashSet("wrench"), SoundType.WRENCH, false, 0, 3.0f, -2.4f, 1.0f, 1.0f, 50, 200, 800),
    FILE("", "craftingToolFile", null, Sets.newHashSet("file"), null, false, 0, 1.5f, -2.4f, 1.0f, 1.0f, 50, 200, 400),
    SCREWDRIVER("Adjusts Covers and Machines", "craftingToolScrewdriver", null, Sets.newHashSet("screwdriver"), SoundType.WRENCH, false, 0, 1.5f, -2.4f, 1.0f, 1.0f, 200, 200, 400),
    CROWBAR("Removes Covers", "craftingToolCrowbar", null, Sets.newHashSet("crowbar"), SoundType.BREAK, false, 0, 2.0f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    MORTAR("Grinds Ingots into Dust", "craftingToolMortar", null, Sets.newHashSet("mortar"), null, false, 0, 2.0f, -2.4f, 1.0f, 1.0f, 50, 200, 400),
    WIRE_CUTTER("", "craftingToolWireCutter", null, Sets.newHashSet("wire_cutter"), null, false, 0, 1.25f, -2.4f, 1.0f, 1.0f, 100, 200, 400),
    SCOOP("Harvests Bees from Hives", "craftingToolScoop", null, Sets.newHashSet("scoop"), null, false, 0, 1.0f, -3.0f, 1.0f, 1.0f, 200, 200, 200), //TODO: Forestry
    BRANCH_CUTTER("", "craftingToolBranchCutter", null, Sets.newHashSet("branch_cutter"), null, false, 0, 2.5f, -3.0f, 0.25f, 0.25f, 100, 200, 800), //TODO: Merge functionality with axe?
    UNIVERSAL_SPADE("", "craftingToolUniversalSpade", null, Sets.newHashSet("shovel"), null, false, 0, 3.0f, -3.0f, 0.75f, 1.0f, 50, 100, 400), //TODO: Universal tool? Rename?
    KNIFE("", "craftingToolKnife", null, Sets.newHashSet("knife", "sword"), null, false, 0, 2.0f, -2.4f, 0.5f, 1.0f, 100, 200, 100),
    SCYTHE("", "craftingToolScythe", null, Sets.newHashSet("scythe"), null, false, 0, 3.0f, -3.0f, 1.0f, 4.0f, 100, 200, 800), //TODO: Sort of a dupe with BRANCH_CUTTER
    PLUNGER("", "craftingToolPlunger", ToolPlunger.class, Sets.newHashSet("plunger"), null, false, 0, 1.25f, -3.0f, 1.0f, 0.25f, 100, 200, 800),
    DRILL("", "craftingToolDrill", null, Sets.newHashSet("drill", "pickaxe"), SoundType.DRILL, true, 1, 3.0f, -2.8f, 9.0f, 4.0f, 800, 3200, 12800),
    CHAINSAW("", "craftingToolChainsaw", ToolChainsaw.class, Sets.newHashSet("axe", "saw", "sword"), null, true, 1, 4.0f, -2.4f, 6.0f, 4.0f, 800, 3200, 12800),
    WRENCH_P("", "craftingToolWrench", null, Sets.newHashSet("wrench"), SoundType.WRENCH, true, 1, 2.0f, -2.4f, 4.0f, 4.0f, 800, 3200, 12800),
    JACKHAMMER("", "craftingToolJackhammer", ToolJackhammer.class, Sets.newHashSet("jackhammer", "pickaxe"), null, true, 1, 3.0f, -3.0f, 0.25f, 2.0f, 400, 800, 3200),
    SCREWDRIVER_P("", "craftingToolScrewdriver", null, Sets.newHashSet("screwdriver"), SoundType.WRENCH, true, 0, 1.0f, -2.4f, 1.0f, 1.0f, 100, 200, 200),
    BUZZSAW("", "craftingToolBuzzsaw", null, Sets.newHashSet("saw", "buzzsaw"), null, true, 0, 1.0f, -3.0f, 1.0f, 1.0f, 100, 300, 100),
    TURBINE("", "craftingToolTurbine", null, Sets.newHashSet("turbine"), null, false, 0, 3.0f, -3.0f, 4.0f, 4.0f, 100, 200, 800);

    public static AntimatterToolType[] VALUES;

    static {
        VALUES = values();
    }

    private String tooltip, oreDict;
    private Class toolClass;
    private Set<String> toolClasses;
    private SoundType useSound;
    private boolean powered;
    private int baseQuality, damageMining, damageEntity, damageCrafting;
    private float baseAttackDamage, baseAttackSpeed, miningSpeedMulti, duraMulti;

    AntimatterToolType(String tooltip, String oreDict, Class toolClass, Set<String> toolClasses, SoundType useSound, boolean powered, int baseQuality, float baseAttackDamage, float baseAttackSpeed, float miningSpeedMulti, float duraMulti, int damageMining, int damageEntity, int damageCrafting) {
        this.tooltip = tooltip;
        this.oreDict = oreDict;
        this.toolClass = toolClass;
        this.toolClasses = toolClasses;
        this.useSound = useSound;
        this.powered = powered;
        this.baseQuality = baseQuality;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.miningSpeedMulti = miningSpeedMulti;
        this.duraMulti = duraMulti;
        this.damageMining = damageMining;
        this.damageEntity = damageEntity;
        this.damageCrafting = damageCrafting;
    }

    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getName());
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getOreDict() {
        return oreDict;
    }

    public SoundType getUseSound() {
        return useSound;
    }

    public MaterialTool instantiate() {
        if (toolClass != null) {
            try {
                return (MaterialTool) toolClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new MaterialTool(this);
    }

    public boolean isPowered() {
        return powered;
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

    public float getMiningSpeedMulti() {
        return miningSpeedMulti;
    }

    public float getDurabilityMulti() {
        return duraMulti;
    }

    public int getDamageMining() {
        return damageMining;
    }

    public int getDamageEntity() {
        return damageEntity;
    }

    public int getDamageCrafting() {
        return damageCrafting;
    }

    public Set<String> getToolClass() {
        return toolClasses;
    }

    public void playUseSound(World world, BlockPos pos) {
        if (useSound != null) useSound.play(world, pos);
    }

    public ItemStack get() {
        return ItemStack.EMPTY;
        //return get(null, null);
    }

    public ItemStack get(Material primary) {
        return ItemStack.EMPTY;
        //return get(primary, primary.getHandleMaterial());
    }

    public ItemStack get(Material primary, Material secondary) {
        //TODO
        //return AntimatterAPI.get(MaterialTool.class, getName()).get(primary, secondary);
        return ItemStack.EMPTY;
    }

    public Texture[] getTextures() {
        List<Texture> textures = new ArrayList<>();
        textures.add(new Texture("item/tool/" + getName()));
        //TODO better solution for this
        if (this == SCREWDRIVER_P || this == BUZZSAW) {
            textures.add(new Texture("item/tool/overlay/" + getName() + "_1"));
            textures.add(new Texture("item/tool/overlay/" + getName() + "_2"));
        } else {
            textures.add(new Texture("item/tool/overlay/" + getName()));
        }
        return textures.toArray(new Texture[0]);
    }

    @Nullable
    public static AntimatterToolType get(ItemStack stack) {
        return stack.getItem() instanceof MaterialTool ? ((MaterialTool) stack.getItem()).getType() : null;
    }

    public static boolean hasBowAnimation(ItemStack stack) {
        AntimatterToolType type = get(stack);
        return type == DRILL;
    }

    public static boolean isWrench(ItemStack stack) {
        AntimatterToolType type = get(stack);
        return type == WRENCH || type == WRENCH_P;
    }

    public static boolean isCrowbar(ItemStack stack) {
        AntimatterToolType type = get(stack);
        return type == CROWBAR;
    }

    public static boolean isScrewdriver(ItemStack stack) {
        AntimatterToolType type = get(stack);
        return type == SCREWDRIVER || type == SCREWDRIVER_P;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return isWrench(stack) || isCrowbar(stack) || isScrewdriver(stack);
    }
}
