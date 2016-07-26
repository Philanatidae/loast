package frc.team2557.loast;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * @author Philip Rader
 */
class NetTablesUpdator {

    public static void updateNetworkTables() {
        ITable lidarTable = NetworkTable.getTable("Lidar");

        double[] distances = new double[360];
        double[] qualities = new double[360];
        for(int i = 1; i < 360; i++) {
            LidarData data = Loast.getData(i);

            distances[i] = data.getDistance();
            qualities[i] = data.getQuality();
        }

        lidarTable.putNumberArray("Distances", distances);
        lidarTable.putNumberArray("Qualities", qualities);
        lidarTable.putNumber("RPM", Loast.getCurrentRPM());
    }

}
