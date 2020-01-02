package muramasa.gtu.data.providers;

import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

import java.util.function.Function;

public abstract class PublicBlockStateProvider extends BlockStateProvider {

    public PublicBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    /** BlockStateProvider **/
    public VariantBlockStateBuilder getVariantBuilder(Block b) {
        return super.getVariantBuilder(b);
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
        return super.getMultipartBuilder(b);
    }

    public ResourceLocation blockTexture(Block block) {
        return super.blockTexture(block);
    }

    public ModelFile cubeAll(Block block) {
        return super.cubeAll(block);
    }

    public BlockModelBuilder cubeAll(String name, ResourceLocation texture) {
        return super.cubeAll(name, texture);
    }

    public void simpleBlock(Block block) {
        super.simpleBlock(block);
    }

    public void simpleBlock(Block block, Function<ModelFile, ConfiguredModel[]> expander) {
        super.simpleBlock(block, expander);
    }

    public void simpleBlock(Block block, ModelFile model) {
        super.simpleBlock(block, model);
    }

    public void simpleBlock(Block block, ConfiguredModel... models) {
        super.simpleBlock(block, models);
    }

    public void axisBlock(RotatedPillarBlock block) {
        super.axisBlock(block);
    }

    public void logBlock(LogBlock block) {
        super.logBlock(block);
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
        super.axisBlock(block, baseName);
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        super.axisBlock(block, side, end);
    }

    public void axisBlock(RotatedPillarBlock block, ModelFile model) {
        super.axisBlock(block, model);
    }

