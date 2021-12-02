package com.phy.app.beans;

import android.Manifest;
import android.bluetooth.BluetoothGattService;
import android.os.Parcelable;

import java.util.UUID;

public class MyBluetoothGattService  extends BluetoothGattService implements Parcelable {
    /**
     * Create a new BluetoothGattService.
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @param uuid        The UUID for this service
     * @param serviceType The type of this service,
     *                    {@link BluetoothGattService#SERVICE_TYPE_PRIMARY}
     *                    or {@link BluetoothGattService#SERVICE_TYPE_SECONDARY}
     */
    public MyBluetoothGattService(UUID uuid, int serviceType) {
        super(uuid, serviceType);
    }
}
