package muramasa.antimatter.item;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.mixin.BucketItemAccessor;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;

import static muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin.intToSuperScript;
import static tesseract.FluidPlatformUtils.createFluidStack;

public class ItemFluidIcon extends ItemBasic<ItemFluidIcon> implements IContainerItem, IFluidItem{

    private final int capacity;

    private final Fluid stack;

    /**
     * Tag name for fluid in a bucket
     */
    private static final String TAG_FLUID = "Fluid";

    public ItemFluidIcon() {
        super(Ref.ID, "fluid_icon");
        AntimatterTextureStitcher.addStitcher(t -> {
            t.accept(new ResourceLocation(domain, "item/mask/icon_fluid"));
        });
        this.capacity = 1;
        this.stack = Fluids.EMPTY;
    }

    public int getCapacity() {
        return capacity;
    }

    /*@Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            //AntimatterMaterialTypes.LIQUID.all().forEach(m -> items.add(fill(m.getLiquid())));
            //AntimatterMaterialTypes.GAS.all().forEach(m -> items.add(fill(m.getGas())));
            //AntimatterMaterialTypes.PLASMA.all().forEach(m -> items.add(fill(m.getPlasma())));
        }
    }*/

    @Override
    public long getTankSize() {
        return capacity * TesseractGraphWrappers.dropletMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.remove(0);
        FluidHooks.safeGetItemFluidManager(stack).ifPresent(x -> {
            FluidHolder fluid = x.getFluidInTank(0);
            if (fluid.isEmpty()) return;
            List<Component> str = new ArrayList<>();
            str.add(FluidPlatformUtils.getFluidDisplayName(fluid));
            str.add(Utils.translatable("antimatter.tooltip.fluid.temp", FluidPlatformUtils.getFluidTemperature(fluid.getFluid())).withStyle(ChatFormatting.RED));
            String liquid = !FluidPlatformUtils.isFluidGaseous(fluid.getFluid()) ? "liquid" : "gas";
            str.add(Utils.translatable("antimatter.tooltip.fluid." + liquid).withStyle(ChatFormatting.GREEN));
            AntimatterJEIREIPlugin.addModDescriptor(str, fluid);
            tooltip.addAll(str);
        });
    }

    public static TagKey<net.minecraft.world.item.Item> getTag() {
        return TagUtils.getItemTag(new ResourceLocation(Ref.ID, "cell"));
    }

    public ItemStack fill(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        ItemStackHolder holder = new ItemStackHolder(stack);
        insert(holder, createFluidStack(fluid, TesseractGraphWrappers.dropletMultiplier));
        return holder.getStack();
    }

    public ItemStack drain(ItemStack old, FluidHolder fluid) {
        ItemStackHolder holder = new ItemStackHolder(old);
        extract(holder, fluid);
        return holder.getStack();
    }

    public Fluid getFluid() {
        return this.stack;
    }

    /**
     * Returns whether a cell has fluid.
     */
    protected boolean hasFluid(ItemStack container) {
        return !getFluidStack(container).isEmpty();
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return hasFluid(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(this);
    }

    @Override
    public BiPredicate<Integer, FluidHolder> getFilter() {
        return (i, f) -> true;
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        prov.getAntimatterBuilder(item).bucketProperties(stack, true, false).parent(new ResourceLocation("antimatter:item/bucket")).tex((map) -> {
            map.put("base", getDomain() + ":block/empty");
            map.put("cover", getDomain() + ":block/empty");
            map.put("fluid", getDomain() + ":item/mask/icon_fluid");
        });
    }
}
