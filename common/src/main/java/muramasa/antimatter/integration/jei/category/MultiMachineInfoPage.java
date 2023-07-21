package muramasa.antimatter.integration.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
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
    private final Component[] descriptions;
    private final TileEntityBasicMultiMachine<?>[] controllers;
    private static MultiMachineInfoPage LAST_PAGE;

    public MultiMachineInfoPage(BasicMultiMachine<?> machine, List<Pattern> patterns) {
        this.machine = machine;
        renderers = new WorldSceneRenderer[patterns.size()];
        this.controllers = new TileEntityBasicMultiMachine[patterns.size()];
        descriptions = new Component[patterns.size()];

        this.buttonNextLayer = new Button(WIDTH - (20 + RIGHT_PADDING), 65, ICON_SIZE, ICON_SIZE, new TextComponent("A"), (b)->toggleNextLayer());
        this.buttonPreviousPattern = new Button(WIDTH - ((2 * ICON_SIZE) + RIGHT_PADDING + 1), 90, ICON_SIZE, ICON_SIZE, new TextComponent("<"), (b) -> switchRenderPage(-1));
        this.buttonNextPattern = new Button(WIDTH - (ICON_SIZE + RIGHT_PADDING), 90, ICON_SIZE, ICON_SIZE, new TextComponent(">"), (b) -> switchRenderPage(1));

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
                descriptions[i] = new TranslatableComponent("InValid Structure");
            }
            Vector3f size = world.getSize();
            Vector3f minPos = world.getMinPos();
            center = new Vector3f(minPos.x() + size.x() / 2, minPos.y() + size.y() / 2, minPos.z() + size.z() / 2);
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
            zoom = (float) Mth.clamp(zoom + (ClientEvents.lastDelta < 0 ? 0.5 : -0.5), 3, 999);
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
        int height = (int) ((TrackedDummyWorld) renderer.world).getSize().y() - 1;
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
        this.buttonNextLayer.setMessage(new TextComponent(layerIndex == -1 ? "A" : Integer.toString(layerIndex + 1)));
        WorldSceneRenderer renderer = getCurrentRenderer();
        if (renderer != null) {
            TrackedDummyWorld world = ((TrackedDummyWorld)renderer.world);
            resetCenter(world);
            renderer.renderedBlocksMap.clear();
            int minY = (int) world.getMinPos().y();
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
        center = new Vector3f(minPos.x() + size.x() / 2, minPos.y() + size.y() / 2, minPos.z() + size.z() / 2);
        getCurrentRenderer().setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
    }
    
    public void drawInfo(@Nonnull PoseStack matrixStack, int mouseX, int mouseY) {
        WorldSceneRenderer renderer = getCurrentRenderer();
        Vector4f transform = new Vector4f(0, 0, 0, 1);
        transform.transform(matrixStack.last().pose());

        renderer.render(transform.x(), transform.y(), WIDTH, HEIGHT, mouseX + (int) transform.x(), mouseY + (int) transform.y());

        buttonNextPattern.render(matrixStack, mouseX, mouseY, 0);
        buttonPreviousPattern.render(matrixStack, mouseX, mouseY, 0);
        buttonNextLayer.render(matrixStack, mouseX, mouseY, 0);
        AbstractWidget.drawCenteredString(matrixStack, Minecraft.getInstance().font, descriptions[currentRendererPage], WIDTH / 2, 15, -1);
        boolean insideView = mouseX >= 0 && mouseY >= 0 && mouseX < WIDTH && mouseY < HEIGHT;
        if (insideView) {
            if (ClientEvents.leftDown) {
                rotationPitch += mouseX - lastMouseX + 360;
                rotationPitch = rotationPitch % 360;
                rotationYaw = (float) Mth.clamp(rotationYaw + (mouseY - lastMouseY), -89.9, 89.9);
            } else if (ClientEvents.rightDown) {
                int mouseDeltaY = mouseY - lastMouseY;
                if (Math.abs(mouseDeltaY) > 1) {
                    this.zoom = (float) Mth.clamp(zoom + (mouseDeltaY > 0 ? 0.5 : -0.5), 3, 999);
                }
            }
            renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    private void renderBlockOverLay(BlockHitResult rayTraceResult) {
        BlockPos pos = rayTraceResult.getBlockPos();
        doOverlay(pos, 1,1,1,0.7f);
        StructureResult res =this.controllers[currentRendererPage].getResult();
        if (res != null) {
            StructureElement el = res.get(pos);
            if (el != null && el.renderShared()) {
                //this.machine.getStructure(this.machine.getFirstTier()).allShared(el, this.controllers[currentRendererPage]).stream().filter(t -> !t.equals(pos) && this.getCurrentRenderer().world.getBlockState(t) != Blocks.AIR.defaultBlockState()).forEach(b -> this.doOverlay(b, 0.5f, 1, 0.5f, 0.4f));
            }
        }
    }

    private void doOverlay(BlockPos pos, float r, float g, float b, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
       // RenderSystem.translated((pos.getX() + 0.5), (pos.getY() + 0.5), (pos.getZ() + 0.5));
       // RenderSystem.scaled(1.01, 1.01, 1.01);

        Tesselator tessellator = Tesselator.getInstance();
        RenderSystem.disableTexture();
        BufferBuilder buffer = tessellator.getBuilder();
       // buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderHelper.renderCubeFace(buffer, -0.5f, -0.5f, -0.5f, 0.5, 0.5, 0.5, r, g, b, alpha);
        tessellator.end();
       // RenderSystem.scaled(1 / 1.01, 1 / 1.01, 1 / 1.01);
       // RenderSystem.translated(-(pos.getX() + 0.5), -(pos.getY() + 0.5), -(pos.getZ() + 0.5));
        RenderSystem.enableTexture();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public boolean handleClick(double mouseX, double mouseY, int mouseButton) {
        return buttonPreviousPattern.mouseClicked(mouseX, mouseY, mouseButton) ||
                buttonNextPattern.mouseClicked(mouseX, mouseY, mouseButton) ||
                buttonNextLayer.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, machine.getItem(machine.getFirstTier()).getDefaultInstance());
    }

    public List<Component> getTooltipStrings(double mouseX, double mouseY) {
        if (getCurrentRenderer() != null && !ClientEvents.leftDown && !ClientEvents.rightDown && !ClientEvents.middleDown) {
            WorldSceneRenderer renderer = getCurrentRenderer();
            BlockHitResult rayTraceResult = renderer.getLastTraceResult();
            if (rayTraceResult != null) {
                Minecraft minecraft = Minecraft.getInstance();
                BlockState blockState = renderer.world.getBlockState(rayTraceResult.getBlockPos());
                ItemStack itemStack = blockState.getBlock().getCloneItemStack(renderer.world, rayTraceResult.getBlockPos(), blockState);
                if (itemStack != null && !itemStack.isEmpty()) {
                    TooltipFlag flag = minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                    List<Component> list = itemStack.getTooltipLines(minecraft.player, flag);
                    StructureResult res = this.controllers[currentRendererPage].getResult();
                    if (res != null) {
                        StructureElement el = res.get(rayTraceResult.getBlockPos());
                        if (el != null) {
                            long count = 0;//this.machine.getStructure(this.machine.getFirstTier()).allShared(el, this.controllers[currentRendererPage]).stream().filter(t -> !t.equals(rayTraceResult.getBlockPos()) && this.getCurrentRenderer().world.getBlockState(t) != Blocks.AIR.defaultBlockState()).count();
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
