package muramasa.antimatter.advancement.trigger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.Ref;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This implementation isn't perfect, it doesn't fire ServerPlayerEntity#sendSlotContents. And we're doing this on an Event level.
 * However, it is Tag sensitive (why don't you do something similar vanilla?).
 *
 * You can theoretically trigger this ICriterionTrigger anywhere you want, as long as you feed it an ItemStack
 * and have your Advancement implement this with a tag.
 *
 * Mixins are great for this application, but its better to avoid doing that, at least in vanilla AntimatterAPI.
 * Feel free to patch it with an external coremod.
 *
 * Right now TagSensitiveInventoryChangedTrigger triggers when Player picks up an item, or on opening/closing their inventory screen.
 */
public class TagSensitiveInventoryChangedTrigger implements ICriterionTrigger<TagSensitiveInventoryChangedTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Ref.ID, "pick_up");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener) {
        TagSensitiveInventoryChangedTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners == null) {
            listeners = new TagSensitiveInventoryChangedTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    public void trigger(ServerPlayerEntity player) {
        TagSensitiveInventoryChangedTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(player.inventory);
        }
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        TagSensitiveInventoryChangedTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(stack);
        }
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener) {
        TagSensitiveInventoryChangedTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners != null) listeners.remove(listener);
        if (listeners.isEmpty()) this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdv) {
        this.listeners.remove(playerAdv);
    }

    @Override
    public TagSensitiveInventoryChangedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        String[] splits = JSONUtils.getString(json, "tag").split(":");
        return new TagSensitiveInventoryChangedTrigger.Instance(Utils.getItemTag(new ResourceLocation(splits[0], splits[1])));
    }

    public static class Instance extends CriterionInstance {

        private Tag<Item> tag;

        public Instance(Tag<Item> tag) {
            super(TagSensitiveInventoryChangedTrigger.ID);
            this.tag = tag;
        }

        public boolean test(PlayerInventory inv) {
            if (tag.getAllElements().contains(inv.getItemStack().getItem())) return true;
            if (inv.hasTag(tag)) return true;
            /*
            for (ItemStack stack : inv.mainInventory) {
                if (tag.getAllElements().contains(stack.getItem())) {
                    return true;
                }
            }
             */
            return false;
        }

        public boolean test(ItemStack stack) {
            return tag.getAllElements().contains(stack.getItem());
        }

        @Override
        public JsonElement serialize() {
            JsonObject obj = new JsonObject();
            obj.addProperty("tag", this.tag.getId().toString());
            return obj;
        }
    }

    private static class Listeners {

        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(PlayerInventory inv) {
            List<Listener<Instance>> list = null;
            for (ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(inv)) {
                    if (list == null) list = Lists.newArrayList();
                    list.add(listener);
                }
            }
            grant(list);
        }

        public void trigger(ItemStack stack) {
            List<Listener<Instance>> list = null;
            for (ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(stack)) {
                    if (list == null) list = Lists.newArrayList();
                    list.add(listener);
                }
            }
            grant(list);
        }

        private void grant(List<Listener<Instance>> list) {
            if (list != null) {
                for (ICriterionTrigger.Listener<TagSensitiveInventoryChangedTrigger.Instance> listener : list) {
                    listener.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
