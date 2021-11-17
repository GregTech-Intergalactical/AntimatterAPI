package muramasa.antimatter.client.scene;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.client.RenderStateHelper;
import muramasa.antimatter.client.glu.GLU;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: KilaBash
 * @Date: 2021/08/23
 * @Description: Abstract class, and extend a lot of features compared with the original one.
 */
@OnlyIn(Dist.CLIENT)
public abstract class WorldSceneRenderer {
    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final IntBuffer VIEWPORT_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    protected static final FloatBuffer PIXEL_DEPTH_BUFFER = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final FloatBuffer OBJECT_POS_BUFFER = ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    //Changed from IBlockDisplayReader to TrackedDummyWorld to access BlockInfo, for now.
    public final IBlockDisplayReader world;
    public final Map<Collection<BlockPos>, ISceneRenderHook> renderedBlocksMap;
    private Consumer<WorldSceneRenderer> beforeRender;
    private Consumer<WorldSceneRenderer> afterRender;
    private Consumer<BlockRayTraceResult> onLookingAt;
    private int clearColor;
    private BlockRayTraceResult lastTraceResult;
    private Vector3f eyePos = new Vector3f(0, 0, 10f);
    private Vector3f lookAt = new Vector3f(0, 0, 0);
    private Vector3f worldUp = new Vector3f(0, 1, 0);

    public WorldSceneRenderer(IBlockDisplayReader world) {
        this.world = world;
        renderedBlocksMap = new LinkedHashMap<>();
    }

    public WorldSceneRenderer setBeforeWorldRender(Consumer<WorldSceneRenderer> callback) {
        this.beforeRender = callback;
        return this;
    }

    public WorldSceneRenderer setAfterWorldRender(Consumer<WorldSceneRenderer> callback) {
        this.afterRender = callback;
        return this;
    }

    public WorldSceneRenderer addRenderedBlocks(Collection<BlockPos> blocks, ISceneRenderHook renderHook) {
        if (blocks != null) {
            this.renderedBlocksMap.put(blocks, renderHook);
        }
        return this;
    }

    public WorldSceneRenderer setOnLookingAt(Consumer<BlockRayTraceResult> onLookingAt) {
        this.onLookingAt = onLookingAt;
        return this;
    }

    public void setClearColor(int clearColor) {
        this.clearColor = clearColor;
    }

    public BlockRayTraceResult getLastTraceResult() {
        return lastTraceResult;
    }

