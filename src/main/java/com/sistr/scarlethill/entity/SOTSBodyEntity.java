package com.sistr.scarlethill.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sistr.scarlethill.entity.goal.NearestHurtByTargetGoal;
import com.sistr.scarlethill.entity.goal.SkillAttackGoal;
import com.sistr.scarlethill.entity.goal.SkillGoal;
import com.sistr.scarlethill.entity.goal.SkillProjectileAttackGoal;
import com.sistr.scarlethill.entity.projectile.MagmaProjectileEntity;
import com.sistr.scarlethill.entity.projectile.RockProjectileEntity;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.util.EffectUtil;
import com.sistr.scarlethill.util.HorizonPathFinder;
import com.sistr.scarlethill.util.VecMathUtil;
import com.sistr.scarlethill.util.Vec2i;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

//旧名MoltenGodEntity / フルネームでSpawnOfTheScarletBodyEntity
public class SOTSBodyEntity extends CreatureEntity implements IMob {
    private static final DataParameter<Boolean> FLOATING = EntityDataManager.createKey(SOTSBodyEntity.class, DataSerializers.BOOLEAN);
    private static final int maxHands = 2;
    private static final int handOffset = 4;
    private static final int defaultFloatHeight = 14;
    private List<SOTSFistEntity> hands = Lists.newArrayList();
    public int floatHeight = defaultFloatHeight;
    @Nullable
    private BlockPos prevLavaPos;
    private BlockPos prevBlockPos;
    public int clientBodyRenderAlpha = 0;
    public int prevClientBodyRenderAlpha = 0;
    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
    public boolean isLowHealth = false;

