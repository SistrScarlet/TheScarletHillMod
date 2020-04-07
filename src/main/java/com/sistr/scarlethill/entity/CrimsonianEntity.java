package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.entity.goal.*;
import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

//todo Goalの単機能化
//todo コーラス
//todo 飼いならしの方法を分かりやすくする
//todo 点呼
//todo 声の加工
//todo 弓を構えさせる
//todo 鉤爪を持たせられるようにする
//todo 溶岩とか炎への対策
public class CrimsonianEntity extends CreatureEntity implements IGroupable<CrimsonianEntity>, ITameable, IChorusable, IPlaySoundController, IRangedAttackMob, IMerchant, INPC {
    private static final DataParameter<Boolean> LEADER = EntityDataManager.createKey(CrimsonianEntity.class, DataSerializers.BOOLEAN);
    private final IGroupable<CrimsonianEntity> groupable = new DefaultGroupable<>(this);
    private final ITameable tameable = new DefaultTameable(this);
    private final IChorusable chorusable = new DefaultChorusable<>(this, Registration.CRIMSONIAN_YEHAR.get());
    private final IPlaySoundController soundController = new DefaultPlaySoundController();
    private final NonMonsterRangedBowAttackGoal<CrimsonianEntity> aiArrowAttack = new NonMonsterRangedBowAttackGoal<>(this, 20, 8.0F, 0.5F, 0.3F);
    private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 0.5D, false) {

        public void resetTask() {
            super.resetTask();
            CrimsonianEntity.this.setAggroed(false);
        }

        public void startExecuting() {
            super.startExecuting();
            CrimsonianEntity.this.setAggroed(true);
        }
    };
    private final IMerchant merchant = new DefaultMerchant() {
        private MerchantOffers offers;

        @Override
        public MerchantOffers getOffers() {
            if (this.offers == null) {
                this.offers = new MerchantOffers();
                if (CrimsonianEntity.this.world instanceof ServerWorld) {
                    ServerWorld serverworld = (ServerWorld) CrimsonianEntity.this.world;
                    BlockPos pos = new BlockPos(CrimsonianEntity.this);
                    getMapOffer(serverworld, pos, Registration.SCARLET_BEAR_NEST_STRUCTURE.get()).ifPresent(offer -> this.offers.add(offer));
                    getMapOffer(serverworld, pos, Registration.MOLTEN_MINE_STRUCTURE.get()).ifPresent(offer -> this.offers.add(offer));
                }
                this.offers.add(new MerchantOffer(Registration.SCARLET_GEM_ITEM.get().getDefaultInstance(), Registration.SCARLET_STONE_ITEM.get().getDefaultInstance(),
                        new ItemStack(Registration.SCARLET_GEM_ITEM.get(), 2), 1000, 0, 0));
            }

            return this.offers;
        }

        //todo ボスドロップアイテム名の偽装
        private Optional<MerchantOffer> getMapOffer(ServerWorld serverworld, BlockPos pos, Structure<?> structure) {
            String structureName = structure.getStructureName();
            BlockPos blockpos = serverworld.findNearestStructure(structureName, new BlockPos(pos), 100, true);
            if (blockpos != null) {
                ItemStack map = FilledMapItem.setupNewMap(serverworld, blockpos.getX(), blockpos.getZ(), (byte) 2, true, true);
                FilledMapItem.func_226642_a_(serverworld, map);
                MapData.addTargetDecoration(map, blockpos, "+", MapDecoration.Type.RED_X);
                map.setDisplayName(new TranslationTextComponent("filled_map." + structureName.toLowerCase(Locale.ROOT)));
                return Optional.of(new MerchantOffer(new ItemStack(Items.PAPER, 9), new ItemStack(Items.COMPASS),
                        map, 1000, 0, 0));
            }
            return Optional.empty();
        }

        @Override
        public void onTrade(MerchantOffer offer) {
            CrimsonianEntity.this.livingSoundTime = -CrimsonianEntity.this.getTalkInterval();
        }

        @Override
        public void verifySellingItem(ItemStack stack) {
            if (!CrimsonianEntity.this.world.isRemote && CrimsonianEntity.this.livingSoundTime > -CrimsonianEntity.this.getTalkInterval() + 20) {
                CrimsonianEntity.this.livingSoundTime = -CrimsonianEntity.this.getTalkInterval();
                CrimsonianEntity.this.playSound(this.getYesNoSound(!stack.isEmpty()), CrimsonianEntity.this.getSoundVolume(), 0.9F + CrimsonianEntity.this.rand.nextFloat() * 0.1F);
            }
        }

        @Override
        public World getWorld() {
            return CrimsonianEntity.this.world;
        }

        @Override
        public SoundEvent getYesSound() {
            return Registration.CRIMSONIAN_NIL.get();
        }

        public SoundEvent getYesNoSound(boolean yes) {
            return yes ? getYesSound() : Registration.CRIMSONIAN_HYF.get();
        }
    };

    public CrimsonianEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        this.setCombatTask();
    }

    public CrimsonianEntity(World world) {
        super(Registration.CRIMSONIAN_MOB.get(), world);
        this.setCombatTask();
    }

    @Override
    protected void registerGoals() {
        //priorityの変更時は、setCombatTask()の方のgoalも変えること
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new TameableWaitGoal<>(this));
        this.goalSelector.addGoal(2, new LeaveTargetPlayerGoal(this, 0.7F, 4, 8));
        this.goalSelector.addGoal(2, new LeaveTargetGoal(this, 0.7F, 6, 12));
        this.goalSelector.addGoal(3, new ChorusLeaveGoal<>(this, 0.7F, 8, 12, 40));
        this.goalSelector.addGoal(4, new HealMyselfGoal(this, 0.3F, 60, 16));
        this.goalSelector.addGoal(6, new FollowLeaderOrOwnerGoal<>(this, 0.5D, 5.0F, 3.0F, false));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new NearestHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new TameableOwnerHurtByTargetGoal<>(this, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 20, true, false, (entity) -> entity instanceof IMob && !(entity instanceof CreeperEntity)));

        //コーラス
        this.goalSelector.addGoal(0, new ChorusGoal<>(this));
        this.goalSelector.addGoal(4, new ChorusTargetBearSkillingGoal<>(this, 10));
        this.goalSelector.addGoal(4, new ChorusFindBossGoal<>(this, 5));
        this.goalSelector.addGoal(4, new ChorusFindEnemyGoal<>(this, 4));
        this.goalSelector.addGoal(4, new ChorusChangeLeaderGoal<>(this, 2));
        this.goalSelector.addGoal(4, new ChorusEnemyDieGoal<>(this, 1));

        //編隊関連
        this.goalSelector.addGoal(0, new TameableMobGroupingGoal<>(this));
        this.goalSelector.addGoal(0, new CheckMemberDieGoal<>(this));
        this.goalSelector.addGoal(0, new CheckLeaderDieGoal<>(this));

    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(7.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(LEADER, false);
        super.registerData();
    }

    @Override
    public void livingTick() {
        if (this.ticksExisted % 200 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(1);
        }
        if (this.isAlive() && this.getAttackTarget() == null && this.rand.nextInt(100000) < this.livingSoundTime++) {
            this.livingSoundTime = -this.getTalkInterval();
            if (this.rand.nextInt(10) < 5) {
                //おなかすいた
                float pitch = 0.8F + this.rand.nextFloat() * 0.1F;
                this.playSound(Registration.CRIMSONIAN_GUTE.get(), 0.5F, pitch);
                this.addPlaySound(new SoundData(this, Registration.CRIMSONIAN_HYF.get(), 0.5F, pitch), this.ticksExisted, 7);
            } else {
                //狩りがしたい
                float pitch = 0.8F + this.rand.nextFloat() * 0.1F;
                this.playSound(Registration.CRIMSONIAN_EEL.get(), 0.5F, pitch);
                this.addPlaySound(new SoundData(this, Registration.CRIMSONIAN_GA.get(), 0.5F, pitch), this.ticksExisted, 5);
                this.addPlaySound(new SoundData(this, Registration.CRIMSONIAN_DAS.get(), 0.5F, pitch), this.ticksExisted, 9);
            }
        }
        this.getSoundData(this.ticksExisted).forEach(SoundData::playSound);
        super.livingTick();
    }

    //落下ダメージ消す
    @Override
    public boolean onLivingFall(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    //味方の攻撃は受けない
    //todo マルチプレイ時の動作、プレイヤー同士でチーム組めるとグッド
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity attacker = source.getTrueSource();
        if (attacker instanceof IGroupable) {
            Optional<IGroupController<CrimsonianEntity>> optional = this.getGroupController();
            if (optional.isPresent()) {
                IGroupController<CrimsonianEntity> controller = optional.get();
                if (controller.isMember(attacker.getUniqueID())) {
                    return false;
                }
            }
        }
        if (attacker instanceof ITameable) {
            Optional<UUID> ownerId = this.getOwnerId();
            if (ownerId.isPresent()) {
                if (((ITameable) attacker).getOwnerId().isPresent() && ((ITameable) attacker).getOwnerId().get().equals(ownerId.get())) {
                    return false;
                }
            }
        }
        Optional<UUID> optionalOwnerId = this.getOwnerId();
        if (optionalOwnerId.isPresent()) {
            if (attacker == this.world.getPlayerByUuid(optionalOwnerId.get())) {
                return false;
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (this.rand.nextInt(10) < 8) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
        } else {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
        this.setCombatTask();
        return spawnDataIn;
    }

    public void setCombatTask() {
        if (this.world != null && !this.world.isRemote) {
            this.goalSelector.removeGoal(this.aiAttackOnCollide);
            this.goalSelector.removeGoal(this.aiArrowAttack);
            if (this.getHeldItemMainhand().getItem() instanceof BowItem) {
                int i = 20;

                this.aiArrowAttack.setAttackCooldown(i);
                this.goalSelector.addGoal(5, this.aiArrowAttack);
            } else if (this.getHeldItemMainhand().getItem() instanceof SwordItem) {
                this.goalSelector.addGoal(5, this.aiAttackOnCollide);
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.writeGroupCompound(compound);
        this.writeTameableNBT(compound);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.readGroupCompound(compound);
        this.readTameableNBT(compound);
        this.getGroupController().ifPresent(controller -> {
            if (controller.isLeader(this)) this.setLeader(true);
        });
        this.setCombatTask();
    }

    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
        if (!this.world.isRemote) {
            this.setCombatTask();
        }
    }

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    @Override
    public Optional<IGroupController<CrimsonianEntity>> getGroupController() {
        return this.groupable.getGroupController();
    }

    @Override
    public void setGroupController(@Nullable IGroupController<CrimsonianEntity> controller) {
        if (controller != null) {
            this.setLeader(controller.isLeader(this));
        } else {
            this.setLeader(false);
        }
        this.groupable.setGroupController(controller);
    }

    @Override
    public IGroupController<CrimsonianEntity> getDefaultGroupController() {
        return this.groupable.getDefaultGroupController();
    }

    @Override
    public void writeGroupCompound(CompoundNBT compound) {
        this.groupable.writeGroupCompound(compound);
    }

    @Override
    public void readGroupCompound(CompoundNBT compound) {
        this.groupable.readGroupCompound(compound);
    }

    //ここのleaderのゲッター/セッターは描画でのみ使用される
    public boolean isLeader() {
        return this.dataManager.get(LEADER);
    }

    public void setLeader(boolean bool) {
        this.dataManager.set(LEADER, bool);
    }

    @Override
    public Optional<UUID> getOwnerId() {
        return this.tameable.getOwnerId();
    }

    @Override
    public void setOwnerId(@Nullable UUID id) {
        this.tameable.setOwnerId(id);
    }

    @Override
    public boolean isWait() {
        return this.tameable.isWait();
    }

    @Override
    public void setWait(boolean state) {
        this.tameable.setWait(state);
    }

    @Override
    public void writeTameableNBT(CompoundNBT nbt) {
        this.tameable.writeTameableNBT(nbt);
    }

    @Override
    public void readTameableNBT(CompoundNBT nbt) {
        this.tameable.readTameableNBT(nbt);
    }

    @Override
    public boolean setChorus(int ticksAgo, int level) {
        return this.chorusable.setChorus(ticksAgo, level);
    }

    @Override
    public int getChorusLevel() {
        return this.chorusable.getChorusLevel();
    }

    @Override
    public void resetLevel() {
        this.chorusable.resetLevel();
    }

    @Override
    public boolean isCoolTime() {
        return this.chorusable.isCoolTime();
    }

    @Override
    public SoundEvent getChorus() {
        return this.chorusable.getChorus();
    }

    @Override
    public void addPlaySound(SoundData data, int nowTicks, int ticksAgo) {
        this.soundController.addPlaySound(data, nowTicks, ticksAgo);
    }

    @Override
    public List<SoundData> getSoundData(int nowTicks) {
        return this.soundController.getSoundData(nowTicks);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
        AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            abstractarrowentity = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(abstractarrowentity);
    }

    protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
        return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
    }

    @Override
    public void setCustomer(@Nullable PlayerEntity player) {
        merchant.setCustomer(player);
    }

    @Nullable
    @Override
    public PlayerEntity getCustomer() {
        return merchant.getCustomer();
    }

    @Override
    public MerchantOffers getOffers() {
        return merchant.getOffers();
    }

    @Override
    public void setClientSideOffers(@Nullable MerchantOffers offers) {
        merchant.setClientSideOffers(offers);
    }

    @Override
    public void onTrade(MerchantOffer offer) {
        merchant.onTrade(offer);
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
        merchant.verifySellingItem(stack);
    }

    @Override
    public World getWorld() {
        return merchant.getWorld();
    }

    @Override
    public int getXp() {
        return merchant.getXp();
    }

    @Override
    public void setXP(int xpIn) {
        merchant.setXP(xpIn);
    }

    @Override
    public boolean func_213705_dZ() {
        return merchant.func_213705_dZ();
    }

    @Override
    public SoundEvent getYesSound() {
        return merchant.getYesSound();
    }


    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        //緋熊の鉤爪を持った場合、テイム / グループ解除
        if (itemstack.getItem() == Registration.SCARLET_BEAR_CLAW_ITEM.get()) {
            //テイム処理
            if (!this.getOwnerId().isPresent()) {
                for (int i = 0; i < 5; i++) {
                    player.world.addParticle(ParticleTypes.HEART,
                            this.getPosX() + this.world.rand.nextFloat() * 2 - 1,
                            this.getPosY() + this.getEyeHeight() + this.world.rand.nextFloat() * 2 - 1,
                            this.getPosZ() + this.world.rand.nextFloat() * 2 - 1,
                            0, 0, 0);
                }
                this.setOwnerId(player.getUniqueID());

                if (this.world instanceof ServerWorld) {
                    //グループに所属中なら、脱退
                    Optional<IGroupController<CrimsonianEntity>> optionalController = this.getGroupController();
                    if (optionalController.isPresent()) {
                        this.setGroupController(null);
                    }
                }
                return true;

                //グループ解除処理
            } else if (player.isShiftKeyDown() && this.getOwnerId().get().equals(player.getUniqueID())) {
                if (this.world instanceof ServerWorld) {
                    //所属中のグループの全員をグループ解除
                    this.getGroupController().ifPresent(controller -> controller.getMembers().forEach(memberId -> {
                        Entity memberEntity = ((ServerWorld) this.world).getEntityByUuid(memberId);
                        if (memberEntity instanceof IGroupable) {
                            ((IGroupable<CrimsonianEntity>) memberEntity).setGroupController(null);
                        }
                    }));
                    this.setGroupController(null);
                }
                return true;
            }
        }

        //剣/弓を渡せる
        if (itemstack.getItem() instanceof SwordItem || itemstack.getItem() instanceof BowItem) {
            this.entityDropItem(this.getHeldItemMainhand());
            ItemStack stack = itemstack.copy();
            stack.setCount(1);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
            itemstack.shrink(1);
        }

        if (itemstack.getItem() == Items.NAME_TAG) {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        }

        //シフトで待機
        if (player.isShiftKeyDown()) {
            Optional<UUID> optionalOwnerId = this.getOwnerId();
            if (optionalOwnerId.isPresent() && player.getUniqueID().equals(optionalOwnerId.get())) {
                this.setWait(!this.isWait());
                return true;
            }
            return false;
        }

        //以下は取引を開く
        if (this.isChild() || !this.isAlive() && this.getCustomer() != null && this.isSleeping() && player.func_226563_dT_()) {
            return super.processInteract(player, hand);
        }

        if (hand == Hand.MAIN_HAND) {
            player.addStat(Stats.TALKED_TO_VILLAGER);
        }

        //元コードママだけど、返り値同じなら上のaddStatする意味とは一体？
        if (this.getOffers().isEmpty()) {
            return super.processInteract(player, hand);
        }

        if (!this.world.isRemote) {
            this.displayMerchantGui(player);
        }
        CrimsonianEntity.this.playSound(Registration.CRIMSONIAN_HEW.get(), 1.0F, 0.9F + CrimsonianEntity.this.rand.nextFloat() * 0.1F);
        return true;


    }

    private void displayMerchantGui(PlayerEntity player) {
        this.setCustomer(player);
        this.openMerchantContainer(player, this.getDisplayName(), 0);
    }

    @Nullable
    public Entity changeDimension(DimensionType destination, net.minecraftforge.common.util.ITeleporter teleporter) {
        this.resetCustomer();
        return super.changeDimension(destination, teleporter);
    }

    protected void resetCustomer() {
        this.setCustomer(null);
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.resetCustomer();
    }
}
