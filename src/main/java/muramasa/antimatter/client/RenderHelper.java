package muramasa.antimatter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.ItemBattery;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.tool.armor.MaterialArmor;
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
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;
import tesseract.graph.Connectivity;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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


    public static void registerBatteryPropertyOverrides(ItemBattery battery) {
        ItemModelsProperties.registerProperty(battery, new ResourceLocation(Ref.ID, "battery"), (stack, world, living) -> {
            LazyOptional<IEnergyHandler> handler = stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY);
            return handler.map(h -> ((float) h.getEnergy() / (float) h.getCapacity())).orElse(1.0F);
        });
    }

    public static void registerProbePropertyOverrides(MaterialArmor armor) {
        ItemModelsProperties.registerProperty(armor, new ResourceLocation(Ref.ID, "probe"), (stack, world, living) -> {
            CompoundNBT nbt = stack.getTag();
            return nbt != null && nbt.contains("theoneprobe") && nbt.getBoolean("theoneprobe") ? 1.0F : 0.0F;
        });
    }

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

    //Returns the quad that would represent the quad at the front of the side, to avoid cover z-fighting.
    //Very complicated method but pretty straight forward nonetheless.
    public static int findPipeFront(PipeSize size, List<BakedQuad> quads, Direction side) {
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            //Only look at the relevant side.
            if (quad.getFace() != side) continue;
            int[] vertices = quad.getVertexData();
            //if not two vertices then skip. (All sides of relevance have just two vertices.)
            if (vertices.length != DefaultVertexFormats.BLOCK.getIntegerSize() * 4) continue;
            float p1 = Float.intBitsToFloat(vertices[0]);
            float p2 = Float.intBitsToFloat(vertices[1]);
            float p3 = Float.intBitsToFloat(vertices[2]);
            int boff = DefaultVertexFormats.BLOCK.getIntegerSize() * 2;
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
    public static ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev, Function<Block, Boolean> validator, BiFunction<Direction, TileEntity, Boolean> getter) {
        Vector3d lookPos = player.getEyePosition(ev.getPartialTicks()), rotation = player.getLook(ev.getPartialTicks()), realLookPos = lookPos.add(rotation.x * INTERACT_DISTANCE, rotation.y * INTERACT_DISTANCE, rotation.z * INTERACT_DISTANCE);
        BlockRayTraceResult result = player.getEntityWorld().rayTraceBlocks(new RayTraceContext(lookPos, realLookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        BlockState state = player.getEntityWorld().getBlockState(result.getPos());
        if (!validator.apply(state.getBlock())) return ActionResultType.PASS;
        VoxelShape shape = player.getEntityWorld().getBlockState(result.getPos()).getShape(player.getEntityWorld(), result.getPos(), ISelectionContext.forEntity(player));

        //Build up view & matrix.
        Vector3d viewPosition = ev.getInfo().getProjectedView();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        IVertexBuilder builderLines = ev.getBuffers().getBuffer(RenderType.LINES);

        MatrixStack matrix = ev.getMatrix();
        double modX = result.getPos().getX() - viewX, modY = result.getPos().getY() - viewY, modZ = result.getPos().getZ() - viewZ;
        matrix.push();
        long time = player.getEntityWorld().getGameTime();
        float r = Math.abs(time % ((255 >> 2) * 2) - (255 >> 2)) * (1 << 2);
        float g = r;
        float b = g;
        float X = 1;
        float Y = 1;

        Matrix4f m = matrix.getLast().getMatrix();
        matrix.push();
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            builderLines.pos(m, (float) (minX + modX), (float) (minY + modY), (float) (minZ + modZ)).color(r, g, b, 0.4F).endVertex();
            builderLines.pos(m, (float) (maxX + modX), (float) (maxY + modY), (float) (maxZ + modZ)).color(r, g, b, 0.4F).endVertex();
        });
        matrix.pop();
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

        builderLines.pos(matrix4f, INDENTATION_SIDE, (float) (0), (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, INDENTATION_SIDE, Y, (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), 0 + INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, X, 0 + INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, X - INDENTATION_SIDE, (float) (0), (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, X - INDENTATION_SIDE, Y, (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), Y - INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, X, Y - INDENTATION_SIDE, (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0), (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (0), Y, (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, (float) (0), (float) (0), (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, X, (float) (0), (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, X, Y, (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, X, (float) (0), (float) (0)).color(r, g, b, 0.4F).endVertex();

        builderLines.pos(matrix4f, X, Y, (float) (0)).color(r, g, b, 0.4F).endVertex();
        builderLines.pos(matrix4f, (float) (0), Y, (float) (0)).color(r, g, b, 0.4F).endVertex();
        TileEntity tile = player.getEntityWorld().getTileEntity(result.getPos());
        if (tile != null) {
            byte sides = 0;
            Direction dir = result.getFace();
            for (Direction d : Ref.DIRS) {
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
                drawX(builderLines, matrix4f, 0, 0, INDENTATION_SIDE, INDENTATION_SIDE, r, g, b);
                drawX(builderLines, matrix4f, X, 0, X - INDENTATION_SIDE, INDENTATION_SIDE, r, g, b);
                drawX(builderLines, matrix4f, X, Y, X - INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
                drawX(builderLines, matrix4f, 0, Y, INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
            if (left) {
                drawX(builderLines, matrix4f, X, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
            if (right) {
                drawX(builderLines, matrix4f, 0, INDENTATION_SIDE, INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
            if (up) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, Y - INDENTATION_SIDE, X - INDENTATION_SIDE, Y, r, g, b);
            }
            if (down) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, 0, X - INDENTATION_SIDE, INDENTATION_SIDE, r, g, b);
            }
            if (front) {
                drawX(builderLines, matrix4f, INDENTATION_SIDE, INDENTATION_SIDE, X - INDENTATION_SIDE, Y - INDENTATION_SIDE, r, g, b);
            }
        }
        matrix.pop();
        return ActionResultType.SUCCESS;
    }

    private static void drawX(IVertexBuilder builder, Matrix4f matrix, float x1, float y1, float x2, float y2) {
        drawX(builder, matrix, x1, y1, x2, y2, 0, 0, 0);
    }

    private static void drawX(IVertexBuilder builder, Matrix4f matrix, float x1, float y1, float x2, float y2, float r, float g, float b) {
        builder.pos(matrix, x1, y1, 0).color(r, g, b, 0.4F).endVertex();
        builder.pos(matrix, x2, y2, 0).color(r, g, b, 0.4F).endVertex();

        builder.pos(matrix, x2, y1, 0).color(r, g, b, 0.4F).endVertex();
        builder.pos(matrix, x1, y2, 0).color(r, g, b, 0.4F).endVertex();
    }
}
