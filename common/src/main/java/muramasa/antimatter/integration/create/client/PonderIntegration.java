package muramasa.antimatter.integration.create.client;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import com.simibubi.create.foundation.ponder.Selection;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.structure.BlockInfo;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PonderIntegration {

    public static void registerMultiblock(BasicMultiMachine<?> machine, Tier t, List<Pattern> patterns){
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);

            if (pattern.getBlockInfos().length > 0){
                CompoundTag nbt = new CompoundTag();
                ListTag size = new ListTag();
                size.add(IntTag.valueOf(pattern.getBlockInfos()[0].length));
                size.add(IntTag.valueOf(pattern.getBlockInfos().length + 1));
                size.add(IntTag.valueOf(pattern.getBlockInfos()[0][0].length));
                nbt.put("size", size);
                ListTag blockTags = new ListTag();
                List<BlockState> states =new ArrayList<>();
                List<BlockPos> controllerPositions = new ArrayList<>();
                BlockInfo[][][] blocks = pattern.getBlockInfos();
                for (int y = 0; y < blocks.length + 1; y++) {
                    BlockInfo[][] aisle = blocks[y == 0 ? y : y - 1];
                    for (int x = 0; x < aisle.length; x++) {
                        BlockInfo[] column = aisle[x];
                        for (int z = 0; z < column.length; z++) {
                            // fill XYZ instead of YZX
                            BlockPos blockPos = new BlockPos(x, y, z);
                            BlockInfo info = column[z];
                            BlockState state = y == 0 ? (x + z) % 2 == 0 ? Blocks.WHITE_CONCRETE.defaultBlockState() :  Blocks.SNOW_BLOCK.defaultBlockState() :  info.getBlockState();
                            if (!states.contains(state)){
                                states.add(state);
                            }
                            if (state.getBlock() instanceof BlockMultiMachine) {
                                controllerPositions.add(blockPos);
                            }
                            int index = states.indexOf(state);
                            CompoundTag entry = new CompoundTag();
                            entry.putInt("state", index);
                            ListTag pos = new ListTag();
                            pos.add(IntTag.valueOf(blockPos.getX()));
                            pos.add(IntTag.valueOf(blockPos.getY()));
                            pos.add(IntTag.valueOf(blockPos.getZ()));
                            entry.put("pos", pos);
                            blockTags.add(entry);
                        }
                    }
                }
                if (blockTags.isEmpty() || states.isEmpty()) return;
                nbt.put("blocks", blockTags);
                ListTag palette = new ListTag();
                states.forEach(s -> {
                    palette.add(NbtUtils.writeBlockState(s));
                });
                nbt.put("palette", palette);
                nbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    NbtIo.writeCompressed(nbt, stream);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                AntimatterDynamics.DYNAMIC_RESOURCE_PACK.addAsset(new ResourceLocation(machine.getDomain(), "ponder/" + machine.getBlockState(t).getId() + "/" +  i + ".nbt"), stream.toByteArray());
                AntimatterDynamics.RUNTIME_DATA_PACK.addData(new ResourceLocation(machine.getDomain(), "structures/" + machine.getBlockState(t).getId() + "/" +  i + ".nbt"), stream.toByteArray());
                var storyBoardentry = new PonderStoryBoardEntry((scene, util) -> {
                    scene.title(machine.getBlockState(t).getId(), machine.getLang("en_us") + " Multiblock");
                    scene.rotateCameraY(180f);
                    scene.configureBasePlate(0, 0, Math.max(pattern.getBlockInfos()[0].length, pattern.getBlockInfos()[0][0].length) + 1);
                    scene.showBasePlate();
                    scene.scaleSceneView(pattern.getScale());
                    scene.idle(5);
                    for (int y = 1; y < blocks.length + 1; y++) {
                        Selection selection = util.select.fromTo(0, y, 0, pattern.getBlockInfos()[0].length - 1, y, pattern.getBlockInfos()[0][0].length - 1);
                        scene.world.showSection(selection, Direction.UP);
                        scene.idleSeconds(4);
                    }
                    controllerPositions.forEach(pos -> {
                        scene.world.modifyTileEntity(pos, BlockEntityBasicMultiMachine.class, b -> {
                            b.checkStructure();
                            b.setMachineState(MachineState.IDLE);
                        });
                        scene.idle(5);
                    });

                    scene.markAsFinished();
                }, machine.getDomain(), new ResourceLocation(machine.getDomain(), machine.getBlockState(t).getId() + "/" +  i), machine.getBlockState(t).getLoc());
                PonderRegistry.addStoryBoard(storyBoardentry);
            }

        }

    }
}
