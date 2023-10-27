package muramasa.antimatter.integration.create.client;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.structure.BlockInfo;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
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
                size.add(IntTag.valueOf(pattern.getBlockInfos().length));
                size.add(IntTag.valueOf(pattern.getBlockInfos()[0][0].length));
                nbt.put("size", size);
                ListTag blockTags = new ListTag();
                List<BlockState> states =new ArrayList<>();
                BlockInfo[][][] blocks = pattern.getBlockInfos();
                for (int y = 0; y < blocks.length; y++) {
                    BlockInfo[][] aisle = blocks[y];
                    for (int x = 0; x < aisle.length; x++) {
                        BlockInfo[] column = aisle[x];
                        for (int z = 0; z < column.length; z++) {
                            // fill XYZ instead of YZX
                            BlockPos blockPos = new BlockPos(x, y, z);
                            BlockInfo info = column[z];
                            BlockState state = info.getBlockState();
                            if (!states.contains(info.getBlockState())){
                                states.add(info.getBlockState());
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
                    scene.title(machine.getId(), machine.getLang("en_us") + " Multiblock");
                    scene.configureBasePlate(0, 0, pattern.getBlockInfos()[0][0].length);
                }, machine.getDomain(), new ResourceLocation(machine.getDomain(), machine.getBlockState(t).getId() + "/" +  i), machine.getBlockState(t).getLoc());
                PonderRegistry.addStoryBoard(storyBoardentry);
            }

        }

    }
}
