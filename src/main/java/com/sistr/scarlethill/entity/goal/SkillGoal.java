package com.sistr.scarlethill.entity.goal;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

//技を使った行動の抽象クラス。
//発動をshouldStartで判定する
//発生、動作、復帰の3段階あり、それぞれでStartメソッドとTickメソッドがある
//Startメソッドは開始直後に一回だけ発火する
//Tickメソッドは現在の段階のTick処理となる。
//このクラスを継承する場合、Goalに元からあるメソッドは利用しない方が好ましい
public abstract class SkillGoal extends Goal {
    protected final MobEntity goalOwner;
    protected final int readyLength;
    protected final int freezeLength;
    protected final int actionLength;
    public SkillStatus status = SkillStatus.READY;
    protected int timer;

    public SkillGoal(MobEntity goalOwner, int readyLength, int actionLength, int freezeLength) {
        this.goalOwner = goalOwner;
        this.readyLength = readyLength;
        this.freezeLength = freezeLength;
        this.actionLength = actionLength;
    }

    //shouldExecuteの代用品。発動させるか否か
    abstract protected boolean shouldStart();

    //発生硬直の開始
    protected void readyStart() {
    }

    //発生硬直中のtick処理
    protected void readyTick() {

    }

    //技の開始
    protected void actionStart() {
    }

    //技のTick処理
    protected void actionTick() {

    }

    //復帰硬直の開始
    protected void freezeStart() {
    }

    //復帰硬直中のtick処理
    protected void freezeTick() {

    }

    //段階の強制以降
    public void setStatus(SkillStatus status) {
        this.timer = 0;
        this.status = status;
        switch (status) {
            case READY:
                this.readyStart();
                break;
            case ACTION:
                this.actionStart();
                break;
            case FREEZE:
                this.freezeStart();
                break;
        }
    }

    //shouldStartメソッドがtrueならstartupを開始する。
    //ここをオーバーライドして直接起動させると、startupが動かない
    @Override
    public boolean shouldExecute() {
        if (shouldStart()) {
            this.setStatus(SkillStatus.READY);
            return true;
        }
        return false;
    }

    //時間が超えていたら次の段階へ進む。ただしフリーズだったら終了させる
    @Override
    public boolean shouldContinueExecuting() {
        ++this.timer;
        switch (this.status) {
            case READY:
                if (isOver(this.readyLength)) {
                    this.setStatus(SkillStatus.ACTION);
                }
                return true;
            case ACTION:
                if (isOver(this.actionLength)) {
                    this.setStatus(SkillStatus.FREEZE);
                }
                return true;
            case FREEZE:
                return !isOver(this.freezeLength);
        }
        return false;
    }

    //progressがLengthを超えているか否か
    private boolean isOver(int checkLength) {
        return this.timer > checkLength;
    }

    @Override
    public void tick() {
        switch (this.status) {
            case READY:
                readyTick();
                break;
            case ACTION:
                actionTick();
                break;
            case FREEZE:
                freezeTick();
                break;
        }
    }

    public enum SkillStatus {
        READY,
        FREEZE,
        ACTION
    }

}
