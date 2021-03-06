package com.sistr.scarlethill.item;

import com.sistr.scarlethill.entity.goal.SkillMeleeAttackGoal;
import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
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
        player.getCooldownTracker().setCooldown(this, 30);
        player.setMotion(player.getMotion().mul(0.2, 1, 0.2));
        if (!world.isRemote) {
            SkillMeleeAttackGoal.attackByTriangle(world, player,
                    player.getPositionVec().add(0, player.getEyeHeight(), 0).add(player.getLookVec()),
                    12, 6, 2);
        }
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 1.0F, 0.5F);
    }

}
