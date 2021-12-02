package com.phy.app.beans;

public class LEDStatus {
    private int hues=0;
    private int cmd=0;                      //数据包中Byte1(命令)
    private int mode=0;                     //数据包中Byte2(模式)
    private int effectCategory=0;           //数据包中byte8(特效类别)
    private int saturation=100;
    private int brightness=5;
    private int colorTemperature=44;
    private int rgbEffectNo=1;
    private int cctEffectNo=61;
    private int effectSpeed=4;
    private boolean isRgbMode=true;
    private int effectRepTimes=10;
    private int effectFreq=1;
    private int presetEffectNo=1;
    private int arrowIndex=Const.ARROW_AT_HUES;
    private String modelNumber;
    private int maxCctValue=75;
    private int minCctValue=32;
    private boolean isLedOn=true;

    public LEDStatus() {

    }

    public LEDStatus(int hues, int saturation, int brightness, int colorTemperature, int rgbEffectNo, int cctEffectNo, int effectSpeed, boolean isRgbMode) {
        this.hues = hues;
        this.saturation = saturation;
        this.brightness = brightness;
        this.colorTemperature = colorTemperature;
        this.rgbEffectNo = rgbEffectNo;
        this.cctEffectNo = cctEffectNo;
        this.effectSpeed = effectSpeed;
        this.isRgbMode = isRgbMode;
    }

    public int getHues() {
        return hues;
    }

    public int getSaturation() {
        return saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }

    public int getRgbEffectNo() {
        return rgbEffectNo;
    }

    public int getCctEffectNo() {
        return cctEffectNo;
    }

    public int getEffectSpeed() {
        return effectSpeed;
    }

    public boolean isRgbMode() {
        return isRgbMode;
    }

    public void setHues(int hues) {
        this.hues = hues;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public void setColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }

    public void setRgbEffectNo(int rgbEffectNo) {
        this.rgbEffectNo = rgbEffectNo;
    }

    public void setCctEffectNo(int cctEffectNo) {
        this.cctEffectNo = cctEffectNo;
    }

    public void setEffectSpeed(int effectSpeed) {
        this.effectSpeed = effectSpeed;
    }

    public void setRgbMode(boolean rgbMode) {
        isRgbMode = rgbMode;
    }

    public int getArrowIndex() {
        return arrowIndex;
    }

    public void setArrowIndex(int arrowIndex) {
        this.arrowIndex = arrowIndex;
    }

    public int getCmd() {
        return cmd;
    }

    public int getEffectCategory() {
        return effectCategory;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public void setEffectCategory(int effectCategory) {
        this.effectCategory = effectCategory;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public int getEffectRepTimes() {
        return effectRepTimes;
    }

    public int getEffectFreq() {
        return effectFreq;
    }

    public void setEffectRepTimes(int effectRepTimes) {
        this.effectRepTimes = effectRepTimes;
    }

    public void setEffectFreq(int effectFreq) {
        this.effectFreq = effectFreq;
    }

    public int getPresetEffectNo() {
        return presetEffectNo;
    }

    public void setPresetEffectNo(int presetEffectNo) {
        this.presetEffectNo = presetEffectNo;
    }

    public int getMaxCctValue() {
        return maxCctValue;
    }

    public void setMaxCctValue(int maxCctValue) {
        this.maxCctValue = maxCctValue;
    }

    public int getMinCctValue() {
        return minCctValue;
    }

    public void setMinCctValue(int minCctValue) {
        this.minCctValue = minCctValue;
    }

    public boolean isLedOn() {
        return isLedOn;
    }

    public void setLedOn(boolean ledOn) {
        isLedOn = ledOn;
    }

    @Override
    public String toString() {
        return "LEDStatus{" +
                "hues=" + hues +
                ", cmd=" + cmd +
                ", mode=" + mode +
                ", effectCategory=" + effectCategory +
                ", saturation=" + saturation +
                ", brightness=" + brightness +
                ", colorTemperature=" + colorTemperature +
                ", rgbEffectNo=" + rgbEffectNo +
                ", cctEffectNo=" + cctEffectNo +
                ", effectSpeed=" + effectSpeed +
                ", isRgbMode=" + isRgbMode +
                ", effectRepTimes=" + effectRepTimes +
                ", effectFreq=" + effectFreq +
                ", presetEffectNo=" + presetEffectNo +
                ", arrowIndex=" + arrowIndex +
                ", modelNumber='" + modelNumber + '\'' +
                ", maxCctValue=" + maxCctValue +
                ", minCctValue=" + minCctValue +
                ", isLedOn=" + isLedOn +
                '}';
    }
}
