package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import static net.minecraft.block.WallHeight.LOW;
import static net.minecraft.block.WallHeight.TALL;

public class BlockStoneWall extends WallBlock implements ISharedAntimatterObject, ITextureProvider, IModelProvider {
    protected String domain, id, suffix;
    CobbleStoneType type;

    public BlockStoneWall(CobbleStoneType type, String suffix) {
        super(getProps(type));
        domain = type.getDomain();
        id = type.getId() + "_" + suffix + "_wall";
        this.suffix = suffix;
        this.type = type;
        AntimatterAPI.register(getClass(), this);
    }

    private static Properties getProps(StoneType type) {
        Properties props = Block.Properties.of(type.getBlockMaterial()).sound(type.getSoundType()).harvestLevel(type.getHarvestLevel()).harvestTool(type.getToolType()).strength(type.getHardness(), type.getResistence());
        if (type.doesRequireTool()) {
            props.requiresCorrectToolForDrops();
        }
        return props;
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
                .part().modelFile(side).uvLock(true).addModel().condition(NORTH_WALL, LOW).end()
                .part().modelFile(side).rotationY(90).uvLock(true).addModel().condition(EAST_WALL, LOW).end()
                .part().modelFile(side).rotationY(180).uvLock(true).addModel().condition(SOUTH_WALL, LOW).end()
                .part().modelFile(side).rotationY(270).uvLock(true).addModel().condition(WEST_WALL, LOW).end()
                .part().modelFile(side_tall).uvLock(true).addModel().condition(NORTH_WALL, TALL).end()
                .part().modelFile(side_tall).rotationY(90).uvLock(true).addModel().condition(EAST_WALL, TALL).end()
                .part().modelFile(side_tall).rotationY(180).uvLock(true).addModel().condition(SOUTH_WALL, TALL).end()
                .part().modelFile(side_tall).rotationY(270).uvLock(true).addModel().condition(WEST_WALL, TALL).end();

    }
}
