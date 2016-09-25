package guankaer.ecg;

import guankaer.bluetooth.BluetoothService;

/**
 * Created by HP on 2016/9/25.
 */
public class EcgProtocol {
    // 心电数据帧长度
    public static final int ECG_DATA_LEN = 18;
    private int sum_1 = 0;
    private int sum_2 = 0;
    private int sum_3 = 0;
    private int count = 0;

    public static int From2ComplementtoUnsigned(int data) {

        String binary = Integer.toBinaryString(data); // Decimal to Binary
        if (binary.length() > 8)
            binary = binary.substring(binary.length() - 8); // Binary to 8
        // digit.
        return Integer.parseInt(binary, 2);// Binary to Decimal.
    }

}
