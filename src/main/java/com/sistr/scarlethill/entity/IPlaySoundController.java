package com.sistr.scarlethill.entity;

import java.util.List;

public interface IPlaySoundController {

    void addPlaySound(SoundData data, int nowTicks, int ticksAgo);

    List<SoundData> getSoundData(int nowTicks);
}
