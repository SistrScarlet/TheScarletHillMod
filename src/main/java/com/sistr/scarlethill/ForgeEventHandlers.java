package com.sistr.scarlethill;

import com.sistr.scarlethill.entity.ITameable;
import com.sistr.scarlethill.entity.MoltenSlimeEntity;
import com.sistr.scarlethill.entity.ScarletBearEntity;
import com.sistr.scarlethill.item.ILeftClickable;
import com.sistr.scarlethill.item.ScarletGemItem;
import com.sistr.scarlethill.network.Networking;
import com.sistr.scarlethill.network.PacketLeftClick;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.util.EffectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;
import java.util.UUID;

public class ForgeEventHandlers {


    //クラサイドオンリー
    @SubscribeEvent
    public static void leftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof ILeftClickable) {
            ((ILeftClickable) item).onLeftClick(event.getWorld(), event.getPlayer(), event.getHand());
            Networking.INSTANCE.sendToServer(new PacketLeftClick());
        }
    }

    //イベントをキャンセルするとなんかバグる
    //まぁそもそもあんまり意味無いけど…
    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof ILeftClickable) {
            ((ILeftClickable) item).onBlockLeftClick(event.getWorld(), event.getPlayer(), event.getHand(), event.getPos());
        }
    }

    //熊さん捕食用
    @SubscribeEvent
    public static void onHitDeadlyDamage(LivingDeathEvent event) {
        LivingEntity victim = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity entity = source.getTrueSource();
        if (entity instanceof ScarletBearEntity) {
            ((ScarletBearEntity) entity).eat(victim.getMaxHealth());
        }
    }

    @SubscribeEvent
    public static void explode(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof MoltenSlimeEntity) {
            event.setResult(Event.Result.DENY);
        }
    }

    //明るさ0のところにのみスポーンするようにする
    /*
    @SubscribeEvent
    public static void onLivingSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getSpawnReason() != SpawnReason.NATURAL) {
            return;
        }
        if (!(event.getEntityLiving() instanceof MonsterEntity)) {
            return;
        }
        if (0 == event.getWorld().getLightFor(LightType.BLOCK, new BlockPos(event.getX(), event.getY(), event.getZ()))) {
            return;
        }
        event.setResult(Event.Result.DENY);
    }
     */

    //ダメージ受けた瞬間
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        onScarletBlessing(event);
        if (!event.isCanceled()) {
            onDamageByTameableMob(event);
        }
    }

    //ダメージを受けた後
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        onScarletBlessing(event);
    }

    //ダメージを無効化する
    private static void onScarletBlessing(LivingEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (living.getActivePotionEffect(Registration.SCARLET_BLESSING.get()) != null) {
            float radius = 3F;
            ScarletGemItem.bomb(living.world, living, radius, 4);
            EffectUtil.spawnParticleSphere((ServerWorld) living.world, ParticleTypes.FLAME, living.getPosX(), living.getPosY() + living.getEyeHeight(), living.getPosZ(), (int) radius * 8, radius);
            living.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.8F + living.world.rand.nextFloat() * 0.2F);
            event.setCanceled(true);
        }
    }

    private static void onDamageByTameableMob(LivingHurtEvent event) {
        Entity attacker = event.getSource().getTrueSource();
        if (attacker instanceof ITameable) {
            Optional<UUID> optionalOwnerId = ((ITameable) attacker).getOwnerId();
            optionalOwnerId.ifPresent(uuid -> {
                LivingEntity living = event.getEntityLiving();
                if (uuid == living.getUniqueID()) {
                    event.setCanceled(true);
                }
            });
        }
    }

}
