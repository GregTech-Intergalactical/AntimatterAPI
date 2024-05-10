package muramasa.antimatter.block;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockStorage extends BlockMaterialType implements ISharedAntimatterObject {

    public BlockStorage(String domain,  MaterialType<?> type, Material material) {
        super(domain, material, type, Properties.of(material == AntimatterMaterials.Wood ? net.minecraft.world.level.material.Material.WOOD : net.minecraft.world.level.material.Material.METAL).strength(type == AntimatterMaterialTypes.FRAME ? 2.0f : 8.0f).sound(material == AntimatterMaterials.Wood ? SoundType.WOOD : SoundType.METAL).requiresCorrectToolForDrops().isValidSpawn((blockState, blockGetter, blockPos, object) -> false));
    }
}
