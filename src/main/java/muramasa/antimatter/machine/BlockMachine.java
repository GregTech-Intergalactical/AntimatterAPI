package muramasa.antimatter.machine;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockBasic;
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
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.BASIC;

public class BlockMachine extends BlockBasic implements IAntimatterObject, IItemBlockProvider, IColorHandler {

    protected Machine type;
    protected Tier tier;

    public BlockMachine(String domain, Machine type, Tier tier) {
        super(domain, type.getId() + "_" + tier.getId(), Properties.create(Material.IRON).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.METAL));
        this.type = type;
        this.tier = tier;
        AntimatterAPI.register(BlockMachine.class, this);
    }

    public Machine getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        //TODO determine if shouldRefresh func needs to be added back in
        //return (oldState.getBlock() != newState.getBlock());
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getType().getTileType().create();
    }

//    @Nullable
//    @Override
//    public TileEntity createTileEntity(World world) {
//        try {
//            return (TileEntityMachine) type.getTileClass().newInstance();
//        } catch (IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//            throw new IllegalArgumentException("Was not able to instantiate a TileEntity class for: " + type);
//        }
//    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) { //Only try opening containers server side
            TileEntity tile = Utils.getTile(world, pos);
            if (getType().hasFlag(MachineFlag.GUI) && tile instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

//    @Override
//    public boolean onBldockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
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
        Direction dir = Direction.getFacingFromVector((float)placer.getLookVec().x, (float)placer.getLookVec().y, (float)placer.getLookVec().z).getOpposite();
        if (dir.getAxis().isVertical()) dir = Direction.NORTH; //TODO fix
        world.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, dir));
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return AntimatterAPI.WRENCH_TOOL_TYPE;
    }

    /** TileEntity Drops Start **/
//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityMachine) drops.add(new MachineStack(((TileEntityMachine) tile).type, ((TileEntityMachine) tile).tier).asItemStack());
//    }
//
//    @Override
//    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
//        if (willHarvest) return true;
//        return super.removedByPlayer(state, world, pos, player, willHarvest);
//    }
//
//    @Override
//    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tile, ItemStack stack) {
//        super.harvestBlock(world, player, pos, state, tile, stack);
//        world.setBlockToAir(pos);
//    }

    /** TileEntity Drops End **/

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
        if (getType().hasFlag(BASIC)) {
            tooltip.add(new TranslationTextComponent("machine.voltage.in").appendText(TextFormatting.GREEN + "" + getTier().getVoltage() + " (" + getTier().getId().toUpperCase() + ")"));
            tooltip.add(new TranslationTextComponent("machine.power.capacity").appendText(TextFormatting.BLUE + "" + (getTier().getVoltage() * 64)));
        }
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockMachine) && world == null || pos == null) return -1;
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof TileEntityMachine && i == 0 ? /*((TileEntityMachine) tile).getTextureData().getTint()*/-1 : -1;
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ItemModelBuilder b = prov.getBuilder(item).parent(prov.existing(Ref.ID, "block/preset/layered")).texture("base", type.getBaseTexture(tier));
        Texture[] overlays = type.getOverlayTextures(MachineState.ACTIVE);
        for (int i = 0; i < 6; i++) {
            b.texture("overlay" + Ref.DIRECTIONS[i].getName(), overlays[i]);
        }
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        prov.state(block, prov.getBuilder(block).config(0, (b, l) -> {
            return l.add(
                b.of(type.getOverlayModel("front")),
                b.of(type.getOverlayModel("top"))
            );
        }));
    }
}
