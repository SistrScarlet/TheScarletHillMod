package com.sistr.scarlethill.entity;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class DefaultGroupController<E extends Entity> implements IGroupController<E> {
    private final Set<UUID> members = Sets.newHashSet();
    private final int memberLimit;
    @Nullable
    private UUID leader;

    public DefaultGroupController(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    @Override
    public boolean isLeader(E target) {
        if (this.leader == null) return false;
        return this.leader.equals(target.getUniqueID());
    }

    @Override
    public void setLeaderId(UUID newLeader) {
        this.leader = newLeader;
        addMember(newLeader);
    }

    @Override
    public Optional<UUID> getLeaderId() {
        return Optional.ofNullable(this.leader);
    }

    @Override
    public Optional<E> getLeader() {
        Optional<ServerWorld> optionalServerWorld = getServerWorld();
        if (optionalServerWorld.isPresent()) {
            Optional<UUID> LeaderId = getLeaderId();
            if (LeaderId.isPresent()) {
                Entity leaderEntity = optionalServerWorld.get().getEntityByUuid(LeaderId.get());
                return Optional.ofNullable((E) leaderEntity);
            }
        }
        return Optional.empty();
    }

    abstract protected Optional<ServerWorld> getServerWorld();

    @Override
    public boolean isMember(UUID target) {
        return this.members.contains(target);
    }

    @Override
    public Set<UUID> getMembers() {
        return Sets.newHashSet(this.members);
    }

    @Override
    public boolean addMember(UUID newMember) {
        if (this.memberLimit <= this.members.size() || this.members.contains(newMember)) {
            return false;
        }
        return this.members.add(newMember);
    }

    @Override
    public void removeMember(UUID member) {
        this.members.remove(member);
    }
}