    public SOTSBodyEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        this.moveController = new MoveHelperController(this);
        this.stepHeight = 2F;
        this.experienceValue = 150;
    }

    public SOTSBodyEntity(World world) {
        super(Registration.SOTS_BODY_BOSS.get(), world);
        this.moveController = new MoveHelperController(this);
        this.stepHeight = 2F;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestHurtByTargetGoal(this, SOTSFistEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));

        this.goalSelector.addGoal(1, new boundGround(5));

        this.goalSelector.addGoal(3, new SkillRePopHandGoal(100, 0, 40, 60));
        this.goalSelector.addGoal(3, new SkillHammerPress(100, 60, 60, 120, 12, 2.5F, 0, 16));
        this.goalSelector.addGoal(3, new SkillRockThrow(20, 0, 40, 240, 10, 3.5F, 12, 32));
        this.goalSelector.addGoal(3, new LowHealthRockThrow(20, 0, 20, 120, 10, 3.5F, 12, 32));
        this.goalSelector.addGoal(3, new SkillSpitMagmaGoal(20, 3, 20, 240, 3, 0, 12, 32));
        this.goalSelector.addGoal(3, new LowHealthSpitMagma(20, 5, 10, 120, 3, 0, 12, 32));
        this.goalSelector.addGoal(3, new SkillLaserGoal(100, 60, 60, 1200, 3, 0, 0, 32));
        this.goalSelector.addGoal(3, new SkillFallAttackGoal(20, 200, 40, 240, 10, 10, 0, 12));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2000.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(12.0D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLOATING, false);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!this.isLowHealth && this.getHealth() < this.getMaxHealth() / 2) {
            this.isLowHealth = true;
            this.addPotionEffect(new EffectInstance(Registration.SCARLET_BLESSING_EFFECT.get(), 200));
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX(), this.getPosY() + this.getEyeHeight(), this.getPosZ(), 0, 0, 0);
            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 3F, 0.5F);
            if (this.world.isRemote) {
                float radius = 8;
                float count = radius * 8;
                for (int k = 0; k < count; k++) {
                    for (int i = 0; i < count; i++) {
                        Vec3d pos = VecMathUtil.getVector(new Vec2f((float) k / count * 360, ((float) i / count * 2 - 1) * 180))
                                .scale(MathHelper.sqrt(this.getRNG().nextFloat()) * (radius - 1) + 1)
                                .add(this.getPosX(), this.getPosY() + this.getEyeHeight(), this.getPosZ());
                        this.world.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
                    }
                }
            }
        }
        boolean floating = this.isFloating();
        if (!floating) {
            damage *= 4;
        }

        if (super.attackEntityFrom(source, damage)) {
            Entity attacker = source.getTrueSource();
            if (floating && attacker != null) {
                //体力の30の区切りを超えるか、ダメージが40以上であればノックバック
                int partition = 30;
                if ((this.getHealth() + damage) % partition < this.getHealth() % partition || partition <= damage) {
                    this.setMotion(this.getPositionVec()
                            .subtract(attacker.getPositionVec())
                            .mul(1, 0, 1)
                            .normalize().scale(4));
                } else if (this.rand.nextInt(40) <= damage && 16 * 16 < this.getDistanceSq(attacker.getPositionVec())) {
                    this.getMoveHelper().setMoveTo(attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), 1.5);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        for (int i = 2; i > 0; i--) {
            Vec3d dropPos = this.getPositionVec();
            Entity entity = source.getTrueSource();
            if (entity != null) {
                dropPos = entity.getPositionVec();
            }
            ItemEntity itementity = this.entityDropItem(Registration.SCARLET_GEM_ITEM.get());
            if (itementity != null) {
                itementity.setNoDespawn();
                itementity.setPosition(dropPos.getX(), dropPos.getY(), dropPos.getZ());
            }
        }

    }

    public void setFloating(boolean flag) {
        this.dataManager.set(FLOATING, flag);
    }

    public boolean isFloating() {
        return this.dataManager.get(FLOATING);
    }

    @Override
    public void tick() {

        this.ignoreFrustumCheck = true;

        super.tick();
    }

    @Override
    public void livingTick() {
        //死んだ拳を省く
        this.hands = this.hands.stream().filter(LivingEntity::isAlive).collect(Collectors.toList());

        this.floating();

        if (isFloating()) {
            //位置を指定
            for (SOTSFistEntity hand : this.hands) {
                if (!hand.isWorking) {
                    Vec3d handPos = getDefaultHandPos(hand.getHand());
                    hand.getMoveHelper().setMoveTo(handPos.x, handPos.y, handPos.z, 1);
                }
            }
        }

        this.prevBlockPos = this.getPosition();
        this.prevClientBodyRenderAlpha = this.clientBodyRenderAlpha;

        LivingEntity target = this.getAttackTarget();
        if (target != null) {
            this.rotationYawHead = -VecMathUtil.getYawPitch(target.getPositionVec().subtract(this.getPositionVec())).x;
        }

        if ((this.ticksExisted & 3) == 0 && this.getHealth() <= 0) {
            playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
        }

        super.livingTick();

    }

    //浮遊処理。長いので分離
    private void floating() {
        if (this.floatHeight < 0) {
            Vec3d motion = this.getMotion();
            motion = motion.add(0, (this.floatHeight - defaultFloatHeight) * 0.05, 0);
            this.setMotion(motion);
            return;
        }

        int nearLavaDis = -1;
        if (this.isInLava()) {
            nearLavaDis = 0;
        } else {
            Optional<Integer> optional = this.getLavaDis();
            if (optional.isPresent()) {
                nearLavaDis = optional.get();
            }
        }

        if (nearLavaDis != -1) {
            this.setFloating(true);
            this.clientBodyRenderAlpha = Math.min(this.clientBodyRenderAlpha + 1, 20);
            //浮遊
            Vec3d motion = this.getMotion();
            motion = motion.add(0, (this.floatHeight - nearLavaDis) * 0.05, 0);
            this.setMotion(motion);
        } else {
            this.setFloating(false);
            this.clientBodyRenderAlpha = Math.max(this.clientBodyRenderAlpha - 1, 0);
        }
    }

    private Optional<Integer> getLavaDis() {
        int nearLavaDis = -1;
        //前回位置から動いていない場合は前回位置を参照
        if (this.prevLavaPos != null && this.getPosition().equals(this.prevBlockPos) && this.isFloating()) {
            nearLavaDis = MathHelper.floor(this.getPosY()) - this.prevLavaPos.getY();
        } else {
            for (int k = 0; k < 4; k++) {
                BlockPos pos = this.getPosition().offset(Direction.byHorizontalIndex(k));
                for (int i = 0; i < this.floatHeight * 2; i++) {
                    //実は真下の判定はしてない
                    BlockPos checkPos = pos.down(i);
                    if (this.world.getFluidState(checkPos).getFluid() instanceof LavaFluid) {
                        int lavaDis = pos.getY() - checkPos.getY();
                        if (nearLavaDis == -1 || lavaDis < nearLavaDis) {
                            nearLavaDis = lavaDis;
                            this.prevLavaPos = checkPos;
                        }
                        if (!this.world.isRemote) {
                            //自身の地点から溶岩までのブロックを破壊
                            for (int j = 0; j < lavaDis; j++) {
                                BlockPos breakPos = pos.down(j);
                                if (this.world.isAirBlock(breakPos)) continue;
                                IFluidState fluidState = this.world.getFluidState(breakPos);
                                if (fluidState.isEmpty()) {
                                    BlockState state = this.world.getBlockState(breakPos).getBlockState();
                                    if (state.getBlock() == Blocks.OBSIDIAN) {
                                        this.world.setBlockState(breakPos, Fluids.LAVA.getDefaultState().getBlockState());
                                    } else {
                                        this.world.setBlockState(breakPos, Fluids.FLOWING_LAVA.getDefaultState().getBlockState());
                                    }
                                } else {
                                    if (fluidState.getFluid() instanceof LavaFluid) break;
                                    if (fluidState.isSource()) {
                                        this.world.setBlockState(breakPos, Fluids.FLOWING_LAVA.getDefaultState().getBlockState());
                                        continue;
                                    }
                                    BlockPos fluidPos = breakPos;
                                    //水流に触れた場合、水源絶対殺すマン(100回まで)
                                    removeSource:
                                    for (int n = 0; n < 100; n++) {
                                        for (int l = 1; true; l++) {
                                            //下を除く5方により大きい水流または水源が無い場合は諦める
                                            if (6 <= l) {
                                                break removeSource;
                                            }
                                            BlockPos tempWaterPos = fluidPos.offset(Direction.byIndex(l));
                                            IFluidState checkFluid = this.world.getFluidState(tempWaterPos);
                                            if (checkFluid.isEmpty()) continue;
                                            this.world.setBlockState(tempWaterPos, Fluids.FLOWING_LAVA.getDefaultState().getBlockState());
                                            //水源が見つかった場合は終了
                                            if (checkFluid.isSource()) {
                                                break removeSource;
                                            }
                                            //大きな水流を次のループで探索起点とする
                                            if (this.world.getFluidState(fluidPos).getLevel() < checkFluid.getLevel()) {
                                                fluidPos = tempWaterPos;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return nearLavaDis == -1 ? Optional.empty() : Optional.of(nearLavaDis);
    }

    private Optional<Set<Vec2i>> getLavaMap() {
        BlockPos originPos = BlockPos.ZERO;
        getOrigin:
        for (int k = 0; k < 4; k++) {
            BlockPos pos = this.getPosition().offset(Direction.byHorizontalIndex(k));
            for (int i = 0; i < this.floatHeight * 2; i++) {
                //実は真下の判定はしてない
                BlockPos checkPos = pos.down(i);
                if (isFloatableBlock(this.world, checkPos)) {
                    originPos = checkPos;
                    break getOrigin;
                }
            }
        }

        if (originPos == BlockPos.ZERO) return Optional.empty();


        List<BlockPos> tempLavaListPrev = Lists.newArrayList();
        //最初の六つ
        for (int i = 0; i < 6; i++) {
            BlockPos checkPos = originPos.offset(Direction.byIndex(i));
            if (isFloatableBlock(this.world, checkPos)) {
                tempLavaListPrev.add(checkPos);
            }
        }

        Set<Vec2i> mapList = Sets.newHashSet(new Vec2i(originPos));
        tempLavaListPrev.forEach(pos -> mapList.add(new Vec2i(pos)));

        int size = 5;
        int maxSize = 4;

        //ブロックを取得するが、比較的軽量な代わりに正確ではない
        for (int k = 1; k < 64; k++) {
            maxSize += 4;
            size += maxSize;
            HashSet<BlockPos> tempLavaList = Sets.newHashSetWithExpectedSize(size);
            for (BlockPos tempPos : tempLavaListPrev) {
                int x = tempPos.getX();
                int y = tempPos.getY();
                int z = tempPos.getZ();
                if (originPos.getY() <= y) {
                    BlockPos checkPos = tempPos.offset(Direction.UP);
                    if (isFloatableBlock(this.world, checkPos)) {
                        tempLavaList.add(checkPos);
                    }
                }
                if (y <= originPos.getY()) {
                    BlockPos checkPos = tempPos.offset(Direction.DOWN);
                    if (isFloatableBlock(this.world, checkPos)) {
                        tempLavaList.add(checkPos);
                    }
                }
                if (originPos.getZ() <= z) {
                    BlockPos checkPos = tempPos.offset(Direction.SOUTH);
                    if (isFloatableBlock(this.world, checkPos)) {
                        tempLavaList.add(checkPos);
                    }
                }
                if (z <= originPos.getZ()) {
                    BlockPos checkPos = tempPos.offset(Direction.NORTH);
                    if (isFloatableBlock(this.world, checkPos)) {
                        tempLavaList.add(checkPos);
                    }
                }
                if (originPos.getX() <= x) {
                    BlockPos checkPos = tempPos.offset(Direction.EAST);
                    if (isFloatableBlock(this.world, checkPos)) {
                        tempLavaList.add(checkPos);
                    }
                }
                if (x <= originPos.getX()) {
                    BlockPos checkPos = tempPos.offset(Direction.WEST);
                    if (isFloatableBlock(this.world, checkPos)) {
                        tempLavaList.add(checkPos);
                    }
                }
            }
            tempLavaList.forEach(pos -> mapList.add(new Vec2i(pos)));
            tempLavaListPrev.clear();
            tempLavaListPrev.addAll(tempLavaList);
        }

         /*負荷が高いが完全
        List<BlockPos> mapList = Lists.newArrayList(originPos);
        HashSet<BlockPos> tempLavaListPrev = Sets.newHashSet(mapList);

        for (int k = 0; k < 64; k++) {
            HashSet<BlockPos> tempLavaList = Sets.newHashSetWithExpectedSize(k * k * k + 1);
            //前のループで取得した溶岩に隣接する溶岩を、重複を省いてtempに入れる
            for (BlockPos listPos : tempLavaListPrev) {
                for (int i = 0; i < 6; i++) {
                    BlockPos checkPos = listPos.offset(Direction.byIndex(i));
                    if (!tempLavaList.contains(checkPos) && !tempLavaListPrev.contains(checkPos) && this.world.getFluidState(checkPos).getFluid() instanceof LavaFluid) {
                        tempLavaList.add(checkPos);
                    }
                }
            }
            mapList.addAll(tempLavaList);
            tempLavaListPrev = tempLavaList;
        }
          */

        return mapList.isEmpty() ? Optional.empty() : Optional.of(mapList);
    }

    public static boolean isFloatableBlock(World world, BlockPos pos) {
        return world.getFluidState(pos).getFluid() instanceof LavaFluid || world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    public void createHand() {
        if (this.world.isRemote) return;
        if (this.hands.size() < 2) {
            int rightCount = (int) this.hands.stream().filter(hand -> hand.getHand() == SOTSFistEntity.FistSide.RIGHT).count();
            int leftCount = this.hands.size() - rightCount;
            SOTSFistEntity.FistSide fistSide;
            if (rightCount < leftCount) {
                fistSide = SOTSFistEntity.FistSide.RIGHT;
            } else {
                fistSide = SOTSFistEntity.FistSide.LEFT;
            }
            createHand(fistSide);
        }
    }

    private void createHand(SOTSFistEntity.FistSide fistSide) {
        if (this.world.isRemote) return;
        SOTSFistEntity handEntity = new SOTSFistEntity(this.world, fistSide, this);
        handEntity.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
        Vec3d handPos = getDefaultHandPos(fistSide);
        handEntity.getMoveHelper().setMoveTo(handPos.x, handPos.y, handPos.z, 1);
        this.world.addEntity(handEntity);
        this.hands.add(handEntity);
        //クライアント側ではhandsはHandEntity側から追加される
    }

    public Vec3d getDefaultHandPos(SOTSFistEntity.FistSide fistSide) {
        return calcHandPos(this.rotationYawHead, 75, 4, fistSide)
                .add(this.getPosX(), this.getPosY() - handOffset, this.getPosZ());
    }

    public static Vec3d calcHandPos(float baseYaw, float yaw, float length, SOTSFistEntity.FistSide fistSide) {
        //左右に回転
        if (fistSide == SOTSFistEntity.FistSide.RIGHT) {
            baseYaw += yaw;
        } else {
            baseYaw -= yaw;
        }
        //線をbaseYawで回転させる
        Vec3d line = new Vec3d(0, 0, length);
        return line.rotateYaw((float) Math.toRadians(-baseYaw));
    }

    public List<SOTSFistEntity> getHands() {
        return this.hands;
    }

    //落下ダメージ消す
    @Override
    public boolean onLivingFall(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5F;
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed() {
        return false;
    }

    protected void collideWithEntity(Entity entityIn) {
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        compound.putBoolean("IsLowHealth", this.isLowHealth);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.isLowHealth = compound.getBoolean("IsLowHealth");
    }

    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    /**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in order
     * to view its associated boss bar.
     */
    public void addTrackingPlayer(ServerPlayerEntity player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    /**
     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
     * more information on tracking.
     */
    public void removeTrackingPlayer(ServerPlayerEntity player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    class SkillRePopHandGoal extends SkillGoal {
        private final int chance;

        public SkillRePopHandGoal(int startupLength, int actionLength, int freezeLength, int chance) {
            super(SOTSBodyEntity.this, startupLength, actionLength, freezeLength);
            this.chance = chance;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        protected boolean shouldStart() {
            return 40 < SOTSBodyEntity.this.ticksExisted
                    && (SOTSBodyEntity.this.ticksExisted < 100 || SOTSBodyEntity.this.rand.nextInt(this.chance) == 0)
                    && SOTSBodyEntity.this.getHands().size() <= 0
                    && SOTSBodyEntity.this.isFloating();
        }

        @Override
        protected void readyStart() {
            SOTSBodyEntity.this.floatHeight = 8;
        }

        @Override
        protected void readyTick() {
            int factor = MathHelper.clamp(this.timer, 4, 40) >> 2;
            Vec3d startPos = SOTSBodyEntity.this.getPositionVec().add(0, SOTSBodyEntity.this.getEyeHeight(), 0);
            for (int i = 0; i < factor; i++) {
                Random random = SOTSBodyEntity.this.world.rand;
                Vec3d endPos = startPos.add((random.nextDouble() - 0.5) * factor, (random.nextDouble() - 0.5) * factor, (random.nextDouble() - 0.5) * factor);
                EffectUtil.spawnParticleLine((ServerWorld) SOTSBodyEntity.this.world, ParticleTypes.FLAME, startPos, endPos, factor, 0);
            }
        }

        @Override
        protected void actionStart() {
            super.actionStart();
            for (int i = 0; i < maxHands; i++) {
                SOTSBodyEntity.this.createHand();
            }
        }

        @Override
        protected void freezeStart() {
            super.freezeStart();
            SOTSBodyEntity.this.floatHeight = SOTSBodyEntity.defaultFloatHeight;
        }

        @Override
        public void resetTask() {
            SOTSBodyEntity.this.floatHeight = SOTSBodyEntity.defaultFloatHeight;
        }
    }

    //こいつだけ発狂時の処理が備わってる
    class SkillHammerPress extends SkillAttackGoal {
        @Nullable
        private SOTSFistEntity hand;
        private Vec3d aimPos;
        private Vec3d aimMotion;

        public SkillHammerPress(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(SOTSBodyEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
        }

        @Override
        protected boolean shouldStart() {
            LivingEntity target = SOTSBodyEntity.this.getAttackTarget();
            if (SOTSBodyEntity.this.rand.nextInt(SOTSBodyEntity.this.isLowHealth ? this.chance / 2 : this.chance) == 0
                    && checkTargetDistance()
                    && target != null
                    && SOTSBodyEntity.this.isFloating()) {
                List<SOTSFistEntity> hands = SOTSBodyEntity.this.getHands();
                if (SOTSBodyEntity.this.getHands().size() > 0) {
                    this.hand = hands.get(SOTSBodyEntity.this.getRNG().nextInt(hands.size()));
                    if (this.hand.isWorking) {
                        return false;
                    }
                    this.hand.isWorking = true;
                    this.aimPos = target.getPositionVec();
                    this.aimMotion = Vec3d.ZERO;
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void readyStart() {
            if (this.hand == null) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            this.hand.world.playMovingSound(null, this.hand, Registration.WHOOSH_MEDIUM.get(), this.hand.getSoundCategory(), 2, 2);
        }

        @Override
        protected void readyTick() {
            LivingEntity target = SOTSBodyEntity.this.getAttackTarget();
            if (target == null) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            if (this.hand == null) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            this.hand.getMoveHelper().setMoveTo(this.aimPos.x, target.getPosY() + target.getEyeHeight() + target.getHeight() * 2 + this.timer / 20F, this.aimPos.z, 1.0F);
            this.aimPos = this.aimPos.add(this.aimMotion);
            Vec3d toTarget = target.getPositionVec().add(target.getMotion()).subtract(this.aimPos);
            toTarget = toTarget.add(0, -toTarget.getY(), 0).normalize().scale(0.1);
            this.aimMotion = this.aimMotion.add(toTarget);

            if (this.timer <= this.readyLength - 20 && this.timer % 20 == 0) {
                this.hand.world.playMovingSound(null, this.hand, Registration.WHOOSH_MEDIUM.get(), this.hand.getSoundCategory(), 1, 2);
            }
        }

        @Override
        protected void actionStart() {
            LivingEntity target = SOTSBodyEntity.this.getAttackTarget();
            if (target == null) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            if (this.hand == null) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            this.hand.getMoveHelper().setMoveTo(target.getPosX(), target.getPosY() - 4, target.getPosZ(), 1);
        }

        @Override
        protected void actionTick() {
            if (this.hand == null) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            if (this.hand.onGround) {
                this.hand.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 4.0F, (1.0F + (this.hand.world.rand.nextFloat() - this.hand.world.rand.nextFloat()) * 0.2F) * 0.5F);
                AxisAlignedBB bb = new AxisAlignedBB(this.hand.getPosX() - this.range, this.hand.getPosY() - this.range, this.hand.getPosZ() - this.range,
                        this.hand.getPosX() + this.range, this.hand.getPosY() + this.range, this.hand.getPosZ() + this.range);
                List<Entity> aroundEntity = this.hand.world.getEntitiesInAABBexcluding(this.hand, bb, (entity ->
                        entity.isAlive() && entity.canBeCollidedWith() && !entity.isSpectator()
                                && entity.getDistanceSq(this.hand) < this.range * this.range));
                aroundEntity.forEach(entity -> entity.attackEntityFrom(DamageSource.causeMobDamage(SOTSBodyEntity.this), this.damage - (float) this.hand.getDistanceSq(entity)));
                this.setStatus(SkillStatus.FREEZE);
            }
        }

        @Override
        public void resetTask() {
            if (this.hand != null) {
                this.hand.isWorking = false;
            }
        }
    }

    class SkillRockThrow extends SkillProjectileAttackGoal<RockProjectileEntity> {
        @Nullable
        private SOTSFistEntity hand;
        private int count = 0;

        public SkillRockThrow(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(SOTSBodyEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
        }

        @Override
        protected boolean shouldStart() {
            if (SOTSBodyEntity.this.isFloating()) {
                List<SOTSFistEntity> hands = SOTSBodyEntity.this.getHands();
                if (0 < hands.size()) {
                    if (super.shouldStart()) {
                        this.hand = hands.get(SOTSBodyEntity.this.getRNG().nextInt(hands.size()));
                        if (this.hand.isWorking) {
                            return false;
                        }
                        this.hand.isWorking = true;
                        this.count++;
                        LivingEntity entity = SOTSBodyEntity.this.getAttackTarget();
                        if (3 < this.count && entity != null) {
                            this.count = 0;
                            SOTSBodyEntity.this.getMoveHelper().setMoveTo(entity.getPosX(), entity.getPosY(), entity.getPosZ(), 1);
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected void readyStart() {
            if (this.hand != null) {
                this.hand.playSound(Registration.WHOOSH_MEDIUM.get(), 2, 1);
            }
        }

        @Override
        protected void readyTick() {
            if (this.hand != null) {
                Vec3d handPos = getDefaultHandPos(this.hand.getHand()).add(0, handOffset, 0);
                this.hand.getMoveHelper().setMoveTo(handPos.x, handPos.y, handPos.z, 1);
            }
        }

        @Override
        public void resetTask() {
            if (this.hand != null) {
                this.hand.isWorking = false;
            }
        }

        @Override
        protected RockProjectileEntity createProjectile() {
            RockProjectileEntity projectile = new RockProjectileEntity(SOTSBodyEntity.this, SOTSBodyEntity.this.world);
            projectile.setExplosionRadius(this.range);
            if (this.hand != null) {
                projectile.setShooter(this.hand);
            }
            return projectile;
        }

        @Override
        public Vec3d getShootPos() {
            if (this.hand == null) {
                return SOTSBodyEntity.this.getEyePosition(1.0F);
            }
            return this.hand.getEyePosition(1.0F);
        }
    }

    class LowHealthRockThrow extends SkillRockThrow {

        public LowHealthRockThrow(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
        }

        @Override
        protected boolean shouldStart() {
            return SOTSBodyEntity.this.isLowHealth && super.shouldStart();
        }
    }

    class SkillSpitMagmaGoal extends SkillProjectileAttackGoal<MagmaProjectileEntity> {
        private Vec3d targetPos;

        public SkillSpitMagmaGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(SOTSBodyEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        protected boolean shouldStart() {
            if (SOTSBodyEntity.this.isFloating()) {
                return super.shouldStart();
            }
            return false;
        }

        @Override
        protected void readyStart() {
            super.readyStart();
            SOTSBodyEntity.this.playSound(Registration.SKILL_PLOPS.get(), 2F, 0.5F);
        }

        @Override
        protected void readyTick() {
            Vec3d shootPos = this.getShootPos();
            EffectUtil.spawnParticleSphereOutline((ServerWorld) SOTSBodyEntity.this.world, ParticleTypes.FLAME, shootPos.x, shootPos.y, shootPos.z, 2, 2.5);
        }

        @Override
        protected void actionStart() {
            Optional<Vec3d> optionalTargetPos = this.getTargetPos();
            if (!optionalTargetPos.isPresent()) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            this.targetPos = optionalTargetPos.get();
            SOTSBodyEntity.this.playSound(SoundEvents.BLOCK_LAVA_POP, 2, 0.5F + SOTSBodyEntity.this.rand.nextFloat() * 0.15F);
        }

        @Override
        protected void actionTick() {
            Vec3d shootPos = this.getShootPos();
            Vec3d angle = getAngle(shootPos, this.targetPos);
            shooting(shootPos, angle);
        }

        @Override
        protected MagmaProjectileEntity createProjectile() {
            return new MagmaProjectileEntity(SOTSBodyEntity.this, SOTSBodyEntity.this.world);
        }

        @Override
        public Vec3d getShootPos() {
            return SOTSBodyEntity.this.getEyePosition(1.0F).add(0, -5, 0);
        }
    }

    class LowHealthSpitMagma extends SkillSpitMagmaGoal {

        public LowHealthSpitMagma(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
        }

        @Override
        protected boolean shouldStart() {
            return SOTSBodyEntity.this.isLowHealth && super.shouldStart();
        }
    }

    class SkillLaserGoal extends SkillProjectileAttackGoal<MagmaProjectileEntity> {
        private Vec3d aimPos;
        private Vec3d aimMotion;

        public SkillLaserGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(SOTSBodyEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        protected boolean shouldStart() {
            return SOTSBodyEntity.this.isLowHealth && SOTSBodyEntity.this.isFloating() && super.shouldStart();
        }

        @Override
        protected void readyTick() {
            Vec3d shootPos = this.getShootPos();
            EffectUtil.spawnParticleSphereOutline((ServerWorld) SOTSBodyEntity.this.world, ParticleTypes.FLAME, shootPos.x, shootPos.y, shootPos.z, 5, 5 - this.timer / 20F);
            if (this.timer <= this.readyLength - 20 && this.timer % 20 == 0) {
                SOTSBodyEntity.this.playSound(Registration.SKILL_WARN.get(), 3F, 0.5F + this.timer / 20F * 0.2F);
            }
        }

        @Override
        protected void actionStart() {
            SOTSBodyEntity.this.playSound(Registration.SKILL_DANG.get(), 3, 1);
            Optional<Vec3d> optional = this.getTargetPos();
            if (optional.isPresent()) {
                this.aimPos = optional.get();
                this.aimMotion = Vec3d.ZERO;
            } else {
                this.setStatus(SkillStatus.FREEZE);
            }
        }

        @Override
        protected void actionTick() {
            Vec3d shootPos = this.getShootPos();
            Vec3d targetPos = this.aimPos;
            Vec3d angle = getAngle(shootPos, targetPos);
            for (int i = 0; i < 3; i++) {
                shooting(shootPos, angle);
            }
            Optional<Vec3d> optional = this.getTargetPos();
            if (!optional.isPresent()) {
                this.setStatus(SkillStatus.FREEZE);
                return;
            }
            this.aimPos = this.aimPos.add(this.aimMotion);
            Vec3d toTarget = optional.get().subtract(this.aimPos);
            toTarget = toTarget.normalize().scale(0.1);
            this.aimMotion = this.aimMotion.add(toTarget);
            SOTSBodyEntity.this.playSound(SoundEvents.BLOCK_LAVA_POP, 2, 0.5F + SOTSBodyEntity.this.rand.nextFloat() * 0.15F);
        }

        @Override
        protected float addVelocity() {
            return 0.5F;
        }

        @Override
        protected MagmaProjectileEntity createProjectile() {
            return new MagmaProjectileEntity(SOTSBodyEntity.this, SOTSBodyEntity.this.world);
        }

        @Override
        public Vec3d getShootPos() {
            return SOTSBodyEntity.this.getEyePosition(1.0F).add(0, -5, 0);
        }

        @Override
        public Optional<Vec3d> getTargetPos() {
            LivingEntity target = this.goalOwner.getAttackTarget();
            return target == null ? Optional.empty() : Optional.of(new Vec3d(target.getPosX(), target.getPosYEye(), target.getPosZ()).add(target.getMotion()));
        }
    }

    class SkillFallAttackGoal extends SkillAttackGoal {

        public SkillFallAttackGoal(int startupLength, int actionLength, int freezeLength, int chance, float damage, float range, float minDistance, float maxDistance) {
            super(SOTSBodyEntity.this, startupLength, actionLength, freezeLength, chance, damage, range, minDistance, maxDistance);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        protected boolean shouldStart() {
            return SOTSBodyEntity.this.getRNG().nextInt(this.chance) == 0
                    && checkTargetDistance()
                    && SOTSBodyEntity.this.isFloating();
        }

        @Override
        protected void readyStart() {
            SOTSBodyEntity.this.floatHeight = defaultFloatHeight * 2;
        }

        @Override
        protected void actionStart() {
            SOTSBodyEntity.this.floatHeight = -1;
            SOTSBodyEntity.this.playSound(Registration.WHOOSH_MEDIUM.get(), 1, 2);
        }

        @Override
        protected void actionTick() {
            SOTSBodyEntity molten = SOTSBodyEntity.this;
            if (molten.onGround || molten.isInLava()) {
                float radius = this.range;
                AxisAlignedBB bb = new AxisAlignedBB(molten.getPosX() + radius, molten.getPosY() + radius, molten.getPosZ() + radius,
                        molten.getPosX() - radius, molten.getPosY() - radius, molten.getPosZ() - radius);
                List<Entity> aroundEntity = molten.world.getEntitiesInAABBexcluding(molten, bb, (entity) ->
                        !entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith()
                                && !(entity instanceof SOTSFistEntity) && entity.getDistanceSq(molten) < radius * radius);
                aroundEntity.forEach(entity -> {
                    float distance = molten.getDistance(entity);
                    entity.attackEntityFrom(DamageSource.causeMobDamage(molten), this.damage - distance);
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).knockBack(molten, 3 * (1 - distance / radius), molten.getPosX() - entity.getPosX(), molten.getPosZ() - entity.getPosZ());
                    }
                });
                EffectUtil.spawnParticleSphere((ServerWorld) molten.world, ParticleTypes.FLAME, molten.getPosX(), molten.getPosY(), molten.getPosZ(), 256, radius);
                molten.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.2F / (molten.rand.nextFloat() * 0.2F + 0.9F));
                this.setStatus(SkillStatus.FREEZE);
            }
        }

        @Override
        public void resetTask() {
            SOTSBodyEntity.this.floatHeight = defaultFloatHeight;
        }
    }

    class boundGround extends Goal {
        private final int chance;
        private int timer = 0;

        boundGround(int chance) {
            this.chance = chance;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            return SOTSBodyEntity.this.getRNG().nextInt(this.chance) == 0 && !SOTSBodyEntity.this.isFloating();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !SOTSBodyEntity.this.isFloating();
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
        }

        @Override
        public void tick() {
            if (SOTSBodyEntity.this.onGround) {
                this.timer++;
                BlockPos prevLavaPos = SOTSBodyEntity.this.prevLavaPos;
                /*
                //スタックした場合の処理
                if (20 < this.timer && SOTSBodyEntity.this.getMotion().lengthSquared() < 0.0001) {
                    SOTSBodyEntity.this.setPosition(prevLavaPos.getX(), prevLavaPos.getY() + SOTSBodyEntity.defaultFloatHeight, prevLavaPos.getZ());
                }
                 */
                Vec3d motion = Vec3d.ZERO;
                if (prevLavaPos != null && isFloatableBlock(SOTSBodyEntity.this.world, prevLavaPos)) {
                    Vec3d toLava = new Vec3d(prevLavaPos.getX(), prevLavaPos.getY(), prevLavaPos.getZ()).subtract(SOTSBodyEntity.this.getPositionVec());
                    double speed = 0.1 * MathHelper.clamp(this.timer / 5, 0, 10);
                    motion = motion.add(toLava.x, 0, toLava.z).normalize().scale(speed);
                } else {
                    motion = motion.add(SOTSBodyEntity.this.rand.nextFloat() * 2 - 1, 0, SOTSBodyEntity.this.rand.nextFloat() * 2 - 1);
                }

                motion = motion.add(0, 0.8, 0);

                if (motion != Vec3d.ZERO) {
                    SOTSBodyEntity.this.setMotion(SOTSBodyEntity.this.getMotion().add(motion));
                }
            }
        }

        @Override
        public void resetTask() {
            this.timer = 0;
            LivingEntity target = SOTSBodyEntity.this.getAttackTarget();
            if (target != null) {
                SOTSBodyEntity.this.getMoveHelper().setMoveTo(target.getPosX(), target.getPosY(), target.getPosZ(), 1);
            }
            for (int i = 0; i < maxHands; i++) {
                SOTSBodyEntity.this.createHand();
            }
        }
    }

    //移動系はここですべて管理している
    //既存のPathNavigatorとかは使わない
    class MoveHelperController extends MovementController {
        private SOTSBodyEntity molten;
        private int timer = 0;
        @Nullable
        private HorizonPathFinder pathFinder;
        @Nullable
        private List<Vec2i> horizonPath;

        public MoveHelperController(SOTSBodyEntity mob) {
            super(mob);
            this.molten = mob;
        }

        public void setMoveTo(double x, double y, double z, double speed) {
            super.setMoveTo(x, y, z, speed);
            this.updatePath();
        }

        //パスの取得。
        private void updatePath() {
            this.timer++;
            if (this.pathFinder == null || 10 < this.timer) {
                this.timer = 0;
                Optional<Set<Vec2i>> lavaMap = this.molten.getLavaMap();
                if (!lavaMap.isPresent()) return;
                this.pathFinder = new HorizonPathFinder(lavaMap.get());
            }
            Optional<List<Vec2i>> path = this.pathFinder.findPath(new Vec2i(this.molten.getPosition()), new Vec2i(this.posX, this.posZ));
            if (path.isPresent()) {
                this.horizonPath = path.get();
            } else {
                this.action = Action.WAIT;
            }
        }

        @Override
        public void tick() {
            if (!SOTSBodyEntity.this.isFloating()) {
                this.action = Action.WAIT;
                return;
            }
            SOTSBodyEntity.this.setMotion(SOTSBodyEntity.this.getMotion().mul(0.75, 0.9, 0.75));
            if (this.action == Action.MOVE_TO) {
                Optional<Vec2i> nextPos = this.getNextPos();
                if (!nextPos.isPresent() || this.molten.onGround) {
                    this.action = MovementController.Action.WAIT;
                    this.molten.setMotion(this.molten.getMotion().mul(0.25, 1, 0.25));
                    return;
                }
                Vec2i toPos = nextPos.get();
                Vec3d toVec = new Vec3d(toPos.getX() - SOTSBodyEntity.this.getPosX(), 0, toPos.getZ() - SOTSBodyEntity.this.getPosZ());
                toVec = toVec.normalize().scale(this.speed * 0.125);
                SOTSBodyEntity.this.setMotion(SOTSBodyEntity.this.getMotion().add(toVec));
            }
        }

        private Optional<Vec2i> getNextPos() {
            if (this.horizonPath == null) return Optional.empty();
            List<Vec2i> pathList = this.horizonPath;
            //pathList.forEach(pos -> EffectUtil.spawnParticleBox((ServerWorld) this.molten.world, RedstoneParticleData.REDSTONE_DUST, pos.getX(), this.molten.getPosY(), pos.getZ(), 1, 0));
            Vec2i nearPath = HorizonPathFinder.getNearPos(new Vec2i(this.molten.getPosition()), pathList);
            //パスから離れている場合はリセット
            int nearPathDistance = VecMathUtil.getManhattan(nearPath, new Vec2i(this.molten.getPosition()));
            if (nearPathDistance > 3) {
                return Optional.empty();
            }
            Iterator<Vec2i> iterator = pathList.iterator();
            while (iterator.hasNext()) {
                Vec2i pathPoint = iterator.next();
                if (pathPoint.equals(nearPath)) {
                    if (iterator.hasNext()) {
                        return Optional.of(iterator.next());
                    }
                }
                iterator.remove();
            }
            return Optional.empty();
        }
    }

}
