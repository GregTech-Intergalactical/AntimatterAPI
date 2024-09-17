package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.network.AntimatterNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StructureCheckPacket  implements Packet<StructureCheckPacket> {
    public static PacketHandler<StructureCheckPacket> HANDLER = new Handler();
    public BlockPos pos;
    public boolean invalidate;

    public StructureCheckPacket(BlockPos pos, boolean invalidate) {
        this.pos = pos;
        this.invalidate = invalidate;
    }

    @Override
    public ResourceLocation getID() {
        return AntimatterNetwork.STRUCTURE_CHECK_PACKET_ID;
    }

    @Override
    public PacketHandler<StructureCheckPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<StructureCheckPacket> {

        @Override
        public void encode(StructureCheckPacket structureCheckPacket, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeBlockPos(structureCheckPacket.pos);
            friendlyByteBuf.writeBoolean(structureCheckPacket.invalidate);
        }

        @Override
        public StructureCheckPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new StructureCheckPacket(friendlyByteBuf.readBlockPos(), friendlyByteBuf.readBoolean());
        }

        @Override
        public PacketContext handle(StructureCheckPacket structureCheckPacket) {
            return (sender, level) -> {
                BlockEntity blockEntity = level.getBlockEntity(structureCheckPacket.pos);
                if (blockEntity instanceof BlockEntityBasicMultiMachine<?> multiMachine){
                    if(structureCheckPacket.invalidate){
                        multiMachine.invalidateStructure();
                    } else {
                        multiMachine.checkStructure();
                    }
                }
            };
        }
    }
}
