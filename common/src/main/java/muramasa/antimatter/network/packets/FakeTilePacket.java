package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import muramasa.antimatter.blockentity.BlockEntityFakeBlock;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FakeTilePacket implements Packet<FakeTilePacket> {
    public static final PacketHandler<FakeTilePacket> HANDLER = new Handler();
    final BlockPos fakeTilePos, controllerPos;

    public FakeTilePacket(BlockPos fakeTilePos, BlockPos controllerPos) {
        this.fakeTilePos = fakeTilePos;
        this.controllerPos = controllerPos;
    }

    @Override
    public ResourceLocation getID() {
        return AntimatterNetwork.FAKE_TILE_PACKET_ID;
    }

    @Override
    public PacketHandler<FakeTilePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<FakeTilePacket> {

        @Override
        public void encode(FakeTilePacket msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.fakeTilePos);
            buf.writeBlockPos(msg.controllerPos);
        }

        @Override
        public FakeTilePacket decode(FriendlyByteBuf buf) {
            return new FakeTilePacket(buf.readBlockPos(), buf.readBlockPos());
        }

        @Override
        public PacketContext handle(FakeTilePacket msg) {
            return (sender, level) -> {
                if (sender != null) {
                    BlockEntity tile = Utils.getTile(sender.getLevel(), msg.fakeTilePos);
                    if (tile instanceof BlockEntityFakeBlock fakeBlock) {
                        BlockEntity controller = Utils.getTile(sender.getLevel(), msg.controllerPos);
                        if (controller instanceof BlockEntityBasicMultiMachine<?> machine){
                            fakeBlock.setController(machine);
                        }
                    }
                }
            };
        }
    }
}
