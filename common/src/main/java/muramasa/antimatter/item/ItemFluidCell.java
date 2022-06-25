package muramasa.antimatter.item;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.fluid.FluidHandlerItemCell;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import net.minecraftforge.client.model.generators.ModelFile;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import tesseract.TesseractPlatformUtils;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class ItemFluidCell extends ItemBasic<ItemFluidCell> implements IContainerItem {

    public final Material material;
    private final int capacity;
    private final int maxTemp;

    private final Fluid stack;

    /**
     * Tag name for fluid in a bucket
     */
    private static final String TAG_FLUID = "Fluid";

    public ItemFluidCell(String domain, Material material, int capacity) {
        super(domain, "cell_".concat(material.getId()));
        AntimatterTextureStitcher.addStitcher(t -> {
            t.accept(new ResourceLocation(domain, "item/other/" + getId() + "_cover"));
            t.accept(new ResourceLocation(domain, "item/other/" + getId() + "_fluid"));
        });
        this.material = material;
        this.capacity = capacity;
        this.maxTemp = MaterialTags.MELTING_POINT.getInt(material);
        this.stack = Fluids.EMPTY;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

   /* @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechItemGroup && ((GregTechItemGroup) tab).getName().equals("items")) {
            if (Configs.JEI.SHOW_ALL_FLUID_CELLS) {
                MaterialType.LIQUID.all().forEach(m -> items.add(fill(m.getLiquid())));
                MaterialType.GAS.all().forEach(m -> items.add(fill(m.getGas())));
                MaterialType.PLASMA.all().forEach(m -> items.add(fill(m.getPlasma())));
            } else {
                items.add(new ItemStack(this));
            }
        }
    }*/

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemCell(stack, capacity, maxTemp);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (worldIn == null) return;
        TesseractPlatformUtils.getFluidHandlerItem(stack).ifPresent(x -> {
            FluidStack fluid = x.getFluidInTank(0);
            if (!fluid.isEmpty()) {
                BaseComponent fluidname = (BaseComponent) fluid.getDisplayName();
                fluidname.append(": ").append(new TextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluid.getAmount()) + " mB").withStyle(ChatFormatting.GRAY));
                tooltip.add(fluidname);
            }
            tooltip.add(new TextComponent("Max Temp: " + ((ItemFluidCell) stack.getItem()).getMaxTemp() + "K"));
        });
    }

    public static TagKey<Item> getTag() {
        return TagUtils.getItemTag(new ResourceLocation(Ref.ID, "cell"));
    }

    public ItemStack fill(Fluid fluid, long amount) {
        ItemStack stack = new ItemStack(this);
        IFluidHandlerItem handler = TesseractPlatformUtils.getFluidHandlerItem(stack).map(h -> {
            h.fillDroplets(new FluidStack(fluid, amount), EXECUTE);
            return h;
        }).orElse(null);
        return handler != null ? handler.getContainer() : stack;
    }

    public ItemStack fill(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        TesseractPlatformUtils.getFluidHandlerItem(stack).ifPresent(h -> {
            h.fillDroplets(new FluidStack(fluid, h.getTankCapacityInDroplets(0)), EXECUTE);
        });
        return stack;
    }

    public ItemStack drain(ItemStack old, FluidStack fluid) {
        TesseractPlatformUtils.getFluidHandlerItem(old).ifPresent(h -> {
            h.drain(fluid, EXECUTE);
        });
        return old;
    }

    public Fluid getFluid() {
        return this.stack;
    }

    /**
     * Returns whether a cell has fluid.
     */
    protected boolean hasFluid(ItemStack container) {
        return !getFluid(container).isEmpty();
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return hasFluid(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(this);
    }

    /**
     * Gets the fluid from the given clay bucket container
     *
     * @param stack Cell stack
     * @return Fluid contained in the container
     */
    public FluidStack getFluid(ItemStack stack) {
        return TesseractPlatformUtils.getFluidHandlerItem(stack).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctxt) {
        //TODO reenable
//      if (world.isRemote) return EnumActionResult.PASS;
        /*TileEntity tile = Utils.getTile(ctxt.getWorld(), ctxt.getPos());
        if (tile != null) {
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                ctxt.getPlayer().getHeldItem(ctxt.getHand()).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cellHandler -> {
                    int countFilled = fluidHandler.fill(cellHandler.getFluidInTank(0), SIMULATE);
                    if (countFilled == 1000) {
                        fluidHandler.fill(cellHandler.getFluidInTank(0), EXECUTE);
                        if (!cellHandler.drain(1000, EXECUTE).isEmpty()) {
                            ctxt.getPlayer().setItemStackToSlot(ctxt.getHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, cellHandler.getContainer());
                        }
                    }
                });
            });
        }*/

        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FluidStack fluid = this.getFluid(stack);
        BlockHitResult trace = getPlayerPOVHitResult(world, player, fluid.isEmpty() ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

        // fire Forge event for bucket use
        InteractionResultHolder<ItemStack> ret = AntimatterPlatformUtils.postBucketUseEvent(player, world, stack, trace);
        if (ret != null) {
            return ret;
        }

        // if we missed, do nothing
        if (trace.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        // normal fluid logic
        BlockPos pos = trace.getBlockPos();
        Direction direction = trace.getDirection();
        BlockPos offset = pos.relative(direction);

        // ensure we can place a fluid there
        if (world.mayInteract(player, pos) && player.mayUseItemAt(offset, direction, stack)) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if ((fluid.isEmpty() || fluid.getAmount() + 1000 <= capacity) && block instanceof BucketPickup) {
                ItemStack bucket = ((BucketPickup) block).pickupBlock(world, pos, state);
                if (!bucket.isEmpty()) {
                    Fluid newFluid = ((BucketItemAccessor)bucket.getItem()).getContent();
                    player.awardStat(Stats.ITEM_USED.get(this));

                    // play sound effect
                    SoundEvent sound = AntimatterPlatformUtils.getFluidSound(newFluid, true);
                    if (sound == null) {
                        sound = newFluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                    }
                    player.playSound(sound, 1.0F, 1.0F);
                    ItemStack newStack = updateCell(stack, player, fill(newFluid, fluid.getAmount() + 1000));
                    if (!world.isClientSide()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, newStack.copy());
                    }

                    return InteractionResultHolder.success(newStack);
                }
            } else if (fluid.getAmount() >= 1000) {
                BlockPos fluidPos = state.getBlock() instanceof LiquidBlockContainer && fluid.getFluid() == Fluids.WATER ? pos : offset;
                if (this.tryPlaceContainedLiquid(player, world, fluidPos, stack, trace)) {
                    onLiquidPlaced(player, fluid.getFluid(), world, stack, fluidPos);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, fluidPos, stack);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    ItemStack newStack = drain(Utils.ca(1, stack), new FluidStack(fluid.getFluid(), 1000));
                    if (stack.getCount() > 1) {
                        stack.shrink(1);
                        addItem(player, newStack);
                        return InteractionResultHolder.success(stack);
                    }
                    return InteractionResultHolder.success(newStack);
                }
            }
        }
        return InteractionResultHolder.fail(stack);

    }

    /**
     * Called when a liquid is placed in world
     *
     * @param fluid Fluid to place
     * @param world World instance
     * @param stack Stack instance
     * @param pos   Position to place the world
     */
    private static void onLiquidPlaced(Player player, Fluid fluid, Level world, ItemStack stack, BlockPos pos) {
        // TODO: is this bad?
        Item item = fluid.getBucket();
        if (item instanceof BucketItem) {
            ((BucketItem) item).checkExtraContent(player, world, stack, pos);
        }
    }

    // TODO: possibly migrate to the Forge method
    @SuppressWarnings("deprecation")
    private boolean tryPlaceContainedLiquid(@Nullable Player player, Level world, BlockPos pos, ItemStack stack, @Nullable BlockHitResult trace) {
        FluidStack fluidStack = this.getFluid(stack);
        Fluid fluid = fluidStack.getFluid();
        if (!(fluid instanceof FlowingFluid)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean replaceable = state.canBeReplaced(fluid);
        if (state.isAir() || replaceable || block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(world, pos, state, fluid)) {
            if (world.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
                world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            } else if (block instanceof LiquidBlockContainer && fluid == Fluids.WATER) {
                if (((LiquidBlockContainer) block).placeLiquid(world, pos, state, ((FlowingFluid) fluid).getSource(false))) {
                    this.playEmptySound(fluid, player, world, pos);
                }
            } else {
                if (!world.isClientSide() && replaceable && !state.getMaterial().isLiquid()) {
                    world.destroyBlock(pos, true);
                }

                this.playEmptySound(fluid, player, world, pos);
                world.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 11);
            }

            return true;
        }
        if (trace == null) {
            return false;
        }
        return this.tryPlaceContainedLiquid(player, world, trace.getBlockPos().relative(trace.getDirection()), stack, null);
    }

    /**
     * Plays the sound on emptying the bucket
     *
     * @param fluid  Fluid placed
     * @param player Player accessing the bucket
     * @param world  World instance
     * @param pos    Position of sound
     */
    private void playEmptySound(Fluid fluid, @Nullable Player player, LevelAccessor world, BlockPos pos) {
        SoundEvent sound = AntimatterPlatformUtils.getFluidSound(fluid, false);
        if (sound == null) {
            sound = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }
        world.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Interacts with a cauldron block
     *
     * @param world  World instance
     * @param pos    Position of the cauldron
     * @param state  Cauldron state
     * @param player Interacting player
     * @param stack  Bucket stack
     * @return Action result from interaction, pass means failed to interact with a cauldron
     */
    //BlockState p_175711_, Level p_175712_, BlockPos p_175713_, Player p_175714_, InteractionHand p_175715_, ItemStack p_175716_
    public static InteractionResult interactWithCauldron(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        // if the bucket is empty, try filling from the cauldron
        if (world.isClientSide) return InteractionResult.PASS;
        ItemFluidCell cell = (ItemFluidCell) stack.getItem();
        FluidStack fluid = cell.getFluid(stack);
        int level = state.getValue(LayeredCauldronBlock.LEVEL);

        if (state.getBlock() instanceof AbstractCauldronBlock cauldron) {
            // if empty, try emptying
            if (level < 3 && !cell.hasFluid(stack)) {
                // empty cauldron logic
                if (player != null) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                if (!world.isClientSide()) {
                    LayeredCauldronBlock.lowerFillLevel(state, world, pos);
                }
                world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                ItemStack newStack = cell.getContainerItem(cell.fill(Fluids.WATER, 1000));
                if (stack.getCount() > 1) {
                    stack.shrink(1);
                    addItem(player, newStack);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.SUCCESS;
            } else if (fluid.getFluid().isSame(Fluids.WATER)) {
                if (level < 3) {
                    if (player != null) {
                        player.awardStat(Stats.FILL_CAULDRON);
                    }
                    if (!world.isClientSide) {
                        world.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, level+1));
                    }
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    ItemStack newStack = cell.drain(Utils.ca(1, stack), new FluidStack(fluid.getFluid(), 1000));
                    if (stack.getCount() > 1) {
                        stack.shrink(1);
                        addItem(player, newStack);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.SUCCESS;
                }
            } else {
                // pass if not empty or water
                return InteractionResult.PASS;
            }

        }
        // consume so we do not accidentally place water next to the cauldron, consistency with vanilla
        return InteractionResult.PASS;
    }

    /**
     * Fills a bucket stack with the given fluid
     *
     * @param originalStack original
     * @param player        Player instance
     * @param newCell       Filled cell stack
     * @return Stack of buckets
     */
    protected static ItemStack updateCell(ItemStack originalStack, Player player, ItemStack newCell) {
        // shrink the stack
        if (player.isCreative()) {
            return originalStack;
        }
        originalStack.shrink(1);
        // fill with fluid
        if (originalStack.isEmpty()) {
            return newCell;
        }
        addItem(player, newCell);
        return originalStack;
    }

    /**
     * Adds an item to the player inventory, dropping if there is no space
     *
     * @param player Player instance
     * @param stack  Stack to add
     */
    protected static void addItem(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        ((AntimatterItemModelBuilder) prov.getAntimatterBuilder(item).bucketProperties(stack, true, false).parent(new ModelFile.UncheckedModelFile("antimatter:item/bucket"))).tex((map) -> {
            map.put("base", getDomain() + ":item/basic/" + getId());
            map.put("cover", getDomain() + ":item/other/" + getId() + "_cover");
            map.put("fluid", getDomain() + ":item/other/" + getId() + "_fluid");
        });
    }


}
