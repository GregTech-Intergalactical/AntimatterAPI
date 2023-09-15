package muramasa.antimatter.item;

import muramasa.antimatter.Ref;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.TesseractCapUtils;
import tesseract.api.context.TesseractItemContext;
import tesseract.api.gt.IEnergyHandlerItem;
import tesseract.api.gt.IEnergyItem;
import tesseract.api.gt.IGTNode;

import java.util.List;
import java.util.Optional;

public class ItemBattery extends ItemBasic<ItemBattery> implements IEnergyItem {

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
            items.add(stack.copy());
            items.add(getFilledBattery(this));
        }
    }

    public static ItemStack getFilledBattery(ItemBasic<?> item) {
        ItemStack stack = item.getDefaultInstance();
        if (!(item instanceof ItemBattery battery)) return stack;
        stack.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA).putLong(Ref.KEY_ITEM_ENERGY, battery.cap);
        return stack;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        long energy = stack.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA).getLong(Ref.KEY_ITEM_ENERGY);
        if (energy <= 0) return super.getBarColor(stack);
        return 0x00BFFF;
        //return TesseractCapUtils.getEnergyHandlerItem(stack).map(IEnergyHandler::getEnergy).filter(l -> l <= 0).map(l -> super.getBarColor(stack)).orElse(0x00BFFF);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int)(13.0f* (stack.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA).getLong(Ref.KEY_ITEM_ENERGY) / (double) cap));
    }

    @NotNull
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

    private boolean canDischarge(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (!nbt.contains(Ref.TAG_ITEM_ENERGY_DATA)) return true;
        CompoundTag energyTag = nbt.getCompound(Ref.TAG_ITEM_ENERGY_DATA);
        if (!energyTag.contains(Ref.KEY_ITEM_DISCHARGE_MODE)) return true;
        return energyTag.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    public boolean chargeModeSwitch(ItemStack stack) {
        boolean discharge = !canDischarge(stack);
        CompoundTag energyTag = stack.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA);
        energyTag.putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, discharge);
        return discharge;
    }

    private static Optional<ItemEnergyHandler> getCastedHandler(ItemStack stack) {
        Optional<IEnergyHandlerItem> itemHandler = TesseractCapUtils.getEnergyHandlerItem(stack);
        return itemHandler.map(e -> (ItemEnergyHandler)e);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        //TODO: Translateable
        if (reusable) {
            tooltip.add(new TranslatableComponent("item.reusable"));
        }
        long energy = TesseractCapUtils.getEnergyHandlerItem(stack).map(IGTNode::getEnergy).orElse(0L);
        tooltip.add(new TranslatableComponent("item.charge").append(": ").append(new TextComponent(energy + "/" + cap).withStyle(energy == 0 ? ChatFormatting.RED : ChatFormatting.GREEN)).append(" (" + tier.getId().toUpperCase() + ")"));
        super.appendHoverText(stack, worldIn, tooltip, flag);
    }

    @Override
    public IEnergyHandlerItem createEnergyHandler(TesseractItemContext context) {
        return new ItemEnergyHandler(context, cap, isReusable() ? tier.getVoltage() : 0, tier.getVoltage(), reusable ? 2 : 0, 1);
    }
}