    public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
        // setupCamera
        int[] positionedRect = getPositionedRect((int)x, (int)y, (int)width, (int)height);
        int[] mousePosition = getPositionedRect(mouseX, mouseY, 0, 0);
        mouseX = mousePosition[0];
        mouseY = mousePosition[1];
//        int[] positionedRect = new int[]{0, 0, 1000, 500};
        setupCamera(positionedRect);
        // render TrackedDummyWorld
        drawWorld();
        // check lookingAt
        this.lastTraceResult = null;
        if (onLookingAt != null && mouseX > positionedRect[0] && mouseX < positionedRect[0] + positionedRect[2]
                && mouseY > positionedRect[1] && mouseY < positionedRect[1] + positionedRect[3]) {
            Vector3f hitPos = unProject(mouseX, mouseY);
            BlockRayTraceResult result = rayTrace(hitPos);
            if (result != null && result.getType() != RayTraceResult.Type.MISS) {
                this.lastTraceResult = null;
                this.lastTraceResult = result;
                onLookingAt.accept(result);
            }
        }
        // resetCamera
        resetCamera();
    }

    public Vector3f getEyePos() {
        return eyePos;
    }

    public Vector3f getLookAt() {
        return lookAt;
    }

    public Vector3f getWorldUp() {
        return worldUp;
    }

    public void setCameraLookAt(Vector3f eyePos, Vector3f lookAt, Vector3f worldUp) {
        this.eyePos = eyePos;
        this.lookAt = lookAt;
        this.worldUp = worldUp;
    }

    public void setCameraLookAt(Vector3f lookAt, double radius, double rotationPitch, double rotationYaw) {
        this.lookAt = lookAt;
        Vector3d vecX = new Vector3d(Math.cos(rotationPitch), 0, Math.sin(rotationPitch));
        Vector3d vecY = new Vector3d(0, Math.tan(rotationYaw) * vecX.length(),0);
        Vector3d pos = new Vector3d(vecX.x, vecX.y, vecX.z).add(vecY).normalize().mul(radius, radius, radius);
        this.eyePos = new Vector3f(pos.add(lookAt.getX(), lookAt.getY(), lookAt.getZ()));
    }

    protected int[] getPositionedRect(int x, int y, int width, int height) {
        return new int[]{x, y, width, height};
    }

    protected void setupCamera(int[] positionedRect) {
        int x = positionedRect[0];
        int y = positionedRect[1];
        int width = positionedRect[2];
        int height = positionedRect[3];

        RenderSystem.pushLightingAttributes();
        
        RenderStateHelper.disableLightmap();

        RenderSystem.disableLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        //setup viewport and clear GL buffers
        RenderSystem.viewport(x, y, width, height);

        clearView(x, y, width, height);

        //setup projection matrix to perspective
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();

        float aspectRatio = width / (height * 1.0f);
        GLU.gluPerspective(60.0f, aspectRatio, 0.1f, 10000.0f);

        //setup modelview matrix
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        GLU.gluLookAt(eyePos.getX(), eyePos.getY(), eyePos.getZ(), lookAt.getX(), lookAt.getY(), lookAt.getZ(), worldUp.getX(), worldUp.getY(), worldUp.getZ());
    }

    protected void clearView(int x, int y, int width, int height) {
        RenderStateHelper.setGlClearColorFromInt(clearColor, clearColor >> 24);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, false);
    }

    protected void resetCamera() {
        //reset viewport
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.viewport(0, 0, minecraft.getMainWindow().getFramebufferWidth(), minecraft.getMainWindow().getFramebufferHeight());

        //reset projection matrix
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();

        //reset modelview matrix
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.popMatrix();

        RenderStateHelper.enableLightmap();

        //reset attributes
        RenderSystem.popAttributes();
    }

    protected void drawWorld() {
        if (beforeRender != null) {
            beforeRender.accept(this);
        }

        Minecraft mc = Minecraft.getInstance();
        RenderSystem.enableCull();
        RenderSystem.enableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        RenderType oldRenderLayer = MinecraftForgeClient.getRenderLayer();
        RenderSystem.disableLighting();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();

        try { // render block in each layer
            for (RenderType layer : RenderType.getBlockRenderTypes()) {
                ForgeHooksClient.setRenderLayer(layer);
                MatrixStack matrixstack = new MatrixStack();
                Random random = new Random();
                renderedBlocksMap.forEach((renderedBlocks, hook)->{
                    if (layer == RenderType.getTranslucent()) {
                        IRenderTypeBuffer.Impl buffers = mc.getRenderTypeBuffers().getBufferSource();
                        if (hook != null) {
                            hook.apply(true, layer);
                        }
                        for (BlockPos pos : renderedBlocks) {
                            TileEntity tile = world.getTileEntity(pos);
                            if (tile != null) {
                                matrixstack.push();
                                matrixstack.translate(pos.getX(), pos.getY(), pos.getZ());
                                TileEntityRendererDispatcher.instance.renderTileEntity(tile, 0, matrixstack, buffers);
                                matrixstack.pop();
                            }
                        }
                        buffers.finish();
                    }
                    if (hook != null) {
                        hook.apply(false, layer);
                    } else {
                        setDefaultRenderLayerState(layer);
                    }
                    BufferBuilder buffer = Tessellator.getInstance().getBuffer();
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                    BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();

                    for (BlockPos pos : renderedBlocks) {
                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        TileEntity te = world.getTileEntity(pos);
                        IModelData modelData = net.minecraftforge.client.model.data.EmptyModelData.INSTANCE;
                        if (te != null) {
                            modelData = te.getModelData();
                        }
                        if (block == Blocks.AIR) continue;
                        if (state.getRenderType() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(state, layer)) {
                            matrixstack.push();
                            matrixstack.translate(pos.getX(), pos.getY(), pos.getZ());
                            blockrendererdispatcher.renderModel(state, pos, world, matrixstack, buffer, false, random, modelData);
                            matrixstack.pop();
                        }
                    }

                    Tessellator.getInstance().draw();
                    Tessellator.getInstance().getBuffer();
                });
            }
        } finally {
            ForgeHooksClient.setRenderLayer(oldRenderLayer);
        }

        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);

        if (afterRender != null) {
            afterRender.accept(this);
        }
    }

    public static void setDefaultRenderLayerState(RenderType layer) {
        RenderSystem.color4f(1, 1, 1, 1);
        if (layer == RenderType.getTranslucent()) { // SOLID
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
        } else { // TRANSLUCENT
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);

        }
    }

    public BlockRayTraceResult rayTrace(Vector3f hitPos) {
        Vector3d startPos = new Vector3d(this.eyePos.getX(), this.eyePos.getY(), this.eyePos.getZ());
        hitPos.mul(2); // Double view range to ensure pos can be seen.
        Vector3d endPos = new Vector3d((hitPos.getX() - startPos.x), (hitPos.getY() - startPos.y), (hitPos.getZ() - startPos.z));
        return this.world.rayTraceBlocks(new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, null));
    }

    public Vector3f project(BlockPos pos) {
        //read current rendering parameters
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);

        //rewind buffers after write by OpenGL glGet calls
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        VIEWPORT_BUFFER.rewind();

        //call gluProject with retrieved parameters
        GLU.gluProject(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, MODELVIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);

        //rewind buffers after read by gluProject
        VIEWPORT_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        MODELVIEW_MATRIX_BUFFER.rewind();

        //rewind buffer after write by gluProject
        OBJECT_POS_BUFFER.rewind();

        //obtain position in Screen
        float winX = OBJECT_POS_BUFFER.get();
        float winY = OBJECT_POS_BUFFER.get();
        float winZ = OBJECT_POS_BUFFER.get();

        //rewind buffer after read
        OBJECT_POS_BUFFER.rewind();

        return new Vector3f(winX, winY, winZ);
    }

    public Vector3f unProject(int mouseX, int mouseY) {
        //read depth of pixel under mouse
        GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, PIXEL_DEPTH_BUFFER);

        //rewind buffer after write by glReadPixels
        PIXEL_DEPTH_BUFFER.rewind();

        //retrieve depth from buffer (0.0-1.0f)
        float pixelDepth = PIXEL_DEPTH_BUFFER.get();

        //rewind buffer after read
        PIXEL_DEPTH_BUFFER.rewind();

        //read current rendering parameters
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);

        //rewind buffers after write by OpenGL glGet calls
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        VIEWPORT_BUFFER.rewind();

        //call gluUnProject with retrieved parameters
        GLU.gluUnProject(mouseX, mouseY, pixelDepth, MODELVIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);

        //rewind buffers after read by gluUnProject
        VIEWPORT_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        MODELVIEW_MATRIX_BUFFER.rewind();

        //rewind buffer after write by gluUnProject
        OBJECT_POS_BUFFER.rewind();

        //obtain absolute position in world
        float posX = OBJECT_POS_BUFFER.get();
        float posY = OBJECT_POS_BUFFER.get();
        float posZ = OBJECT_POS_BUFFER.get();

        //rewind buffer after read
        OBJECT_POS_BUFFER.rewind();

        return new Vector3f(posX, posY, posZ);
    }

    /***
     * For better performance, You'd better handle the event {@link #setOnLookingAt(Consumer)} or {@link #getLastTraceResult()}
     * @param mouseX xPos in Texture
     * @param mouseY yPos in Texture
     * @return BlockRayTraceResult Hit
     */
    protected BlockRayTraceResult screenPos2BlockPosFace(int mouseX, int mouseY, int x, int y, int width, int height) {
        // render a frame
        RenderSystem.enableDepthTest();
        setupCamera(getPositionedRect(x, y, width, height));

        drawWorld();

        Vector3f hitPos = unProject(mouseX, mouseY);
        BlockRayTraceResult result = rayTrace(hitPos);

        resetCamera();

        return result;
    }

    /***
     * For better performance, You'd better do project in {@link #setAfterWorldRender(Consumer)}
     * @param pos BlockPos
     * @param depth should pass Depth Test
     * @return x, y, z
     */
    protected Vector3f blockPos2ScreenPos(BlockPos pos, boolean depth, int x, int y, int width, int height){
        // render a frame
        RenderSystem.enableDepthTest();
        setupCamera(getPositionedRect(x, y, width, height));

        drawWorld();
        Vector3f winPos = project(pos);

        resetCamera();

        return winPos;
    }

    public static class BlockPosFace extends BlockPos {
        public final Direction facing;

        public BlockPosFace(BlockPos pos, Direction facing) {
            super(pos);
            this.facing = facing;
        }

        @Override
        public boolean equals(@Nullable Object bp) {
            if (bp instanceof BlockPosFace) {
                return super.equals(bp) && ((BlockPosFace) bp).facing == facing;
            }
            return false;
        }
    }
}
