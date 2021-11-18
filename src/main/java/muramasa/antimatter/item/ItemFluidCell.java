package muramasa.antimatter.item;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.fluid.FluidHandlerItemCell;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class ItemFluidCell extends ItemBasic<ItemFluidCell> {

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
        Data.EMPTY_CELLS.add(this);
        AntimatterTextureStitcher.addStitcher(t -> {
            t.accept(new ResourceLocation(domain, "item/other/" + getId() + "_cover"));
            t.accept(new ResourceLocation(domain, "item/other/" + getId() + "_fluid"));
        });
        this.material = material;
        this.capacity = capacity;
        this.maxTemp = material.getMeltingPoint();
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
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FluidHandlerItemCell(stack, capacity, maxTemp);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(x -> {
            FluidStack fluid = x.getFluidInTank(0);
            if (!fluid.isEmpty()) {
                TextComponent fluidname = (TextComponent) fluid.getDisplayName();
                fluidname.append(": ").append(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluid.getAmount()) + " mB").withStyle(TextFormatting.GRAY));
                tooltip.add(fluidname);
            }
            tooltip.add(new StringTextComponent("Max Temp: " + ((ItemFluidCell) stack.getItem()).getMaxTemp() + "K"));
        });
    }

    public static ITag.INamedTag<Item> getTag() {
        return TagUtils.getItemTag(new ResourceLocation(Ref.ID, "cell"));
    }

    public ItemStack fill(Fluid fluid, int amount) {
        ItemStack stack = new ItemStack(this);
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).map(h -> {
            h.fill(new FluidStack(fluid, amount), EXECUTE);
            return h;
        }).orElse(null);
        return handler != null ? handler.getContainer() : stack;
    }

    public ItemStack fill(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(h -> {
            h.fill(new FluidStack(fluid, h.getTankCapacity(0)), EXECUTE);
        });
        return stack;
    }

    public ItemStack drain(ItemStack old, FluidStack fluid) {
        old.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(h -> {
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

    /**
     * Gets the fluid from the given clay bucket container
     *
     * @param stack Cell stack
     * @return Fluid contained in the container
     */
    public FluidStack getFluid(ItemStack stack) {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctxt) {
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

        return ActionResultType.PASS;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FluidStack fluid = this.getFluid(stack);
        BlockRayTraceResult trace = getPlayerPOVHitResult(world, player, fluid.isEmpty() ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

        // fire Forge event for bucket use
        ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, stack, trace);
        if (ret != null) {
            return ret;
        }

        // if we missed, do nothing
        if (trace.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(stack);
        }

        // normal fluid logic
        BlockPos pos = trace.getBlockPos();
        Direction direction = trace.getDirection();
        BlockPos offset = pos.relative(direction);

        // ensure we can place a fluid there
        if (world.mayInteract(player, pos) && player.mayUseItemAt(offset, direction, stack)) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block == Blocks.CAULDRON && !player.isCrouching()) {
                ActionResult<ItemStack> result = interactWithCauldron(world, pos, state, player, stack, fluid);
                if (result.getResult() != ActionResultType.PASS) {
                    return result;
                }
            }

            if ((fluid.isEmpty() || fluid.getAmount() + 1000 <= capacity) && block instanceof IBucketPickupHandler) {
                Fluid newFluid = ((IBucketPickupHandler) block).takeLiquid(world, pos, state);
                if (newFluid != Fluids.EMPTY) {
                    player.awardStat(Stats.ITEM_USED.get(this));

                    // play sound effect
                    SoundEvent sound = newFluid.getAttributes().getFillSound();
                    if (sound == null) {
                        sound = newFluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                    }
                    player.playSound(sound, 1.0F, 1.0F);
                    ItemStack newStack = updateCell(stack, player, fill(newFluid, fluid.getAmount() + 1000));
                    if (!world.isClientSide()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) player, newStack.copy());
                    }

                    return ActionResult.success(newStack);
                }
            } else if (fluid.getAmount() >= 1000) {
                BlockPos fluidPos = state.getBlock() instanceof ILiquidContainer && fluid.getFluid() == Fluids.WATER ? pos : offset;
                if (this.tryPlaceContainedLiquid(player, world, fluidPos, stack, trace)) {
                    onLiquidPlaced(fluid.getFluid(), world, stack, fluidPos);
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, fluidPos, stack);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    ItemStack newStack = drain(Utils.ca(1, stack), new FluidStack(fluid.getFluid(), 1000));
                    if (stack.getCount() > 1) {
                        stack.shrink(1);
                        addItem(player, newStack);
                        return ActionResult.success(stack);
                    }
                    return ActionResult.success(newStack);
                }
            }
        }
        return ActionResult.fail(stack);

    }

    /**
     * Called when a liquid is placed in world
     *
     * @param fluid Fluid to place
     * @param world World instance
     * @param stack Stack instance
     * @param pos   Position to place the world
     */
    private static void onLiquidPlaced(Fluid fluid, World world, ItemStack stack, BlockPos pos) {
        // TODO: is this bad?
        Item item = fluid.getBucket();
        if (item instanceof BucketItem) {
            ((BucketItem) item).checkExtraContent(world, stack, pos);
        }
    }

    // TODO: possibly migrate to the Forge method
    @SuppressWarnings("deprecation")
    private boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World world, BlockPos pos, ItemStack stack, @Nullable BlockRayTraceResult trace) {
        FluidStack fluidStack = this.getFluid(stack);
        Fluid fluid = fluidStack.getFluid();
        if (!(fluid instanceof FlowingFluid)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean replaceable = state.canBeReplaced(fluid);
        if (state.isAir(world, pos) || replaceable || block instanceof ILiquidContainer && ((ILiquidContainer) block).canPlaceLiquid(world, pos, state, fluid)) {
            if (world.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
                world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            } else if (block instanceof ILiquidContainer && fluid == Fluids.WATER) {
                if (((ILiquidContainer) block).placeLiquid(world, pos, state, ((FlowingFluid) fluid).getSource(false))) {
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
    private void playEmptySound(Fluid fluid, @Nullable PlayerEntity player, IWorld world, BlockPos pos) {
        SoundEvent sound = fluid.getAttributes().getEmptySound();
        if (sound == null) {
            sound = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }
        world.playSound(player, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Interacts with a cauldron block
     *
     * @param world  World instance
     * @param pos    Position of the cauldron
     * @param state  Cauldron state
     * @param player Interacting player
     * @param stack  Bucket stack
     * @param fluid  Contained fluid
     * @return Action result from interaction, pass means failed to interact with a cauldron
     */
    private ActionResult<ItemStack> interactWithCauldron(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack, FluidStack fluid) {
        // if the bucket is empty, try filling from the cauldron
        int level = state.getValue(CauldronBlock.LEVEL);
        if (fluid.isEmpty()) {
            // if empty, try emptying
            if (level == 3) {
                // empty cauldron logic
                if (player != null) {
                    player.awardStat(Stats.USE_CAULDRON);
                }
                if (!world.isClientSide()) {
                    ((CauldronBlock) Blocks.CAULDRON).setWaterLevel(world, pos, state, 0);
                }
                world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ItemStack newStack = fill(Fluids.WATER, 1000);
                if (stack.getCount() > 1) {
                    stack.shrink(1);
                    addItem(player, newStack);
                    return ActionResult.success(stack);
                }
                return ActionResult.success(newStack);
            }
        } else if (fluid.getFluid() == Fluids.WATER && fluid.getAmount() >= 1000) {
            // fill cauldron if not full
            if (level < 3) {
                if (player != null) {
                    player.awardStat(Stats.FILL_CAULDRON);
                }
                if (!world.isClientSide) {
                    ((CauldronBlock) Blocks.CAULDRON).setWaterLevel(world, pos, state, 3);
                }
                world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ItemStack newStack = drain(Utils.ca(1, stack), new FluidStack(fluid.getFluid(), 1000));
                if (stack.getCount() > 1) {
                    stack.shrink(1);
                    addItem(player, newStack);
                    return ActionResult.success(stack);
                }
                return ActionResult.success(newStack);
            }
        } else {
            // pass if not empty or water
            return ActionResult.pass(stack);
        }
        // consume so we do not accidentally place water next to the cauldron, consistency with vanilla
        return ActionResult.success(stack);
    }

    /**
     * Fills a bucket stack with the given fluid
     *
     * @param originalStack original
     * @param player        Player instance
     * @param newCell       Filled cell stack
     * @return Stack of buckets
     */
    protected static ItemStack updateCell(ItemStack originalStack, PlayerEntity player, ItemStack newCell) {
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
    protected static void addItem(PlayerEntity player, ItemStack stack) {
        if (!player.inventory.add(stack)) {
            player.drop(stack, false);
        }
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ((AntimatterItemModelBuilder) prov.getAntimatterBuilder(item).bucketProperties(stack, true, false).parent(new ModelFile.UncheckedModelFile("forge:item/bucket"))).tex((map) -> {
            map.put("base", getDomain() + ":item/basic/" + getId());
            map.put("cover", getDomain() + ":item/other/" + getId() + "_cover");
            map.put("fluid", getDomain() + ":item/other/" + getId() + "_fluid");
        });
    }


}