    public void horizontalBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
        super.horizontalBlock(block, side, front, top);
    }

    public void horizontalBlock(Block block, ModelFile model) {
        super.horizontalBlock(block, model);
    }

    public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
        super.horizontalBlock(block, model, angleOffset);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        super.horizontalBlock(block, modelFunc);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        super.horizontalBlock(block, modelFunc, angleOffset);
    }

    public void horizontalFaceBlock(Block block, ModelFile model) {
        super.horizontalFaceBlock(block, model);
    }

    public void horizontalFaceBlock(Block block, ModelFile model, int angleOffset) {
        super.horizontalFaceBlock(block, model, angleOffset);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        super.horizontalFaceBlock(block, modelFunc);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        super.horizontalFaceBlock(block, modelFunc, angleOffset);
    }

    public void directionalBlock(Block block, ModelFile model) {
        super.directionalBlock(block, model);
    }

    public void directionalBlock(Block block, ModelFile model, int angleOffset) {
        super.directionalBlock(block, model, angleOffset);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        super.directionalBlock(block, modelFunc);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        super.directionalBlock(block, modelFunc, angleOffset);
    }

    public void stairsBlock(StairsBlock block, ResourceLocation texture) {
        super.stairsBlock(block, texture);
    }

    public void stairsBlock(StairsBlock block, String name, ResourceLocation texture) {
        super.stairsBlock(block, name, texture);
    }

    public void stairsBlock(StairsBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        super.stairsBlock(block, side, bottom, top);
    }

    public void stairsBlock(StairsBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        super.stairsBlock(block, name, side, bottom, top);
    }

    public void stairsBlock(StairsBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
        super.stairsBlock(block, stairs, stairsInner, stairsOuter);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
        super.slabBlock(block, doubleslab, texture);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        super.slabBlock(block, doubleslab, side, bottom, top);
    }

    public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
        super.slabBlock(block, bottom, top, doubleslab);
    }

    public void fourWayBlock(FourWayBlock block, ModelFile post, ModelFile side) {
        super.fourWayBlock(block, post, side);
    }

    public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
        super.fourWayMultipart(builder, side);
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        super.fenceBlock(block, texture);
    }

    public void fenceBlock(FenceBlock block, String name, ResourceLocation texture) {
        super.fenceBlock(block, name, texture);
    }

    public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
        super.fenceGateBlock(block, texture);
    }

    public void fenceGateBlock(FenceGateBlock block, String name, ResourceLocation texture) {
        super.fenceGateBlock(block, name, texture);
    }

    public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
        super.fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        super.wallBlock(block, texture);
    }

    public void wallBlock(WallBlock block, String name, ResourceLocation texture) {
        super.wallBlock(block, name, texture);
    }

    public void wallBlock(WallBlock block, ModelFile post, ModelFile side) {
        super.wallBlock(block, post, side);
    }

    public void paneBlock(PaneBlock block, ResourceLocation pane, ResourceLocation edge) {
        super.paneBlock(block, pane, edge);
    }

    public void paneBlock(PaneBlock block, String name, ResourceLocation pane, ResourceLocation edge) {
        super.paneBlock(block, name, pane, edge);
    }

    public void paneBlock(PaneBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
        super.paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
    }

    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        super.doorBlock(block, bottom, top);
    }

    public void doorBlock(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top) {
        super.doorBlock(block, name, bottom, top);
    }

    public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomRight, ModelFile topLeft, ModelFile topRight) {
        super.doorBlock(block, bottomLeft, bottomRight, topLeft, topRight);
    }

    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        super.trapdoorBlock(block, texture, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable) {
        super.trapdoorBlock(block, name, texture, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
        super.trapdoorBlock(block, bottom, top, open, orientable);
    }

    /** Model Provider **/
    public BlockModelBuilder getBuilder(String path) {
        return super.getBuilder(path);
    }

    public ResourceLocation modLoc(String name) {
        return super.modLoc(name);
    }

    public ResourceLocation mcLoc(String name) {
        return super.mcLoc(name);
    }

    public BlockModelBuilder withExistingParent(String name, String parent) {
        return super.withExistingParent(name, parent);
    }

    public BlockModelBuilder withExistingParent(String name, ResourceLocation parent) {
        return super.withExistingParent(name, parent);
    }

    public BlockModelBuilder cube(String name, ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation east, ResourceLocation west) {
        return super.cube(name, down, up, north, south, east, west);
    }

    public BlockModelBuilder singleTexture(String name, ResourceLocation parent, ResourceLocation texture) {
        return super.singleTexture(name, parent, texture);
    }

    public BlockModelBuilder singleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
        return super.singleTexture(name, parent, textureKey, texture);
    }

    public BlockModelBuilder cubeTop(String name, ResourceLocation side, ResourceLocation top) {
        return super.cubeTop(name, side, top);
    }

    public BlockModelBuilder cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return super.cubeBottomTop(name, side, bottom, top);
    }

    public BlockModelBuilder cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
        return super.cubeColumn(name, side, end);
    }

    public BlockModelBuilder orientableVertical(String name, ResourceLocation side, ResourceLocation front) {
        return super.orientableVertical(name, side, front);
    }

    public BlockModelBuilder orientableWithBottom(String name, ResourceLocation side, ResourceLocation front, ResourceLocation bottom, ResourceLocation top) {
        return super.orientableWithBottom(name, side, front, bottom, top);
    }

    public BlockModelBuilder orientable(String name, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
        return super.orientable(name, side, front, top);
    }

    public BlockModelBuilder crop(String name, ResourceLocation crop) {
        return super.crop(name, crop);
    }

    public BlockModelBuilder cross(String name, ResourceLocation cross) {
        return super.cross(name, cross);
    }

    public BlockModelBuilder stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return super.stairs(name, side, bottom, top);
    }

    public BlockModelBuilder stairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return super.stairsOuter(name, side, bottom, top);
    }

    public BlockModelBuilder stairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return super.stairsInner(name, side, bottom, top);
    }

    public BlockModelBuilder slab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return super.slab(name, side, bottom, top);
    }

    public BlockModelBuilder slabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return super.slab(name, side, bottom, top);
    }

    public BlockModelBuilder fencePost(String name, ResourceLocation texture) {
        return super.fencePost(name, texture);
    }

    public BlockModelBuilder fenceSide(String name, ResourceLocation texture) {
        return super.fenceSide(name, texture);
    }

    public BlockModelBuilder fenceInventory(String name, ResourceLocation texture) {
        return super.fenceInventory(name, texture);
    }

    public BlockModelBuilder fenceGate(String name, ResourceLocation texture) {
        return super.fenceGate(name, texture);
    }

    public BlockModelBuilder fenceGateOpen(String name, ResourceLocation texture) {
        return super.fenceGateOpen(name, texture);
    }

    public BlockModelBuilder fenceGateWall(String name, ResourceLocation texture) {
        return super.fenceGateWall(name, texture);
    }

    public BlockModelBuilder fenceGateWallOpen(String name, ResourceLocation texture) {
        return super.fenceGateWallOpen(name, texture);
    }

    public BlockModelBuilder wallPost(String name, ResourceLocation wall) {
        return super.wallPost(name, wall);
    }

    public BlockModelBuilder wallSide(String name, ResourceLocation wall) {
        return super.wallSide(name, wall);
    }

    public BlockModelBuilder wallInventory(String name, ResourceLocation wall) {
        return super.wallInventory(name, wall);
    }

    public BlockModelBuilder panePost(String name, ResourceLocation pane, ResourceLocation edge) {
        return super.panePost(name, pane, edge);
    }

    public BlockModelBuilder paneSide(String name, ResourceLocation pane, ResourceLocation edge) {
        return super.paneSide(name, pane, edge);
    }

    public BlockModelBuilder paneSideAlt(String name, ResourceLocation pane, ResourceLocation edge) {
        return super.paneSideAlt(name, pane, edge);
    }

    public BlockModelBuilder paneNoSide(String name, ResourceLocation pane) {
        return super.paneNoSide(name, pane);
    }

    public BlockModelBuilder paneNoSideAlt(String name, ResourceLocation pane) {
        return super.paneNoSideAlt(name, pane);
    }

    public BlockModelBuilder doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
        return super.doorBottomLeft(name, bottom, top);
    }

    public BlockModelBuilder doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
        return super.doorBottomRight(name, bottom, top);
    }

    public BlockModelBuilder doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
        return super.doorTopLeft(name, bottom, top);
    }

    public BlockModelBuilder doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
        return super.doorTopRight(name, bottom, top);
    }

    public BlockModelBuilder trapdoorBottom(String name, ResourceLocation texture) {
        return super.trapdoorBottom(name, texture);
    }

    public BlockModelBuilder trapdoorTop(String name, ResourceLocation texture) {
        return super.trapdoorTop(name, texture);
    }

    public BlockModelBuilder trapdoorOpen(String name, ResourceLocation texture) {
        return super.trapdoorOpen(name, texture);
    }

    public BlockModelBuilder trapdoorOrientableBottom(String name, ResourceLocation texture) {
        return super.trapdoorOrientableBottom(name, texture);
    }

    public BlockModelBuilder trapdoorOrientableTop(String name, ResourceLocation texture) {
        return super.trapdoorOrientableTop(name, texture);
    }

    public BlockModelBuilder trapdoorOrientableOpen(String name, ResourceLocation texture) {
        return super.trapdoorOrientableOpen(name, texture);
    }

    public BlockModelBuilder torch(String name, ResourceLocation torch) {
        return super.torch(name, torch);
    }

    public BlockModelBuilder torchWall(String name, ResourceLocation torch) {
        return super.torchWall(name, torch);
    }

    public BlockModelBuilder carpet(String name, ResourceLocation wool) {
        return super.carpet(name, wool);
    }

    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
        return super.getExistingFile(path);
    }
}
