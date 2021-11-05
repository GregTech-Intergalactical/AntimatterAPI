package muramasa.antimatter.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.machine.MachineFlag.BASIC;
import static net.minecraft.util.Direction.*;

public class BlockMachine extends BlockDynamic implements IItemBlockProvider {

    public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.create("horizontal_facing", Direction.Plane.HORIZONTAL);

    protected Machine<?> type;
    protected Tier tier;
    protected final StateContainer<Block, BlockState> stateContainer;

    public BlockMachine(Machine<?> type, Tier tier) {
        this(type, tier, Properties.create(WRENCH_MATERIAL).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL).setRequiresTool());
    }

    public BlockMachine(Machine<?> type, Tier tier, Properties properties) {
        super(type.getDomain(), type.getId() + "_" + tier.getId(), properties);
        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
        this.type = type;
        this.tier = tier;
        this.fillStateContainer(builder);
        this.stateContainer = builder.createStateContainer(Block::getDefaultState, BlockState::new);
        this.setDefaultState(this.stateContainer.getBaseState());
    }

    public Machine<?> getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        if (type == null) return; // means this is the first run
        if (type.allowVerticalFacing()) {
            builder.add(BlockStateProperties.FACING).add(HORIZONTAL_FACING);
        } else {
            builder.add(BlockStateProperties.HORIZONTAL_FACING);
        }
    }

    @Override
    public StateContainer<Block, BlockState> getStateContainer() {
        return stateContainer;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (type.allowVerticalFacing()) {
            Direction dir = context.getNearestLookingDirection().getOpposite();
            dir = dir.getAxis() == Axis.Y ? dir.getOpposite() : dir;
            return this.getDefaultState().with(HORIZONTAL_FACING, type.handlePlacementFacing(context, BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite())).with(BlockStateProperties.FACING, type.handlePlacementFacing(context, BlockStateProperties.FACING, dir));
        } else {
            return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, type.handlePlacementFacing(context, HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()));
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileEntityMachine machine = (TileEntityMachine) getType().getTileType().create();
        machine.ofState(state);
        return machine;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isRemote) {
            TileEntityMachine<?> tile = (TileEntityMachine<?>) worldIn.getTileEntity(pos);
            if (tile != null) {
                tile.onBlockUpdate(fromPos);
            }
        }
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType ty = onBlockActivatedBoth(state, world, pos, player, hand, hit);
        if (ty.isSuccessOrConsume()) return ty;
        if (!world.isRemote) {
            TileEntityMachine<?> tile = (TileEntityMachine<?>) world.getTileEntity(pos);
            if (tile != null) {
                ItemStack stack = player.getHeldItem(hand);
                AntimatterToolType type = Utils.getToolType(player);
                ty = tile.onInteract(state, world, pos, player, hand, hit, type);
                if (ty.isSuccessOrConsume()) return ty;
                if (hand == Hand.MAIN_HAND) {
                    if (player.getHeldItem(hand).getItem() instanceof IHaveCover) {
                        CoverFactory factory = ((IHaveCover) stack.getItem()).getCover();
                        Direction dir = Utils.getInteractSide(hit);
                        boolean ok = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, Utils.getInteractSide(hit)).map(i -> i.placeCover(player, Utils.getInteractSide(hit), stack, factory.get().get(i, ((IHaveCover) stack.getItem()).getTier(), dir, factory))).orElse(false);
                        if (ok) {
                            return ActionResultType.SUCCESS;
                        }
                    }
                    //Handle tool types.
                    if (type == WRENCH || type == ELECTRIC_WRENCH) {
                        if (tile.wrenchMachine(player, hit, player.isCrouching())) {
                            return ActionResultType.SUCCESS;
                        }
                    } else if (type == SOFT_HAMMER) {
                        tile.toggleMachine();
                        if (tile.getMachineState() == MachineState.DISABLED) {
                            player.sendMessage(new StringTextComponent("Disabled machine."), player.getUniqueID());
                        } else {
                            player.sendMessage(new StringTextComponent("Enabled machine."), player.getUniqueID());
                        }
                        Utils.damageStack(stack, player);
                        return ActionResultType.SUCCESS;
                    } else if (type == CROWBAR) {
                        if (!player.isCrouching()) {
                            if (tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.removeCover(player, Utils.getInteractSide(hit), false)).orElse(false)) {
                                Utils.damageStack(stack, player);
                                return ActionResultType.SUCCESS;
                            }
                        } else {
                            if (tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.moveCover(player, hit.getFace(), Utils.getInteractSide(hit))).orElse(false)) {
                                Utils.damageStack(stack, player);
                                return ActionResultType.SUCCESS;
                            }
                        }
                    } else if (type == SCREWDRIVER || type == ELECTRIC_SCREWDRIVER) {
                        ICover instance = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.get(hit.getFace())).orElse(ICover.empty);
                        if (!player.isCrouching()) {
                            if (!instance.isEmpty() && instance.openGui(player, hit.getFace())) {
                                Utils.damageStack(stack, player);
                                return ActionResultType.SUCCESS;
                            }
                        }
                    }
                    boolean coverInteract = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, hit.getFace()).map(h -> h.onInteract(player, hand, hit.getFace(), Utils.getToolType(player))).orElse(false);
                    if (coverInteract) return ActionResultType.SUCCESS;
                    //Has gui?
                    if (tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).map(fh -> {
                        FluidActionResult res = FluidUtil.tryEmptyContainer(stack, fh, 1000, player, true);
                        if (res.isSuccess() && !player.isCreative()) {
                            boolean single = stack.getCount() == 1;
                            stack.shrink(1);
                            if (single) {
                                player.setHeldItem(hand, res.result);
                            } else {
                                if (!player.addItemStackToInventory(res.result)) {
                                    player.dropItem(res.result, true);
                                }
                            }

                        }
                        if (!res.isSuccess()) {
                            res = FluidUtil.tryFillContainer(stack, fh, 1000, player, true);
                            if (res.isSuccess() && !player.isCreative()) {
                                boolean single = stack.getCount() == 1;
                                stack.shrink(1);
                                if (single) {
                                    player.setHeldItem(hand, res.result);
                                } else {
                                    if (!player.addItemStackToInventory(res.result)) {
                                        player.dropItem(res.result, true);
                                    }
                                }
                            }
                        }
                        return res.isSuccess();
                    }).orElse(false)) {
                        return ActionResultType.SUCCESS;
                    }
                    if (getType().has(MachineFlag.GUI) && tile.canPlayerOpenGui(player)) {
                        NetworkHooks.openGui((ServerPlayerEntity) player, tile, extra -> {
                            extra.writeBlockPos(pos);
                        });
                        return ActionResultType.SUCCESS;
                    }
                    return ActionResultType.PASS;
                }
            }
        }
        return ActionResultType.CONSUME;
    }

    //This is also a hack. Since the game relies on multiblock checks being done on both sides this method is split out.
    protected ActionResultType onBlockActivatedBoth(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        /*TileEntityMachine tile = (TileEntityMachine)world.getTileEntity(pos);
        if (tile != null) {
            AntimatterToolType type = Utils.getToolType(player);
            if (type == WRENCH || type == ELECTRIC_WRENCH) {
                if (player.isCrouching()) {
                    boolean ok = tile.setFacing(Utils.getInteractSide(hit));
                    return ok ? ActionResultType.CONSUME : ActionResultType.PASS;
                }
            }
        }*/
        return ActionResultType.PASS;
    }

    /* //This messes up cover logic.
        @Override
        public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
            if (placer != null) { //Y = 0 , reduce to xz plane
                float y = (float) (type.allowVerticalFacing() ? placer.getLookVec().y : 0);
                Direction dir = getFacingFromVector((float) placer.getLookVec().x, y, (float) placer.getLookVec().z).getOpposite();
                BlockState state1 = state.with((type.allowVerticalFacing() ? BlockStateProperties.FACING : BlockStateProperties.HORIZONTAL_FACING), dir);
                if (type.allowVerticalFacing()) state1 = state1.with(HORIZONTAL_FACING, placer.getHorizontalFacing().getOpposite());
                world.setBlockState(pos, state1);
            }
        }
    */
    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return Data.WRENCH.getToolType();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.getBlock().matchesBlock(newState.getBlock())) {
            if (!worldIn.isRemote) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile == null) return;
                TileEntityMachine<?> machine = (TileEntityMachine<?>) tile;
                machine.itemHandler.ifPresent(t -> t.getAllItems().forEach(stack -> InventoryHelper.spawnItemStack(worldIn, machine.getPos().getX(), machine.getPos().getY(), machine.getPos().getZ(), stack)));
                machine.coverHandler.ifPresent(t -> t.getDrops().forEach(stack -> InventoryHelper.spawnItemStack(worldIn, machine.getPos().getX(), machine.getPos().getY(), machine.getPos().getZ(), stack)));
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return getType().getDisplayName(getTier());
    }

    @Override
    public ItemGroup getItemGroup() {
        return getType().getGroup();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (getType().has(BASIC)) {
            if (getTier().getVoltage() > 0) {
                tooltip.add(new TranslationTextComponent("machine.voltage.in").appendString(": ").appendSibling(new StringTextComponent(getTier().getVoltage() + " (" + getTier().getId().toUpperCase() + ")")).mergeStyle(TextFormatting.GREEN));
                tooltip.add(new TranslationTextComponent("machine.power.capacity").appendString(": ").appendSibling(new StringTextComponent("" + (getTier().getVoltage() * 64))).mergeStyle(TextFormatting.BLUE));
            }
        }
    }

    //This makes no sense because it is manually derived by testing, to make sure covers render properly.
    //Probably needs a rewrite of the model logic to get rid of this mess.
    private Direction getDir(Direction hFace, Direction which, Direction face) {
        if (which.getAxis() == Axis.Y) return which.getOpposite();
        switch (hFace) {
            case NORTH:
                if (which.getAxis() == Axis.Z) return which.getOpposite();
                break;
            case SOUTH:
                if (which.getAxis() == Axis.X) return which.getOpposite();
                break;
            case EAST:
                switch (which) {
                    case SOUTH:
                        return EAST;
                    case EAST:
                        return SOUTH;
                    case NORTH:
                        return WEST;
                    case WEST:
                        return NORTH;
                }
                break;
            case WEST:
                switch (which) {
                    case SOUTH:
                        return WEST;
                    case WEST:
                        return SOUTH;
                    case NORTH:
                        return EAST;
                    case EAST:
                        return NORTH;
                }
                break;
        }
        return which;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        Direction facing = type.allowVerticalFacing() ? state.get(BlockStateProperties.FACING) : state.get(BlockStateProperties.HORIZONTAL_FACING);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMachine) {
            MachineState machineState = ((TileEntityMachine<?>) tile).getMachineState();
            if (((TileEntityMachine<?>) tile).coverHandler.isPresent()) {
                CoverHandler<?> h = ((TileEntityMachine<?>) tile).coverHandler.orElse(null);
                if (type.allowVerticalFacing() && facing.getAxis() == Axis.Y) {
                    Direction horizontalFacing = state.get(HORIZONTAL_FACING);
                    return config.set(new int[]{
                            h.get(DOWN).isEmpty() ? getModelId(facing, horizontalFacing, Utils.coverRotateFacing(getDir(horizontalFacing, DOWN, facing), facing), machineState) : 0,
                            h.get(UP).isEmpty() ? getModelId(facing, horizontalFacing, Utils.coverRotateFacing(getDir(horizontalFacing, UP, facing), facing), machineState) : 0,
                            h.get(NORTH).isEmpty() ? getModelId(facing, horizontalFacing, Utils.coverRotateFacing(getDir(horizontalFacing, NORTH, facing), facing), machineState) : 0,
                            h.get(SOUTH).isEmpty() ? getModelId(facing, horizontalFacing, Utils.coverRotateFacing(getDir(horizontalFacing, SOUTH, facing), facing), machineState) : 0,
                            h.get(WEST).isEmpty() ? getModelId(facing, horizontalFacing, Utils.coverRotateFacing(getDir(horizontalFacing, WEST, facing), facing), machineState) : 0,
                            h.get(EAST).isEmpty() ? getModelId(facing, horizontalFacing, Utils.coverRotateFacing(getDir(horizontalFacing, EAST, facing), facing), machineState) : 0
                    });
                }
                return config.set(new int[]{
                        h.get(DOWN).isEmpty() ? getModelId(facing, DOWN, machineState) : 0,
                        h.get(UP).isEmpty() ? getModelId(facing, UP, machineState) : 0,
                        h.get(NORTH).isEmpty() ? getModelId(facing, Utils.coverRotateFacing(NORTH, facing), machineState) : 0,
                        h.get(SOUTH).isEmpty() ? getModelId(facing, Utils.coverRotateFacing(SOUTH, facing), machineState) : 0,
                        h.get(WEST).isEmpty() ? getModelId(facing, Utils.coverRotateFacing(WEST, facing), machineState) : 0,
                        h.get(EAST).isEmpty() ? getModelId(facing, Utils.coverRotateFacing(EAST, facing), machineState) : 0
                });
            } else {
                if (type.allowVerticalFacing() && facing.getAxis() == Axis.Y) {
                    Direction horizontalFacing = state.get(HORIZONTAL_FACING);
                    int[] configInts = new int[]{
                            getModelId(facing, horizontalFacing, DOWN, machineState),
                            getModelId(facing, horizontalFacing, UP, machineState),
                            getModelId(facing, horizontalFacing, NORTH, machineState),
                            getModelId(facing, horizontalFacing, SOUTH, machineState),
                            getModelId(facing, horizontalFacing, WEST, machineState),
                            getModelId(facing, horizontalFacing, EAST, machineState)
                    };
                    return config.set(configInts);
                }
                return config.set(new int[]{
                        getModelId(facing, DOWN, machineState),
                        getModelId(facing, UP, machineState),
                        getModelId(facing, NORTH, machineState),
                        getModelId(facing, SOUTH, machineState),
                        getModelId(facing, WEST, machineState),
                        getModelId(facing, EAST, machineState)
                });
            }
        }
        return config.set(new int[]{0});
    }

    protected int getModelId(Direction facing, Direction horizontalFacing, Direction overlay, MachineState state) {
        state = (state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE; //Map to only ACTIVE/IDLE.
        return ((state.ordinal() + 1) * 10000) + ((facing.getIndex() + 1) * 1000) + ((horizontalFacing.getIndex() + 1) * 100) + (overlay.getIndex() + 1);
    }

    protected int getModelId(Direction facing, Direction overlay, MachineState state) {
        state = (state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE; //Map to only ACTIVE/IDLE.
        return ((state.ordinal() + 1) * 10000) + ((facing.getIndex() + 1) * 1000) + (overlay.getIndex() + 1);
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ItemModelBuilder b = prov.getBuilder(item).parent(prov.existing(Ref.ID, "block/preset/layered")).texture("base", type.getBaseTexture(tier)[0]);
        Texture[] base = type.getBaseTexture(tier);
        if (base.length >= 6) {
            for (int s = 0; s < 6; s++) {
                b.texture("base" + Ref.DIRS[s].getString(), base[s]);
            }
        }
        Texture[] overlays = type.getOverlayTextures(MachineState.ACTIVE, tier);
        for (int s = 0; s < 6; s++) {
            b.texture("overlay" + Ref.DIRS[s].getString(), overlays[s]);
        }
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        buildModelsForState(builder, MachineState.IDLE);
        buildModelsForState(builder, MachineState.ACTIVE);
        builder.loader(AntimatterModelManager.LOADER_MACHINE);
        builder.property("particle", getType().getBaseTexture(tier)[0].toString());
        prov.state(block, builder);
    }

    void buildModelsForState(AntimatterBlockModelBuilder builder, MachineState state) {
        Texture[] overlays = type.getOverlayTextures(state, tier);
        for (Direction f : Plane.HORIZONTAL) {
            for (Direction o : Ref.DIRS) {
                builder.config(getModelId(f, o, state), (b, l) -> l.add(b.of(type.getOverlayModel(o)).tex(of("base", type.getBaseTexture(tier, o), "overlay", overlays[o.getIndex()])).rot(f)));
            }
        }
        if (type.allowVerticalFacing()) {
            for (Direction f : Plane.VERTICAL) {
                for (Direction h : Plane.HORIZONTAL) {
                    for (Direction o : Ref.DIRS) {
                        builder.config(getModelId(f, h, o, state), (b, l) -> l.add(b.of(type.getOverlayModel(o)).tex(of("base", type.getBaseTexture(tier, o), "overlay", overlays[o.getIndex()])).rot(f, h)));
                    }
                }
            }
        }
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        TileEntity entity = blockAccess.getTileEntity(pos);
        if (entity instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) entity;
            return machine.getWeakRedstonePower(side);
        }
        return super.getWeakPower(blockState, blockAccess, pos, side);
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        TileEntity entity = blockAccess.getTileEntity(pos);
        if (entity instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) entity;
            return machine.getStrongRedstonePower(side);
        }
        return super.getStrongPower(blockState, blockAccess, pos, side);
    }
}
