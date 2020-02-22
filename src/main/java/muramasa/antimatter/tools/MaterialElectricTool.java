package muramasa.antimatter.tools;

import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.Material;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MaterialElectricTool extends MaterialTool {

    private int energyTier;
    private long maxEnergy;

    public MaterialElectricTool(String domain, AntimatterToolType type, AntimatterItemTier tier, Properties properties, Material primary, @Nullable Material secondary, int energyTier) {
        super(domain, Ref.VN[energyTier].toLowerCase(Locale.ENGLISH), type, tier, properties, primary, secondary);
        this.energyTier = energyTier;
        this.maxEnergy = type.getBaseMaxEnergy() * energyTier;// Utils.getNumberOfDigits(type.getBaseMaxEnergy(), true);
    }

    @Override
    public String getId() {
        return super.getId() + "_" + concatId;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (flag.isAdvanced()) {
            tooltip.add(new StringTextComponent("Energy: " + getEnergy(stack) + " / " + getMaxEnergy(stack)));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.UNBREAKING && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        if (world.getBlockState(pos) == Blocks.REDSTONE_BLOCK.getDefaultState()) {
            CompoundNBT nbt = getTag(stack);
            if (getMaxEnergy(stack) - getEnergy(stack) <= 50000) nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, getMaxEnergy(stack));
            else nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, nbt.getLong(Ref.KEY_TOOL_DATA_ENERGY) + 50000);
        }
        return super.onItemUse(context);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) return 0;
        return damage(stack, amount);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        stack = stack.copy();
        damage(stack, type.getCraftingDurability());
        return stack;
    }

    protected int damage(ItemStack stack, int amount) {
        CompoundNBT tag = getTag(stack);
        long currentEnergy = tag.getLong(Ref.KEY_TOOL_DATA_ENERGY);
        int multipliedDamage = amount * 100;
        if (Ref.RNG.nextInt(25) == 0) {
            System.out.println("Roll 25 sided dice and received a 0");
            return amount; // 1/25 chance of taking durability off the tool
        }
        else if (currentEnergy >= multipliedDamage) {
            System.out.println("Enough energy to subtract from energy buffer");
            tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, currentEnergy - multipliedDamage); // Otherwise take energy off of tool if energy is larger than multiplied damage
            return 0; // Nothing is taken away from main durability
        }
        else { // Lastly, set energy to 0 and take leftovers off of tool durability itself
            int leftOver = (int) (multipliedDamage - currentEnergy);
            tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            return leftOver / 10;
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        // return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
        return getEnergy(stack) > 0 ? 0x00BFFF : super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        long currentEnergy = getEnergy(stack);
        if (currentEnergy > 0) {
            double maxAmount = getMaxEnergy(stack), difference = maxAmount - currentEnergy;
            return difference / maxAmount;
        }
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return true;
    }

    public long getEnergy(ItemStack stack) {
        return getTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY);
    }

    public long getMaxEnergy(ItemStack stack) {
        return getTag(stack).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    public CompoundNBT getTag(ItemStack stack) {
        if (!stack.hasTag() || stack.getTag().get(Ref.TAG_TOOL_DATA) == null) validateTag(stack);
        return (CompoundNBT) stack.getTag().get(Ref.TAG_TOOL_DATA);
    }

    public void validateTag(ItemStack stack) {
        stack.setTag(new CompoundNBT());
        CompoundNBT compound = new CompoundNBT();
        compound.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
        compound.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, maxEnergy);
        stack.getTag().put(Ref.TAG_TOOL_DATA, compound);
    }

    public int getEnergyTier() {
        return energyTier;
    }

}
