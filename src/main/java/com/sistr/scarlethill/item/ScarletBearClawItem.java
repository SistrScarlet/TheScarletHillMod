package com.sistr.scarlethill.item;

import com.google.common.collect.Lists;
import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

//todo 親指の後ろにUVミスが一点
public class ScarletBearClawItem extends Item implements ILeftClickable {

    public ScarletBearClawItem() {
        super(new Item.Properties()
                .group(ModSetup.ITEM_GROUP)
                .maxStackSize(1)
                .maxDamage(500));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip0").applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip1").applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip2").applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".tooltip3").applyTextStyle(TextFormatting.GRAY));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.onGround && !playerIn.getCooldownTracker().hasCooldown(this)) {
            playerIn.getCooldownTracker().setCooldown(this, 8);
            playerIn.setActiveHand(handIn);

            playerIn.setMotion(playerIn.getMotion().add(playerIn.getLookVec()
                    .mul(1, 0, 1).normalize().scale(1.2).add(0, 0.5, 0)));
            playerIn.playSound(worldIn.getBlockState(playerIn.getPosition().down(1)).getSoundType().getBreakSound(), 1.0F, 1.2F);
            playerIn.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 0.5F, 1.0F);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity playerIn, Entity entity) {
        attack(playerIn.world, playerIn);
        return true;
    }

    @Override
    public void onLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        attack(playerIn.world, playerIn);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.getCooldownTracker().hasCooldown(this)) {
            player.getCooldownTracker().setCooldown(this, 8);
            player.setActiveHand(context.getHand());

            player.setMotion(player.getMotion().mul(1, 0.1, 1).add(player.getLookVec().mul(1, 0, 1)
                    .mul(0.2, 0, 0.2).add(0, 0.75, 0)));
            player.fallDistance = 0;
            player.playSound(context.getWorld().getBlockState(context.getPos()).getSoundType().getBreakSound(), 1.0F, 1.2F);
            return ActionResultType.SUCCESS;
        }
        return super.onItemUse(context);
    }

    @Override
    public void onBlockLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn, BlockPos pos) {
        attack(playerIn.world, playerIn);
    }

    private void attack(World world, PlayerEntity player) {
        if (player.getCooldownTracker().hasCooldown(this)) return;
        player.getCooldownTracker().setCooldown(this, 20);
        player.setMotion(player.getMotion().mul(0.2, 1, 0.2));
        attackByTriangle(world, player, 10, 4, 1);
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 1.0F, 0.5F);
    }

    //SkillMeleeAttackの処理を改変したもの
    private void attackByTriangle(World world, PlayerEntity player, float damage, double range, double radius) {
        Vec3d attackerPos = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        //攻撃者から敵対象が居る地点へのベクトル。直接レイトレースには使わない
        Vec3d toTargetVec = player.getLookVec();

        float rotate = 90F * ((float) Math.PI / 180F);

        //このベクトル自体は直接使用しない。真横に線を伸ばす
        Vec3d rightVec = toTargetVec.rotateYaw(rotate).scale(radius);
        Vec3d leftVec = toTargetVec.rotateYaw(-rotate).scale(radius);

        //これも使わない。線を上下に分割
        Vec3d rightTopVec = rightVec.add(0, radius, 0);
        Vec3d rightBottomVec = rightVec.subtract(0, radius, 0);
        Vec3d leftTopVec = leftVec.add(0, radius, 0);
        Vec3d leftBottomVec = leftVec.subtract(0, radius, 0);

        //実際にレイトレースに使用する。線の先っぽの地点
        Vec3d rightTopPos = attackerPos.add(rightTopVec);
        Vec3d rightBottomPos = attackerPos.add(rightBottomVec);
        Vec3d leftTopPos = attackerPos.add(leftTopVec);
        Vec3d leftBottomPos = attackerPos.add(leftBottomVec);

        //レイトレースの終点。大体の場合、敵対象の背後の地点になる
        Vec3d toTargetPos = attackerPos.add(toTargetVec.scale(range));

        //壁抜けできないように追加rayTrace
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(attackerPos, toTargetPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
        if (result.getType() != RayTraceResult.Type.MISS) {
            toTargetPos = result.getHitVec();
        }

        //周囲の敵のリスト
        List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, player.getBoundingBox().grow(range), (entity ->
                entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator() && entity != player));

        //攻撃範囲内の敵のリスト
        List<Entity> targetList = Lists.newArrayList();

        //レイトレースx5。要素がめちゃ重複するかも
        rayTraceEntity(rightTopPos, toTargetPos, entityList, targetList);
        rayTraceEntity(rightBottomPos, toTargetPos, entityList, targetList);
        rayTraceEntity(leftTopPos, toTargetPos, entityList, targetList);
        rayTraceEntity(leftBottomPos, toTargetPos, entityList, targetList);
        rayTraceEntity(attackerPos, toTargetPos, entityList, targetList);

        DamageSource source = DamageSource.causePlayerDamage(player).setDamageBypassesArmor();

        if (!targetList.isEmpty()) {
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, player.getSoundCategory(), 0.5F, 1.2F);
            //重複は省いて、範囲内のエンティティにダメージを与える
            targetList.stream().distinct().forEach((entity -> {
                if (entity.attackEntityFrom(source, damage) && entity instanceof LivingEntity) {
                    float pi = (float) Math.PI;
                    ((LivingEntity) entity).knockBack(player, damage / 10, MathHelper.sin(player.rotationYaw * (pi / 180F)), -MathHelper.cos(player.rotationYaw * (pi / 180F)));
                }
            }));
        }

        //パーティクル演出
        spawnParticleLine(world, rightTopPos, toTargetPos);
        spawnParticleLine(world, rightBottomPos, toTargetPos);
        spawnParticleLine(world, leftTopPos, toTargetPos);
        spawnParticleLine(world, leftBottomPos, toTargetPos);
        spawnParticleLine(world, attackerPos, toTargetPos);

    }

    //始点から終点までの座標上の全エンティティを、targetのリストに加える
    //一行でできるならわざわざメソッド化しなくてもよかったかもしれない
    private void rayTraceEntity(Vec3d start, Vec3d end, List<Entity> check, List<Entity> target) {
        check.stream().filter(entity -> entity.getBoundingBox().rayTrace(start, end).isPresent()).forEach(target::add);
    }

    private <T extends IParticleData> void spawnParticleLine(World world, Vec3d start, Vec3d end) {
        for (int i = 0; i < 8; i++) {
            double division = 1F / 8;
            double x = MathHelper.lerp(division * i, start.x, end.x) + (world.rand.nextFloat() * 0.2 * 2 - 0.2);
            double y = MathHelper.lerp(division * i, start.y, end.y) + (world.rand.nextFloat() * 0.2 * 2 - 0.2);
            double z = MathHelper.lerp(division * i, start.z, end.z) + (world.rand.nextFloat() * 0.2 * 2 - 0.2);
            world.addParticle(ParticleTypes.CRIT, x, y, z, 0, 0, 0);
        }
    }
}
