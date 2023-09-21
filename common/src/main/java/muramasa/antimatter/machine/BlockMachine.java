package muramasa.antimatter.machine;

import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.BlockEntityTickable;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.SoundHelper;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.AntimatterItemModelBuilder;
import muramasa.antimatter.datagen.json.JLoaderModel;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.AntimatterCapUtils;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.WRENCH_MATERIAL;
import static muramasa.antimatter.machine.MachineFlag.BASIC;
import static muramasa.antimatter.machine.MachineFlag.RF;

public class BlockMachine extends BlockBasic implements IItemBlockProvider, EntityBlock {
    public static final EnumProperty<MachineState> MACHINE_STATE = EnumProperty.create("machine_state", MachineState.class, MachineState.IDLE, MachineState.ACTIVE);

    protected Machine<?> type;
    protected Tier tier;
    protected final StateDefinition<Block, BlockState> stateContainer;

    public BlockMachine(Machine<?> type, Tier tier) {
        this(type, tier, Properties.of(WRENCH_MATERIAL).strength(1.0f, 10.0f).sound(SoundType.METAL).requiresCorrectToolForDrops());
    }

    public BlockMachine(Machine<?> type, Tier tier, Properties properties) {
        super(type.getDomain(), type.getId() + (tier == Tier.NONE ? "" : "_" + tier.getId()), properties);
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.type = type;
        this.tier = tier;
        this.createBlockStateDefinition(builder);
        this.stateContainer = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateContainer.any());
    }

    public Machine<?> getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (type == null) return; // means this is the first run
        if (type.allowVerticalFacing()) {
            builder.add(BlockStateProperties.FACING).add(MACHINE_STATE);
        } else {
            builder.add(BlockStateProperties.HORIZONTAL_FACING).add(MACHINE_STATE);
        }
    }

    @Override
    public StateDefinition<Block, BlockState> getStateDefinition() {
        return stateContainer;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (type.allowVerticalFacing()) {
            Direction dir = context.getNearestLookingDirection().getOpposite();
            dir = dir.getAxis() == Axis.Y ? dir.getOpposite() : dir;
            return this.defaultBlockState().setValue(BlockStateProperties.FACING, type.handlePlacementFacing(context, BlockStateProperties.FACING, dir));
        } else {
            return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, type.handlePlacementFacing(context, BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()));
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        BlockEntityMachine<?> tile = (BlockEntityMachine<?>) worldIn.getBlockEntity(pos);
        if (tile != null) {
            tile.onBlockUpdate(fromPos);
        }
    }


    @NotNull
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult ty; //= onBlockActivatedBoth(state, world, pos, player, hand, hit);
        BlockEntityMachine<?> tile = (BlockEntityMachine<?>) world.getBlockEntity(pos);
        if (tile != null) {
            ItemStack stack = player.getItemInHand(hand);
            AntimatterToolType type = Utils.getToolType(player);
            ty = tile.onInteractBoth(state, world, pos, player, hand, hit, type);
            if (ty.consumesAction()) return ty;
            if (!world.isClientSide) {
                if (hand == InteractionHand.MAIN_HAND) {
                    if (player.getItemInHand(hand).getItem() instanceof IHaveCover) {
                        CoverFactory factory = ((IHaveCover) stack.getItem()).getCover();
                        Direction dir = Utils.getInteractSide(hit);
                        boolean ok = AntimatterCapUtils.getCoverHandler(tile, Utils.getInteractSide(hit)).map(i -> i.placeCover(player, Utils.getInteractSide(hit), stack, factory.get().get(i, ((IHaveCover) stack.getItem()).getTier(), dir, factory))).orElse(false);
                        if (ok) {
                            return InteractionResult.SUCCESS;
                        }
                    }
                    //Handle tool types.
                    if (type == AntimatterDefaultTools.WRENCH || type == AntimatterDefaultTools.ELECTRIC_WRENCH) {
                        if (tile.wrenchMachine(player, hit, player.isCrouching())) {
                            Utils.damageStack(stack, hand, player);
                            return InteractionResult.SUCCESS;
                        }
                    } else if (type == AntimatterDefaultTools.SOFT_HAMMER) {
                        tile.toggleMachine();
                        if (tile.getMachineState() == MachineState.DISABLED) {
                            player.sendMessage(Utils.literal("Disabled machine."), player.getUUID());
                        } else {
                            player.sendMessage(Utils.literal("Enabled machine."), player.getUUID());
                        }
                        Utils.damageStack(stack, player);
                        return InteractionResult.SUCCESS;
                    } else if (type == AntimatterDefaultTools.CROWBAR) {
                        if (!player.isCrouching()) {
                            if (AntimatterCapUtils.getCoverHandler(tile, Utils.getInteractSide(hit)).map(h -> h.removeCover(player, Utils.getInteractSide(hit), false)).orElse(false)) {
                                Utils.damageStack(stack,hand, player);
                                return InteractionResult.SUCCESS;
                            }
                        } else {
                            if (AntimatterCapUtils.getCoverHandler(tile, Utils.getInteractSide(hit)).map(h -> h.moveCover(player, hit.getDirection(), Utils.getInteractSide(hit))).orElse(false)) {
                                Utils.damageStack(stack,hand, player);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    } else if (type == AntimatterDefaultTools.SCREWDRIVER || type == AntimatterDefaultTools.ELECTRIC_SCREWDRIVER) {
                        ICover instance = AntimatterCapUtils.getCoverHandler(tile, Utils.getInteractSide(hit)).map(h -> h.get(Utils.getInteractSide(hit))).orElse(ICover.empty);
                        if (!player.isCrouching()) {
                            if (!instance.isEmpty() && instance.openGui(player, Utils.getInteractSide(hit))) {
                                Utils.damageStack(stack,hand, player);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                    boolean coverInteract = AntimatterCapUtils.getCoverHandler(tile, Utils.getInteractSide(hit)).map(h -> h.onInteract(player, hand, Utils.getInteractSide(hit), Utils.getToolType(player))).orElse(false);
                    if (coverInteract) return InteractionResult.SUCCESS;
                    //Has gui?
                    if (FluidHooks.safeGetBlockFluidManager(tile, hit.getDirection()).map(fh -> {
                        Consumer<ItemStack> consumer = s -> {
                            if (player.isCreative()) return;
                            boolean single = stack.getCount() == 1;
                            stack.shrink(1);
                            if (single) {
                                player.setItemInHand(hand, s);
                            } else {
                                if (!player.addItem(s)) {
                                    player.drop(s, true);
                                }
                            }
                        };
                        boolean success = false;
                        if (FluidPlatformUtils.fillItemFromContainer(Utils.ca(1, stack), fh, consumer)){
                            success = true;
                        } else if (FluidPlatformUtils.emptyItemIntoContainer(Utils.ca(1, stack), fh, consumer)){
                            success = true;
                        }
                        return success;
                    }).orElse(false)) {
                        return InteractionResult.SUCCESS;
                    }
                    if (getType().has(MachineFlag.GUI) && tile.canPlayerOpenGui(player)) {
                        AntimatterPlatformUtils.openGui((ServerPlayer) player, tile, extra -> {
                            extra.writeBlockPos(pos);
                        });
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> list = super.getDrops(state, builder);
        BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tileentity instanceof BlockEntityMachine<?> machine){
            machine.onDrop(state, builder, list);
            machine.itemHandler.ifPresent(t -> list.addAll(t.getAllItems()));
            machine.coverHandler.ifPresent(t -> list.addAll(t.getDrops()));
        }
        return list;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BlockEntityMachine<?> machine){
            machine.onPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (worldIn.isClientSide) {
                SoundHelper.clear(worldIn, pos);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public Component getDisplayName(ItemStack stack) {
        return getType().getDisplayName(getTier());
    }

    @Override
    public CreativeModeTab getItemGroup() {
        return getType().getGroup();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        if (getType().has(BASIC) && !getType().has(RF)) {
            if (getTier().getVoltage() > 0 && getType().has(MachineFlag.ENERGY)) {
                tooltip.add(Utils.translatable("machine.voltage.in").append(": ").append(Utils.literal(getTier().getVoltage() + " (" + getTier().getId().toUpperCase() + ")")).withStyle(ChatFormatting.GREEN));
                tooltip.add(Utils.translatable("machine.power.capacity").append(": ").append(Utils.literal("" + (getTier().getVoltage() * 64L))).withStyle(ChatFormatting.BLUE));
            }
        }
        this.type.getTooltipFunction().getTooltips(this, stack, world, tooltip, flag);
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        AntimatterItemModelBuilder b = prov.getBuilder(item).parent(prov.existing(Ref.ID, "block/preset/layered")).texture("base", type.getBaseTexture(tier)[0]);
        Texture[] base = type.getBaseTexture(tier);
        if (base.length >= 6) {
            for (int s = 0; s < 6; s++) {
                b.texture("base" + Utils.coverRotateFacing(Ref.DIRS[s], Direction.NORTH).getSerializedName(), base[s]);
            }
        }
        Texture[] overlays = type.getOverlayTextures(MachineState.ACTIVE, tier);
        for (int s = 0; s < 6; s++) {
            b.texture("overlay" + Utils.coverRotateFacing(Ref.DIRS[s], Direction.NORTH).getSerializedName(), overlays[s]);
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
        List<JLoaderModel> arr = new ArrayList<>();

        for (Direction dir : Ref.DIRS) {
            JLoaderModel obj = builder.addModelObject(JLoaderModel.modelKeepElements(), this.getType().getOverlayModel(state, dir).toString(), of("base", getType().getBaseTexture(tier, dir).toString(), "overlay", overlays[dir.get3DDataValue()].toString()));
            //obj.loader(AntimatterModelManager.LOADER_MACHINE_SIDE.getLoc().toString());
            arr.add(obj);
        }

        builder.property(state.toString().toLowerCase(), arr);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof BlockEntityMachine) {
            BlockEntityMachine<?> machine = (BlockEntityMachine<?>) entity;
            return machine.getWeakRedstonePower(side);
        }
        return super.getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof BlockEntityMachine) {
            BlockEntityMachine<?> machine = (BlockEntityMachine<?>) entity;
            return machine.getStrongRedstonePower(side);
        }
        return super.getDirectSignal(blockState, blockAccess, pos, side);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        BlockEntityMachine<?> machine = (BlockEntityMachine) getType().getTileType().create(pos, state);
        return machine;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() && !getType().canClientTick()) return null;
        return BlockEntityTickable::commonTick;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        if (!type.isAmbientTicking()) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockEntityMachine<?> machine){
            machine.animateTick(state, level, pos, random);
        }
    }
}
