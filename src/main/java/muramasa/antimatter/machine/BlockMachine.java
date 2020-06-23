package muramasa.antimatter.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IInteractHandler;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.machine.MachineFlag.BASIC;

public class BlockMachine extends BlockDynamic implements IAntimatterObject, IItemBlockProvider, IColorHandler {

    static int OFFSET_ACTIVE = 5;

    protected Machine<?> type;
    protected Tier tier;

    public BlockMachine(Machine<?> type, Tier tier) {
        super(type.getDomain(), type.getId() + "_" + tier.getId(), Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
        this.type = type;
        this.tier = tier;
    }

    public Machine<?> getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        //TODO determine if shouldRefresh func needs to be added back in
        //return (oldState.getBlock() != newState.getBlock());
        super.onReplaced(state, world, pos, newState, isMoving);
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getType().getTileType().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) { //Only try opening containers server side
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                //TODO: priority order, or call this inside InteractHandler?
                //I am not sure if the cover interaction is supposed to be done from IInteractHandler.

                //TODO: This runs twice when right clicking a machine!
                LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE, hit.getFace());
                boolean consume = coverable.map(i -> {
                    return i.onInteract(player, hand, hit.getFace(), Utils.getToolType(player));
                }).orElse(false);
                if (consume) {
                    return ActionResultType.SUCCESS;
                }
                if (getType().has(MachineFlag.GUI) && tile instanceof INamedContainerProvider) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
                    return ActionResultType.SUCCESS;
                }
                LazyOptional<IInteractHandler> interaction = tile.getCapability(AntimatterCaps.INTERACTABLE);
                interaction.ifPresent(i -> i.onInteract(player, hand, hit.getFace(), Utils.getInteractSide(hit), Utils.getToolType(player)));
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile != null && GregTechAPI.interact(tile, player, hand, side, hitX, hitY, hitZ)) return true;
//        if (tile instanceof TileEntityMachine) {
//            TileEntityMachine machine = (TileEntityMachine) tile;
//            //TODO machine gui member -> Optional<GuiData>?
//            //TODO possibly drop flags for optionals?
//            if (machine.getMachineType().hasFlag(GUI)) {
//                GuiData gui = machine.getMachineType().getGui();
//                player.openGui(gui.getInstance(), gui.getGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null) {
            //Y = 0 , reduce to xz plane
            Direction dir = Direction.getFacingFromVector((float) placer.getLookVec().x, (float) 0, (float) placer.getLookVec().z).getOpposite();
            world.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, dir));
        }
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return Data.WRENCH.getToolType();
    }

