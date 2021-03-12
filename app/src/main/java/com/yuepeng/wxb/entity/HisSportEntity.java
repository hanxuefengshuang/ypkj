package com.yuepeng.wxb.entity;



import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.model.LatLng;
import com.wstro.thirdlibrary.entity.TjDateResponse;

/**
 * Created by wangjun on 2021/2/9.
 */
public class HisSportEntity {


    private HistoryTrackResponse historyTrackResponse;
    private String startAddress;
    private String endAddress;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private long startTime;
    private long endTime;
    private double startdistance;
    private double enddistance;

    public double getStartdistance() {
        return startdistance;
    }

    public void setStartdistance(double startdistance) {
        this.startdistance = startdistance;
    }

    public double getEnddistance() {
        return enddistance;
    }

    public void setEnddistance(double enddistance) {
        this.enddistance = enddistance;
    }

    private TjDateResponse tjDateResponse;

    public TjDateResponse getTjDateResponse() {
        return tjDateResponse;
    }

    public void setTjDateResponse(TjDateResponse tjDateResponse) {
        this.tjDateResponse = tjDateResponse;
    }



    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }



    public HistoryTrackResponse getHistoryTrackResponse() {
        return historyTrackResponse;
    }

    public void setHistoryTrackResponse(HistoryTrackResponse historyTrackResponse) {
        this.historyTrackResponse = historyTrackResponse;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public LatLng getStartLatLng() {
        return startLatLng;
    }

    public void setStartLatLng(LatLng startLatLng) {
        this.startLatLng = startLatLng;
    }

    public LatLng getEndLatLng() {
        return endLatLng;
    }

    public void setEndLatLng(LatLng endLatLng) {
        this.endLatLng = endLatLng;
    }
}