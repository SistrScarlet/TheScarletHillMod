package com.sistr.scarlethill.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;

public class DefaultPlaySoundController implements IPlaySoundController {
    private final HashMap<Integer, List<SoundData>> soundData = Maps.newHashMap();

    @Override
    public void addPlaySound(SoundData data, int nowTicks, int ticksAgo) {
        List<SoundData> soundList = this.soundData.get(nowTicks + ticksAgo);
        if (soundList == null) {
            soundList = Lists.newArrayList();
        }
        soundList.add(data);
        this.soundData.put(nowTicks + ticksAgo, soundList);
    }

    @Override
    public List<SoundData> getSoundData(int nowTicks) {
        this.soundData.keySet().removeIf(ticks -> ticks < nowTicks);
        List<SoundData> sounds = this.soundData.get(nowTicks);
        return sounds != null ? sounds : Lists.newArrayList();
    }


}
