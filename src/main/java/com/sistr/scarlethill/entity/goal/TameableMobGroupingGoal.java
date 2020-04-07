package com.sistr.scarlethill.entity.goal;

import com.sistr.scarlethill.entity.IGroupController;
import com.sistr.scarlethill.entity.IGroupable;
import com.sistr.scarlethill.entity.ITameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//todo 分離が必要
//編隊を組む
public class TameableMobGroupingGoal<E extends MobEntity & IGroupable<E> & ITameable> extends Goal {
    private final E mob;

    public TameableMobGroupingGoal(E mob) {
        this.mob = mob;
    }

    //1秒に一回、何にも属していない場合かつ、戦闘中またはオーナーが居る場合、発動
    @Override
    public boolean shouldExecute() {
        if (this.mob.ticksExisted % 20 == 0) {
            Optional<IGroupController<E>> optional = this.mob.getGroupController();
            return !optional.isPresent() && (this.mob.getAttackTarget() != null || this.mob.getOwnerId().isPresent());
        }
        return false;
    }

    //周囲にリーダーが居ればそいつに従う
    //居なければ周囲の仲間のリーダーになる
    //仲間すら居なければ何もしない
    //オーナーが違う場合(どちらかオーナーが居ない場合も含む)はチームを組めない
    @Override
    public void startExecuting() {
        float radius = 8;
        AxisAlignedBB bb = new AxisAlignedBB(this.mob.getPosX() + radius, this.mob.getPosY() + radius, this.mob.getPosZ() + radius,
                this.mob.getPosX() - radius, this.mob.getPosY() - radius, this.mob.getPosZ() - radius);
        List<Entity> aroundFriends = this.mob.world.getEntitiesInAABBexcluding(this.mob, bb, (entity ->
                entity.isAlive() && !entity.isSpectator() && this.mob.getClass().isInstance(entity)));
        for (Entity friend : aroundFriends) {
            Optional<IGroupController<E>> optionalController = ((IGroupable<E>) friend).getGroupController();
            if (optionalController.isPresent()) {
                //どちらも居ない、またはオーナーが一致する場合のみ参加する
                if ((!this.mob.getOwnerId().isPresent() && !((ITameable) friend).getOwnerId().isPresent()) || (this.mob.getOwnerId().isPresent() && ((ITameable) friend).getOwnerId().isPresent() && this.mob.getOwnerId().get().equals(((ITameable) friend).getOwnerId().get()))) {
                    IGroupController<E> controller = optionalController.get();
                    if (controller.addMember(this.mob.getUniqueID())) {
                        this.mob.setGroupController(controller);
                        return;
                    }
                }
            }
        }
        if (aroundFriends.isEmpty()) {
            return;
        }
        IGroupController<E> controller = this.mob.getDefaultGroupController();
        //リーダーは居ないが仲間は居る場合、周囲の無所属の味方を引き入れる、ただしオーナーが違う場合はスルー
        for (Entity friend : aroundFriends) {
            //無所属
            if (!((IGroupable<E>) friend).getGroupController().isPresent()) {
                //どちらも居ない、またはオーナーが一致する場合のみ参加させる
                if ((!this.mob.getOwnerId().isPresent() && !((ITameable) friend).getOwnerId().isPresent()) || (this.mob.getOwnerId().isPresent() && ((ITameable) friend).getOwnerId().isPresent() && this.mob.getOwnerId().get().equals(((ITameable) friend).getOwnerId().get()))) {
                    if (controller.addMember(friend.getUniqueID())) {
                        ((IGroupable<E>) friend).setGroupController(controller);
                    }
                }
            }
        }
        //参加者が居る場合のみグループに入る
        //参加者ゼロ人でもバグりはしないけど、入った後脱退を繰り返すだろうと思う
        if (0 < controller.getMembers().size()) {
            controller.setLeaderId(this.mob.getUniqueID());
            this.mob.setGroupController(controller);
        }
    }

    @Override
    public void resetTask() {
        this.mob.getGroupController().ifPresent(controller -> {
            for (UUID id : controller.getMembers()) {
                Entity member = ((ServerWorld) this.mob.world).getEntityByUuid(id);
                if (member instanceof MobEntity) {
                    ((MobEntity) member).addPotionEffect(new EffectInstance(Effects.GLOWING, 100));
                }
            }
        });
    }
}