//    @Override
//    public void breakBlock(World world, BlockPos pos, BlockState state) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityMachine) {
//            TileEntityMachine machine = (TileEntityMachine) tile;
//            machine.itemHandler.ifPresent(h -> {
//                h.getInputList().forEach(i -> Utils.spawnItems(world, pos, null, i));
//                h.getOutputList().forEach(i -> Utils.spawnItems(world, pos, null, i));
//            });
//        }
//    }

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
            tooltip.add(new TranslationTextComponent("machine.voltage.in").appendText(TextFormatting.GREEN + "" + getTier().getVoltage() + " (" + getTier().getId().toUpperCase() + ")"));
            tooltip.add(new TranslationTextComponent("machine.power.capacity").appendText(TextFormatting.BLUE + "" + (getTier().getVoltage() * 64)));
        }
    }

    //return id given dir & state
    private int getModelId(Direction dir, MachineState state) {
        //Map to only ACTIVE/IDLE.
        state = (state == MachineState.ACTIVE) ? MachineState.ACTIVE : MachineState.IDLE;
        switch (dir) {
            case NORTH:
            case UP:
            case DOWN:
                return state.ordinal();
            default:
                return (dir.getIndex()*100) + (state.ordinal());
        }
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockMachine) && world == null || pos == null) return -1;
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof TileEntityMachine && i == 0 ? /*((TileEntityMachine) tile).getTextureData().getTint()*/-1 : -1;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        Direction dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMachine) {
            MachineState machineState = ((TileEntityMachine) tile).getMachineState();
            return config.set(new int[]{getModelId(dir, machineState)});
        }
        return config.set(new int[]{0});
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ItemModelBuilder b = prov.getBuilder(item).parent(prov.existing(Ref.ID, "block/preset/layered")).texture("base", type.getBaseTexture(tier));
        Texture[] overlays = type.getOverlayTextures(MachineState.ACTIVE);
        for (int s = 0; s < 6; s++) {
            b.texture("overlay" + Ref.DIRECTIONS[s].getName(), overlays[s]);
        }
    }

    //registers textures for a given machine state
    private void registerForState(AntimatterBlockModelBuilder builder, MachineState state, int offset) {
        Texture[] overlays = type.getOverlayTextures(state);
        ResourceLocation zero = type.getOverlayModel(Ref.DIRECTIONS[0]);
        ResourceLocation one = type.getOverlayModel(Ref.DIRECTIONS[1]);
        ResourceLocation two = type.getOverlayModel(Ref.DIRECTIONS[2]);
        ResourceLocation three = type.getOverlayModel(Ref.DIRECTIONS[3]);
        ResourceLocation four = type.getOverlayModel(Ref.DIRECTIONS[4]);
        ResourceLocation five = type.getOverlayModel(Ref.DIRECTIONS[5]);
        Texture base = tier.getBaseTexture();

        //Default.
        builder.config(getModelId(Direction.NORTH,state), (b, l) -> l.add(
                b.of(zero).tex(of("base", base, "overlay", overlays[0])),
                b.of(one).tex(of("base", base, "overlay", overlays[1])),
                b.of(two).tex(of("base", base, "overlay", overlays[2])),
                b.of(three).tex(of("base", base, "overlay", overlays[3])),
                b.of(four).tex(of("base", base, "overlay", overlays[4])),
                b.of(five).tex(of("base", base, "overlay", overlays[5]))
        ));

        builder.config(getModelId(Direction.WEST, state), (b, l) -> l.add(
                b.of(zero).tex(of("base", base, "overlay", overlays[0])).rot(0, 90, 0),
                b.of(one).tex(of("base", base, "overlay", overlays[1])).rot(0, 90, 0),
                b.of(two).tex(of("base", base, "overlay", overlays[2])).rot(0, 90, 0),
                b.of(three).tex(of("base", base, "overlay", overlays[3])).rot(0, 90, 0),
                b.of(four).tex(of("base", base, "overlay", overlays[4])).rot(0, 90, 0),
                b.of(five).tex(of("base", base, "overlay", overlays[5])).rot(0, 90, 0)
        ));

        builder.config(getModelId(Direction.EAST,state), (b, l) -> l.add(
                b.of(zero).tex(of("base", base, "overlay", overlays[0])).rot(0, -90, 0),
                b.of(one).tex(of("base", base, "overlay", overlays[1])).rot(0, -90, 0),
                b.of(two).tex(of("base", base, "overlay", overlays[2])).rot(0, -90, 0),
                b.of(three).tex(of("base", base, "overlay", overlays[3])).rot(0, -90, 0),
                b.of(four).tex(of("base", base, "overlay", overlays[4])).rot(0, -90, 0),
                b.of(five).tex(of("base", base, "overlay", overlays[5])).rot(0, -90, 0)
        ));

        builder.config(getModelId(Direction.SOUTH, state), (b, l) -> l.add(
                b.of(zero).tex(of("base", base, "overlay", overlays[0])).rot(0, 180, 0),
                b.of(one).tex(of("base", base, "overlay", overlays[1])).rot(0, 180, 0),
                b.of(two).tex(of("base", base, "overlay", overlays[2])).rot(0, 180, 0),
                b.of(three).tex(of("base", base, "overlay", overlays[3])).rot(0, 180, 0),
                b.of(four).tex(of("base", base, "overlay", overlays[4])).rot(0, 180, 0),
                b.of(five).tex(of("base", base, "overlay", overlays[5])).rot(0, 180, 0)
        ));
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        registerForState(builder, MachineState.IDLE,0);
        registerForState(builder, MachineState.ACTIVE, OFFSET_ACTIVE);
        prov.state(block, builder);
    }
}
