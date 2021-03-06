package com.sistr.scarlethill.block.tile;

import com.google.common.collect.Lists;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.util.CustomNBTUtil;
import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class AreaSpawnerTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity, IHasWizardBlock {
    private boolean isActiveWizard = false;
    private WizardStep wizardStep = WizardStep.AREA;
    private BlockPos wizardPos = null;
    private final List<BlockPos> wizardPosList = Lists.newArrayList();
    private final ServerBossInfo bossInfo = new ServerBossInfo(
            new TranslationTextComponent("block.scarlethill.area_spawner.bossinfo"),
            BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

    private final AbstractAreaSpawner spawnerLogic = new AbstractAreaSpawner() {

        //現在スポーン数がマーカーの数以下であればtrue
        @Override
        boolean spawnConditions() {
            return this.spawningMobList.size() < this.spawnMarkerList.size();
        }

        @Override
        public World getWorld() {
            return AreaSpawnerTile.this.getWorld();
        }

        @Override
        public BlockPos getSpawnerPosition() {
            return AreaSpawnerTile.this.getPos();
        }

        @Override
        public void stop() {
            AreaSpawnerTile.this.bossInfo.removeAllPlayers();
            EffectUtil.spawnParticleBox((ServerWorld) getWorld(), ParticleTypes.EXPLOSION,
                    this.getSpawnerPosition().getX() + 0.5, this.getSpawnerPosition().getY() + 0.5, this.getSpawnerPosition().getZ() + 0.5,
                    1, 0);
            getWorld().setBlockState(this.getSpawnerPosition(), Blocks.IRON_BLOCK.getDefaultState());
            getWorld().playSound(null, this.getSpawnerPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.2F);
            AreaSpawnerTile.this.markDirty();
        }
    };

    public AreaSpawnerTile() {
        super(Registration.AREA_SPAWNER_TILE.get());
    }

    @Override
    public void tick() {
        //ボスバー制御
        //3体以下は表示しない、ボス部屋で使ってるので…
        //ロジックの方でボスバーの消滅処理を行うため、先にこちらから処理する
        //todo ボスバーの表示非表示及び表記の設定
        World world = getWorld();
        if (this.spawnerLogic.isActivated() && 4 < this.spawnerLogic.getSpawnLimit() && world instanceof ServerWorld) {
            this.bossInfo.setPercent(1 - MathHelper.clamp((float) this.spawnerLogic.getSpawnCount() / (float) this.spawnerLogic.getSpawnLimit(), 0, 1));
            Collection<ServerPlayerEntity> seeingPlayers = this.bossInfo.getPlayers();
            AxisAlignedBB activeArea = this.spawnerLogic.getAbsActiveArea();
            for (PlayerEntity player : world.getPlayers()) {
                if (!seeingPlayers.contains(player) && checkPlayerWithin(player, activeArea) && player.isAlive()) {
                    this.bossInfo.addPlayer((ServerPlayerEntity) player);
                } else if (seeingPlayers.contains(player) && (!checkPlayerWithin(player, activeArea) || !player.isAlive())) {
                    this.bossInfo.removePlayer((ServerPlayerEntity) player);
                }
            }
        }
        this.spawnerLogic.tick();
    }


    //ボックス内にプレイヤーが居るかどうか
    //XZの方がハズれる確率高いだろうし、Yを一番最後にした方が負荷が軽いと思う
    public static boolean checkPlayerWithin(PlayerEntity player, AxisAlignedBB area) {
        BlockPos playerPos = player.getPosition();
        return area.minX <= playerPos.getX() && playerPos.getX() <= area.maxX
                && area.minZ <= playerPos.getZ() && playerPos.getZ() <= area.maxZ
                && area.minY <= playerPos.getY() && playerPos.getY() <= area.maxY;
    }

    public AbstractAreaSpawner getSpawnerLogic() {
        return this.spawnerLogic;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.spawnerLogic.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        this.spawnerLogic.write(compound);
        return compound;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AreaSpawnerContainer(windowId, world, pos, playerInventory, player);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void startWizard(PlayerEntity player) {
        if (this.world.isRemote) return;
        if (this.isActiveWizard) {
            cancelWizard(player);
        }
        this.isActiveWizard = true;
        this.wizardStep = WizardStep.AREA;
        sendMessageTemplate(player, "start_wizard");
        sendMessageTemplate(player, "select_area0");
    }

    @Override
    public void sendMessageTemplate(PlayerEntity player, String text) {
        player.sendMessage(new StringTextComponent("<Wizard> ")
                .appendSibling(new TranslationTextComponent(Registration.AREA_SPAWNER_BLOCK.get().getTranslationKey() + "." + text)));
    }

    @Override
    public void receiveDate(PlayerEntity player, Object object) {
        if (this.world.isRemote || !this.isActiveWizard) {
            return;
        }
        if (object instanceof BlockPos) {
            BlockPos receivePos = (BlockPos) object;
            if (this.wizardStep == WizardStep.AREA) {
                if (this.wizardPos == null) {
                    this.wizardPos = receivePos.subtract(this.getPos());
                    sendMessageTemplate(player, "select_area1");
                } else {
                    AxisAlignedBB bb = new AxisAlignedBB(this.wizardPos, receivePos.subtract(this.getPos()));
                    CompoundNBT nbt = this.write(new CompoundNBT());
                    CustomNBTUtil.writeArea("ActiveRange", nbt, bb);
                    this.read(nbt);
                    this.markDirty();
                    player.sendMessage(new StringTextComponent("<Wizard> ")
                            .appendSibling(new TranslationTextComponent(Registration.AREA_SPAWNER_BLOCK.get().getTranslationKey() + ".select_area2"))
                            .appendText(bb.toString()));
                    sendMessageTemplate(player, "select_spawn_pos0");
                    this.wizardStep = WizardStep.SPAWN_POS;
                }
            } else if (this.wizardStep == WizardStep.SPAWN_POS && this.world.getTileEntity(receivePos) instanceof ISpawnMarker) {
                this.wizardPosList.add(receivePos.subtract(this.getPos()));
                player.sendMessage(new StringTextComponent("<Wizard> " + object.toString() + " " + this.wizardPosList.size())
                        .appendSibling(new TranslationTextComponent(Registration.AREA_SPAWNER_BLOCK.get().getTranslationKey() + ".select_spawn_pos1")));
            }
        } else if (object instanceof Boolean) {
            if (this.wizardStep == WizardStep.SPAWN_POS && (Boolean) object) {
                CompoundNBT nbt = this.write(new CompoundNBT());
                CustomNBTUtil.writeBlockPosList("SpawnMarkerPos", nbt, this.wizardPosList);
                this.read(nbt);
                this.markDirty();
                sendMessageTemplate(player, "select_spawn_pos2");
                endWizard(player);
            }
        }

    }

    @Override
    public void cancelWizard(PlayerEntity player) {
        if (this.world.isRemote) return;
        this.isActiveWizard = false;
        sendMessageTemplate(player, "cancel_wizard");
    }

    @Override
    public void endWizard(PlayerEntity player) {
        if (this.world.isRemote) return;
        this.isActiveWizard = false;
        sendMessageTemplate(player, "end_wizard");
    }

    enum WizardStep {
        AREA,
        SPAWN_POS
    }
}
