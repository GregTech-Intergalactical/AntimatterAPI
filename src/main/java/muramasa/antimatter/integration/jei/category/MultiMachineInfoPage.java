package muramasa.antimatter.integration.jei.category;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.event.ClientEvents;
import muramasa.antimatter.client.scene.ImmediateWorldSceneRenderer;
import muramasa.antimatter.client.scene.TrackedDummyWorld;
import muramasa.antimatter.client.scene.WorldSceneRenderer;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.structure.BlockInfo;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiMachineInfoPage {

    private final BasicMultiMachine<?> machine;
    private int layerIndex = -1;
    private int currentRendererPage = 0;
    private int lastMouseX;
    private int lastMouseY;
    private Vector3f center;
    private float rotationYaw;
    private float rotationPitch;
    private float zoom;

    private final static int WIDTH = 176;
    private final static int HEIGHT = 150;
    private final static int ICON_SIZE = 20;
    private final static int RIGHT_PADDING = 5;

    private final Button buttonPreviousPattern;
    private final Button buttonNextPattern;
    private final Button buttonNextLayer;

    private final WorldSceneRenderer[] renderers;
    private final ITextComponent[] descriptions;
    private final TileEntityBasicMultiMachine<?>[] controllers;
    private static MultiMachineInfoPage LAST_PAGE;

    public MultiMachineInfoPage(BasicMultiMachine<?> machine, List<Pattern> patterns) {
        this.machine = machine;
        renderers = new WorldSceneRenderer[patterns.size()];
        this.controllers = new TileEntityBasicMultiMachine[patterns.size()];
        descriptions = new ITextComponent[patterns.size()];

        this.buttonNextLayer = new Button(WIDTH - (20 + RIGHT_PADDING), 65, ICON_SIZE, ICON_SIZE, new StringTextComponent("A"), (b)->toggleNextLayer());
        this.buttonPreviousPattern = new Button(WIDTH - ((2 * ICON_SIZE) + RIGHT_PADDING + 1), 90, ICON_SIZE, ICON_SIZE, new StringTextComponent("<"), (b) -> switchRenderPage(-1));
        this.buttonNextPattern = new Button(WIDTH - (ICON_SIZE + RIGHT_PADDING), 90, ICON_SIZE, ICON_SIZE, new StringTextComponent(">"), (b) -> switchRenderPage(1));

        for (int i = 0; i < patterns.size(); i++) {
            descriptions[i] = patterns.get(i).getDescription();
            Map<BlockPos, BlockInfo> blockMap = new HashMap<>();
            BlockInfo[][][] blocks = patterns.get(i).getBlockInfos();
            TileEntityBasicMultiMachine<?> controllers = null;
            for (int z = 0; z < blocks.length; z++) {
                BlockInfo[][] aisle = blocks[z];
                for (int y = 0; y < aisle.length; y++) {
                    BlockInfo[] column = aisle[y];
                    for (int x = 0; x < column.length; x++) {
                        // fill XYZ instead of YZX
                        BlockPos blockPos = new BlockPos(y, z, x);
                        BlockInfo blockInfo = column[x];
                        blockMap.put(blockPos, blockInfo);
                        if (blockInfo.getTileEntity() instanceof TileEntityBasicMultiMachine) {
                            controllers = (TileEntityBasicMultiMachine<?>) blockInfo.getTileEntity();
                            this.controllers[i] = controllers;
                        }
                    }
                }
            }
            TrackedDummyWorld world = new TrackedDummyWorld();
            ImmediateWorldSceneRenderer worldSceneRenderer = new ImmediateWorldSceneRenderer(world);
            worldSceneRenderer.setClearColor(0xC6C6C6);
            world.addBlocks(blockMap);
            if (controllers == null || !controllers.checkStructure()) {
                descriptions[i] = new TranslationTextComponent("InValid Structure");
            }
            Vector3f size = world.getSize();
            Vector3f minPos = world.getMinPos();
            center = new Vector3f(minPos.getX() + size.getX() / 2, minPos.getY() + size.getY() / 2, minPos.getZ() + size.getZ() / 2);
            worldSceneRenderer.addRenderedBlocks(world.getRenderedBlocks().keySet(), null);
            worldSceneRenderer.setOnLookingAt(this::renderBlockOverLay);
            world.setRenderFilter(pos->worldSceneRenderer.renderedBlocksMap.keySet().stream().anyMatch(c->c.contains(pos)));
            renderers[i] = worldSceneRenderer;
        }
    }

    public void setRecipeLayout(IRecipeLayout layout, IGuiHelper guiHelper) {
        if (ClientEvents.lastDelta == 0 || LAST_PAGE != this) {
            LAST_PAGE = this;
            this.zoom = 8;
            this.rotationYaw = 20.0f;
            this.rotationPitch = 135.0f;
            this.currentRendererPage = 0;
            setNextLayer(-1);
        } else {
            zoom = (float) MathHelper.clamp(zoom + (ClientEvents.lastDelta < 0 ? 0.5 : -0.5), 3, 999);
            setNextLayer(getLayerIndex());
        }
        if (getCurrentRenderer() != null) {
            TrackedDummyWorld world = (TrackedDummyWorld) getCurrentRenderer().world;
            resetCenter(world);
        }
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    private void toggleNextLayer() {
        WorldSceneRenderer renderer = getCurrentRenderer();
        int height = (int) ((TrackedDummyWorld)renderer.world).getSize().getY() - 1;
        if (++this.layerIndex > height) {
            //if current layer index is more than max height, reset it
            //to display all layers
            this.layerIndex = -1;
        }
        setNextLayer(layerIndex);
    }

    private void switchRenderPage(int i) {
        int maxIndex = renderers.length - 1;
        int newIndex = Math.max(0, Math.min(currentRendererPage + i, maxIndex));
        if (currentRendererPage != newIndex) {
            this.currentRendererPage = newIndex;
            this.buttonNextPattern.active = newIndex < maxIndex;
            this.buttonPreviousPattern.active = newIndex > 0;
            setNextLayer(-1);
            getCurrentRenderer().setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        }
    }

    private void setNextLayer(int newLayer) {
        this.layerIndex = newLayer;
        this.buttonNextLayer.setMessage(new StringTextComponent(layerIndex == -1 ? "A" : Integer.toString(layerIndex + 1)));
        WorldSceneRenderer renderer = getCurrentRenderer();
        if (renderer != null) {
            TrackedDummyWorld world = ((TrackedDummyWorld)renderer.world);
            resetCenter(world);
            renderer.renderedBlocksMap.clear();
            int minY = (int) world.getMinPos().getY();
            Collection<BlockPos> renderBlocks;
            if (newLayer == -1) {
                renderBlocks = world.getRenderedBlocks().keySet();
            } else {
                renderBlocks = world.getRenderedBlocks().keySet().stream().filter(pos->pos.getY() - minY == newLayer).collect(Collectors.toSet());
            }
            renderer.addRenderedBlocks(renderBlocks, null);
        }
    }

    public WorldSceneRenderer getCurrentRenderer() {
        return renderers[currentRendererPage];
    }

    private void resetCenter(TrackedDummyWorld world) {
        Vector3f size = world.getSize();
        Vector3f minPos = world.getMinPos();
        center = new Vector3f(minPos.getX() + size.getX() / 2, minPos.getY() + size.getY() / 2, minPos.getZ() + size.getZ() / 2);
        getCurrentRenderer().setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
    }
    
    public void drawInfo(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        WorldSceneRenderer renderer = getCurrentRenderer();
        Vector4f transform = new Vector4f(0, 0, 0, 1);
        transform.transform(matrixStack.getLast().getMatrix());

        renderer.render(transform.getX(), transform.getY(), WIDTH, HEIGHT, mouseX + (int)transform.getX(), mouseY + (int)transform.getY());

        buttonNextPattern.render(matrixStack, mouseX, mouseY, 0);
        buttonPreviousPattern.render(matrixStack, mouseX, mouseY, 0);
        buttonNextLayer.render(matrixStack, mouseX, mouseY, 0);
        Widget.drawCenteredString(matrixStack, Minecraft.getInstance().fontRenderer, descriptions[currentRendererPage], WIDTH / 2, 15, -1);
        boolean insideView = mouseX >= 0 && mouseY >= 0 && mouseX < WIDTH && mouseY < HEIGHT;
        if (insideView) {
            if (ClientEvents.leftDown) {
                rotationPitch += mouseX - lastMouseX + 360;
                rotationPitch = rotationPitch % 360;
                rotationYaw = (float) MathHelper.clamp(rotationYaw + (mouseY - lastMouseY), -89.9, 89.9);
            } else if (ClientEvents.rightDown) {
                int mouseDeltaY = mouseY - lastMouseY;
                if (Math.abs(mouseDeltaY) > 1) {
                    this.zoom = (float) MathHelper.clamp(zoom + (mouseDeltaY > 0 ? 0.5 : -0.5), 3, 999);
                }
            }
            renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    private void renderBlockOverLay(BlockRayTraceResult rayTraceResult) {
        BlockPos pos = rayTraceResult.getPos();
        doOverlay(pos, 1,1,1,0.7f);
        StructureResult res =this.controllers[currentRendererPage].getResult();
        if (res != null) {
            StructureElement el = res.get(pos);
            if (el != null && el.renderShared()) {
                this.machine.getStructure(this.machine.getFirstTier()).allShared(el, this.controllers[currentRendererPage]).stream().filter(t -> !t.equals(pos) && this.getCurrentRenderer().world.getBlockState(t) != Blocks.AIR.getDefaultState()).forEach(b -> this.doOverlay(b, 0.5f,1,0.5f,0.4f));
            }
        }
    }

    private void doOverlay(BlockPos pos, float r, float g, float b, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.translated((pos.getX() + 0.5), (pos.getY() + 0.5), (pos.getZ() + 0.5));
        RenderSystem.scaled(1.01, 1.01, 1.01);

        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.disableTexture();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        RenderHelper.renderCubeFace(buffer, -0.5f, -0.5f, -0.5f, 0.5, 0.5, 0.5, r,g,b, alpha);
        tessellator.draw();
        RenderSystem.scaled(1 / 1.01, 1 / 1.01, 1 / 1.01);
        RenderSystem.translated(-(pos.getX() + 0.5), -(pos.getY() + 0.5), -(pos.getZ() + 0.5));
        RenderSystem.enableTexture();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1, 1, 1, 1);
    }

    public boolean handleClick(double mouseX, double mouseY, int mouseButton) {
        return buttonPreviousPattern.mouseClicked(mouseX, mouseY, mouseButton) ||
                buttonNextPattern.mouseClicked(mouseX, mouseY, mouseButton) ||
                buttonNextLayer.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, machine.getItem(machine.getFirstTier()).getDefaultInstance());
    }

    public List<ITextComponent> getTooltipStrings(double mouseX, double mouseY) {
        if (getCurrentRenderer() != null && !ClientEvents.leftDown && !ClientEvents.rightDown && !ClientEvents.middleDown) {
            WorldSceneRenderer renderer = getCurrentRenderer();
            BlockRayTraceResult rayTraceResult = renderer.getLastTraceResult();
            if (rayTraceResult != null) {
                Minecraft minecraft = Minecraft.getInstance();
                BlockState blockState = renderer.world.getBlockState(rayTraceResult.getPos());
                ItemStack itemStack = blockState.getBlock().getPickBlock(blockState, rayTraceResult, renderer.world, rayTraceResult.getPos(), minecraft.player);
                if (itemStack != null && !itemStack.isEmpty()) {
                    ITooltipFlag flag = minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
                    List<ITextComponent> list = itemStack.getTooltip(minecraft.player, flag);
                    StructureResult res = this.controllers[currentRendererPage].getResult();
                    if (res != null) {
                        StructureElement el = res.get(rayTraceResult.getPos());
                        if (el != null) {
                            long count = this.machine.getStructure(this.machine.getFirstTier()).allShared(el, this.controllers[currentRendererPage]).stream().filter(t -> !t.equals(rayTraceResult.getPos()) && this.getCurrentRenderer().world.getBlockState(t) != Blocks.AIR.getDefaultState()).count();
                            el.onInfoTooltip(list, count, this.controllers[currentRendererPage]);
                        }
                    }
                    return list;
                }
            }
        }
        return Collections.emptyList();
    }
}
