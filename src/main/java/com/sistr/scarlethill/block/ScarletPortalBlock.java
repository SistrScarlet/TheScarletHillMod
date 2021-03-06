package com.sistr.scarlethill.block;

import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.dimension.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class ScarletPortalBlock extends Block {

    public ScarletPortalBlock() {
        super(Block.Properties.create(Material.PORTAL, MaterialColor.RED)
                .tickRandomly().hardnessAndResistance(5.0F).sound(SoundType.METAL).lightValue(11));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        ItemStack item = player.getHeldItem(hand);
        if (!item.isEmpty() && item.getItem() == Registration.SCARLET_KEY_ITEM.get()) {

            boolean isToHill = player.dimension != ModDimensions.SCARLETHILL_TYPE;

            if (!world.isRemote) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                DimensionType transferDimension;
                BlockPos transferPos;

                if (isToHill) {//紅の丘へ行くときは現在地の上空へTP
                    transferDimension = ModDimensions.SCARLETHILL_TYPE;
                    transferPos = new BlockPos(serverPlayer.getPosX(), world.getActualHeight() + 10, serverPlayer.getPosZ());

                } else {//地上へ帰るときはスポーンポイントへTP
                    transferDimension = DimensionType.OVERWORLD;
                    transferPos = serverPlayer.getBedLocation(transferDimension);
                    if (!serverPlayer.server.getWorld(transferDimension).getBlockState(transferPos).isIn(BlockTags.BEDS)) {
                        transferPos = serverPlayer.getServerWorld().getSpawnPoint();
                    }
                }

                serverPlayer.teleport(serverPlayer.server.getWorld(transferDimension), transferPos.getX(), transferPos.getY(), transferPos.getZ(), serverPlayer.rotationYaw, serverPlayer.rotationPitch);

            }

            if (isToHill) {//紅の丘へ行くときはポーション付与
                player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 600));
            }

            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, trace);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 1.3F, false);
        }

        for (int i = 0; i < 4; ++i) {
            double x = pos.getX() + rand.nextFloat();
            double y = pos.getY() + rand.nextFloat();
            double z = pos.getZ() + rand.nextFloat();
            double xSpeed = (rand.nextFloat() - 0.5D) * 0.5D;
            double ySpeed = (rand.nextFloat() - 0.5D) * 0.5D;
            double zSpeed = (rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (rand.nextInt(2) == 0) {
                x = pos.getX() + 0.5D + 0.25D * j;
                xSpeed = (rand.nextFloat() * 2.0F * j);
            } else {
                z = pos.getZ() + 0.5D + 0.25D * j;
                zSpeed = (rand.nextFloat() * 2.0F * j);
            }

            worldIn.addParticle(Registration.SCARLET_PORTAL_PARTICLE.get(), x, y, z, xSpeed, ySpeed, zSpeed);
        }

    }
}
