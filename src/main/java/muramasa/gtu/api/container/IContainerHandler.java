package muramasa.gtu.api.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IContainerHandler {

    Container getContainer(int windowId, World world, BlockPos pos, PlayerEntity player, PlayerInventory inv, PacketBuffer data);

    Container getContainer(int windowId, World world, BlockPos pos, PlayerEntity player, PlayerInventory inv);
}
