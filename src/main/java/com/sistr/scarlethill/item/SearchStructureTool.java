package com.sistr.scarlethill.item;

import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class SearchStructureTool extends Item {

    public SearchStructureTool() {
        super(new Item.Properties()
                .group(ModSetup.ITEM_GROUP)
                .maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            private double rotation;
            @OnlyIn(Dist.CLIENT)
            private double rota;
            @OnlyIn(Dist.CLIENT)
            private long lastUpdateTick;

            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity user) {
                if (user == null && !stack.isOnItemFrame()) {
                    return 0.0F;
                } else {
                    boolean flag = user != null;
                    Entity entity = flag ? user : stack.getItemFrame();
                    if (world == null) {
                        world = entity.world;
                    }

                    double actualAngle;
                    if (world.dimension.isSurfaceWorld()) {
                        double rotation = flag ? (double) entity.rotationYaw : this.getFrameRotation((ItemFrameEntity) entity);
                        rotation = MathHelper.positiveModulo(rotation / 360.0D, 1.0D);
                        double angle = this.getSpawnToAngle(world, entity) / (double) ((float) Math.PI * 2F);
                        actualAngle = 0.5D - (rotation - 0.25D - angle);
                    } else {
                        actualAngle = Math.random();
                    }

                    if (flag) {
                        actualAngle = this.wobble(world, actualAngle);
                    }

                    return MathHelper.positiveModulo((float) actualAngle, 1.0F);
                }
            }

            @OnlyIn(Dist.CLIENT)
            private double wobble(World worldIn, double p_185093_2_) {
                if (worldIn.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getGameTime();
                    double d0 = p_185093_2_ - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }

            @OnlyIn(Dist.CLIENT)
            private double getFrameRotation(ItemFrameEntity p_185094_1_) {
                return MathHelper.wrapDegrees(180 + p_185094_1_.getHorizontalFacing().getHorizontalIndex() * 90);
            }

            @OnlyIn(Dist.CLIENT)
            private double getSpawnToAngle(IWorld world, Entity entity) {
                BlockPos blockpos = world.getSpawnPoint();
                return Math.atan2((double) blockpos.getZ() - entity.getPosZ(), (double) blockpos.getX() - entity.getPosX());
            }
        });
    }
}
