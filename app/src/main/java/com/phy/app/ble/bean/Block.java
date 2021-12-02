package com.phy.app.ble.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Block
 *
 * @author:zhoululu
 * @date:2018/5/19
 */

public class Block {

    private List<List<String>> bursts = new ArrayList<List<String>>();
    private String block;
    private int blockLength;

    public Block(String block) {
        this.block = block;

        this.blockLength = block.length()/2;

        analyzeBlock(block);
    }

    public List<List<String>> getBursts() {
        return bursts;
    }

    private void analyzeBlock(String block){
        String blockStr = block;
        int index = 0;
        List<String> list = null;
        while (true){
            if(index == 0){
                list = new ArrayList<>();
            }

            if(blockStr.length() <= 20*2){
                list.add(blockStr);
                bursts.add(list);
                break;
            }else{
               String str = blockStr.substring(0,20*2);
               blockStr = blockStr.substring(20*2,blockStr.length());
               list.add(str);

               index ++;
            }

            if (list.size() == 16){
                bursts.add(list);
                index = 0;
            }
        }
    }

    public int getBlockLength() {
        return blockLength;
    }

    public String getBlock() {
        return block;
    }
}
