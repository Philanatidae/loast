package frc.team2557.loast;

/**
 * @author Philip Rader
 */
public class LidarData {

    int distance;
    int quality;

    LidarData() {

    }

    /**
     * Returns the distance in mm.
     *
     * @return Distance in mm.
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Returns the quality
     *
     * @return Quality
     */
    public int getQuality() {
        return quality;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        if(o == this)
            return true;

        if(!(o instanceof LidarData))
            return false;

        LidarData other = (LidarData) o;
        return other.distance == distance
                && other.quality == quality;
    }

}
