package muramasa.antimatter.item;

import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.forge.TesseractCaps;
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            getCastedHandler(stack).ifPresent(e -> {
                e.setEnergy(e.getCapacity());
            });
            items.add(new ItemStack(this));
            items.add(stack);
        }
    }

    public static ItemStack getFilledBattery(ItemBasic<?> item) {
        ItemStack stack = item.getDefaultInstance();
        getCastedHandler(stack).ifPresent(t -> {
            t.setEnergy(t.getCapacity());
        });
        return stack;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag) {
        return new ItemEnergyHandler.Provider(() -> new ItemEnergyHandler(cap, isReusable() ? tier.getVoltage() : 0, tier.getVoltage(), reusable ? 2 : 0, 1));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return stack.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).map(IEnergyHandler::getEnergy).filter(l -> l <= 0).map(l -> super.getBarColor(stack)).orElse(0x00BFFF);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int)(13.0f* (stack.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).map(IEnergyHandler::getEnergy).orElse(0L) / (double) cap));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide() && player.isCrouching()) {
            boolean newMode = chargeModeSwitch(stack);
            player.sendMessage(new TranslatableComponent(newMode ? "message.discharge.on" : "message.discharge.off"), player.getUUID());
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    private static LazyOptional<ItemEnergyHandler> getCastedHandler(ItemStack stack) {
        return stack.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).cast();
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
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        //TODO: Translateable
        if (reusable) {
            tooltip.add(new TranslatableComponent("item.reusable"));
        }
        long energy = stack.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).map(IGTNode::getEnergy).orElse(0L);
        tooltip.add(new TranslatableComponent("item.charge").append(": ").append(new TextComponent(energy + "/" + cap).withStyle(energy == 0 ? ChatFormatting.RED : ChatFormatting.GREEN)).append(" (" + tier.getId().toUpperCase() + ")"));
        super.appendHoverText(stack, worldIn, tooltip, flag);
    }

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = super.getShareTag(stack);
        CompoundTag inner = getCastedHandler(stack).map(ItemEnergyHandler::serializeNBT).orElse(null);
        if (inner != null) {
            if (nbt == null) nbt = new CompoundTag();
            nbt.put("E", inner);
        }
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readShareTag(stack, nbt);
        if (nbt != null) {
            getCastedHandler(stack).ifPresent(t -> t.deserializeNBT(nbt.getCompound("E")));
        }
    }
}
