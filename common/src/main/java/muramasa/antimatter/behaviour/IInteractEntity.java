package muramasa.antimatter.behaviour;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IInteractEntity<T> extends IBehaviour<T> {
    @Override
    default String getId() {
        return "interact_entity";
    }

    InteractionResult interactLivingEntity(T instance, ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand);
}
