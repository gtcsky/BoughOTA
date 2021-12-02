package com.phy.app.ble.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Partition
 *
 * @author:zhoululu
 * @date:2018/5/19
 */

public class Partition {

    private String address;
    private int partitionLength;

    private List<Block> blocks = new ArrayList<>();

    public Partition(String address, String result) {
        this.address = address;

        partitionLength = result.length()/2;

        analyzePartition(result);
    }

    public String getAddress() {
        return address;
    }

    public int getPartitionLength() {
        return partitionLength;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    private void analyzePartition(String data) {
        String partitionStr = data;

        while (true){
            String blockData = "";
            if(partitionStr.length() <= 1024*2){
                blockData = partitionStr;
                blocks.add(new Block(blockData));
                break;
            }else{
                blockData = partitionStr.substring(0,1024*2);
                partitionStr = partitionStr.substring(1024*2,partitionStr.length());
                blocks.add(new Block(blockData));
            }
        }
    }

    public static String Make_CRC(int crc, byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;

        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else{
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
                }
            }
        }
        String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
        }
        return c;
    }

}