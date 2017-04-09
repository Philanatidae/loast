package frc.team2557.loast;

import jaci.openrio.module.cereal.Cereal;
import jaci.openrio.module.cereal.SerialPortWrapper;
import jaci.openrio.toast.core.thread.Heartbeat;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ToastModule;
import jssc.SerialPortException;

/**
 * @author Philip Rader
 */
public class Loast extends ToastModule {

    public static Logger logger;

    private static LidarSerialListener lidarListener = new LidarSerialListener();

    private SerialPortWrapper lidarSerialPort;

    @Override
    public String getModuleName() {
        return "Loast";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void prestart() {
        logger = new Logger("loast", Logger.ATTR_DEFAULT);

        LoastConfig.init();
    }

    @Override
    public void start() {
        try {
            lidarSerialPort = Cereal.getPort(LoastConfig.Property.COM_PORT.asString(), 115200, 8, 1, 0);

            lidarSerialPort.registerListener(lidarListener);
        } catch (SerialPortException e) {
            e.printStackTrace();
            logger.severe("Loast could not open the serial port! Ensure that the COM_PORT is correctly set in the Loast config file.");
        }

        Heartbeat.add(skipped -> {
            NetTablesUpdator.updateNetworkTables();
        });
    }

    // STATICS!

    /**
     * Gets the current recorded data from the sensor.
     * If no data can be collected, a 0 for distance
     * will be returned. Disregard all distances with
     * a values of 0 (this is safe to do, since the
     * unit has a minumum range before it gives an
     * error. No valid data will be lost).
     *
     * @param angle Angle of Lidar head
     * @return LidarData at given angle
     */
    public static LidarData getData(int angle) {
        return lidarListener.getData(angle);
    }

    /**
     * Gets the current revolutsion per minute (RPM)
     * of the head.
     *
     * @return RPM of Lidar head
     */
    public static float getCurrentRPM() {
        return lidarListener.getCurrentRPM();
    }

}
