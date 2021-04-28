package muramasa.antimatter.item;

import muramasa.antimatter.Ref;
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
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBattery extends ItemBasic<ItemBattery> {

    protected Tier tier;
    protected final long cap;
    protected final boolean reusable;

    public ItemBattery(String domain, String id, Tier tier, long cap, boolean reusable) {
        super(domain, id);
        this.tier = tier;
        this.cap = cap;
        this.reusable = reusable;
    }

    public Tier getTier() {
        return tier;
    }

    public long getCapacity() {
        return cap;
    }

    public boolean isReusable() {
        return reusable;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putLong(Ref.KEY_ITEM_ENERGY, cap);
            stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).ifPresent(e -> {
                if (e instanceof ItemEnergyHandler) {
                    ((ItemEnergyHandler)e).setEnergy(e.getCapacity());
                }
            });
            items.add(new ItemStack(this));
            items.add(stack);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT tag) {
        return new ItemEnergyHandler.Provider(() -> new ItemEnergyHandler(stack, cap, reusable ? tier.getVoltage() : 0, tier.getVoltage(), reusable ? 2 : 0, 1));
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(IEnergyHandler::getEnergy).filter(l -> l <= 0).map(l -> super.getRGBDurabilityForDisplay(stack)).orElse(0x00BFFF);
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
        return 1D - stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(IEnergyHandler::getEnergy).orElse(0L) / (double) cap;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote() && player.isCrouching()) {
            boolean newMode = chargeModeSwitch(stack);
            player.sendMessage(new TranslationTextComponent(newMode ? "message.discharge.on" : "message.discharge.off"), player.getUniqueID());
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    /** Switches the discharge mode for an item.
     *  False does nothing, true disables discharge.
     *  @param stack the stack to switch.
     */
    private boolean chargeModeSwitch(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        boolean mode;
        if (nbt.contains(Ref.KEY_ITEM_DISCHARGE_MODE)) {
            mode = !stack.getOrCreateTag().getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
        } else {
            mode = false;
        }
        stack.getOrCreateTag().putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, mode);
        return mode;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        //TODO: Translateable
        if (reusable) {
            tooltip.add(new TranslationTextComponent("item.reusable"));
        }
        long energy = ItemEnergyHandler.getEnergyFromStack(stack, stack.getTag());
        tooltip.add(new TranslationTextComponent("item.charge").appendString(": ").append(new StringTextComponent(energy + "/" + cap).mergeStyle(energy == 0 ? TextFormatting.RED : TextFormatting.GREEN)).appendString(" (" + tier.getId().toUpperCase() + ")"));
        super.addInformation(stack, worldIn, tooltip, flag);
    }
}
