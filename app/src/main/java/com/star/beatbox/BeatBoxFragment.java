package com.star.beatbox;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.star.beatbox.databinding.FragmentBeatBoxBinding;
import com.star.beatbox.databinding.ListItemSoundBinding;

import java.util.List;

public class BeatBoxFragment extends Fragment {

    private static final int SEEK_BAR_MAX = 200;

    private BeatBox mBeatBox;

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mBeatBox = new BeatBox(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBeatBoxBinding fragmentBeatBoxBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_beat_box, container, false);

        RecyclerView fragmentBeatBoxRecyclerView =
                fragmentBeatBoxBinding.fragmentBeatBoxRecyclerView;

        fragmentBeatBoxRecyclerView.setLayoutManager(
                new GridLayoutManager(getActivity(), 3)
        );
        fragmentBeatBoxRecyclerView.setAdapter(
                new SoundAdapter(mBeatBox.getSounds())
        );

        AppCompatSeekBar fragmentBeatBoxSeekBar = fragmentBeatBoxBinding.fragmentBeatBoxSeekBar;

        fragmentBeatBoxSeekBar.setMax(SEEK_BAR_MAX);
        fragmentBeatBoxSeekBar.setProgress(fragmentBeatBoxSeekBar.getMax() / 2);
        fragmentBeatBoxBinding.fragmentBeatBoxSeekBarTextView.setText(
                getString(R.string.playback_speed_label, 100, "%")
        );
        fragmentBeatBoxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int rate;

                if (progress < (fragmentBeatBoxSeekBar.getMax() / 2)) {
                    rate = progress / 2 + 50;
                } else {
                    rate = progress;
                }

                mBeatBox.setRate(rate / 100f);
                fragmentBeatBoxBinding.fragmentBeatBoxSeekBarTextView.setText(
                        getString(R.string.playback_speed_label, rate, "%")
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return fragmentBeatBoxBinding.getRoot();
    }

    private class SoundHolder extends RecyclerView.ViewHolder {
        private ListItemSoundBinding mListItemSoundBinding;

        public SoundHolder(ListItemSoundBinding listItemSoundBinding) {
            super(listItemSoundBinding.getRoot());

            mListItemSoundBinding = listItemSoundBinding;
            mListItemSoundBinding.setSoundViewModel(new SoundViewModel(mBeatBox));
        }

        public void bindSound(Sound sound) {
            mListItemSoundBinding.getSoundViewModel().setSound(sound);
            mListItemSoundBinding.executePendingBindings();
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemSoundBinding listItemSoundBinding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_sound, parent, false);

            return new SoundHolder(listItemSoundBinding);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBeatBox.release();
    }
}
