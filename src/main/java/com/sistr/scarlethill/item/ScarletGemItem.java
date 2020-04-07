package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.util.MathUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ScarletGemItem extends Item implements ILeftClickable {

    public ScarletGemItem() {
        super(new Item.Properties()
                .group(ModSetup.ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip0").applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip1").applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip2").applyTextStyle(TextFormatting.GRAY));
    }

    //一瞬無敵
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.getCooldownTracker().hasCooldown(this)) return ActionResult.resultFail(stack);
        player.getCooldownTracker().setCooldown(this, 40);
        player.addPotionEffect(new EffectInstance(Registration.SCARLET_BLESSING.get(), 20, 0));
        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1, 1.5F + world.rand.nextFloat() * 0.5F);
        return ActionResult.resultSuccess(stack);
    }

    //周囲の敵のみを弾き飛ばす
    @Override
    public boolean hitEntity(ItemStack p_77644_1_, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            if (player.getCooldownTracker().hasCooldown(this)) return false;
            player.getCooldownTracker().setCooldown(this, 40);
        }
        attacker.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1.8F + attacker.world.rand.nextFloat() * 0.2F);
        bomb(attacker.world, attacker, 8F, 8);
        return true;
    }

    @Override
    public void onLeftClick(World world, PlayerEntity player, Hand hand) {
        if (player.getCooldownTracker().hasCooldown(this)) return;
        player.getCooldownTracker().setCooldown(this, 40);
        player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1.8F + world.rand.nextFloat() * 0.2F);
        bomb(world, player, 8F, 8);
    }

    public static void bomb(World worldIn, LivingEntity user, float radius, float damage) {
        AxisAlignedBB bb = new AxisAlignedBB(user.getPosX() + radius, user.getPosY() + radius, user.getPosZ() + radius,
                user.getPosX() - radius, user.getPosY() - radius, user.getPosZ() - radius);
        List<Entity> aroundEnemy = worldIn.getEntitiesInAABBexcluding(user, bb, entity ->
                entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator() && entity instanceof MonsterEntity && entity.getDistanceSq(user) < radius * radius);
        aroundEnemy.forEach(entity -> {
            if (entity instanceof LivingEntity) {
                DamageSource damageSource;
                if (user instanceof PlayerEntity) {
                    damageSource = DamageSource.causePlayerDamage((PlayerEntity) user);
                } else {
                    damageSource = DamageSource.causeMobDamage(user);
                }
                entity.attackEntityFrom(damageSource.setFireDamage(), damage);
                ((LivingEntity) entity).knockBack(user, 3, user.getPosX() - entity.getPosX(), user.getPosZ() - entity.getPosZ());
            }
        });
        user.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0F, 2.6F + (user.world.getRandom().nextFloat() - user.world.getRandom().nextFloat()) * 0.8F);
        if (worldIn.isRemote) {
            float count = radius * 8;
            for (int k = 0; k < count; k++) {
                for (int i = 0; i < count; i++) {
                    Vec3d pos = MathUtil.getVector(new Vec2f((float) k / count * 360, ((float) i / count * 2 - 1) * 180))
                            .scale(MathHelper.sqrt(user.getRNG().nextFloat()) * (radius - 1) + 1)
                            .add(user.getPosX(), user.getPosY() + user.getEyeHeight(), user.getPosZ());
                    worldIn.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
                }
            }
        }
    }
}
