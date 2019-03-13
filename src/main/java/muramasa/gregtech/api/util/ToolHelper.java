package muramasa.gregtech.api.util;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolHelper {

    public static NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) validateTag(stack);
        return (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_TOOL_DATA);
    }

    public static String getType(ItemStack stack) {
        return getTag(stack).getString(Ref.KEY_TOOL_DATA_TYPE);
    }

    public static void setType(ItemStack stack, String newType) {
        getTag(stack).setString(Ref.KEY_TOOL_DATA_TYPE, newType);
    }

    public static int getQuality(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_QUALITY);
    }

    public static void setQuality(ItemStack stack, int newQuality) {
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_QUALITY, newQuality);
    }

    public static Material getPrimaryMaterial(ItemStack stack) {
        return Materials.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MAT));
    }

    public static void setPrimaryMaterial(ItemStack stack, String name) {
        getTag(stack).setString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, name);
    }

    public static Material getSecondaryMaterial(ItemStack stack) {
        return Materials.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MAT));
    }

    public static void setSecondaryMaterial(ItemStack stack, String name) {
        getTag(stack).setString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, name);
    }

    public static int getEnergy(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_ENERGY);
    }

    public static void setEnergy(ItemStack stack, int newEnergy) {
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_ENERGY, newEnergy);
    }

    public static int getMaxEnergy(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    public static void setMaxEnergy(ItemStack stack, int newMaxEnergy) {
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_MAX_ENERGY, newMaxEnergy);
    }

    public static int getDurability(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_DURABILITY);
    }

    public static void setDurability(ItemStack stack, int newDurability) {
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDurability);
    }

    public static int getMaxDurability(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_MAX_DURABILITY);
    }

    public static void setMaxDurability(ItemStack stack, int newMaxDurability) {
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_MAX_DURABILITY, newMaxDurability);
    }

    public static int getAttackSpeed(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_ATTACK_SPEED);
    }

    public static void setAttackSpeed(ItemStack stack, int newAttackSpeed) {
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_ATTACK_SPEED, newAttackSpeed);
    }

    public static float getAttackDamage(ItemStack stack) {
        return getTag(stack).getFloat(Ref.KEY_TOOL_DATA_ATTACK_DAMAGE);
    }

    public static void setAttackDamage(ItemStack stack, float newDamage) {
        getTag(stack).setFloat(Ref.KEY_TOOL_DATA_ATTACK_DAMAGE, newDamage);
    }

    public static int getMiningSpeed(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_MINING_SPEED);
    }

    public static void setMiningSpeed(ItemStack stack, float newMiningSpeed) {
        getTag(stack).setFloat(Ref.KEY_TOOL_DATA_MINING_SPEED, newMiningSpeed);
    }

    public static void damage(ItemStack stack, int damage) {
        setDurability(stack, getDurability(stack) - damage);
        if (ToolType.isPowered(stack)) {
            setEnergy(stack, getEnergy(stack) - damage);
        }
    }

    public static void damageForMining(ItemStack stack, int multi) {
        damage(stack, ToolType.get(stack).getDamageMining() * multi);
    }

    public static void damageForEntity(ItemStack stack, int multi) {
        damage(stack, ToolType.get(stack).getDamageEntity() * multi);
    }

    public static void damageForCrafting(ItemStack stack, int multi) {
        damage(stack, ToolType.get(stack).getDamageCrafting() * multi);
    }

    public static void remove(ItemStack stack, World world, BlockPos pos) {
        stack.setCount(0);
        Sounds.BREAK.play(world, pos);
    }

    public static void setup(ItemStack stack, Material primary, Material secondary) {
        validateTag(stack);
        ToolType type = ToolType.get(stack);
        if (type != null) {
            setType(stack, type.getName());
            setQuality(stack, type.getBaseQuality() + primary.getToolQuality());
            setPrimaryMaterial(stack, primary.getName());
            setSecondaryMaterial(stack, secondary.getName());
            setDurability(stack, 100 * (int)(primary.getToolDurability() * type.getDurabilityMulti()));
            setMaxDurability(stack, getDurability(stack));
            setAttackDamage(stack, type.getBaseDamage() + primary.getToolQuality());
            setAttackSpeed(stack, 0);
            setMiningSpeed(stack, type.getSpeedMulti() * primary.getToolSpeed());
            setEnergy(stack, 1600000); //TODO
            setMaxEnergy(stack, 1600000);
        }
    }

    public static void validateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.getTagCompound().hasKey(Ref.TAG_TOOL_DATA)) {
            ToolType type = ToolType.get(stack);
            if (type != null) {
                stack.getTagCompound().setTag(Ref.TAG_TOOL_DATA, new NBTTagCompound());
                setType(stack, type.getName());
                setQuality(stack, 0);
                setPrimaryMaterial(stack, "NULL");
                setSecondaryMaterial(stack, "NULL");
                setDurability(stack, 0);
                setMaxDurability(stack, 0);
                setAttackDamage(stack, 0);
                setAttackSpeed(stack, 0);
                setMiningSpeed(stack, 0);
                setEnergy(stack, 0);
                setMaxEnergy(stack, 0);
            }
        }
    }
}
