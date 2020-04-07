package com.sistr.scarlethill.entity;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

public class DummyEntity extends Entity {
    @Nullable
    private final Entity dummyTarget;

    //登録用
    public DummyEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.dummyTarget = null;
    }

    public DummyEntity(World world, @Nullable Entity dummyTarget) {
        super(Registration.DUMMY_ENTITY.get(), world);
        this.dummyTarget = dummyTarget;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Optional<Entity> getDummyTarget() {
        return Optional.ofNullable(dummyTarget);
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return null;
    }
}
