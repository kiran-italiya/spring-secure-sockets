package com.kiran.securesockets.common.utils;

public class GetIpmac {

	public static String getClientMACAddress(String clientIp) {
		String macAddress = "";
//		try {
//			String str = "";
//			Process p = Runtime.getRuntime().exec("nbtstat -A " + clientIp);
//			InputStreamReader ir = new InputStreamReader(p.getInputStream());
//			LineNumberReader input = new LineNumberReader(ir);
//			for (int i = 1; i < 100;
//			     i++) {
//				str = input.readLine();
//				if (str != null) {
//					if
//					(str.indexOf("MAC Address") > 1) {
//						macAddress =
//								str.substring(str.indexOf("MAC Address") + 14, str.length());
//						break;
//					}
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace(System.out);
//		}
		return macAddress;
	}
}


