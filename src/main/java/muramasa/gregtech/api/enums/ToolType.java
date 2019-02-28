package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.api.util.Sounds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

public enum ToolType implements IStringSerializable {

    SWORD("Sword", "", null, 0, 4.0f, 1.0f, 1.0f, 200, 100, 100),
    PICKAXE("Pickaxe" , "", null, 0, 1.5f, 1.0f, 1.0f, 50, 200, 100),
    SHOVEL("Shovel", "", null, 0, 1.5f, 1.0f, 1.0f, 50, 200, 100),
    AXE("Axe", "", null, 0, 3.0f, 2.0f, 1.0f, 50, 200, 100),
    HOE("Hoe", "", null, 0, 1.75f, 1.0f, 1.0f, 50, 200, 100),
    SAW("Saw", "", null, 0, 1.75f, 1.0f, 1.0f, 50, 200, 200),
    HAMMER("Hammer", "", null, 0, 3.0f, 0.75f, 1.0f, 50, 200, 400),
    WRENCH("Wrench", "", null, 0, 3.0f, 1.0f, 1.0f, 50, 200, 800),
    FILE("File", "", null, 0, 1.5f, 1.0f, 1.0f, 50, 200, 400),
    SCREWDRIVER("Screwdriver", "Adjusts Covers and Machines", null, 0, 1.5f, 1.0f, 1.0f, 200, 200, 400),
    CROWBAR("Crowbar", "Removes Covers", null, 0, 2.0f, 1.0f, 1.0f, 50, 200, 100),
    MORTAR("Mortar", "Grinds Ingots into Dust", null, 0, 2.0f, 1.0f, 1.0f, 50, 200, 400),
    WIRE_CUTTER("Wire Cutter", "", null, 0, 1.25f, 1.0f, 1.0f, 100, 200, 400),
    SCOOP("Scoop", "Harvests Bees from Hives", null, 0, 1.0f, 1.0f, 1.0f, 200, 200, 200),
    BRANCH_CUTTER("Branch Cutter", "", null, 0, 2.5f, 0.25f, 0.25f, 100, 200, 800),
    UNIVERSAL_SPADE("Universal Spade", "", null, 0, 3.0f, 0.75f, 1.0f, 50, 100, 400),
    KNIFE("Knife", "", null, 0, 2.0f, 0.5f, 1.0f, 100, 200, 100),
    SCYTHE("Scythe", "", null, 0, 3.0f, 1.0f, 4.0f, 100, 200, 800),
    PLUNGER("Plunger", "", null, 0, 1.25f, 1.0f, 0.25f, 100, 200, 800),
    DRILL("Electric Drill", "", Sounds.DRILL, 1, 3.0f, 9.0f, 4.0f, 800, 3200, 12800),
    CHAINSAW("Electric Chainsaw", "", null, 1, 4.0f, 4.0f, 4.0f, 800, 3200, 12800),
    WRENCH_P("Electric Wrench", "", null, 1, 2.0f, 4.0f, 4.0f, 800, 3200, 12800),
    JACKHAMMER("Electric Jackhammer", "", null, 1, 3.0f, 12.0f, 2.0f, 400, 800, 3200),
    SCREWDRIVER_P("Electric Screwdriver", "", null, 0, 1.0f, 1.0f, 1.0f, 100, 200, 200),
    BUZZSAW("Electric Buzzsaw", "", null, 0, 1.0f, 1.0f, 1.0f, 100, 300, 100),
    TURBINE("Turbine Rotor", "", null, 0, 3.0f, 4.0f, 4.0f, 100, 200, 800);

    private String displayName, tooltip;
    private Sounds useSound;
    private int baseQuality, damageMining, damageEntity, damageCrafting;
    private float baseDamage, speedMulti, duraMulti;

    ToolType(String displayName, String tooltip, Sounds useSound, int baseQuality, float baseDamage, float speedMulti, float duraMulti, int damageMining, int damageEntity, int damageCrafting) {
        this.displayName = displayName;
        this.tooltip = tooltip;
        this.useSound = useSound;
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

    public boolean isItemEqual(ItemStack stack) {
        return stack.getItem() instanceof MetaTool && stack.getMetadata() == ordinal();
    }

    public static ToolType get(ItemStack stack) {
        return values()[stack.getMetadata()];
    }

    public static void playDigSound(World world, BlockPos pos, ItemStack stack) {
        ToolType type = get(stack);
        if (type.useSound != null) {
            type.useSound.play(world, pos);
        }
    }

    public static boolean hasBowAnimation(ItemStack stack) {
        return stack.getItem() instanceof MetaTool && (stack.getMetadata() == DRILL.ordinal());
    }

    public static boolean isWrench(ItemStack stack) {
        return stack.getItem() instanceof MetaTool && (stack.getMetadata() == WRENCH.ordinal() || stack.getMetadata() == WRENCH_P.ordinal());
    }

    public static boolean isCrowbar(ItemStack stack) {
        return stack.getItem() instanceof MetaTool && (stack.getMetadata() == CROWBAR.ordinal());
    }

    public static boolean isScrewdriver(ItemStack stack) {
        return stack.getItem() instanceof MetaTool && (stack.getMetadata() == SCREWDRIVER.ordinal() || stack.getMetadata() == SCREWDRIVER_P.ordinal());
    }

    public static boolean isPowered(ItemStack stack) {
        return stack.getItem() instanceof MetaTool && (stack.getMetadata() == WRENCH_P.ordinal() || stack.getMetadata() == SCREWDRIVER_P.ordinal() || stack.getMetadata() == DRILL.ordinal());
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return isWrench(stack) || isCrowbar(stack) || isScrewdriver(stack);
    }
}
