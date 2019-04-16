package com.shawn.video.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author shawn
 * @Description 视频转换测试
 * @Date 10:11 2019/4/16 0016
 * @Param
 * @return
 **/
public class FFMpegTest {

	private String ffmpegEXE;
	
	public FFMpegTest(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}
	
	public void convertor(String videoInputPath, String videoOutputPath) throws Exception {
//		ffmpeg -i input.mp4 -y output.avi
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);
		
		command.add("-i");
		command.add(videoInputPath);
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

		//
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
		FFMpegTest ffmpeg = new FFMpegTest("F:\\WechatDev\\Utils\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffmpeg.convertor("F:\\WechatDev\\Utils\\ffmpeg\\bin\\烟火.mp4", "F:\\WechatDev\\Utils\\ffmpeg\\bin\\烟火11.avi");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
