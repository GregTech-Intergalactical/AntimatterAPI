package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import static net.minecraft.block.WallHeight.LOW;
import static net.minecraft.block.WallHeight.TALL;
import static net.minecraft.state.properties.StairsShape.STRAIGHT;
import static net.minecraft.util.Direction.EAST;
import static net.minecraft.util.Direction.NORTH;
import static net.minecraft.util.Direction.SOUTH;
import static net.minecraft.util.Direction.WEST;

public class BlockStoneWall extends WallBlock implements IAntimatterObject, ITextureProvider, IModelProvider {
    protected String domain, id, suffix;
    CobbleStoneType type;

    public BlockStoneWall(CobbleStoneType type, String suffix) {
        super(getProps(type));
        domain = type.getDomain();
        id = type.getId() + "_" + suffix + "_wall";
        this.suffix = suffix;
        this.type = type;
        AntimatterAPI.register(getClass(), getId(), this);
    }

    private static Properties getProps(StoneType type){
        Properties props = Block.Properties.create(type.getBlockMaterial()).sound(type.getSoundType()).harvestLevel(type.getHarvestLevel()).harvestTool(type.getToolType()).hardnessAndResistance(type.getHardness(), type.getResistence());
        if (type.doesRequireTool()){
            props.setRequiresTool();
        }
        return props;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(type.getDomain(), type.getBeginningPath() + type.getId() + "/" + (suffix.isEmpty() ? "stone" : suffix))};
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(new ModelFile.UncheckedModelFile(new ResourceLocation("minecraft", "block/wall_inventory"))).texture("wall", getTextures()[0]);
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        BlockModelBuilder post = prov.models().getBuilder(getId() + "_post").parent(prov.existing("minecraft", "block/template_wall_post")).texture("wall", getTextures()[0]);
        BlockModelBuilder side = prov.models().getBuilder(getId() + "_side").parent(prov.existing("minecraft", "block/template_wall_side")).texture("wall", getTextures()[0]);
        BlockModelBuilder side_tall = prov.models().getBuilder(getId() + "_side_tall").parent(prov.existing("minecraft", "block/template_wall_side_tall")).texture("wall", getTextures()[0]);
        prov.getMultipartBuilder(block)
                .part().modelFile(post).addModel().condition(UP, true).end()
                .part().modelFile(side).uvLock(true).addModel().condition(WALL_HEIGHT_NORTH, LOW).end()
                .part().modelFile(side).rotationY(90).uvLock(true).addModel().condition(WALL_HEIGHT_EAST, LOW).end()
                .part().modelFile(side).rotationY(180).uvLock(true).addModel().condition(WALL_HEIGHT_SOUTH, LOW).end()
                .part().modelFile(side).rotationY(270).uvLock(true).addModel().condition(WALL_HEIGHT_WEST, LOW).end()
                .part().modelFile(side_tall).uvLock(true).addModel().condition(WALL_HEIGHT_NORTH, TALL).end()
                .part().modelFile(side_tall).rotationY(90).uvLock(true).addModel().condition(WALL_HEIGHT_EAST, TALL).end()
                .part().modelFile(side_tall).rotationY(180).uvLock(true).addModel().condition(WALL_HEIGHT_SOUTH, TALL).end()
                .part().modelFile(side_tall).rotationY(270).uvLock(true).addModel().condition(WALL_HEIGHT_WEST, TALL).end();

    }
}
