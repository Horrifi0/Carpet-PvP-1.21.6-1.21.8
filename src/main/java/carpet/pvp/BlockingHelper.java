package carpet.pvp;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public final class BlockingHelper {
    private BlockingHelper() {}

    public static boolean isBlocking(LivingEntity entity) {
        if (entity == null) return false;
        if (!entity.isUsingItem()) return false;
        ItemStack stack = entity.getUseItem();
        if (stack == null || stack.isEmpty()) return false;
        return stack.getUseAnimation() == UseAnim.BLOCK;
    }
}
