package muramasa.antimatter.datagen.providers;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.*;
import muramasa.antimatter.datagen.builder.VariantBlockStateBuilder.VariantBuilder;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AntimatterBlockStateProvider implements IAntimatterProvider {

    protected final String modid, providerName;
    protected final AntimatterBlockModelProvider blockModelProvider;
    protected final Map<Block, IStateBuilder> registeredBlocks = new LinkedHashMap<>();

    public AntimatterBlockStateProvider(String modid, String providerName) {
        this(modid, providerName, AntimatterBlockModelProvider::new);
    }

    public AntimatterBlockStateProvider(String modid, String providerName, BiFunction<String, String, AntimatterBlockModelProvider> function) {
        this.modid = modid;
        this.providerName = providerName;
        this.blockModelProvider = function.apply(modid, providerName);
    }

    @Override
    public void run(HashCache cache) throws IOException {

    }

    @NotNull
    @Override
    public String getName() {
        return providerName;
    }

    protected void registerStatesAndModels() {
        processBlocks(modid);
    }

    @Override
    public void run() {
        registerStatesAndModels();
    }

    @Override
    public void onCompletion() {
        models().buildAll();
        registeredBlocks.forEach((b, s) -> {
            if (AntimatterPlatformUtils.INSTANCE.getIdFromBlock(b) == null) { //TODO ?
                BlockBasic block = (BlockBasic) b;
            } else {
                AntimatterDynamics.DYNAMIC_RESOURCE_PACK.addBlockState(s.toState(), AntimatterPlatformUtils.INSTANCE.getIdFromBlock(b));
            }
        });
    }

    public AntimatterBlockModelProvider models() {
        return blockModelProvider;
    }

    public void processBlocks(String domain) {
        AntimatterAPI.all(Block.class, domain).forEach(b -> AntimatterModelManager.onBlockModelBuild(b, this));
        AntimatterAPI.all(AntimatterFluid.class, domain).forEach(f -> state(f.getFluidBlock(), getBuilder(f.getFluidBlock()).texture("particle", f.getAttributes().still())));
    }

    public AntimatterBlockModelBuilder getBuilder(Block block) {
        if (AntimatterPlatformUtils.INSTANCE.getIdFromBlock(block) == null) {
            return models().getBuilder(((BlockBasic) block).getId());
        }
        return models().getBuilder(AntimatterPlatformUtils.INSTANCE.getIdFromBlock(block).getPath());
    }

    public AntimatterBlockModelBuilder cubeAll(Block block, ResourceLocation texture) {
        return models().cubeAll(AntimatterPlatformUtils.INSTANCE.getIdFromBlock(block).toString(), texture);
    }

    public void state(Block block, IModelLocation model) {
        simpleBlock(block, model);
    }

    public void state(Block block, ResourceLocation... textures) {
        if (textures.length == 1) {
            simpleBlock(block, getSimpleModel(block, textures[0]));
        } else if (textures.length == 2) {
            simpleBlock(block, getLayeredModel(block, textures[0], textures[1]));
        } else if (textures.length == 6) {
            horizontalBlock(block, getSimpleModel(block, textures));
        } else if (textures.length == 12) {
            horizontalBlock(block, getLayeredModel(block, textures));
        }
    }

    public AntimatterBlockModelBuilder getSimpleModel(Block block, ResourceLocation texture) {
        return getBuilder(block).parent(loc(Ref.ID, "block/preset/simple")).texture("all", texture);
    }

    public AntimatterBlockModelBuilder getSimpleModel(Block block, ResourceLocation... texture) {
        return getBuilder(block).parent(loc(Ref.ID, "block/preset/simple")).texture("down", texture[0]).texture("up", texture[1]).texture("south", texture[2]).texture("north", texture[3]).texture("west", texture[4]).texture("east", texture[5]).texture("particle", texture[1]);
    }

    public AntimatterBlockModelBuilder getLayeredModel(Block block, ResourceLocation... texture) {
        return getBuilder(block).parent(loc(Ref.ID, "block/preset/layered")).texture("basedown", texture[0]).texture("baseup", texture[1]).texture("basesouth", texture[2]).texture("basenorth", texture[3]).texture("basewest", texture[4]).texture("baseeast", texture[5]).texture("overlaydown", texture[6]).texture("overlayup", texture[7]).texture("overlaysouth", texture[8]).texture("overlaynorth", texture[9]).texture("overlaywest", texture[10]).texture("overlayeast", texture[11]).texture("particle", texture[1]);
    }

    public AntimatterBlockModelBuilder getLayeredModel(Block block, ResourceLocation base, ResourceLocation overlay) {
        return getBuilder(block).parent(loc(Ref.ID, "block/preset/layered")).texture("base", base).texture("overlay", overlay);
    }

    public ResourceLocation existing(String domain, String path) {
        return loc(domain, path);
    }

    public ResourceLocation loc(String domain, String path) {
        return new ResourceLocation(domain, path);
    }

    private String name(Block block) {
        return AntimatterPlatformUtils.INSTANCE.getIdFromBlock(block).getPath();
    }


    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = AntimatterPlatformUtils.INSTANCE.getIdFromBlock(block);
        return new ResourceLocation(name.getNamespace(), AntimatterModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }

    public VariantBlockStateBuilder getVariantBuilder(Block b) {
        if (registeredBlocks.containsKey(b)) {
            IStateBuilder old = registeredBlocks.get(b);
            Preconditions.checkState(old instanceof VariantBlockStateBuilder);
            return (VariantBlockStateBuilder) old;
        } else {
            VariantBlockStateBuilder ret = new VariantBlockStateBuilder(b);
            registeredBlocks.put(b, ret);
            return ret;
        }
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
        if (registeredBlocks.containsKey(b)) {
            IStateBuilder old = registeredBlocks.get(b);
            Preconditions.checkState(old instanceof MultiPartBlockStateBuilder);
            return (MultiPartBlockStateBuilder) old;
        } else {
            MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
            registeredBlocks.put(b, ret);
            return ret;
        }
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }

    public AntimatterBlockModelBuilder cubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block));
    }

    public void simpleBlock(Block block) {
        simpleBlock(block, cubeAll(block));
    }

    public void simpleBlock(Block block, Function<IModelLocation, VariantBuilder> expander) {
        simpleBlock(block, expander.apply(cubeAll(block)));
    }

    public void simpleBlock(Block block, IModelLocation model) {
        getVariantBuilder(block).wildcard(new VariantBuilder().modelFile(model));
    }

    public void simpleBlock(Block block, VariantBuilder... models) {
        //getVariantBuilder(block)
        //        .partialState().setModels(models);
    }

    public void axisBlock(RotatedPillarBlock block) {
        axisBlock(block, blockTexture(block));
    }

    public void logBlock(RotatedPillarBlock block) {
        axisBlock(block, blockTexture(block), extend(blockTexture(block), "_top"));
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
        axisBlock(block, extend(baseName, "_side"), extend(baseName, "_end"));
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        axisBlock(block, models().cubeColumn(name(block), side, end), models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
    }

    public void axisBlock(RotatedPillarBlock block, IModelLocation vertical, IModelLocation horizontal) {
        getVariantBuilder(block).forAllStates(state -> {
            VariantBuilder builder = new VariantBuilder();
            return switch (state.getValue(RotatedPillarBlock.AXIS)){
                case X -> builder.modelFile(vertical);
                case Y -> builder.modelFile(horizontal).rotationX(90);
                case Z -> builder.modelFile(horizontal).rotationX(90).rotationY(90);
            };
        });
    }

    private static final int DEFAULT_ANGLE_OFFSET = 180;

    public void horizontalBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
        horizontalBlock(block, models().orientable(name(block), side, front, top));
    }

    public void horizontalBlock(Block block, IModelLocation model) {
        horizontalBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalBlock(Block block, IModelLocation model, int angleOffset) {
        horizontalBlock(block, $ -> model, angleOffset);
    }

    public void horizontalBlock(Block block, Function<BlockState, IModelLocation> modelFunc) {
        horizontalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalBlock(Block block, Function<BlockState, IModelLocation> modelFunc, int angleOffset) {
        if (block.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            getVariantBuilder(block)
                    .forAllStates(state -> new VariantBuilder()
                            .modelFile(modelFunc.apply(state))
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360)
                    );
        } else {
            getVariantBuilder(block).wildcard(new VariantBuilder().modelFile(modelFunc.apply(block.defaultBlockState())));
        }
    }

    public void horizontalFaceBlock(Block block, IModelLocation model) {
        horizontalFaceBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalFaceBlock(Block block, IModelLocation model, int angleOffset) {
        horizontalFaceBlock(block, $ -> model, angleOffset);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, IModelLocation> modelFunc) {
        horizontalFaceBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, IModelLocation> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> new VariantBuilder()
                        .modelFile(modelFunc.apply(state))
                        .rotationX(state.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90)
                        .rotationY((((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) + (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360)
                );
    }

    public void directionalBlock(Block block, IModelLocation model) {
        directionalBlock(block, model, DEFAULT_ANGLE_OFFSET);
    }

    public void directionalBlock(Block block, IModelLocation model, int angleOffset) {
        directionalBlock(block, $ -> model, angleOffset);
    }

    public void directionalBlock(Block block, Function<BlockState, IModelLocation> modelFunc) {
        directionalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
    }

    public void directionalBlock(Block block, Function<BlockState, IModelLocation> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return new VariantBuilder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360);
                });
    }

    public void stairsBlock(StairBlock block, ResourceLocation texture) {
        stairsBlock(block, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, String name, ResourceLocation texture) {
        stairsBlock(block, name, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        stairsBlockInternal(block, Registry.BLOCK.getKey(block).toString(), side, bottom, top);
    }

    public void stairsBlock(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        stairsBlockInternal(block, name + "_stairs", side, bottom, top);
    }

    private void stairsBlockInternal(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        AntimatterBlockModelBuilder stairs = models().stairs(baseName, side, bottom, top);
        AntimatterBlockModelBuilder stairsInner = models().stairsInner(baseName + "_inner", side, bottom, top);
        AntimatterBlockModelBuilder stairsOuter = models().stairsOuter(baseName + "_outer", side, bottom, top);
        stairsBlock(block, stairs, stairsInner, stairsOuter);
    }

    public void stairsBlock(StairBlock block, IModelLocation stairs, IModelLocation stairsInner, IModelLocation stairsOuter) {
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    Half half = state.getValue(StairBlock.HALF);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);
                    int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                        yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
                    }
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                        yRot += 90; // Top stairs are rotated 90 degrees clockwise
                    }
                    yRot %= 360;
                    boolean uvlock = yRot != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation
                    return new VariantBuilder()
                            .modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
                            .rotationX(half == Half.BOTTOM ? 0 : 180)
                            .rotationY(yRot)
                            .uvLock(uvlock);
                }, StairBlock.WATERLOGGED);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
        slabBlock(block, doubleslab, texture, texture, texture);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        slabBlock(block, models().slab(name(block), side, bottom, top), models().slabTop(name(block) + "_top", side, bottom, top), () -> doubleslab);
    }

    public void slabBlock(SlabBlock block, IModelLocation bottom, IModelLocation top, IModelLocation doubleslab) {
        getVariantBuilder(block).forAllStates(state -> {
            VariantBuilder builder = new VariantBuilder();
            return switch (state.getValue(SlabBlock.TYPE)){
                case TOP -> builder.modelFile(top);
                case BOTTOM -> builder.modelFile(bottom);
                case DOUBLE -> builder.modelFile(doubleslab);
            };
        });
    }

    public void buttonBlock(ButtonBlock block, ResourceLocation texture) {
        AntimatterBlockModelBuilder button = models().button(name(block), texture);
        AntimatterBlockModelBuilder buttonPressed = models().buttonPressed(name(block) + "_pressed", texture);
        buttonBlock(block, button, buttonPressed);
    }

    public void buttonBlock(ButtonBlock block, IModelLocation button, IModelLocation buttonPressed) {
        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(ButtonBlock.FACING);
            AttachFace face = state.getValue(ButtonBlock.FACE);
            boolean powered = state.getValue(ButtonBlock.POWERED);

            return new VariantBuilder()
                    .modelFile(powered ? buttonPressed : button)
                    .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
                    .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                    .uvLock(face == AttachFace.WALL);
        });
    }

    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
        AntimatterBlockModelBuilder pressurePlate = models().pressurePlate(name(block), texture);
        AntimatterBlockModelBuilder pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture);
        pressurePlateBlock(block, pressurePlate, pressurePlateDown);
    }

    public void pressurePlateBlock(PressurePlateBlock block, IModelLocation pressurePlate, IModelLocation pressurePlateDown) {
        getVariantBuilder(block).forAllStates(state -> state.getValue(PressurePlateBlock.POWERED) ? new VariantBuilder().modelFile(pressurePlateDown) : new VariantBuilder().modelFile(pressurePlate));
    }

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ResourceLocation texture) {
        IModelLocation sign = models().sign(name(signBlock), texture);
        signBlock(signBlock, wallSignBlock, sign);
    }

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, IModelLocation sign) {
        simpleBlock(signBlock, sign);
        simpleBlock(wallSignBlock, sign);
    }

    public void fourWayBlock(CrossCollisionBlock block, IModelLocation post, IModelLocation side) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel().end();
        fourWayMultipart(builder, side);
    }

    public void fourWayMultipart(MultiPartBlockStateBuilder builder, IModelLocation side) {
        PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            if (dir.getAxis().isHorizontal()) {
                builder.part().modelFile(side).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock().addModel()
                        .condition(e.getValue(), true);
            }
        });
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        String baseName = Registry.BLOCK.getKey(block).toString();
        fourWayBlock(block, models().fencePost(baseName + "_post", texture), models().fenceSide(baseName + "_side", texture));
    }

    public void fenceBlock(FenceBlock block, String name, ResourceLocation texture) {
        fourWayBlock(block, models().fencePost(name + "_fence_post", texture), models().fenceSide(name + "_fence_side", texture));
    }

    public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
        fenceGateBlockInternal(block, Registry.BLOCK.getKey(block).toString(), texture);
    }

    public void fenceGateBlock(FenceGateBlock block, String name, ResourceLocation texture) {
        fenceGateBlockInternal(block, name + "_fence_gate", texture);
    }

    private void fenceGateBlockInternal(FenceGateBlock block, String baseName, ResourceLocation texture) {
        IModelLocation gate = models().fenceGate(baseName, texture);
        IModelLocation gateOpen = models().fenceGateOpen(baseName + "_open", texture);
        IModelLocation gateWall = models().fenceGateWall(baseName + "_wall", texture);
        IModelLocation gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture);
        fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
    }

    public void fenceGateBlock(FenceGateBlock block, IModelLocation gate, IModelLocation gateOpen, IModelLocation gateWall, IModelLocation gateWallOpen) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            IModelLocation model = gate;
            if (state.getValue(FenceGateBlock.IN_WALL)) {
                model = gateWall;
            }
            if (state.getValue(FenceGateBlock.OPEN)) {
                model = model == gateWall ? gateWallOpen : gateOpen;
            }
            return new VariantBuilder()
                    .modelFile(model)
                    .rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot())
                    .uvLock();
        }, FenceGateBlock.POWERED);
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        wallBlockInternal(block, Registry.BLOCK.getKey(block).toString(), texture);
    }

    public void wallBlock(WallBlock block, String name, ResourceLocation texture) {
        wallBlockInternal(block, name + "_wall", texture);
    }

    private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
        wallBlock(block, models().wallPost(baseName + "_post", texture), models().wallSide(baseName + "_side", texture), models().wallSideTall(baseName + "_side_tall", texture));
    }

    public static final ImmutableMap<Direction, Property<WallSide>> WALL_PROPS = ImmutableMap.<Direction, Property<WallSide>>builder()
            .put(Direction.EAST,  BlockStateProperties.EAST_WALL)
            .put(Direction.NORTH, BlockStateProperties.NORTH_WALL)
            .put(Direction.SOUTH, BlockStateProperties.SOUTH_WALL)
            .put(Direction.WEST,  BlockStateProperties.WEST_WALL)
            .build();

    public void wallBlock(WallBlock block, IModelLocation post, IModelLocation side, IModelLocation sideTall) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel()
                .condition(WallBlock.UP, true).end();
        WALL_PROPS.entrySet().stream()
                .filter(e -> e.getKey().getAxis().isHorizontal())
                .forEach(e -> {
                    wallSidePart(builder, side, e, WallSide.LOW);
                    wallSidePart(builder, sideTall, e, WallSide.TALL);
                });
    }

    private void wallSidePart(MultiPartBlockStateBuilder builder, IModelLocation model, Map.Entry<Direction, Property<WallSide>> entry, WallSide height) {
        builder.part()
                .modelFile(model)
                .rotationY((((int) entry.getKey().toYRot()) + 180) % 360)
                .uvLock()
                .addModel()
                .condition(entry.getValue(), height);
    }

    public void paneBlock(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge) {
        paneBlockInternal(block, Registry.BLOCK.getKey(block).toString(), pane, edge);
    }

    public void paneBlock(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge) {
        paneBlockInternal(block, name + "_pane", pane, edge);
    }

    private void paneBlockInternal(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge) {
        IModelLocation post = models().panePost(baseName + "_post", pane, edge);
        IModelLocation side = models().paneSide(baseName + "_side", pane, edge);
        IModelLocation sideAlt = models().paneSideAlt(baseName + "_side_alt", pane, edge);
        IModelLocation noSide = models().paneNoSide(baseName + "_noside", pane);
        IModelLocation noSideAlt = models().paneNoSideAlt(baseName + "_noside_alt", pane);
        paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
    }

    public void paneBlock(IronBarsBlock block, IModelLocation post, IModelLocation side, IModelLocation sideAlt, IModelLocation noSide, IModelLocation noSideAlt) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel().end();
        PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            if (dir.getAxis().isHorizontal()) {
                boolean alt = dir == Direction.SOUTH;
                builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0).addModel()
                        .condition(e.getValue(), true).end()
                        .part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
                        .condition(e.getValue(), false);
            }
        });
    }

    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        doorBlockInternal(block, Registry.BLOCK.getKey(block).toString(), bottom, top);
    }

    public void doorBlock(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top) {
        doorBlockInternal(block, name + "_door", bottom, top);
    }

    private void doorBlockInternal(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
        IModelLocation bottomLeft = models().doorBottomLeft(baseName + "_bottom", bottom, top);
        IModelLocation bottomRight = models().doorBottomRight(baseName + "_bottom_hinge", bottom, top);
        IModelLocation topLeft = models().doorTopLeft(baseName + "_top", bottom, top);
        IModelLocation topRight = models().doorTopRight(baseName + "_top_hinge", bottom, top);
        doorBlock(block, bottomLeft, bottomRight, topLeft, topRight);
    }

    public void doorBlock(DoorBlock block, IModelLocation bottomLeft, IModelLocation bottomRight, IModelLocation topLeft, IModelLocation topRight) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
            boolean rh = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.getValue(DoorBlock.OPEN);
            boolean right = rh ^ open;
            if (open) {
                yRot += 90;
            }
            if (rh && open) {
                yRot += 180;
            }
            yRot %= 360;
            return new VariantBuilder().modelFile(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? (right ? bottomRight : bottomLeft) : (right ? topRight : topLeft))
                    .rotationY(yRot);
        }, DoorBlock.POWERED);
    }

    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        trapdoorBlockInternal(block, Registry.BLOCK.getKey(block).toString(), texture, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable) {
        trapdoorBlockInternal(block, name + "_trapdoor", texture, orientable);
    }

    private void trapdoorBlockInternal(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable) {
        IModelLocation bottom = orientable ? models().trapdoorOrientableBottom(baseName + "_bottom", texture) : models().trapdoorBottom(baseName + "_bottom", texture);
        IModelLocation top = orientable ? models().trapdoorOrientableTop(baseName + "_top", texture) : models().trapdoorTop(baseName + "_top", texture);
        IModelLocation open = orientable ? models().trapdoorOrientableOpen(baseName + "_open", texture) : models().trapdoorOpen(baseName + "_open", texture);
        trapdoorBlock(block, bottom, top, open, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, IModelLocation bottom, IModelLocation top, IModelLocation open, boolean orientable) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int xRot = 0;
            int yRot = ((int) state.getValue(TrapDoorBlock.FACING).toYRot()) + 180;
            boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
            if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
                xRot += 180;
                yRot += 180;
            }
            if (!orientable && !isOpen) {
                yRot = 0;
            }
            yRot %= 360;
            return new VariantBuilder().modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)
                    .rotationX(xRot)
                    .rotationY(yRot);
        }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
    }
}
