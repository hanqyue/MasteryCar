package vstc2.nativecaller;


import android.content.Context;
import android.provider.Settings;

public class NativeCaller {
	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("vstc2_jni");
		System.loadLibrary("avi_utils");
		System.loadLibrary("PPPP_API");
	}
	public native static int PPPPAlarmSetting(String did, int alarm_audio,
			int motion_armed, int motion_sensitivity, int input_armed,
			int ioin_level, int iolinkage, int ioout_level, int alarmpresetsit,
			int mail, int snapshot, int record, int upload_interval,
			int schedule_enable, int schedule_sun_0, int schedule_sun_1,
			int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
			int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
			int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
			int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
			int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
			int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
			int schedule_sat_2, int defense_plan1, int defense_plan2,
			int defense_plan3, int defense_plan4, int defense_plan5,
			int defense_plan6, int defense_plan7, int defense_plan8,
			int defense_plan9, int defense_plan10, int defense_plan11,
			int defense_plan12, int defense_plan13, int defense_plan14,
			int defense_plan15, int defense_plan16, int defense_plan17,
			int defense_plan18, int defense_plan19, int defense_plan20,
			int defense_plan21,int remind_rate);

	public native static int RecordLocal(String uid, int bRecordLocal); // 0解码后的数据，1全部数据

	public native static int PPPPCameraStatus(String did, int bEnableLanSearch);// 第一个参数传入UID，第二个参数传
																				// 0x7F。通过返回值即可获得该UID在线状态

	public native static void PPPPInitialOther(String svr);

	public native static void UpgradeFirmware(String did, String servPath,
			String filePath, int type);

	public native static int SetSensorStatus(String did, int status);// set_sensorstatus.cgi

	public native static int DeleSensor(String did, int status);// del_sensor.cgi

	public native static int EditSensor(String did, int status, String name);// set_sensorname.cgi

	public native static int SetSensorPrest(String did, int preset, int sensorid);// set_sensor_preset.cgi

	public native static int TransferMessage(String did, String msg, int len);

	public native static void StartSearch();

	public native static void StopSearch();

	public native static void Init();

	public native static void Free();

	public native static void FormatSD(String did);

	public native static int StartPPPP(String did, String user, String pwd,
			int bEnableLanSearch, String accountname);

	public native static int StopPPPP(String did);

	public native static int StartPPPPLivestream(String did, int streamid,
			int substreamid);

	public native static int StopPPPPLivestream(String did);

	public native static int PPPPPTZControl(String did, int command);

	public native static int PPPPCameraControl(String did, int param, int value);

	public native static int PPPPGetCGI(String did, int cgi);

	public native static int PPPPStartAudio(String did);

	public native static int PPPPStopAudio(String did);

	public native static int PPPPStartTalk(String did);

	public native static int PPPPStopTalk(String did);

	public native static int PPPPTalkAudioData(String did, byte[] data, int len);

	public native static int PPPPNetworkDetect();

	public native static void PPPPInitial(String svr);

	public native static int PPPPSetCallbackContext(Context object);

	public native static int PPPPRebootDevice(String did);

	public native static int PPPPRestorFactory(String did);

	public native static int StartPlayBack(String did, String filename,
			int offset, int picTag);

	public native static int StopPlayBack(String did);

	public native static int PausePlayBack(String did,int pause);

	public native static int PPPPGetSDCardRecordFileList(String did,
			int PageIndex, int PageSize);

	public native static int PPPPWifiSetting(String did, int enable,
			String ssid, int channel, int mode, int authtype, int encryp,
			int keyformat, int defkey, String key1, String key2, String key3,
			String key4, int key1_bits, int key2_bits, int key3_bits,
			int key4_bits, String wpa_psk);

	public native static int PPPPNetworkSetting(String did, String ipaddr,
			String netmask, String gateway, String dns1, String dns2, int dhcp,
			int port, int rtsport);

	public native static int PPPPUserSetting(String did, String user1,
			String pwd1, String user2, String pwd2, String user3, String pwd3);

	public native static int PPPPDatetimeSetting(String did, int now, int tz,
			int ntp_enable, String ntp_svr);

	public native static int PPPPDDNSSetting(String did, int service,
			String user, String pwd, String host, String proxy_svr,
			int ddns_mode, int proxy_port);

	public native static int PPPPMailSetting(String did, String svr, int port,
			String user, String pwd, int ssl, String sender, String receiver1,
			String receiver2, String receiver3, String receiver4);

	public native static int PPPPFtpSetting(String did, String svr_ftp,
			String user, String pwd, String dir, int port, int mode,
			int upload_interval);

	public native static int PPPPPTZSetting(String did, int led_mod,
			int ptz_center_onstart, int ptz_run_times, int ptz_patrol_rate,
			int ptz_patrul_up_rate, int ptz_patrol_down_rate,
			int ptz_patrol_left_rate, int ptz_patrol_right_rate,
			int disable_preset);

	// public native static int PPPPAlarmSetting(String did, int motion_armed,
	// int motion_sensitivity, int input_armed, int ioin_level,
	// int iolinkage, int ioout_level, int alarmpresetsit, int mail,
	// int snapshot, int record, int upload_interval, int schedule_enable,
	// int schedule_sun_0, int schedule_sun_1, int schedule_sun_2,
	// int schedule_mon_0, int schedule_mon_1, int schedule_mon_2,
	// int schedule_tue_0, int schedule_tue_1, int schedule_tue_2,
	// int schedule_wed_0, int schedule_wed_1, int schedule_wed_2,
	// int schedule_thu_0, int schedule_thu_1, int schedule_thu_2,
	// int schedule_fri_0, int schedule_fri_1, int schedule_fri_2,
	// int schedule_sat_0, int schedule_sat_1, int schedule_sat_2);

	public native static int PPPPSDRecordSetting(String did,
			int record_cover_enable, int record_timer, int record_size,int record_chnl,
			int record_time_enable, int record_schedule_sun_0,
			int record_schedule_sun_1, int record_schedule_sun_2,
			int record_schedule_mon_0, int record_schedule_mon_1,
			int record_schedule_mon_2, int record_schedule_tue_0,
			int record_schedule_tue_1, int record_schedule_tue_2,
			int record_schedule_wed_0, int record_schedule_wed_1,
			int record_schedule_wed_2, int record_schedule_thu_0,
			int record_schedule_thu_1, int record_schedule_thu_2,
			int record_schedule_fri_0, int record_schedule_fri_1,
			int record_schedule_fri_2, int record_schedule_sat_0,
			int record_schedule_sat_1, int record_schedule_sat_2);

	public native static int PPPPGetSystemParams(String did, int paramType);

	// takepicture
	public native static int YUV4202RGB565(byte[] yuv, byte[] rgb, int width,
			int height);

	public native static int DecodeH264Frame(byte[] h264frame, int bIFrame,
			byte[] yuvbuf, int length, int[] size);

}