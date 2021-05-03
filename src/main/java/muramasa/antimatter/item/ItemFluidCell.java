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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class ItemFluidCell extends ItemBasic<ItemFluidCell> {

    public final Material material;
    private int capacity, maxTemp;

    private final Fluid stack;

    /** Tag name for fluid in a bucket */
    private static final String TAG_FLUID = "Fluid";

    public ItemFluidCell(String domain, Material material, int capacity) {
        super(domain, "cell_".concat(material.getId()));
        Data.EMPTY_CELLS.add(this);
        AntimatterTextureStitcher.addStitcher(t -> {
            t.accept(new ResourceLocation(domain, "item/other/"+getId()+"_cover"));
            t.accept(new ResourceLocation(domain, "item/other/"+getId()+"_fluid"));
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(x -> {
            FluidStack fluid = x.getFluidInTank(0);
            if (!fluid.isEmpty()) {
                TextComponent fluidname = (TextComponent) fluid.getDisplayName();
                fluidname.appendString(": ").append(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluid.getAmount()) + " mB").mergeStyle(TextFormatting.GRAY));
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
     * @param stack  Cell stack
     * @return  Fluid contained in the container
     */
    public FluidStack getFluid(ItemStack stack) {
        CompoundNBT tags = stack.getTag();
        if(tags != null) {
            return FluidStack.loadFluidStackFromNBT(tags.getCompound(TAG_FLUID));
        }

        return FluidStack.EMPTY;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctxt) {
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

    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        FluidStack fluid = this.getFluid(stack);
        BlockRayTraceResult trace = rayTrace(world, player, fluid.isEmpty() ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

        // fire Forge event for bucket use
        ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, stack, trace);
        if (ret != null) {
            return ret;
        }

        // if we missed, do nothing
        if (trace.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.resultPass(stack);
        }

        // normal fluid logic
        BlockPos pos = trace.getPos();
        Direction direction = trace.getFace();
        BlockPos offset = pos.offset(direction);

        // ensure we can place a fluid there
        if (world.isBlockModifiable(player, pos) && player.canPlayerEdit(offset, direction, stack)) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block == Blocks.CAULDRON && !player.isCrouching()) {
                ActionResult<ItemStack> result = interactWithCauldron(world, pos, state, player, stack, fluid);
                if (result.getType() != ActionResultType.PASS) {
                    return result;
                }
            }

            if ((fluid.isEmpty() || fluid.getAmount() + 1000 <= capacity) && block instanceof IBucketPickupHandler) {
                Fluid newFluid = ((IBucketPickupHandler)block).pickupFluid(world, pos, state);
                if (newFluid != Fluids.EMPTY) {
                    player.addStat(Stats.ITEM_USED.get(this));

                    // play sound effect
                    SoundEvent sound = newFluid.getAttributes().getFillSound();
                    if (sound == null) {
                        sound = newFluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                    }
                    player.playSound(sound, 1.0F, 1.0F);
                    ItemStack newStack = updateCell(stack, player, fill(newFluid, fluid.getAmount() + 1000));
                    if (!world.isRemote()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)player, newStack.copy());
                    }

                    return ActionResult.resultSuccess(newStack);
                }
            } else if (fluid.getAmount() >= 1000){
                BlockPos fluidPos = state.getBlock() instanceof ILiquidContainer && fluid.getFluid() == Fluids.WATER ? pos : offset;
                if (this.tryPlaceContainedLiquid(player, world, fluidPos, stack, trace)) {
                    onLiquidPlaced(fluid.getFluid(), world, stack, fluidPos);
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, fluidPos, stack);
                    }

                    player.addStat(Stats.ITEM_USED.get(this));
                    ItemStack newStack = drain(Utils.ca(1, stack), new FluidStack(fluid.getFluid(), 1000));
                    if (stack.getCount() > 1){
                        stack.shrink(1);
                        addItem(player, newStack);
                        return ActionResult.resultSuccess(stack);
                    }
                    return ActionResult.resultSuccess(newStack);
                }
            }
        }
        return ActionResult.resultFail(stack);

    }

    /**
     * Called when a liquid is placed in world
     * @param fluid  Fluid to place
     * @param world  World instance
     * @param stack  Stack instance
     * @param pos    Position to place the world
     */
    private static void onLiquidPlaced(Fluid fluid, World world, ItemStack stack, BlockPos pos) {
        // TODO: is this bad?
        Item item = fluid.getFilledBucket();
        if (item instanceof BucketItem) {
            ((BucketItem)item).onLiquidPlaced(world, stack, pos);
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
        boolean replaceable = state.isReplaceable(fluid);
        if (state.isAir(world, pos) || replaceable || block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(world, pos, state, fluid)) {
            if (world.getDimensionType().isUltrawarm() && fluid.isIn(FluidTags.WATER)) {
                world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            } else if (block instanceof ILiquidContainer && fluid == Fluids.WATER) {
                if (((ILiquidContainer)block).receiveFluid(world, pos, state, ((FlowingFluid)fluid).getStillFluidState(false))) {
                    this.playEmptySound(fluid, player, world, pos);
                }
            } else {
                if (!world.isRemote() && replaceable && !state.getMaterial().isLiquid()) {
                    world.destroyBlock(pos, true);
                }

                this.playEmptySound(fluid, player, world, pos);
                world.setBlockState(pos, fluid.getDefaultState().getBlockState(), 11);
            }

            return true;
        }
        if (trace == null) {
            return false;
        }
        return this.tryPlaceContainedLiquid(player, world, trace.getPos().offset(trace.getFace()), stack, null);
    }

    /**
     * Plays the sound on emptying the bucket
     * @param fluid   Fluid placed
     * @param player  Player accessing the bucket
     * @param world   World instance
     * @param pos     Position of sound
     */
    private void playEmptySound(Fluid fluid, @Nullable PlayerEntity player, IWorld world, BlockPos pos) {
        SoundEvent sound = fluid.getAttributes().getEmptySound();
        if (sound == null) {
            sound = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        }
        world.playSound(player, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Interacts with a cauldron block
     * @param world   World instance
     * @param pos     Position of the cauldron
     * @param state   Cauldron state
     * @param player  Interacting player
     * @param stack   Bucket stack
     * @param fluid   Contained fluid
     * @return  Action result from interaction, pass means failed to interact with a cauldron
     */
    private ActionResult<ItemStack> interactWithCauldron(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack, FluidStack fluid) {
        // if the bucket is empty, try filling from the cauldron
        int level = state.get(CauldronBlock.LEVEL);
        if (fluid.isEmpty()) {
            // if empty, try emptying
            if(level == 3) {
                // empty cauldron logic
                if(player != null) {
                    player.addStat(Stats.USE_CAULDRON);
                }
                if(!world.isRemote()) {
                    ((CauldronBlock)Blocks.CAULDRON).setWaterLevel(world, pos, state, 0);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ItemStack newStack = fill(Fluids.WATER, 1000);
                if (stack.getCount() > 1){
                    stack.shrink(1);
                    addItem(player, newStack);
                    return ActionResult.resultSuccess(stack);
                }
                return ActionResult.resultSuccess(newStack);
            }
        } else if(fluid.getFluid() == Fluids.WATER && fluid.getAmount() >= 1000) {
            // fill cauldron if not full
            if(level < 3) {
                if(player != null) {
                    player.addStat(Stats.FILL_CAULDRON);
                }
                if(!world.isRemote) {
                    ((CauldronBlock)Blocks.CAULDRON).setWaterLevel(world, pos, state, 3);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ItemStack newStack = drain(Utils.ca(1, stack), new FluidStack(fluid.getFluid(), 1000));
                if (stack.getCount() > 1){
                    stack.shrink(1);
                    addItem(player, newStack);
                    return ActionResult.resultSuccess(stack);
                }
                return ActionResult.resultSuccess(newStack);
            }
        } else {
            // pass if not empty or water
            return ActionResult.resultPass(stack);
        }
        // consume so we do not accidentally place water next to the cauldron, consistency with vanilla
        return ActionResult.resultSuccess(stack);
    }

    /**
     * Fills a bucket stack with the given fluid
     * @param originalStack original
     * @param player        Player instance
     * @param newCell       Filled cell stack
     * @return  Stack of buckets
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
     * @param player  Player instance
     * @param stack   Stack to add
     */
    protected static void addItem(PlayerEntity player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ((AntimatterItemModelBuilder)prov.getAntimatterBuilder(item).bucketProperties(stack,true,false).parent(new ModelFile.UncheckedModelFile("forge:item/bucket"))).tex((map) -> {
            map.put("base", getDomain() + ":item/basic/" + getId());
            map.put("cover",getDomain() + ":item/other/"+getId() + "_cover");
            map.put("fluid", getDomain() + ":item/other/"+getId() + "_fluid");
        });
    }


}
