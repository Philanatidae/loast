package frc.team2557.loast;

import jaci.openrio.toast.lib.module.ModuleConfig;

/**
 * Majority of this file was originally from Open-RIO/ToastAPI
 * under the MIT license. (c) OpenRIO
 * @author  Jaci
 */
class LoastConfig {

    public static ModuleConfig config;

    public static void init() {
        config = new ModuleConfig("Loast");
        for(Property prop : Property.values())
            prop.read(config);
    }

    public enum Property {

        COM_PORT("com.port", "COM0")
        ;

        String key;
        Object defaultValue;

        Object value;

        Property(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        /**
         * Read the property from the ModuleConfig file, with the default value and comments
         * if it doesn't exist
         */
        public void read(ModuleConfig config) {
            value = config.get(key, defaultValue);
        }

        public Number asNumber() {
            return (Number) value;
        }

        public int asInt() {
            return asNumber().intValue();
        }

        public double asDouble() {
            return asNumber().doubleValue();
        }

        public float asFloat() {
            return asNumber().floatValue();
        }

        public byte asByte() {
            return asNumber().byteValue();
        }

        public String asString() {
            return (String) value;
        }

        public boolean asBoolean() {
            return (boolean) value;
        }

    }

}
