package muramasa.gtu.api.tools;

import com.google.common.collect.Sets;
import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.util.Sounds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Set;

public enum ToolType implements IGregTechObject {

    SWORD("Sword", "", null, Sets.newHashSet("sword"), null, false, 0, 4.0f, -2.4f, 1.0f, 1.0f, 200, 100, 100),
    PICKAXE("Pickaxe" , "", null, Sets.newHashSet("pickaxe"), null, false, 0, 1.5f, -2.8f, 1.0f, 1.0f, 50, 200, 100),
    SHOVEL("Shovel", "", null, Sets.newHashSet("shovel"), null, false, 0, 1.5f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    AXE("Axe", "", ToolAxe.class, Sets.newHashSet("axe"), null, false, 0, 3.0f, -3.0f, 2.0f, 1.0f, 50, 200, 100),
    HOE("Hoe", "", null, Sets.newHashSet("hoe"), null, false, 0, 1.75f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    SAW("Saw", "", null, Sets.newHashSet("saw"), null, false, 0, 1.75f, -3.0f, 1.0f, 1.0f, 50, 200, 200),
    HAMMER("Hammer", "", null, Sets.newHashSet("hammer"), Sounds.HAMMER, false, 0, 3.0f, -3.0f, 0.75f, 1.0f, 50, 200, 400),
    WRENCH("Wrench", "", null, Sets.newHashSet("wrench"), Sounds.WRENCH, false, 0, 3.0f, -2.4f, 1.0f, 1.0f, 50, 200, 800),
    FILE("File", "", null, Sets.newHashSet("file"), null, false, 0, 1.5f, -2.4f, 1.0f, 1.0f, 50, 200, 400),
    SCREWDRIVER("Screwdriver", "Adjusts Covers and Machines", null, Sets.newHashSet("screwdriver"), Sounds.WRENCH, false, 0, 1.5f, -2.4f, 1.0f, 1.0f, 200, 200, 400),
    CROWBAR("Crowbar", "Removes Covers", null, Sets.newHashSet("crowbar"), Sounds.BREAK, false, 0, 2.0f, -3.0f, 1.0f, 1.0f, 50, 200, 100),
    MORTAR("Mortar", "Grinds Ingots into Dust", null, Sets.newHashSet("mortar"), null, false, 0, 2.0f, -2.4f, 1.0f, 1.0f, 50, 200, 400),
    WIRE_CUTTER("Wire Cutter", "", null, Sets.newHashSet("wire_cutter"), null, false, 0, 1.25f, -2.4f, 1.0f, 1.0f, 100, 200, 400),
    SCOOP("Scoop", "Harvests Bees from Hives", null, Sets.newHashSet("scoop"), null, false, 0, 1.0f, -3.0f, 1.0f, 1.0f, 200, 200, 200), //TODO: Forestry
    BRANCH_CUTTER("Branch Cutter", "", null, Sets.newHashSet("branch_cutter"), null, false, 0, 2.5f, -3.0f, 0.25f, 0.25f, 100, 200, 800), //TODO: Merge functionality with axe?
    UNIVERSAL_SPADE("Universal Spade", "", null, Sets.newHashSet("shovel"), null, false, 0, 3.0f, -3.0f, 0.75f, 1.0f, 50, 100, 400), //TODO: Universal tool? Rename?
    KNIFE("Knife", "", null, Sets.newHashSet("knife", "sword"), null, false, 0, 2.0f, -2.4f, 0.5f, 1.0f, 100, 200, 100),
    SCYTHE("Scythe", "", null, Sets.newHashSet("scythe"), null, false, 0, 3.0f, -3.0f, 1.0f, 4.0f, 100, 200, 800), //TODO: Sort of a dupe with BRANCH_CUTTER
    PLUNGER("Plunger", "", null, Sets.newHashSet("plunger"), null, false, 0, 1.25f, -3.0f, 1.0f, 0.25f, 100, 200, 800),
    DRILL("Electric Drill", "", null, Sets.newHashSet("drill", "pickaxe"), Sounds.DRILL, true, 1, 3.0f, -2.8f, 9.0f, 4.0f, 800, 3200, 12800),
    CHAINSAW("Electric Chainsaw", "", ToolChainsaw.class, Sets.newHashSet("axe", "saw", "sword"), null, true, 1, 4.0f, -2.4f, 6.0f, 4.0f, 800, 3200, 12800),
    WRENCH_P("Electric Wrench", "", null, Sets.newHashSet("wrench"), Sounds.WRENCH, true, 1, 2.0f, -2.4f, 4.0f, 4.0f, 800, 3200, 12800),
    JACKHAMMER("Electric Jackhammer", "", ToolJackhammer.class, Sets.newHashSet("jackhammer", "pickaxe"), null, true, 1, 3.0f, -3.0f, 0.25f, 2.0f, 400, 800, 3200),
    SCREWDRIVER_P("Electric Screwdriver", "", null, Sets.newHashSet("screwdriver"), Sounds.WRENCH, true, 0, 1.0f, -2.4f, 1.0f, 1.0f, 100, 200, 200),
    BUZZSAW("Electric Buzzsaw", "", null, Sets.newHashSet("saw", "buzzsaw"), null, true, 0, 1.0f, -3.0f, 1.0f, 1.0f, 100, 300, 100),
    TURBINE("Turbine Rotor", "", null, Sets.newHashSet("turbine"), null, false, 0, 3.0f, -3.0f, 4.0f, 4.0f, 100, 200, 800);

    private String displayName, tooltip;
    private Class toolClass;
    private Set<String> toolClasses;
    private Sounds useSound;
    private boolean powered;
    private int baseQuality, damageMining, damageEntity, damageCrafting;
    private float baseAttackDamage, baseAttackSpeed, miningSpeedMulti, duraMulti;

    ToolType(String displayName, String tooltip, Class toolClass, Set<String> toolClasses, Sounds useSound, boolean powered, int baseQuality, float baseAttackDamage, float baseAttackSpeed, float miningSpeedMulti, float duraMulti, int damageMining, int damageEntity, int damageCrafting) {
        this.displayName = displayName;
        this.tooltip = tooltip;
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

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public MaterialTool getInstance() {
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
        return get(null, null);
    }

    public ItemStack get(Material primary) {
        return get(primary, primary.getHandleMaterial());
    }

    public ItemStack get(Material primary, Material secondary) {
        return GregTechRegistry.getMaterialTool(this).get(primary, secondary);
    }

    @Nullable
    public static ToolType get(ItemStack stack) {
        return stack.getItem() instanceof MaterialTool ? ((MaterialTool) stack.getItem()).getType() : null;
    }

    public static boolean hasBowAnimation(ItemStack stack) {
        ToolType type = get(stack);
        return type == DRILL;
    }

    public static boolean isWrench(ItemStack stack) {
        ToolType type = get(stack);
        return type == WRENCH || type == WRENCH_P;
    }

    public static boolean isCrowbar(ItemStack stack) {
        ToolType type = get(stack);
        return type == CROWBAR;
    }

    public static boolean isScrewdriver(ItemStack stack) {
        ToolType type = get(stack);
        return type == SCREWDRIVER || type == SCREWDRIVER_P;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return isWrench(stack) || isCrowbar(stack) || isScrewdriver(stack);
    }
}
