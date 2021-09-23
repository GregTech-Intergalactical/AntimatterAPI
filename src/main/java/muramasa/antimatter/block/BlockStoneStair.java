package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

import static net.minecraft.state.properties.StairsShape.STRAIGHT;
import static net.minecraft.util.Direction.EAST;
import static net.minecraft.util.Direction.NORTH;
import static net.minecraft.util.Direction.SOUTH;
import static net.minecraft.util.Direction.WEST;

public class BlockStoneStair extends StairsBlock implements IAntimatterObject, ITextureProvider, IModelProvider {
    protected String domain, id, suffix;
    CobbleStoneType type;

    public BlockStoneStair(CobbleStoneType type, String suffix, Block baseState) {
        super(baseState::getDefaultState, getProps(type));
        domain = type.getDomain();
        id = type.getId() + "_" + suffix + "_stairs";
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

    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        BlockModelBuilder outer = prov.models().getBuilder(getId() + "_outer").parent(prov.existing("minecraft", "block/outer_stairs")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        BlockModelBuilder inner = prov.models().getBuilder(getId() + "_inner").parent(prov.existing("minecraft", "block/inner_stairs")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        BlockModelBuilder regular = prov.models().getBuilder(getId()).parent(prov.existing("minecraft", "block/stairs")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        prov.getVariantBuilder(block).forAllStates(s -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
            BlockModelBuilder b = regular;
            StairsShape shape = s.get(SHAPE);
            Half half = s.get(HALF);
            Direction facing = s.get(FACING);
            boolean left = shape.getString().contains("left"), right = shape.getString().contains("right");
            if (shape.getString().contains("outer")) b = outer;
            if (shape.getString().contains("inner")) b = inner;
            builder.modelFile(b);
            if (half == Half.TOP){
                builder.rotationX(180);
                if ((facing == EAST && right) || (facing == SOUTH && (shape == STRAIGHT || left))){
                    builder.rotationY(90);
                }
                if ((facing == SOUTH && right) || (facing == WEST && (left || shape == STRAIGHT))){
                    builder.rotationY(180);
                }
                if ((facing == WEST && right) || (facing == NORTH && (left || shape == STRAIGHT))){
                    builder.rotationY(270);
                }
                builder.uvLock(true);
            } else {
                if ((facing == SOUTH && (shape == STRAIGHT || right)) || (facing == WEST && left)){
                    builder.rotationY(90);
                }
                if ((facing == NORTH && left) || (facing == WEST && (right || shape == STRAIGHT))){
                    builder.rotationY(180);
                }
                if ((facing == EAST && left) || (facing == NORTH && (right || shape == STRAIGHT))){
                    builder.rotationY(270);
                }
                if (!((facing == EAST && (right || shape ==STRAIGHT)) || (facing == SOUTH && left))){
                    builder.uvLock(true);
                }
            }
            return builder.build();
        });
    }
}
