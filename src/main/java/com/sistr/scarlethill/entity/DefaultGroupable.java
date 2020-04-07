package com.sistr.scarlethill.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

//todo グループの読み書きがうまくいってない
public class DefaultGroupable<E extends Entity> implements IGroupable<E> {
    @Nullable
    private IGroupController<E> controller;
    private final E entity;

    public DefaultGroupable(E entity) {
        this.entity = entity;
    }

    @Override
    public Optional<IGroupController<E>> getGroupController() {
        return Optional.ofNullable(this.controller);
    }

    @Override
    public void setGroupController(@Nullable IGroupController<E> controller) {
        this.controller = controller;
    }

    @Override
    public IGroupController<E> getDefaultGroupController() {
        return new DefaultGroupController<E>(10) {
            @Override
            protected Optional<ServerWorld> getServerWorld() {
                World world = DefaultGroupable.this.entity.world;
                return Optional.ofNullable(world instanceof ServerWorld ? (ServerWorld) world : null);
            }
        };
    }

    public void writeGroupCompound(CompoundNBT compound) {
        getGroupController().ifPresent(controller -> {
            controller.getLeaderId().ifPresent(uuid -> compound.putUniqueId("Leader", uuid));
            if (controller.isLeader(this.entity)) {
                ListNBT members = new ListNBT();
                controller.getMembers().forEach(member -> members.add(NBTUtil.writeUniqueId(member)));
                compound.put("GroupMembers", members);
            }
        });
    }

    public void readGroupCompound(CompoundNBT compound) {
        this.getGroupController().ifPresent(oldController -> oldController.removeMember(this.entity.getUniqueID()));
        this.setGroupController(null);
        UUID leaderID = compound.getUniqueId("Leader");
        if (!(this.entity.world instanceof ServerWorld)) return;
        //自身がリーダーの場合
        if (this.entity.getUniqueID().equals(leaderID)) {
            //ワールドに居るメンバーを自身のグループに引き込む
            if (compound.contains("GroupMembers", 9)) {
                IGroupController<E> controller = getDefaultGroupController();
                controller.setLeaderId(this.entity.getUniqueID());
                this.setGroupController(controller);
                ListNBT members = compound.getList("GroupMembers", 10);
                for (int i = 0; i < members.size(); i++) {
                    UUID memberId = NBTUtil.readUniqueId(members.getCompound(i));
                    Entity memberEntity = ((ServerWorld) this.entity.world).getEntityByUuid(memberId);
                    if (memberEntity instanceof IGroupable) {
                        controller.addMember(memberId);
                        ((IGroupable<E>) memberEntity).setGroupController(controller);
                    }
                }
            }
        } else {
            //リーダーがグループを持っていれば参加
            Entity leader = ((ServerWorld) this.entity.world).getEntityByUuid(leaderID);
            if (leader instanceof IGroupable) {
                ((IGroupable<E>) leader).getGroupController().ifPresent(controller -> {
                    controller.addMember(this.entity.getUniqueID());
                    this.setGroupController(controller);
                });
            }
        }
    }

}
