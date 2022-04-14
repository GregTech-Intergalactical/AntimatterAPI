package muramasa.antimatter.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.ItemBattery;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.mixin.LevelRendererAccessor;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.tool.armor.MaterialArmor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.DrawSelectionEvent.HighlightBlock;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.forge.TesseractCaps;
import tesseract.api.gt.IEnergyHandler;
import tesseract.graph.Connectivity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RenderHelper {
    //protected static final RenderStateShard.ShaderStateShard RENDERTYPE_LINES_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader);
    //public static final Object LINES = RenderType.create("lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));


    public static float xFromQuad(BakedQuad quad, int index) {
        int size = DefaultVertexFormat.BLOCK.getOffset(index);

        return Float.intBitsToFloat(quad.getVertices()[size]);
    }

    public static float yFromQuad(BakedQuad quad, int index) {
        int size = DefaultVertexFormat.BLOCK.getOffset(index);

        return Float.intBitsToFloat(quad.getVertices()[size + 1]);
    }

    public static float zFromQuad(BakedQuad quad, int index) {
        int size = DefaultVertexFormat.BLOCK.getOffset(index);

        return Float.intBitsToFloat(quad.getVertices()[size + 2]);
    }

    public static Vector3f normalFromQuad(BakedQuad quad, int index) {
        int size = DefaultVertexFormat.BLOCK.getOffset(index);
        int off = DefaultVertexFormat.ELEMENT_POSITION.getByteSize() + DefaultVertexFormat.ELEMENT_COLOR.getByteSize() + DefaultVertexFormat.ELEMENT_UV0.getByteSize() + DefaultVertexFormat.ELEMENT_UV2.getByteSize();
        float first = Float.intBitsToFloat(quad.getVertices()[off]);
        float two = Float.intBitsToFloat(quad.getVertices()[off+1]);
        float three = Float.intBitsToFloat(quad.getVertices()[off+2]);
        return new Vector3f(first, two, three);
    }


    public static void registerBatteryPropertyOverrides(ItemBattery battery) {
        ItemProperties.register(battery, new ResourceLocation(Ref.ID, "battery"), (stack, world, living, some_int) -> {
            LazyOptional<IEnergyHandler> handler = stack.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY);
            return handler.map(h -> ((float) h.getEnergy() / (float) h.getCapacity())).orElse(1.0F);
        });
    }

    public static void registerProbePropertyOverrides(MaterialArmor armor) {
        ItemProperties.register(armor, new ResourceLocation(Ref.ID, "probe"), (stack, world, living, some_int) -> {
            CompoundTag nbt = stack.getTag();
            return nbt != null && nbt.contains("theoneprobe") && nbt.getBoolean("theoneprobe") ? 1.0F : 0.0F;
        });
    }

    public static void drawFluid(PoseStack mstack, Minecraft mc, int posX, int posY, int width, int height, int scaledAmount, FluidStack stack) {
        if (stack == null) return;
        Fluid fluid = stack.getFluid();
        if (fluid == null) return;
        RenderSystem.enableBlend();
        //TODO 1.18
        //RenderSystem.enableAlphaTest();
        TextureAtlasSprite fluidStillSprite = mc.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(fluid.getAttributes().getStillTexture());
        int fluidColor = fluid.getAttributes().getColor();

        //Draw the fluid texture
        drawTiledSprite(mstack, mc, posX, posY, width, height, 16, 16, fluidColor, scaledAmount, fluidStillSprite);
        //RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void drawTiledSprite(PoseStack stack, Minecraft mc, int posX, int posY, int tiledWidth, int tiledHeight, int texWidth, int texHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        float red = FastColor.ARGB32.red(color);
        float green = FastColor.ARGB32.green(color);
        float blue = FastColor.ARGB32.blue(color);
        float alpha = FastColor.ARGB32.alpha(color);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        RenderSystem.setShaderColor(red/255,green/255, blue/255, alpha/255);
        Matrix4f matrix = stack.last().pose();

        int xTileCount = tiledWidth / texWidth;
        int xRemainder = tiledWidth - (xTileCount * texWidth);
        int yTileCount = scaledAmount / texHeight;
        int yRemainder = scaledAmount - (yTileCount * texHeight);

        final int yStart = posY + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : texWidth;
                int height = (yTile == yTileCount) ? yRemainder : texHeight;
                int x = posX + (xTile * texWidth);
                int y = yStart - ((yTile + 1) * texHeight);
                if (width > 0 && height > 0) {
                    int maskTop = texHeight - height;
                    int maskRight = texWidth - width;

                    drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100, color);
                }
            }
        }
        RenderSystem.setShaderColor(1,1,1,1);

    }

    //Credit: JEI. Some alterations.
    public static void drawTextureWithMasking(Matrix4f stack, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel, int color) {
        double uMin = textureSprite.getU0();
        double uMax = textureSprite.getU1();
        double vMin = textureSprite.getV0();
        double vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(stack, xCoord, yCoord + 16, zLevel).uv((float) uMin, (float) vMax).endVertex();
        bufferBuilder.vertex(stack, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv((float) uMax, (float) vMax).endVertex();
        bufferBuilder.vertex(stack, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv((float) uMax, (float) vMin).endVertex();
        bufferBuilder.vertex(stack, xCoord, yCoord + maskTop, zLevel).uv((float) uMin, (float) vMin).endVertex();
        tessellator.end();
    }

    /*public static int rgbToABGR(int rgb) {
        rgb |= 0xFF000000;
        int r = (rgb >> 16) & 0xFF;
        int b = rgb & 0xFF;
        return (rgb & 0xFF00FF00) | (b << 16) | r;
    }*/

    /**
     * Colors a quad, defaults to using vertex format block.
     */
    public static void colorQuad(BakedQuad quad, int rgb) {
        colorQuad(quad, DefaultVertexFormat.BLOCK.getIntegerSize(), DefaultVertexFormat.BLOCK.getOffset(1) / 4, rgb);
    }

    public static void colorQuad(BakedQuad quad, int size, int offset, int rgb) {
        int[] vertices = quad.getVertices();
        for (int i = 0; i < 4; i++) {
            vertices[offset + size * i] = convertRGB2ABGR(rgb);
        }
    }

    //Returns the quad that would represent the quad at the front of the side, to avoid cover z-fighting.
    //Very complicated method but pretty straight forward nonetheless.
    public static int findPipeFront(PipeSize size, List<BakedQuad> quads, Direction side) {
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            //Only look at the relevant side.
            if (quad.getDirection() != side) continue;
            int[] vertices = quad.getVertices();
            //if not two vertices then skip. (All sides of relevance have just two vertices.)
            if (vertices.length != DefaultVertexFormat.BLOCK.getIntegerSize() * 4) continue;
            float p1 = Float.intBitsToFloat(vertices[0]);
            float p2 = Float.intBitsToFloat(vertices[1]);
            float p3 = Float.intBitsToFloat(vertices[2]);
            int boff = DefaultVertexFormat.BLOCK.getIntegerSize() * 2;
            float p4 = Float.intBitsToFloat(vertices[boff]);
            float p5 = Float.intBitsToFloat(vertices[1 + boff]);
            float p6 = Float.intBitsToFloat(vertices[2 + boff]);
            float offset = 0.0625f * size.ordinal();
            float a1 = 0.4375f - offset;
            float a2 = 0.5625f + offset;
            //Just check that both edges are present.
            if (p1 == a1 || p2 == a1 || p3 == a1 || p4 == a1 || p5 == a1 || p6 == a1) {
                if (p1 == a2 || p2 == a2 || p3 == a2 || p4 == a2 || p5 == a2 || p6 == a2) {
                    //Hate floating point math. But this is only way to actually check if integer.
                    float d = p1 + p2 + p3 + p4 + p5 + p6;
                    double ans = Math.abs(d - Math.floor(d));
                    double anss = Math.abs(d - Math.ceil(d));
                    if (ans < 0.01 || anss < 0.01) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static int convertRGB2ABGR(int colour) {
        return 0xFF << 24 | ((colour & 0xFF) << 16) | ((colour >> 8) & 0xFF) << 8 | (colour >> 16) & 0xFF;
    }

    static float INDENTATION_SIDE = 0.25F;
    static double INTERACT_DISTANCE = 5;


    //This code is pretty complicated but it was written while I knew nothing about rendering. it works though
    public static InteractionResult onDrawHighlight(Player player, LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, Function<Block, Boolean> validator, BiFunction<Direction, BlockEntity, Boolean> getter) {
        Vec3 lookPos = player.getEyePosition(partialTicks), rotation = player.getViewVector(partialTicks), realLookPos = lookPos.add(rotation.x * INTERACT_DISTANCE, rotation.y * INTERACT_DISTANCE, rotation.z * INTERACT_DISTANCE);
        BlockHitResult result = player.getCommandSenderWorld().clip(new ClipContext(lookPos, realLookPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        BlockState state = player.getCommandSenderWorld().getBlockState(result.getBlockPos());
        if (!validator.apply(state.getBlock())) return InteractionResult.PASS;
        VoxelShape shape = player.getCommandSenderWorld().getBlockState(result.getBlockPos()).getShape(player.getCommandSenderWorld(), result.getBlockPos(), CollisionContext.of(player));

        //Build up view & matrix.
        Vec3 viewPosition = camera.getPosition();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        VertexConsumer builderLines = multiBufferSource.getBuffer(RenderType.LINES);

        double modX = result.getBlockPos().getX() - viewX, modY = result.getBlockPos().getY() - viewY, modZ = result.getBlockPos().getZ() - viewZ;
        poseStack.pushPose();
        long time = player.getCommandSenderWorld().getGameTime();
        float r = Math.abs(time % ((255 >> 2) * 2) - (255 >> 2)) * (1 << 2);
        float g = r;
        float b = g;
        float X = 1;
        float Y = 1;

        poseStack.pushPose();
        LevelRendererAccessor.renderShape(poseStack, builderLines, shape, modX, modY, modZ, 0,0,0,0.4f);

        poseStack.popPose();
        switch (result.getDirection()) {
            case UP:
                poseStack.translate(modX, modY + 1, modZ + 1);
                poseStack.mulPose(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                break;
            case DOWN:
                poseStack.translate(modX, modY, modZ + 1);
                poseStack.mulPose(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                break;
            case EAST:
                poseStack.translate(modX + 1, modY, modZ);
                poseStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -90, true));
                break;
            case WEST:
                poseStack.translate(modX, modY, modZ);
                poseStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -90, true));
                break;
            case SOUTH:
                poseStack.translate(modX, modY, modZ + 1);
                break;
            case NORTH:
                poseStack.translate(modX, modY, modZ);
                break;
        }

        //Draw 4 lines.
        Matrix4f matrix4f = poseStack.last().pose();

        //TODO: Use SHAPE to get actual size of box.
        Matrix3f mat = poseStack.last().normal();
        builderLines.vertex(matrix4f, INDENTATION_SIDE, (float) (0), (float) (0)).color(r, g, b, 0.4F).normal(mat,INDENTATION_SIDE, (float) (0), (float) (0)).endVertex();
        builderLines.vertex(matrix4f, INDENTATION_SIDE, Y, (float) (0)).color(r, g, b, 0.4F).normal(mat, INDENTATION_SIDE, Y, (float) (0)).endVertex();

        builderLines.vertex(matrix4f, (float) (0), 0 + INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).normal(mat,(float) (0), 0 + INDENTATION_SIDE, (float) (0)).endVertex();
        builderLines.vertex(matrix4f, X, 0 + INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).normal(mat, X, 0 + INDENTATION_SIDE, (float) (0)).endVertex();

        builderLines.vertex(matrix4f, X - INDENTATION_SIDE, (float) (0), (float) (0)).color(r, g, b, 0.4F).normal(mat,X - INDENTATION_SIDE, (float) (0), (float) (0)).endVertex();
        builderLines.vertex(matrix4f, X - INDENTATION_SIDE, Y, (float) (0)).color(r, g, b, 0.4F).normal(mat,X - INDENTATION_SIDE, Y, (float) (0)).endVertex();

        builderLines.vertex(matrix4f, (float) (0), Y - INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).normal(mat,(float) (0), Y - INDENTATION_SIDE, (float) (0)).endVertex();
        builderLines.vertex(matrix4f, X, Y - INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).normal(mat,X, Y - INDENTATION_SIDE, (float) (0)).endVertex();

        builderLines.vertex(matrix4f, (float) (0), (float) (0), (float) (0)).color(r, g, b, 0.4F).normal(mat,(float) (0), (float) (0), (float) (0)).endVertex();
        builderLines.vertex(matrix4f, (float) (0), Y, (float) (0)).color(r, g, b, 0.4F).normal(mat,(float) (0), Y, (float) (0)).endVertex();

        builderLines.vertex(matrix4f, (float) (0), (float) (0), (float) (0)).color(r, g, b, 0.4F).normal(mat,(float) (0), (float) (0), (float) (0)).endVertex();
        builderLines.vertex(matrix4f, X, (float) (0), (float) (0)).color(r, g, b, 0.4F).normal(mat, X, (float) (0), (float) (0)).endVertex();

        builderLines.vertex(matrix4f, X, Y, (float) (0)).color(r, g, b, 0.4F).normal(mat,X, Y, (float) (0)).endVertex();
        builderLines.vertex(matrix4f, X, (float) (0), (float) (0)).color(r, g, b, 0.4F).normal(mat,X, (float) (0), (float) (0)).endVertex();

        builderLines.vertex(matrix4f, X, Y, (float) (0)).color(r, g, b, 0.4F).normal(mat, X, Y, (float) (0)).endVertex();
        builderLines.vertex(matrix4f, (float) (0), Y, (float) (0)).color(r, g, b, 0.4F).normal(mat,(float) (0), Y, (float) (0)).endVertex();
        BlockEntity tile = player.getCommandSenderWorld().getBlockEntity(result.getBlockPos());
        if (tile != null) {
            byte sides = 0;
            Direction dir = result.getDirection();
            for (Direction d : Ref.DIRS) {
                if (getter.apply(d, tile)) {
                    sides |= 1 << d.get3DDataValue();
                }
            }
            boolean left, right, up, down, back, front;
            back = Connectivity.has(sides, dir.getOpposite().get3DDataValue());
            front = Connectivity.has(sides, dir.get3DDataValue());
            if (dir.getAxis().isVertical()) {
                if (dir == Direction.UP) {
                    right = Connectivity.has(sides, 4);
                    left = Connectivity.has(sides, 5);
                    up = Connectivity.has(sides, 2);
                    down = Connectivity.has(sides, 3);
                } else {
                    right = Connectivity.has(sides, 5);
                    left = Connectivity.has(sides, 4);
                    up = Connectivity.has(sides, 3);
                    down = Connectivity.has(sides, 2);
                }
            } else {
                if (dir == Direction.EAST || dir == Direction.NORTH) {
                    right = Connectivity.has(sides, dir.getCounterClockWise().get3DDataValue());
                    left = Connectivity.has(sides, dir.getClockWise().get3DDataValue());
                } else {
                    right = Connectivity.has(sides, dir.getClockWise().get3DDataValue());
                    left = Connectivity.has(sides, dir.getCounterClockWise().get3DDataValue());
                }
                up = Connectivity.has(sides, 1);
                down = Connectivity.has(sides, 0);
            }
            if (back) {
                drawX(builderLines, matrix4f, mat, 0, 0, INDENTATION_SIDE, INDENTATION_SIDE, r, g, b);
                drawX(builderLines, matrix4f, mat, X, 0, X - INDENTATION_SIDE, INDENTATION_SIDE, r, g, b);
                drawX(builderLines, matrix4f, mat, X, Y, X - INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
                drawX(builderLines, matrix4f, mat, 0, Y, INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
            if (left) {
                drawX(builderLines, matrix4f, mat, X, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
            if (right) {
                drawX(builderLines, matrix4f, mat, 0, INDENTATION_SIDE, INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
            if (up) {
                drawX(builderLines, matrix4f, mat, INDENTATION_SIDE, Y - INDENTATION_SIDE, X - INDENTATION_SIDE, Y, r, g, b);
            }
            if (down) {
                drawX(builderLines, matrix4f, mat, INDENTATION_SIDE, 0, X - INDENTATION_SIDE, INDENTATION_SIDE, r, g, b);
            }
            if (front) {
                drawX(builderLines, matrix4f, mat, INDENTATION_SIDE, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
        }
        poseStack.popPose();
        return InteractionResult.SUCCESS;
    }

    public static Transformation faceRotation(BlockState state) {
        if (state.hasProperty(BlockMachine.HORIZONTAL_FACING)) {
            return faceRotation(state.getValue(BlockStateProperties.FACING), state.getValue(BlockMachine.HORIZONTAL_FACING));
        } 
        return faceRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    public static Transformation faceRotation(Direction facing, @Nullable Direction horiz) {
        if (horiz == null) {
            Quaternion quat = facing.getAxis() != Axis.Y ? Vector3f.YP.rotationDegrees(-facing.toYRot()) : Vector3f.XP.rotationDegrees(-facing.getNormal().getY()*90f);
            return new Transformation(null, quat, null, null);
        } else {
            if (facing.getAxis() != Axis.Y) {
                Quaternion quat = Vector3f.YP.rotationDegrees(-facing.toYRot());
                return new Transformation(null, quat, null, null);
            }
            //vert = vert.getOpposite();
            Quaternion quat = Vector3f.XP.rotationDegrees(-facing.getNormal().getY()*90f);
            Quaternion rot = Vector3f.YP.rotationDegrees(-horiz.toYRot());
            Transformation mat = new Transformation(null, rot, null, null);
            return mat.compose(new Transformation(null, quat, null, null));
        }
    }

    public static Transformation faceRotation(Direction side) {
        return faceRotation(side, null);  
    }


    private static void drawX(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, float x1, float y1, float x2, float y2, float r, float g, float b) {
        builder.vertex(matrix, x1, y1, 0).color(r, g, b, 0.4F).normal(normal, x1,y1,0).endVertex();
        builder.vertex(matrix, x2, y2, 0).color(r, g, b, 0.4F).normal(normal, x2,y2,0).endVertex();

        builder.vertex(matrix, x2, y1, 0).color(r, g, b, 0.4F).normal(normal, x2,y1,0).endVertex();
        builder.vertex(matrix, x1, y2, 0).color(r, g, b, 0.4F).normal(normal, x1,y2,0).endVertex();
    }

    public static void renderCubeFace(VertexConsumer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        buffer.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();

        buffer.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();

        buffer.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, minY, minZ).color(r, g, b, a).endVertex();

        buffer.vertex(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(minX, maxY, maxZ).color(r, g, b, a).endVertex();
    }
}
