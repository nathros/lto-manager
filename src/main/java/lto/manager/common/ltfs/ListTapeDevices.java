package lto.manager.common.ltfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lto.manager.common.ExternalProcess;

public class ListTapeDevices extends ExternalProcess {
	private static final String[] cmd = {"bash", "list.sh" };
	private List<TapeDevicesInfo> devices = null;

	public boolean start() throws IOException {
		return start(cmd);
	}

	public class TapeDevicesInfo {
		private String deviceName;
		private String vendorID;
		private String productID;
		private String serialNumber;
		private String productName;

		public TapeDevicesInfo(String stdout) {
			String[] comma = stdout.split(",");
			for (int i = 0; i < comma.length; i++) {
				String[] keyValue = comma[i].split("=");

				String key = keyValue[0].trim();
				switch (key) {
				case "Device Name": {
					deviceName = keyValue[1].trim();
					break;
				}
				case "Vender ID": {
					vendorID = keyValue[1].trim();
					break;
				}
				case "Product ID": {
					productID = keyValue[1].trim();
					break;
				}
				case "Serial Number": {
					serialNumber = keyValue[1].trim();
					break;
				}
				case "Product Name": {
					int indexFirst = keyValue[1].indexOf('[');
					int indexLast = keyValue[1].lastIndexOf(']');
					if ((indexFirst > 0) && (indexLast > 0)) {
						productName = keyValue[1].substring(indexFirst + 1, indexLast);
					} else productName = keyValue[1].trim();
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + key);
				}
			}
			if (deviceName == null) throw new IllegalArgumentException("Missing Device Name");
			if (vendorID == null) throw new IllegalArgumentException("Missing Vender ID");
			if (productID == null) throw new IllegalArgumentException("Missing Product ID");
			if (serialNumber == null) throw new IllegalArgumentException("Missing Serial Number");
			if (productName == null) throw new IllegalArgumentException("Missing Product Name");
		}

		public String getDeviceName() { return deviceName; }
		public String getVendorID() { return vendorID; }
		public String getProductID() { return productID; }
		public String getSerialNumber() { return serialNumber; }
		public String getProductName() { return productName; }
	}

	public List<TapeDevicesInfo> getDevices() { return devices; }

	@Override
	public void onProcessExit() {
		if (exitCode == 0) {
			List<TapeDevicesInfo> items = new ArrayList<TapeDevicesInfo>();
			for (String i: stdout) {
				try {
					TapeDevicesInfo info = new TapeDevicesInfo(i);
					items.add(info);
				} catch (Exception e) {}
			}
			this.devices = items;
		}

	}
}
