package com.sistr.scarlethill.block.tile;

import com.google.common.collect.Lists;
import com.sistr.scarlethill.util.CustomNBTUtil;
import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractAreaSpawner {
    //パラメータとして事前にセットすべき変数
    private int spawnLimit; //スポーンできる上限数
    private float exitDistance; //稼働範囲からどの程度離れると退出になるか
    //ウィザードで設定する変数
    public AxisAlignedBB relActiveArea; //相対稼働領域
    public List<BlockPos> spawnMarkerList = Lists.newArrayList(); //マーカーの位置のリスト

    //稼働中に使う変数
    public List<UUID> spawningMobList = Lists.newArrayList(); //現在スポーンしているモブ
    public int spawnCount; //スポーンした数
    private boolean isActivated; //前tickで稼働していたかどうか

    public void tick() {

        //クライアントなら停止
        //ちょっと横暴な気もする
        if (getWorld().isRemote) return;

        //マーカーゼロなら停止
        if (this.spawnMarkerList.isEmpty()) return;

        boolean reachedLimit = false;

        //上限に達したらフラグを立てる
        if (this.spawnCount >= this.spawnLimit) {
            reachedLimit = true;
        }

        //入退室の処理
        //範囲内に居ないとき
        if (!isPlayerWithin(getWorld(), getAbsActiveArea())) {

            //1tick前に稼働していなかったら停止
            if (!this.isActivated) return;

            //退室していたらリセット処理して停止
            //退室判定は入室より広い
            if (!isPlayerWithin(getWorld(), getAbsActiveArea().grow(this.exitDistance))) {
                this.reset();
                return;
            }
        }

        this.isActivated = true;

        //スポーン可能ならスポーン処理
        if (spawnConditions() && !reachedLimit) {
            List<UUID> mobs = getRandomSpawnMarker().spawn();
            if (!mobs.isEmpty()) {
                this.spawningMobList.addAll(mobs);
                this.addCount(mobs.size());
            }
        }

        //死亡などによりワールドから消えたモブはリストから消去
        //これisAliveでも代用できるかな？
        World world = getWorld();
        if (!this.spawningMobList.isEmpty()) {
            this.spawningMobList = this.spawningMobList.stream().filter((id -> ((ServerWorld) world).getEntityByUuid(id) != null)).collect(Collectors.toList());
        }

        //全滅時の停止処理
        if (reachedLimit && this.spawningMobList.isEmpty()) {
            this.stop();
        }

        //えっふぇくと
        if (world.rand.nextInt(1) == 0) {
            EffectUtil.spawnParticleBox((ServerWorld) world, RedstoneParticleData.REDSTONE_DUST,
                    this.getSpawnerPosition().getX() + 0.5, this.getSpawnerPosition().getY() + 0.5, this.getSpawnerPosition().getZ() + 0.5,
                    1, 1);
        }

    }

    //リセット処理
    public void reset() {
        this.isActivated = false;
        this.spawnCount = 0;

        World world = getWorld();
        if (!(world instanceof ServerWorld)) return;
        for (UUID id : this.spawningMobList) {
            Entity entity = ((ServerWorld) world).getEntityByUuid(id);
            if (entity == null) continue;
            entity.remove();
        }
        this.spawningMobList.clear();
    }

    //クリエプレイヤーは除外
    public static boolean isPlayerWithin(World world, AxisAlignedBB area) {
        for (PlayerEntity player : world.getPlayers()) {
            if (EntityPredicates.CAN_AI_TARGET.test(player)) {
                BlockPos playerPos = player.getPosition();
                //ボックス内にプレイヤーが居るかどうか
                //XZの方がハズれる確率高いだろうし、Yを一番最後にした方が負荷が軽いと思う
                if (area.minX <= playerPos.getX() && playerPos.getX() <= area.maxX
                        && area.minZ <= playerPos.getZ() && playerPos.getZ() <= area.maxZ
                        && area.minY <= playerPos.getY() && playerPos.getY() <= area.maxY) {
                    return true;
                }
            }
        }
        return false;
    }

    abstract boolean spawnConditions();

    public int getSpawnLimit() {
        return this.spawnLimit;
    }

    public void setSpawnLimit(int limit) {
        this.spawnLimit = limit;
    }

    public float getExitDistance() {
        return this.exitDistance;
    }

    public void setExitDistance(float exitDistance) {
        this.exitDistance = exitDistance;
    }

    public int getSpawnCount() {
        return this.spawnCount;
    }

    public boolean isActivated() {
        return this.isActivated;
    }

    public AxisAlignedBB getAbsActiveArea() {
        return this.relActiveArea.offset(getSpawnerPosition());
    }

    //リストから特定のスポーンマーカーをゲットする。
    //存在しなかった場合はラムダ式で空っぽのISpawnMarker.spawn()の返り値を返す
    @Nonnull
    private ISpawnMarker getSpawnMarker(int index) {
        BlockPos spawnerPos = this.spawnMarkerList.get(index)
                .add(getSpawnerPosition().getX(), getSpawnerPosition().getY(), getSpawnerPosition().getZ());
        TileEntity tile = getWorld().getTileEntity(spawnerPos);
        return tile instanceof ISpawnMarker ? (ISpawnMarker) tile : Lists::newArrayList;
    }

    //ランダムに取得する
    @Nonnull
    private ISpawnMarker getRandomSpawnMarker() {
        return this.getSpawnMarker(getWorld().rand.nextInt(this.spawnMarkerList.size()));
    }

    //カウントを増やす
    public void addCount(int count) {
        this.spawnCount += count;
    }

    public void read(CompoundNBT nbt) {
        this.spawnLimit = nbt.getShort("Limit");
        this.exitDistance = nbt.getFloat("Exit");
        CustomNBTUtil.getArea("ActiveRange", nbt).ifPresent(axisAlignedBB -> this.relActiveArea = axisAlignedBB);
        this.spawnMarkerList = CustomNBTUtil.getBlocks("SpawnMarkerPos", nbt).stream().distinct().collect(Collectors.toList());

        this.spawnCount = nbt.getShort("Count");
        this.isActivated = nbt.getBoolean("Activated");

        this.spawningMobList.clear();
        if (nbt.contains("SpawningMob", 9)) {
            ListNBT spawningMobNBT = nbt.getList("SpawningMob", 8);
            for (int i = 0; i < spawningMobNBT.size(); i++) {
                UUID id = UUID.fromString(spawningMobNBT.getString(i));
                this.spawningMobList.add(id);
            }
        }

    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putShort("Limit", (short) this.spawnLimit);
        compound.putFloat("Exit", this.exitDistance);
        CustomNBTUtil.writeArea("ActiveRange", compound, this.relActiveArea);
        CustomNBTUtil.writeBlockPosList("SpawnMarkerPos", compound, this.spawnMarkerList);

        compound.putShort("Count", (short) this.spawnCount);
        compound.putBoolean("Activated", this.isActivated);

        ListNBT spawningMobNBT = new ListNBT();
        if (!this.spawningMobList.isEmpty()) {
            for (UUID id : this.spawningMobList) {
                spawningMobNBT.add(StringNBT.valueOf(id.toString()));
            }
        }
        compound.put("SpawningMob", spawningMobNBT);
        return compound;

    }

    public abstract World getWorld();

    public abstract BlockPos getSpawnerPosition();

    public abstract void stop();
}
