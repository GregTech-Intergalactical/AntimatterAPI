package muramasa.antimatter.item;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBattery extends ItemBasic<ItemBattery> {

    protected Tier tier;
    final long cap;
    final boolean reusable;

    public ItemBattery(String domain, String id, Tier tier, long cap, boolean reusable) {
        super(domain, id);
        this.tier = tier;
        this.cap = cap;
        this.reusable = reusable;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            AntimatterCaps.getCustomEnergyHandler(stack).ifPresent(e -> e.insert(this.cap, false));
            items.add(new ItemStack(this));
            items.add(stack);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT tag) {
        return new ItemEnergyHandler(stack, 0, cap, reusable ? tier.getVoltage() : 0, tier.getVoltage(), reusable ? 1 : 0, 1);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return AntimatterCaps.getCustomEnergyHandler(stack).map(EnergyHandler::getEnergy).orElse(0L) > 0 ? 0x00BFFF : super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return 1D;
        }
        return 1D - AntimatterCaps.getCustomEnergyHandler(stack).map(EnergyHandler::getEnergy).orElse(0L) / (double) cap;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isShiftKeyDown() && !world.isRemote()) {
            boolean newMode = chargeModeSwitch(stack);
            player.sendMessage(new TranslationTextComponent(newMode ? "message.discharge.on" : "message.discharge.off"));
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    /** Switches the discharge mode for an item.
     *  False does nothing, true disables discharge.
     *  @param stack the stack to switch.
     */
    private boolean chargeModeSwitch(ItemStack stack) {
        boolean mode = !stack.getOrCreateTag().getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
        stack.getOrCreateTag().putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, mode);
        return mode;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        //TODO: Translateable
        if (reusable) {
            tooltip.add(new TranslationTextComponent("item.reusable"));
        }
        long energy = ItemEnergyHandler.getEnergyFromStack(stack);
        tooltip.add(new TranslationTextComponent("item.charge").appendText(": ").appendSibling(new StringTextComponent(energy + "/" + cap).applyTextStyle(energy == 0 ? TextFormatting.RED : TextFormatting.GREEN)).appendText(" (" + tier.getId().toUpperCase() + ")"));
        super.addInformation(stack, worldIn, tooltip, flag);
    }
}
