package com.sistr.scarlethill.entity;

import net.minecraft.entity.Entity;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IGroupController<E extends Entity> {

    boolean isLeader(E target);

    void setLeaderId(UUID newLeader);

    Optional<UUID> getLeaderId();

    Optional<E> getLeader();

    boolean isMember(UUID target);

    Set<UUID> getMembers();

    boolean addMember(UUID newMember);

    void removeMember(UUID member);

}
