package muramasa.antimatter.client;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import muramasa.antimatter.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.fluids.FluidStack;
import tesseract.graph.Connectivity;

public class RenderHelper {

    /*private static DoubleBuffer glBuf = ByteBuffer.allocateDirect(128).order(ByteOrder.nativeOrder()).asDoubleBuffer();

    public static float[][] sideToMatrixRotation = new float[][]{
            new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
            new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
            new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
            new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
            new float[]{0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
            new float[]{0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
    };

//    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
//        return Minecraft.getInstance().getTextureMap().getSprite(loc);
//    }
//
//    public static TextureAtlasSprite getSprite(Fluid fluid) {
//        return Minecraft.getInstance().getTextureMap().getSprite(fluid.getAttributes().getStillTexture());
//    }*/


    public static void drawFluid(MatrixStack mstack, Minecraft mc, int posX, int posY, int width, int height, int scaledAmount, FluidStack stack) {
        if (stack == null) return;
        Fluid fluid = stack.getFluid();
        if (fluid == null) return;
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        TextureAtlasSprite fluidStillSprite = mc.getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(fluid.getAttributes().getStillTexture());
        int fluidColor = fluid.getAttributes().getColor();

        //Draw the fluid texture
        drawTiledSprite(mstack, mc, posX, posY, width, height, 16, 16, fluidColor, scaledAmount, fluidStillSprite);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void drawTiledSprite(MatrixStack stack, Minecraft mc, int posX, int posY, int tiledWidth, int tiledHeight, int texWidth, int texHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;
        RenderSystem.color4f(red, green, blue, alpha);
        Matrix4f matrix = stack.getLast().getMatrix();

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

                    drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    //Credit: JEI. Some alterations.
    public static void drawTextureWithMasking(Matrix4f stack, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
        double uMin = textureSprite.getMinU();
        double uMax = textureSprite.getMaxU();
        double vMin = textureSprite.getMinV();
        double vMax = textureSprite.getMaxV();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(stack, xCoord, yCoord + 16, zLevel).tex((float) uMin, (float) vMax).endVertex();
        bufferBuilder.pos(stack, xCoord + 16 - maskRight, yCoord + 16, zLevel).tex((float) uMax, (float) vMax).endVertex();
        bufferBuilder.pos(stack, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex((float) uMax, (float) vMin).endVertex();
        bufferBuilder.pos(stack, xCoord, yCoord + maskTop, zLevel).tex((float) uMin, (float) vMin).endVertex();
        tessellator.draw();
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
        colorQuad(quad, DefaultVertexFormats.BLOCK.getIntegerSize(), DefaultVertexFormats.BLOCK.getOffset(1) / 4, rgb);
    }

    public static void colorQuad(BakedQuad quad, int size, int offset, int rgb) {
        int[] vertices = quad.getVertexData();
        for (int i = 0; i < 4; i++) {
            vertices[offset + size * i] = convertRGB2ABGR(rgb);
        }
    }

    public static int convertRGB2ABGR(int colour) {
        return 0xFF << 24 | ((colour & 0xFF) << 16) | ((colour >> 8) & 0xFF) << 8 | (colour >> 16) & 0xFF;
    }

    static float INDENTATION_SIDE = 0.25F;
    static double INTERACT_DISTANCE = 5;


    //This code is pretty complicated but it was written while I knew nothing about rendering. it works though
    public static ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev, Function<Block, Boolean> validator, BiFunction<Direction, TileEntity, Boolean> getter) {
        Vector3d lookPos = player.getEyePosition(ev.getPartialTicks()), rotation = player.getLook(ev.getPartialTicks()), realLookPos = lookPos.add(rotation.x * INTERACT_DISTANCE, rotation.y * INTERACT_DISTANCE, rotation.z * INTERACT_DISTANCE);
        BlockRayTraceResult result = player.getEntityWorld().rayTraceBlocks(new RayTraceContext(lookPos, realLookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        BlockState state = player.getEntityWorld().getBlockState(result.getPos());
        if (!validator.apply(state.getBlock())) return ActionResultType.PASS;

        //Build up view & matrix.
        Vector3d viewPosition = ev.getInfo().getProjectedView();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        IVertexBuilder builderLines = ev.getBuffers().getBuffer(RenderType.LINES);

        MatrixStack matrix = ev.getMatrix();
        double modX = result.getPos().getX() - viewX, modY = result.getPos().getY() - viewY, modZ = result.getPos().getZ() - viewZ;
        matrix.push();

        // VoxelShape shape = player.getEntityWorld().getBlockState(pos).getShape(player.getEntityWorld(), pos, ISelectionContext.forEntity(player));
        float X = 1;//(float) shape.getEnd(Direction.Axis.X);
        float Y = 1;//(float) shape.getEnd(Direction.Axis.Y);
        //  float Z = 1//(float) shape.getEnd(Direction.Axis.Z);
        //TODO: Better way to do this. dont know if forge has this built in? blit on certain face
        //Rotate & translate to the correct face.
        switch (result.getFace()) {
            case UP:
                matrix.translate(modX, modY + 1, modZ + 1);
                matrix.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                break;
            case DOWN:
                matrix.translate(modX, modY, modZ + 1);
                matrix.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                break;
            case EAST:
                matrix.translate(modX + 1, modY, modZ);
                matrix.rotate(new Quaternion(new Vector3f(0, 1, 0), -90, true));
                break;
            case WEST:
                matrix.translate(modX, modY, modZ);
                matrix.rotate(new Quaternion(new Vector3f(0, 1, 0), -90, true));
                break;
            case SOUTH:
                matrix.translate(modX, modY, modZ + 1);
                break;
            case NORTH:
                matrix.translate(modX, modY, modZ);
                break;
        }

        //Draw 4 lines.
        Matrix4f matrix4f = matrix.getLast().getMatrix();

        //TODO: Use SHAPE to get actual size of box.

        builderLines.pos(matrix4f, (float) (INDENTATION_SIDE), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (INDENTATION_SIDE), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0 + INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (0 + INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (X - INDENTATION_SIDE), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X - INDENTATION_SIDE), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (Y - INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (Y - INDENTATION_SIDE), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (0), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (X), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (X), (float) (0), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (X), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (0), (float) (Y), (float) (0)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        TileEntity tile = player.getEntityWorld().getTileEntity(result.getPos());
        if (tile != null) {
            byte sides = 0;
            Direction dir = result.getFace();
            for (Direction d: Ref.DIRS) {
                if (getter.apply(d, tile)) {
                    sides |= 1 << d.getIndex();
                }
            }
            boolean left, right, up, down, back, front;
            back = Connectivity.has(sides, dir.getOpposite().getIndex());
            front = Connectivity.has(sides, dir.getIndex());
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
                    right = Connectivity.has(sides, dir.rotateYCCW().getIndex());
                    left = Connectivity.has(sides, dir.rotateY().getIndex());
                } else {
                    right = Connectivity.has(sides, dir.rotateY().getIndex());
                    left = Connectivity.has(sides, dir.rotateYCCW().getIndex());
                }
                up = Connectivity.has(sides, 1);
                down = Connectivity.has(sides, 0);
            }
            if (back) {
                drawX(builderLines, matrix4f, 0, 0, INDENTATION_SIDE, INDENTATION_SIDE);
                drawX(builderLines, matrix4f, X, 0, X - INDENTATION_SIDE, INDENTATION_SIDE);
                drawX(builderLines, matrix4f, X, Y, X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
                drawX(builderLines, matrix4f, 0, Y, INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
            if (left) {
                drawX(builderLines, matrix4f, X, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
            if (right) {
                drawX(builderLines, matrix4f, 0, INDENTATION_SIDE, INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
            if (up) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, Y - INDENTATION_SIDE, X - INDENTATION_SIDE, Y);
            }
            if (down) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, 0, X - INDENTATION_SIDE, INDENTATION_SIDE);
            }
            if (front) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, INDENTATION_SIDE,X - INDENTATION_SIDE, Y - INDENTATION_SIDE);
            }
        }
        matrix.pop();
        return ActionResultType.SUCCESS;
    }

    private static void drawX(IVertexBuilder builder, Matrix4f matrix, float x1, float y1, float x2, float y2) {
        builder.pos(matrix, x1, y1, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builder.pos(matrix, x2, y2, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();

        builder.pos(matrix, x2, y1, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
        builder.pos(matrix, x1, y2, 0).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
    }
}
