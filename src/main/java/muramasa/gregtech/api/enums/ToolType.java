package muramasa.gregtech.api.enums;

import com.google.common.collect.Sets;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.items.MaterialTool;
import muramasa.gregtech.api.util.Sounds;
import muramasa.gregtech.loaders.GregTechRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Set;

public enum ToolType implements IStringSerializable {

    SWORD("Sword", "", Sets.newHashSet("sword"), null, false, 0, 4.0f, 1.0f, 1.0f, 200, 100, 100),
    PICKAXE("Pickaxe" , "", Sets.newHashSet("pickaxe"), null, false, 0, 1.5f, 1.0f, 1.0f, 50, 200, 100),
    SHOVEL("Shovel", "", Sets.newHashSet("shovel"), null, false, 0, 1.5f, 1.0f, 1.0f, 50, 200, 100),
    AXE("Axe", "", Sets.newHashSet("axe"), null, false, 0, 3.0f, 2.0f, 1.0f, 50, 200, 100),
    HOE("Hoe", "", Sets.newHashSet("hoe"), null, false, 0, 1.75f, 1.0f, 1.0f, 50, 200, 100),
    SAW("Saw", "", Sets.newHashSet("saw"), null, false, 0, 1.75f, 1.0f, 1.0f, 50, 200, 200),
    HAMMER("Hammer", "", Sets.newHashSet("hammer"), Sounds.HAMMER, false, 0, 3.0f, 0.75f, 1.0f, 50, 200, 400),
    WRENCH("Wrench", "", Sets.newHashSet("wrench"), Sounds.WRENCH, false, 0, 3.0f, 1.0f, 1.0f, 50, 200, 800),
    FILE("File", "", Sets.newHashSet("file"), null, false, 0, 1.5f, 1.0f, 1.0f, 50, 200, 400),
    SCREWDRIVER("Screwdriver", "Adjusts Covers and Machines", Sets.newHashSet("screwdriver"), Sounds.WRENCH, false, 0, 1.5f, 1.0f, 1.0f, 200, 200, 400),
    CROWBAR("Crowbar", "Removes Covers", Sets.newHashSet("crowbar"), Sounds.BREAK, false, 0, 2.0f, 1.0f, 1.0f, 50, 200, 100),
    MORTAR("Mortar", "Grinds Ingots into Dust", Sets.newHashSet("mortar"), null, false, 0, 2.0f, 1.0f, 1.0f, 50, 200, 400),
    WIRE_CUTTER("Wire Cutter", "", Sets.newHashSet("wire_cutter"), null, false, 0, 1.25f, 1.0f, 1.0f, 100, 200, 400),
    SCOOP("Scoop", "Harvests Bees from Hives", Sets.newHashSet("scoop"), null, false, 0, 1.0f, 1.0f, 1.0f, 200, 200, 200),
    BRANCH_CUTTER("Branch Cutter", "", Sets.newHashSet("branch_cutter"), null, false, 0, 2.5f, 0.25f, 0.25f, 100, 200, 800),
    UNIVERSAL_SPADE("Universal Spade", "", Sets.newHashSet("shovel"), null, false, 0, 3.0f, 0.75f, 1.0f, 50, 100, 400),
    KNIFE("Knife", "", Sets.newHashSet("knife", "sword"), null, false, 0, 2.0f, 0.5f, 1.0f, 100, 200, 100),
    SCYTHE("Scythe", "", Sets.newHashSet("scythe"), null, false, 0, 3.0f, 1.0f, 4.0f, 100, 200, 800),
    PLUNGER("Plunger", "", Sets.newHashSet("plunger"), null, false, 0, 1.25f, 1.0f, 0.25f, 100, 200, 800),
    DRILL("Electric Drill", "", Sets.newHashSet("drill", "pickaxe"), Sounds.DRILL, true, 1, 3.0f, 9.0f, 4.0f, 800, 3200, 12800),
    CHAINSAW("Electric Chainsaw", "", Sets.newHashSet("axe", "sword"), null, true, 1, 4.0f, 4.0f, 4.0f, 800, 3200, 12800),
    //TODO merge powered versions of tools (add isPowered())
    WRENCH_P("Electric Wrench", "", Sets.newHashSet("wrench"), null, true, 1, 2.0f, 4.0f, 4.0f, 800, 3200, 12800),
    JACKHAMMER("Electric Jackhammer", "", Sets.newHashSet("jackhammer"), null, true, 1, 3.0f, 12.0f, 2.0f, 400, 800, 3200),
    SCREWDRIVER_P("Electric Screwdriver", "", Sets.newHashSet("screwdriver"), null, true, 0, 1.0f, 1.0f, 1.0f, 100, 200, 200),
    BUZZSAW("Electric Buzzsaw", "", Sets.newHashSet("buzzsaw", "saw"), null, true, 0, 1.0f, 1.0f, 1.0f, 100, 300, 100),
    TURBINE("Turbine Rotor", "", Sets.newHashSet("turbine"), null, false, 0, 3.0f, 4.0f, 4.0f, 100, 200, 800);

    private String displayName, tooltip;
    private Set<String> toolClasses;
    private Sounds useSound;
    private boolean powered;
    private int baseQuality, damageMining, damageEntity, damageCrafting;
    private float baseDamage, speedMulti, duraMulti;

    ToolType(String displayName, String tooltip, Set<String> toolClasses, Sounds useSound, boolean powered, int baseQuality, float baseDamage, float speedMulti, float duraMulti, int damageMining, int damageEntity, int damageCrafting) {
        this.displayName = displayName;
        this.tooltip = tooltip;
        this.toolClasses = toolClasses;
        this.useSound = useSound;
        this.powered = powered;
        this.baseQuality = baseQuality;
        this.baseDamage = baseDamage;
        this.speedMulti = speedMulti;
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

    public Sounds getUseSound() {
        return useSound;
    }

    public boolean isPowered() {
        return powered;
    }

    public int getBaseQuality() {
        return baseQuality;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public float getSpeedMulti() {
        return speedMulti;
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

//    public boolean isItemEqual(ItemStack stack) {
//        return stack.getItem() instanceof MetaTool && stack.getMetadata() == ordinal();
//    }
//

    public static void playDigSound(World world, BlockPos pos, ItemStack stack) {
        ToolType type = get(stack);
        if (type.useSound != null) {
            type.useSound.play(world, pos);
        }
    }

    public MaterialTool getItem(Material primary) {
        return GregTechRegistry.getMaterialTool(this, primary);
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
