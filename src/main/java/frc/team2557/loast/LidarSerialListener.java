package frc.team2557.loast;

import jaci.openrio.module.cereal.SerialListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Rader
 */
class LidarSerialListener extends SerialListener {

    private List<Byte> _byteBuilder;
    private LidarData[] _data;
    private float _currentMotorRPM;

    public LidarSerialListener() {
        this._byteBuilder = new ArrayList<Byte>();

        this._data = new LidarData[360];
        for(int i = 0; i < this._data.length; i++) {
            this._data[i] = new LidarData();
        }

        this.expect(1); // Work with one byte at a time, fixes issue with the header string on boot of the unit
    }

    float getCurrentRPM() {
        return this._currentMotorRPM;
    }

    LidarData getData(int angle) {
        while(angle > 360) angle -= 360;
        while(angle <= 0) angle += 360;

        return this._data[angle - 1];
    }

    @Override
    public void onSerialData(byte[] data) {
        byte b = data[0];

        if(b == (byte) 0xFA) {
            if(this._byteBuilder.size() == 22) {
                this.readPacket(this._byteBuilder.toArray(new Byte[this._byteBuilder.size()]));
            }
            this._byteBuilder.clear();
        }
        this._byteBuilder.add(b);
    }

    private void readPacket(Byte[] data) {

        // Skip 0: always 0xFA

        int index = data[1] & 0xFF; // 0xA0 (160) - 0xF9 (249)
        index -= 160; // Turn the index into 0-89

        // Get checksum from packet
        int cs_L = data[20] & 0xFF;
        int cs_H = data[21] & 0xFF;
        int pdChecksum = (cs_H << 8) | cs_L;

        // Validate checksum
        if(calcChecksum(data) == pdChecksum) {

            // Motor RPM
            int speed_L = data[2] & 0xFF;
            int speed_H = data[3] & 0xFF;
            float motor_rpm = (float) ((speed_H << 8) | speed_L) / 64.0f;
            this._currentMotorRPM = motor_rpm;

            // Loop through the data for this packet (4 per packet)
            for(int i = 0; i < 4; i++) {
                int angle = index * 4 + i;

                int d1 = data[4 + 4 * i] & 0xFF; // First half of distance data
                int d2 = data[5 + 4 * i] & 0xFF; // Invalid flag or second half of distance data
                int d3 = data[6 + 4 * i] & 0xFF; // First half of quality data
                int d4 = data[7 + 4 * i] & 0xFF; // Second half of quality data

                // Check for valid distance
                int distance = 0;
                if((d2 & 0x80) == 0) { // Valid data
                    distance = ((d2 & 0x3F) << 8) | d1; // Strips out last two bits of higher value; distance = 14 bits
                } else { // Invalid data!
                    Loast.logger.error("Invalid data (error 0x" + String.format("%02X", d1) + ") for angle "
                            + angle + ". Not fatal, but angle data is lost.");
                }

                int quality = (d4 << 8) | d3;

                // Update data
                // If data was lost, it will simply be 0.
                this.getData(angle).distance = distance;
                this.getData(angle).quality = quality;
            }

        } else {
            Loast.logger.error("Checksum failed! Not fatal, but packet is lost.");
        }

    }

    private int calcChecksum(Byte[] data) {
        int[] data_list = new int[10];
        // Group the data by word, little-endian
        for(int i = 0; i < data_list.length; i++) {
            int d1 = data[2 * i] & 0xFF;
            int d2 = data[2 * i + 1] & 0xFF;

            data_list[i] = (d2 << 8) | d1;
        }

        // Compute the checksum on 32 bits
        int chk32 = 0;
        for(int d : data_list) {
            chk32 = (chk32 << 1) + d;
        }

        // Return a value wrapped around on 15 bits, and truncated to still fit into 15 bits
        int checksum = 0;
        checksum = (chk32 & 0x7FFF) + (chk32 >> 15); // Wrap around to fit into 15 bits
        checksum &= 0x7FFF; // Truncate to 15 bits

        return checksum;
    }

}
