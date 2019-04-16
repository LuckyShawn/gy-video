package com.shawn.video.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author shwan
 * @Description //视频与背景音乐合并
 * @Date 10:55 2019/4/16 0016
 * @Param  ffmpeg.exe -i 11.mp4 -i 22.mp3 -t 5 -y 3.mp4
 * @return
 **/
public class MergeVideoMp3 {

	private String ffmpegEXE;
	
	public MergeVideoMp3(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}
	
	public void convertor(String videoInputPath, String mp3InputPath,
			double seconds, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 11.mp4 -i 22.mp3 -t 5 -y 33.mp4     11视频和22音频合成33.mp4
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);
		
		command.add("-i");
		command.add(videoInputPath);
		
		command.add("-i");
		command.add(mp3InputPath);
		
		command.add("-t");
		command.add(String.valueOf(seconds));
		
		command.add("-y");
		command.add(videoOutputPath);
		
		for (String c : command) {
			System.out.print(c + " ");
		}
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		
		String line = "";
		while ( (line = br.readLine()) != null ) {
		}
		
		if (br != null) {
			br.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}
		
	}

	public static void main(String[] args) {
		MergeVideoMp3 ffmpeg = new MergeVideoMp3("F:\\WechatDev\\Utils\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffmpeg.convertor("F:\\WechatDev\\Utils\\ffmpeg\\bin\\11.mp4",
					"F:\\WechatDev\\Utils\\ffmpeg\\bin\\22.mp3",
					5,
					"F:\\WechatDev\\Utils\\ffmpeg\\bin\\33.avi");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
