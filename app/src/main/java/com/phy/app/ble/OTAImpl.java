package com.phy.app.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.phy.app.ble.bean.Block;
import com.phy.app.ble.bean.FirmWareFile;
import com.phy.app.ble.bean.Partition;
import com.phy.app.util.HexString;
import com.phy.app.util.Util;
import com.phy.app.util.LogUtil;

import java.util.List;
import java.util.UUID;

/**
 * OTACallBack
 *
 * @author:zhoululu
 * @date:2018/5/19
 */

public class OTAImpl {

    FirmWareFile firmWareFile;
    BluetoothGatt bluetoothGatt;

    private int partitionIndex = 0;
    private int blockIndex = 0;
    private int brustIndex = 0;
    private int cmdIndex = 0;
    private int flash_addr = 0;

    private boolean isResponse;
    private String response;

    private List<String> brusts;

    private long totalSize;
    private long finshSize;


    public OTAImpl(FirmWareFile firmWareFile) {
        this.firmWareFile = firmWareFile;

        totalSize = firmWareFile.getLength();
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }


    //uuid 判断发送数据

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if(characteristic.getUuid().toString().equals(OperateConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID)){

            if(status == 0){
                cmdIndex ++;
                if(cmdIndex < brusts.size()){
                    sendOTADate(brusts.get(cmdIndex));
                }

                BandUtil.callBack.onProcess(finshSize*100/totalSize);
            }else{
                BandUtil.callBack.onError(status+"");
            }

            LogUtil.getLogUtilInstance().save("send ota data status: "+status);

        }else{
            //start response
            if(("0081").equals(response) && isResponse) {
                Partition partition = firmWareFile.getList().get(partitionIndex);
                int checsum = getPartitionCheckSum(partition);
                String cmd = make_part_cmd(partitionIndex, flash_addr, partition.getAddress(), partition.getPartitionLength(), checsum);
                sendOTACommand(cmd, true);

            }else if(("0084").equals(response) && isResponse) {
                blockIndex = 0;

                int size = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex).getBlockLength();
                String cmd = make_block_cmd(size, blockIndex);
                sendOTACommand(cmd, true);
            }else if(("0086").equals(response) && isResponse){
                brustIndex = 0;

                brusts = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex).getBursts().get(brustIndex);
                sendOTADate(brusts.get(cmdIndex));

            }

            isResponse = false;

            LogUtil.getLogUtilInstance().save("send ota command status: "+status);
        }

    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        response = HexString.parseStringHex(characteristic.getValue());
        if(("0087").equals(response)) {
            brustIndex++;

            cmdIndex = 0;

            if (brustIndex < firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex).getBursts().size()) {

                brusts = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex).getBursts().get(brustIndex);
                sendOTADate(brusts.get(cmdIndex));
            }
        }else if(("0088").equals(response) && isResponse){
            blockIndex ++;

            if(blockIndex < firmWareFile.getList().get(partitionIndex).getBlocks().size()){
                int size = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex).getBlockLength();
                String cmd = make_block_cmd(size,blockIndex);
                sendOTACommand(cmd,true);
            }

        }if(("0085").equals(response)){
            partitionIndex++;

            if(partitionIndex < firmWareFile.getList().size()){
                //后面地址由前一个长度决定
                Partition prePartition = firmWareFile.getList().get(partitionIndex-1);
                Partition partition = firmWareFile.getList().get(partitionIndex);
                flash_addr = flash_addr + prePartition.getPartitionLength() + 16 - (prePartition.getPartitionLength()+4)%4;
                int checsum = getPartitionCheckSum(partition);
                String cmd = make_part_cmd(partitionIndex,flash_addr,partition.getAddress(),partition.getPartitionLength(),checsum);
                sendOTACommand(cmd,true);
            }
        }else if(("0083").equals(response) && isResponse){
            sendOTACommand("04",false);
            initData();

            BandUtil.callBack.onComplete();

        }

        if(!("0081").equals(response) && !("0084").equals(response) && !("0086").equals(response) && !("0083").equals(response) && !("0085").equals(response) && !("0087").equals(response) && !("0088").equals(response)){
            BandUtil.callBack.onError(response);
        }

        LogUtil.getLogUtilInstance().save("ota commond response: "+response);

        isResponse = true;
    }

    private void sendOTACommand(String commd,boolean respons){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_OTA_UUID));
        if(bluetoothGattService == null){
            Log.e(" OTA service", "service is null");
            return;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_OTA_WRITE_UUID));
        if(!respons){
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }else{
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }
        bluetoothGattCharacteristic.setValue(HexString.parseHexString(commd.toLowerCase()));
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

        Log.d("send ota commond", commd);

        LogUtil.getLogUtilInstance().save("send ota commond: "+commd);
    }

    private void sendOTADate(String cmd){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_OTA_UUID));
        if(bluetoothGattService == null){
            Log.e(" OTA service", "service is null");
            return;
        }

        finshSize += cmd.length()/2;

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID));

        bluetoothGattCharacteristic.setValue(HexString.parseHexString(cmd.toLowerCase()));
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

        Log.d("send ota data", cmd);

        LogUtil.getLogUtilInstance().save("send ota data: "+cmd);
    }

    private String make_part_cmd(int index,int flash_addr,String run_addr,int size,int checksum){
        String fa = Util.translateStr(Util.strAdd0(Integer.toHexString(flash_addr),8));
        String ra = Util.translateStr(Util.strAdd0(run_addr,8));
        String sz = Util.translateStr(Util.strAdd0(Integer.toHexString(size),8));
        String cs = Util.translateStr(Util.strAdd0(Integer.toHexString(checksum),4));
        String in = Util.strAdd0(Integer.toHexString(index),2);

        return "02"+ in +fa + ra + sz + cs;
    }

    private String make_block_cmd(int size,int index){
        String sz = Util.translateStr(Util.strAdd0(Integer.toHexString(size),4));
        String in = Util.strAdd0(Integer.toHexString(index),2);
        return "03"+ sz + in;
    }

    private int getPartitionCheckSum(Partition partition){
        List<Block> blocks = partition.getBlocks();
        int check = 0;
        for (int i =0;i<blocks.size();i++){
            String block = blocks.get(i).getBlock();
            byte[] bytes = HexString.parseHexString(block);

            check = checkSum(check,bytes);
        }

        return check;
    }

    private int checkSum(int crc, byte[] data) {
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
        /*String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
        }*/
        return crc;
    }

    private void initData(){
        partitionIndex = 0;
        blockIndex = 0;
        brustIndex = 0;
        cmdIndex = 0;
        flash_addr = 0;
    }


    public FirmWareFile getFirmWareFile() {
        return firmWareFile;
    }
}
