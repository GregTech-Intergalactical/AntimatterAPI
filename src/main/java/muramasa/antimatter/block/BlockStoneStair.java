package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import static net.minecraft.state.properties.StairsShape.STRAIGHT;
import static net.minecraft.util.Direction.*;

public class BlockStoneStair extends StairsBlock implements ISharedAntimatterObject, ITextureProvider, IModelProvider {
    protected String domain, id, suffix;
    CobbleStoneType type;

    public BlockStoneStair(CobbleStoneType type, String suffix, Block baseState) {
        super(baseState::defaultBlockState, getProps(type));
        domain = type.getDomain();
        id = type.getId() + "_" + suffix + "_stairs";
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

    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        BlockModelBuilder outer = prov.models().getBuilder(getId() + "_outer").parent(prov.existing("minecraft", "block/outer_stairs")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        BlockModelBuilder inner = prov.models().getBuilder(getId() + "_inner").parent(prov.existing("minecraft", "block/inner_stairs")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        BlockModelBuilder regular = prov.models().getBuilder(getId()).parent(prov.existing("minecraft", "block/stairs")).texture("bottom", getTextures()[0]).texture("top", getTextures()[0]).texture("side", getTextures()[0]);
        prov.getVariantBuilder(block).forAllStates(s -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
            BlockModelBuilder b = regular;
            StairsShape shape = s.getValue(SHAPE);
            Half half = s.getValue(HALF);
            Direction facing = s.getValue(FACING);
            boolean left = shape.getSerializedName().contains("left"), right = shape.getSerializedName().contains("right");
            if (shape.getSerializedName().contains("outer")) b = outer;
            if (shape.getSerializedName().contains("inner")) b = inner;
            builder.modelFile(b);
            if (half == Half.TOP) {
                builder.rotationX(180);
                if ((facing == EAST && right) || (facing == SOUTH && (shape == STRAIGHT || left))) {
                    builder.rotationY(90);
                }
                if ((facing == SOUTH && right) || (facing == WEST && (left || shape == STRAIGHT))) {
                    builder.rotationY(180);
                }
                if ((facing == WEST && right) || (facing == NORTH && (left || shape == STRAIGHT))) {
                    builder.rotationY(270);
                }
                builder.uvLock(true);
            } else {
                if ((facing == SOUTH && (shape == STRAIGHT || right)) || (facing == WEST && left)) {
                    builder.rotationY(90);
                }
                if ((facing == NORTH && left) || (facing == WEST && (right || shape == STRAIGHT))) {
                    builder.rotationY(180);
                }
                if ((facing == EAST && left) || (facing == NORTH && (right || shape == STRAIGHT))) {
                    builder.rotationY(270);
                }
                if (!((facing == EAST && (right || shape == STRAIGHT)) || (facing == SOUTH && left))) {
                    builder.uvLock(true);
                }
            }
            return builder.build();
        });
    }
}
