package muramasa.antimatter.item;

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
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTNode;

import javax.annotation.Nonnull;
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            getCastedHandler(stack).ifPresent(e -> {
                e.setEnergy(e.getCapacity());
            });
            items.add(new ItemStack(this));
            items.add(stack);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT tag) {
        return new ItemEnergyHandler.Provider(() -> new ItemEnergyHandler(cap, isReusable() ? tier.getVoltage() : 0, tier.getVoltage(), reusable ? 2 : 0, 1));
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
        return 1D - stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(IEnergyHandler::getEnergy).orElse(0L) / (double) cap;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide() && player.isCrouching()) {
            boolean newMode = chargeModeSwitch(stack);
            player.sendMessage(new TranslationTextComponent(newMode ? "message.discharge.on" : "message.discharge.off"), player.getUUID());
            return ActionResult.success(stack);
        }
        return ActionResult.pass(stack);
    }

    private LazyOptional<ItemEnergyHandler> getCastedHandler(ItemStack stack) {
        return stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).cast();
    }

    /**
     * Switches the discharge mode for an item.
     * False does nothing, true disables discharge.
     *
     * @param stack the stack to switch.
     */
    private boolean chargeModeSwitch(ItemStack stack) {
        return getCastedHandler(stack).map(ItemEnergyHandler::chargeModeSwitch).orElse(true);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        //TODO: Translateable
        if (reusable) {
            tooltip.add(new TranslationTextComponent("item.reusable"));
        }
        long energy = stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(IGTNode::getEnergy).orElse(0L);
        tooltip.add(new TranslationTextComponent("item.charge").append(": ").append(new StringTextComponent(energy + "/" + cap).withStyle(energy == 0 ? TextFormatting.RED : TextFormatting.GREEN)).append(" (" + tier.getId().toUpperCase() + ")"));
        super.appendHoverText(stack, worldIn, tooltip, flag);
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = super.getShareTag(stack);
        CompoundNBT inner = getCastedHandler(stack).map(ItemEnergyHandler::serializeNBT).orElse(null);
        if (inner != null) {
            if (nbt == null) nbt = new CompoundNBT();
            nbt.put("E", inner);
        }
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
        if (nbt != null) {
            getCastedHandler(stack).ifPresent(t -> t.deserializeNBT(nbt.getCompound("E")));
        }
    }
}
