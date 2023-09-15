package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.VariantBlockStateBuilder.VariantBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT;

public class BlockStoneStair extends StairBlock implements ISharedAntimatterObject, ITextureProvider, IModelProvider {
    protected String domain, id, suffix;
    CobbleStoneType type;

    public BlockStoneStair(CobbleStoneType type, String suffix, Block baseState) {
        super(baseState.defaultBlockState(), getProps(type));
        domain = type.getDomain();
        String s = suffix.isEmpty() ? "" : "_";
        id = type.getId() + s + suffix + "_stairs";
        this.suffix = suffix;
        this.type = type;
        AntimatterAPI.register(getClass(), this);
    }

    private static Properties getProps(StoneType type) {
        Properties props = Properties.of(type.getBlockMaterial()).sound(type.getSoundType()).strength(type.getHardness(), type.getResistence());
        if (type.doesRequireTool()) {
            props.requiresCorrectToolForDrops();
        }
        return props;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(type.getDomain(), type.getBeginningPath() + type.getId() + "/" + (suffix.isEmpty() ? "stone" : suffix))};
    }

    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        Texture bottom, top, side;
        bottom = top = side = getTextures()[0];
        if (type == AntimatterStoneTypes.BASALT && (suffix.isEmpty() || suffix.equals("smooth"))){
            if (suffix.isEmpty()) {
                top = bottom = new Texture("block/basalt_top");
                side = new Texture("block/basalt_side");
            } else {
                top = bottom = side = new Texture("block/smooth_basalt");
            }
        }
        AntimatterBlockModelBuilder outer = prov.models().getBuilder(getId() + "_outer").parent(prov.existing("minecraft", "block/outer_stairs")).texture("bottom", bottom).texture("top", top).texture("side", side);
        AntimatterBlockModelBuilder inner = prov.models().getBuilder(getId() + "_inner").parent(prov.existing("minecraft", "block/inner_stairs")).texture("bottom", bottom).texture("top", top).texture("side", side);
        AntimatterBlockModelBuilder regular = prov.models().getBuilder(getId()).parent(prov.existing("minecraft", "block/stairs")).texture("bottom", bottom).texture("top", top).texture("side", side);
        prov.getVariantBuilder(block).forAllStates(s -> {
            VariantBuilder builder = new VariantBuilder();
            AntimatterBlockModelBuilder b = regular;
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
            return builder;
        });
    }
}
