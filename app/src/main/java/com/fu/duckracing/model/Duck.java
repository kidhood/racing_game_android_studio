package com.fu.duckracing.model;

import android.widget.CheckBox;
import android.widget.SeekBar;

public class Duck {
    private SeekBar seekBar;
    private CheckBox checkBox;
    private String name;

    public Duck(SeekBar seekBar, CheckBox checkBox, String name) {
        this.seekBar = seekBar;
        this.checkBox = checkBox;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
