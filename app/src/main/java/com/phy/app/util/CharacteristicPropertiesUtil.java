package com.phy.app.util;

import java.util.ArrayList;
import java.util.List;

public class CharacteristicPropertiesUtil {

    public final static int GATT_PROP_BROADCAST = 0x01; //!< Permits broadcasts of the Characteristic Value
    public final static int GATT_PROP_READ = 0x02; //!< Permits reads of the Characteristic Value
    public final static int GATT_PROP_WRITE_NO_RSP = 0x04; //!< Permits writes of the Characteristic Value without response
    public final static int GATT_PROP_WRITE = 0x08; //!< Permits writes of the Characteristic Value with response
    public final static int GATT_PROP_NOTIFY = 0x10; //!< Permits notifications of a Characteristic Value without acknowledgement
    public final static int GATT_PROP_INDICATE = 0x20; //!< Permits indications of a Characteristic Value with acknowledgement
    public final static int GATT_PROP_AUTHED = 0x40; //!< Permits signed writes to the Characteristic Value
    public final static int GATT_PROP_EXTENDED = 0x80; //!< Additional characteristic properties are defined in the Characteristic Extended Properties Descriptor

    public static List<String> getPropertiesName(int properties) {
        if (properties >= 256 || properties <= 0)
            return null;
        int i = 0;
        List<String> propertiesNames = new ArrayList<>();
        if ((properties & GATT_PROP_BROADCAST) != 0)
            propertiesNames.add("Broadcast");

        if ((properties & GATT_PROP_READ) != 0)
            propertiesNames.add("Read");

        if ((properties & GATT_PROP_WRITE_NO_RSP) != 0)
            propertiesNames.add("Write No Response");

        if ((properties & GATT_PROP_WRITE) != 0)
            propertiesNames.add("Write");

        if ((properties & GATT_PROP_NOTIFY) != 0)
            propertiesNames.add("Notify");

        if ((properties & GATT_PROP_INDICATE) != 0)
            propertiesNames.add("Indicate");

        if ((properties & GATT_PROP_AUTHED) != 0)
            propertiesNames.add("Authed");
        if ((properties & GATT_PROP_EXTENDED) != 0)
            propertiesNames.add("Extended");
        return propertiesNames;
    }
}
