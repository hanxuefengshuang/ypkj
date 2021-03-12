package com.yuepeng.wxb.ui.activity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityPlayBellBinding;
import com.yuepeng.wxb.entity.AddBellPresenter;
import com.yuepeng.wxb.ui.pop.OpenVipPop;

import java.io.IOException;

public class PlayBellActivity extends BaseActivity<ActivityPlayBellBinding, AddBellPresenter> implements View.OnClickListener, BaseDetailView {

    private boolean isPlaying = false;
    MediaPlayer myMediaPlayer;
    private OpenVipPop openVipPop;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected AddBellPresenter createPresenter() {
        return new AddBellPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play_bell;
    }

    @Override
    protected void initView() {
        mBinding.title.titlebar.setTitle("一键警铃");
        mBinding.playBellBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int membertype = App.getInstance().getUserModel().getMemberType();
        if (membertype == 0){
            openVipPop = (OpenVipPop) new XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(new OpenVipPop(this))
                    .show();
            return;
        }
        if (isPlaying){
            mBinding.playBellBtn.setImageResource(R.mipmap.play_bell_icon);
            stopPlay();
            isPlaying = false;
        }else {
            mPresenter.addBellLog();
            playBell();
            isPlaying = true;
            mBinding.playBellBtn.setImageResource(R.mipmap.stop_play_bell);
        }
    }

    private void stopPlay() {
        myMediaPlayer.stop();
    }

    private void playBell() {
        AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            localAudioManager.setStreamVolume(3, localAudioManager.getStreamMaxVolume(3), 4);
//            int mVolume = localAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前音乐音量
        int maxVolume = localAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大声音
        localAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0); // 设置为最大声音，可通过SeekBar更改音量大小

        AssetFileDescriptor fileDescriptor;
        try {
            fileDescriptor = getAssets().openFd("110.wav");

            myMediaPlayer = new MediaPlayer();
            myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            myMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),

                    fileDescriptor.getStartOffset(),

                    fileDescriptor.getLength());
            myMediaPlayer.prepare();
            myMediaPlayer.start();
            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (myMediaPlayer != null){
                        myMediaPlayer.start();
                    }else {
                        myMediaPlayer = new MediaPlayer();
                        myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            myMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                                    fileDescriptor.getStartOffset(),
                                    fileDescriptor.getLength());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            myMediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        myMediaPlayer.start();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myMediaPlayer != null){
            myMediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myMediaPlayer != null){
            myMediaPlayer.stop();
            myMediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myMediaPlayer != null){
            if (myMediaPlayer.isPlaying()){
                mBinding.playBellBtn.setImageResource(R.mipmap.stop_play_bell);
                isPlaying = true;
            }else {
                mBinding.playBellBtn.setImageResource(R.mipmap.play_bell_icon);
                isPlaying = false;
            }
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Throwable e) {
        showException(e);
    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }


}